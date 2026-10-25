package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.model;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Clase Abstracta que provee los atributos y Listeners reutilizables para
 * todas las Entidades del Modelo de Dominio.
 * <p>
 * Al estar anotada con {@link MappedSuperclass}, hereda sus mapeos a las subclases
 * sin generar una tabla física independiente en la base de datos.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 24-10-2026
 * @see AuditingEntityListener
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class EntidadAuditable {
    /**
     * Fecha y hora en la cual la entidad fue creada.
     */
    @CreatedDate
    @Column(name = "fecha_creación", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última modificación de la entidad.
     */
    @LastModifiedDate
    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime ultimaModificacion;
}
