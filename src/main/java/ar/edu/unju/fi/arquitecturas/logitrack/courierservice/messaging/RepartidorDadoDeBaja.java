package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.messaging;

/**
 * Evento INTERNO de Spring (no viaja por RabbitMQ) que el servicio de negocio anuncia cuando un
 * repartidor se desactiva o se elimina. Así {@code RepartidorServiceImpl} no depende de RabbitMQ:
 * si la mensajería está apagada, nadie escucha este evento y el servicio funciona igual.
 *
 * @param tipo           Tipo del evento: {@link #DESACTIVADO} o {@link #ELIMINADO}.
 * @param dni            DNI del repartidor.
 * @param estadoAnterior Estado previo al cambio.
 * @param estadoNuevo    Estado nuevo; {@code null} si fue eliminado.
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
public record RepartidorDadoDeBaja(String tipo, String dni, String estadoAnterior, String estadoNuevo) {

    /** Routing key del evento de desactivación. */
    public static final String DESACTIVADO = "repartidor.desactivado";

    /** Routing key del evento de eliminación. */
    public static final String ELIMINADO = "repartidor.eliminado";
}
