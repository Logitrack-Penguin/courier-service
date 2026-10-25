package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception;

/**
 * Excepción personalizada que hereda de {@link RuntimeException}.
 * <p>
 *     Es lanzada cuando se intenta realizar una operación sin tener los permisos adecuados, por ejemplo, actualizar
 *     el estado de un envío. Esta se traduce en un código de estado HTTP 403 (Forbidden)
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see RuntimeException
 */

public class OperacionNoPermitidaException extends RuntimeException {
    public OperacionNoPermitidaException(String message) {
        super(message);
    }
}
