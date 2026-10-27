package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.service.interfaces;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.model.Repartidor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz de la capa de servicio que define el contrato de negocio para la gestión
 * de la entidad {@link Repartidor}.
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 25-10-2026
 * @see RepartidorRequestDto
 * @see RepartidorResponseDto
 */
public interface RepartidorService {

    /**
     * Registra un nuevo repartidor en el sistema validando restricciones de unicidad.
     *
     * @param requestDto Payload con los datos del nuevo repartidor.
     * @return DTO {@link RepartidorResponseDto} con la información del repartidor guardado.
     */
    RepartidorResponseDto registrarRepartidor(RepartidorRequestDto requestDto);

    /**
     * Permite la actualización de un repartidor existente en la Base de Datos.
     *
     * @param dniRepartidor Documento de Identificación Nacional del Repartidor.
     * @param requestDto Payload con los nuevos datos del repartidor.
     * @return DTO {@link RepartidorResponseDto} con la información actualizada del repartidor.
     * @throws RecursoNoEncontradoException En caso de que no se encuentre el repartidor con el DNI ingresado.
     * @throws RecursoDuplicadoException En caso de que se intente registrar un teléfono ya existente.
     */
    RepartidorResponseDto actualizarRepartidor(String dniRepartidor, RepartidorRequestDto requestDto);

    /**
     * Retorna un booleano representando la disponibilidad del repartidor encontrado.
     *
     * @param idRepartidor Identificador único del repartidor.
     * @return {@link Boolean} Que representa si el repartidor encontrado está disponible o no.
     * @throws RecursoNoEncontradoException En caso de que no se encuentre el repartidor con el ID ingresado.
     */
    boolean estaDisponible(UUID idRepartidor);

    /**
     * Retorna un repartidor basándonos en el DNI ingresado.
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor a buscar.
     * @return DTO {@link RepartidorResponseDto} con la información del repartidor encontrado.
     * @throws RecursoNoEncontradoException En caso de que no se encuentre el repartidor con el DNI ingresado.
     */
    RepartidorResponseDto obtenerPorDni(String dniRepartidor);

    /**
     * Permite gestionar el estado del repartidor.
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor encontrado.
     * @return DTO {@link RepartidorResponseDto} con el estado actualizado.
     * @throws RecursoNoEncontradoException En caso de que no se encuentre el repartidor con el DNI ingresado.
     */
    RepartidorResponseDto gestionarEstado(String dniRepartidor);

    /**
     * Obtiene los datos detallados de un repartidor a partir de su identificador único.
     *
     * @param idRepartidor Identificador UUID del repartidor.
     * @return DTO {@link RepartidorResponseDto} asociado al ID consultado.
     */
    RepartidorResponseDto obtenerPorId(UUID idRepartidor);

    /**
     * Consulta la lista de repartidores registrados, opcionalmente filtrada por estado operativo.
     *
     * @param estado Estado operativo opcional por el cual filtrar (e.g., ACTIVO, INACTIVO).
     * @return Lista de {@link RepartidorResponseDto}.
     */
    List<RepartidorResponseDto> listarRepartidores(EstadoRepartidor estado);

    /**
     * Permite eliminar un repartidor registrado ingresando su DNI
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor a eliminar.
     * @throws RecursoNoEncontradoException En caso de no encontrar ningún repartidor con el DNI ingresado.
     */
    void eliminarRepartidor(String dniRepartidor);
}