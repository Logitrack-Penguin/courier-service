package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception;

/**
 * Excepción personalizada que hereda de {@link RuntimeException}.
 * <p>
 *     Lanzada cuando se intenta registrar un recurso ya existente que cuenta con requisito de unicidad en el sistema.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see RuntimeException
 */

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
