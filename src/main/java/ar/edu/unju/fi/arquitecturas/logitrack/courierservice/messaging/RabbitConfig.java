package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ del lado productor. Solo se activa con
 * {@code logitrack.messaging.enabled=true}, para poder apagar la mensajería sin tocar código.
 * <p>
 * courier solo declara el exchange: no conoce las colas de sus consumidores. Cada consumidor
 * (por ejemplo, shipment-service) declara su propia cola y la vincula al exchange.
 * </p>
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@Configuration
@ConditionalOnProperty(name = "logitrack.messaging.enabled", havingValue = "true")
public class RabbitConfig {

    /** Exchange de tipo topic donde courier publica sus eventos. */
    public static final String EXCHANGE = "logitrack.courier.events";

    /**
     * Exchange durable (sobrevive a reinicios de RabbitMQ) y que no se borra solo.
     *
     * @return Exchange de eventos de courier.
     */
    @Bean
    public TopicExchange courierEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    /**
     * Convierte los mensajes a JSON con Jackson 3 (el que usa Spring Boot 4). Spring Boot lo
     * asigna automáticamente al RabbitTemplate.
     *
     * @return Conversor JSON.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
