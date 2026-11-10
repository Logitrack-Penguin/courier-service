package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Traduce el evento interno {@link RepartidorDadoDeBaja} a un mensaje de RabbitMQ.
 * <p>
 * Escucha con {@code AFTER_COMMIT}: publica recién cuando la base de datos confirmó el cambio.
 * Si publicara antes y el guardado fallara, shipment liberaría envíos de un repartidor que en
 * realidad sigue activo.
 * </p>
 * <p>
 * Si RabbitMQ no está disponible, el error se registra en el log y no se propaga: el cambio del
 * repartidor ya está guardado y no tiene sentido devolverle un error al usuario.
 * </p>
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "logitrack.messaging.enabled", havingValue = "true")
public class RepartidorEventPublisher {

    /** Plantilla de Spring AMQP para enviar mensajes (usa el conversor JSON de {@link RabbitConfig}). */
    private final RabbitTemplate rabbitTemplate;

    /**
     * Publica el evento en el exchange de courier, con el tipo como routing key.
     *
     * @param evento Evento interno emitido por el servicio de repartidores.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(RepartidorDadoDeBaja evento) {
        EventoIntegracion<RepartidorEstadoPayload> mensaje = EventoIntegracion.de(evento.tipo(),
                new RepartidorEstadoPayload(evento.dni(), evento.estadoAnterior(), evento.estadoNuevo()));
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, evento.tipo(), mensaje);
            log.info("Evento {} publicado para el DNI {} (eventId={})", evento.tipo(), evento.dni(),
                    mensaje.eventId());
        } catch (AmqpException ex) {
            log.error("No se pudo publicar {} para el DNI {}: los envíos de shipment pueden quedar "
                    + "desactualizados", evento.tipo(), evento.dni(), ex);
        }
    }
}
