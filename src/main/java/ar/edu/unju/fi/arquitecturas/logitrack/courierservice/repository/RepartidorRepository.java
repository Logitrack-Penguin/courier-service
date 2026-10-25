package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.repository;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.model.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la gestión de la persistencia de la entidad {@link Repartidor}.
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 25-10-2026
 * @see Repartidor
 */
@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, UUID> {

    /**
     * Consulta un repartidor por su Documento Nacional de Identidad.
     *
     * @param dni DNI del repartidor.
     * @return Un {@link Optional} conteniendo el repartidor si fue encontrado.
     */
    Optional<Repartidor> findByDni(String dni);

    /**
     * Comprueba la existencia de un repartidor registrado con el DNI indicado.
     *
     * @param dni DNI a comprobar.
     * @return {@code true} si existe un repartidor con dicho DNI; {@code false} en caso contrario.
     */
    boolean existsByDni(String dni);

    /**
     * Comprueba la existencia de un repartidor registrado con el teléfono indicado.
     *
     * @param telefono Número de teléfono a comprobar.
     * @return {@code true} si existe un repartidor con dicho teléfono; {@code false} en caso contrario.
     */
    boolean existsByTelefono(String telefono);

    /**
     * Obtiene el listado de repartidores filtrados por su estado operativo.
     *
     * @param estadoRepartidor Estado operativo por el cual filtrar (e.g., ACTIVO, INACTIVO).
     * @return Lista de entidades {@link Repartidor} asociadas al estado solicitado.
     */
    List<Repartidor> findByEstadoRepartidor(EstadoRepartidor estadoRepartidor);
}