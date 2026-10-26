package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception;

/**
 * Excepción personalizada que hereda de {@link RuntimeException}.
 * <p>
 *     Es lanzada desde la capa service cuando se quiere acceder a un recurso persistente en la BDD y no es encontrado.
 *     Es traducido en un código de estado HTTP 404 (Not Found)
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see RuntimeException
 */

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
