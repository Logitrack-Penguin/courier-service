package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception;

/**
 * Excepción personalizada que hereda de {@link RuntimeException}.
 * <p>
 *     Lanzada cuando el estado actual de un objeto no permite realizar la acción solicitada, a
 *     pesar de que los argumentos enviados sean válidos. Esta se traduce en un código de estado HTTP 400 (Bad Request)
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see RuntimeException
 */
public class OperacionInvalidaException extends RuntimeException {
    public OperacionInvalidaException(String message) {
        super(message);
    }
}
