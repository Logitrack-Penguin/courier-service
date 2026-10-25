package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.mapper;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.model.Repartidor;

/**
 * Clase utilitaria encargada de la transformación bidireccional entre el modelo de persistencia
 * {@link Repartidor} y los objetos de transferencia de datos {@link RepartidorRequestDto} y {@link RepartidorResponseDto}.
 * <p>
 * Implementa el patrón Data Mapper para aislar el dominio relacional de las capas externas de transporte (REST/JSON),
 * evitando fugas de abstracción y problemas de recursión infinita en serializaciones bidireccionales.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 25-10-2026
 * @see Repartidor
 * @see RepartidorRequestDto
 * @see RepartidorResponseDto
 */

public class RepartidorMapper {

    /**
     * Constructor privado que previene la instanciación de una clase utilitaria.
     */
    private RepartidorMapper(){
        throw new UnsupportedOperationException("RepartidorMapper es una clase utilitaria estática y no debe ser instanciada.");
    }

    /**
     * Transforma un {@link RepartidorRequestDto} en una instancia persistible de {@link Repartidor}
     * inicializada con los valores mandatorios para evitar infracciones de integridad.
     *
     * @param requestDto Contrato de entrada.
     * @return Entidad {@link Repartidor}
     */
    public static Repartidor toEntity(RepartidorRequestDto requestDto){
        if (requestDto == null) return null;

        return Repartidor.builder()
                .dni(requestDto.getDni().trim())
                .nombre(requestDto.getNombre().trim())
                .apellido(requestDto.getApellido().trim())
                .telefono(requestDto.getTelefono().trim())
                .tipoVehiculo(requestDto.getTipoVehiculo().trim())
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
                .build();
    }

    /**
     * Convierte una instancia concreta de la jerarquía {@link Repartidor} en su correspondiente DTO de respuesta.
     *
     * @param repartidor Instancia de {@link Repartidor} persistida en base de datos.
     * @return {@link RepartidorResponseDto}
     */
    public static RepartidorResponseDto toResponseDto(Repartidor repartidor){
        if (repartidor == null) return null;

        return RepartidorResponseDto.builder()
                .idRepartidor(repartidor.getIdRepartidor())
                .dni(repartidor.getDni())
                .nombreCompleto(String.format("%s, %s", repartidor.getApellido(), repartidor.getNombre()))
                .telefono(repartidor.getTelefono())
                .tipoVehiculo(repartidor.getTipoVehiculo())
                .estadoRepartidor(repartidor.getEstadoRepartidor())
                .fechaCreacion(repartidor.getFechaCreacion())
                .build();
    }
}
