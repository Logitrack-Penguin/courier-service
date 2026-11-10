package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Pruebas unitarias de {@link RepartidorEventPublisher} (LT4-34).
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@ExtendWith(MockitoExtension.class)
class RepartidorEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RepartidorEventPublisher publisher;

    @Test
    @DisplayName("Publica en el exchange de courier con el tipo como routing key y el formato común")
    @SuppressWarnings("unchecked")
    void publicar_EnviaEventoConFormatoComun() {
        publisher.publicar(new RepartidorDadoDeBaja(RepartidorDadoDeBaja.DESACTIVADO, "28111222",
                "ACTIVO", "INACTIVO"));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.EXCHANGE), eq("repartidor.desactivado"),
                captor.capture());

        EventoIntegracion<RepartidorEstadoPayload> evento =
                (EventoIntegracion<RepartidorEstadoPayload>) captor.getValue();
        assertNotNull(evento.eventId());
        assertNotNull(evento.occurredAt());
        assertEquals("repartidor.desactivado", evento.eventType());
        assertEquals(1, evento.version());
        assertEquals("courier-service", evento.source());
        assertEquals(new RepartidorEstadoPayload("28111222", "ACTIVO", "INACTIVO"), evento.payload());
    }

    @Test
    @DisplayName("Si RabbitMQ no responde, registra el error y no lo propaga")
    void publicar_RabbitCaido_NoPropagaExcepcion() {
        doThrow(new AmqpConnectException(new ConnectException("Connection refused")))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        assertDoesNotThrow(() -> publisher.publicar(new RepartidorDadoDeBaja(
                RepartidorDadoDeBaja.ELIMINADO, "28111222", "ACTIVO", null)));
    }
}
