package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.messaging;

/**
 * Datos de los eventos {@code repartidor.desactivado} y {@code repartidor.eliminado}.
 *
 * @param dni            DNI del repartidor.
 * @param estadoAnterior Estado que tenía antes del cambio (ACTIVO o INACTIVO).
 * @param estadoNuevo    Estado después del cambio; {@code null} si el repartidor fue eliminado.
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
public record RepartidorEstadoPayload(String dni, String estadoAnterior, String estadoNuevo) {
}
