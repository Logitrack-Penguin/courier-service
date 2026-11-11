package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.security;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception.OperacionNoPermitidaException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Identidad del usuario que hace el pedido, armada con las cabeceras que agrega el api-gateway
 * después de validar el JWT ({@code X-User-Roles} y {@code X-User-Dni}).
 * <p>
 * El gateway solo controla el rol. Este objeto agrega el control de "quién es": un REPARTIDOR
 * únicamente puede ver o cambiar su propia ficha (errores 3 y 4 de LT4-40). Si el pedido no trae
 * cabeceras (por ejemplo, la llamada interna de shipment por Feign, que no pasa por el gateway),
 * no se restringe: courier no publica puertos, así que desde afuera solo se llega por el gateway.
 * </p>
 *
 * @param roles Roles del usuario (vacío si el pedido no pasó por el gateway).
 * @param dni   DNI del usuario, o {@code null} si el token no lo trae.
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 11-11-2026
 */
public record UsuarioActual(Set<String> roles, String dni) {

    /** Cabecera con los roles separados por coma. */
    public static final String HEADER_ROLES = "X-User-Roles";

    /** Cabecera con el DNI del usuario. */
    public static final String HEADER_DNI = "X-User-Dni";

    /**
     * Arma la identidad a partir de las cabeceras del gateway.
     *
     * @param rolesHeader Valor de {@code X-User-Roles} (puede ser nulo).
     * @param dniHeader   Valor de {@code X-User-Dni} (puede ser nulo).
     * @return La identidad del usuario.
     */
    public static UsuarioActual desde(String rolesHeader, String dniHeader) {
        Set<String> roles = rolesHeader == null ? Set.of() : Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
        String dni = dniHeader == null || dniHeader.isBlank() ? null : dniHeader.trim();
        return new UsuarioActual(roles, dni);
    }

    /**
     * Indica si el usuario es un REPARTIDOR (sin rol ADMIN) y, por lo tanto, solo puede acceder a lo suyo.
     *
     * @return {@code true} si hay que restringirlo a su propia ficha.
     */
    public boolean esRepartidorRestringido() {
        return roles.contains("REPARTIDOR") && !roles.contains("ADMIN");
    }

    /**
     * Indica si el usuario puede ver o modificar al repartidor con ese DNI.
     *
     * @param dniRepartidor DNI del repartidor pedido.
     * @return {@code true} si no está restringido o si el DNI es el suyo.
     */
    public boolean puedeAccederA(String dniRepartidor) {
        return !esRepartidorRestringido() || (dni != null && dni.equals(dniRepartidor));
    }

    /**
     * Lanza un 403 si el usuario no puede acceder al repartidor con ese DNI.
     *
     * @param dniRepartidor DNI del repartidor pedido.
     * @throws OperacionNoPermitidaException Si un REPARTIDOR pide los datos de otro.
     */
    public void verificarAcceso(String dniRepartidor) {
        if (!puedeAccederA(dniRepartidor)) {
            throw new OperacionNoPermitidaException("Un repartidor solo puede ver o cambiar sus propios datos");
        }
    }
}
