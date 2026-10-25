package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.model;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Representa al repartidor del envío dentro del sistema. Hereda de {@link EntidadAuditable}
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 24-10-2026
 * @see EntidadAuditable
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "repartidores")
public class Repartidor extends EntidadAuditable{

    /**
     * Identificador único universal del repartidor dentro del sistema.
     * <p>
     * El valor se genera automáticamente mediante la configuración
     * {@code @GeneratedValue(strategy = GenerationType.UUID)}.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID idRepartidor;

    /**
     * Documento Nacional de Identidad del repartidor.
     */
    @Column(name = "dni", nullable = false, updatable = false, unique = true)
    private String dni;

    /**
     * Nombre de pila del repartidor.
     */
    @Column(name = "nombre", nullable = false)
    private String nombre;

    /**
     * Apellido del repartidor.
     */
    @Column(name = "apellido", nullable = false)
    private String apellido;

    /**
     * Número telefónico de contacto del repartidor.
     */
    @Column(name = "teléfono", nullable = false, unique = true)
    private String telefono;

    /**
     * Tipo de vehículo de trabajo del repartidor.
     */
    @Column(name = "tipo_vehículo", nullable = false)
    private String tipoVehiculo;

    /**
     * Enumerado del estado del repartidor.
     * @see EstadoRepartidor
     */
    @Column(name = "estado_repartidor", nullable = false)
    private EstadoRepartidor estadoRepartidor;

}
