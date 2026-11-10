package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * Formato común de los mensajes que courier-service publica en RabbitMQ. Viaja como JSON simple:
 * ningún otro servicio necesita conocer las clases Java de courier.
 *
 * @param eventId    Identificador único del mensaje, para rastrearlo en los logs.
 * @param eventType  Tipo del evento; coincide con la routing key (por ejemplo, {@code repartidor.desactivado}).
 * @param version    Versión del formato, para poder cambiarlo en el futuro sin romper a los consumidores.
 * @param occurredAt Momento en que se publicó el evento.
 * @param source     Servicio que originó el evento.
 * @param payload    Datos propios del evento.
 * @param <T>        Tipo del payload.
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
public record EventoIntegracion<T>(UUID eventId, String eventType, int version, Instant occurredAt,
                                   String source, T payload) {

    /** Versión actual del formato del mensaje. */
    public static final int VERSION = 1;

    /** Nombre con el que courier-service se identifica como origen. */
    public static final String SOURCE = "courier-service";

    /**
     * Crea un evento nuevo con id aleatorio y la fecha actual.
     *
     * @param tipo    Tipo del evento (routing key).
     * @param payload Datos del evento.
     * @param <T>     Tipo del payload.
     * @return Evento listo para publicar.
     */
    public static <T> EventoIntegracion<T> de(String tipo, T payload) {
        return new EventoIntegracion<>(UUID.randomUUID(), tipo, VERSION, Instant.now(), SOURCE, payload);
    }
}
