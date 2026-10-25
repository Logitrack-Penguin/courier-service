package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.mapper.RepartidorMapper;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.model.Repartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.repository.RepartidorRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.service.interfaces.RepartidorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementación de la capa de servicio para la gestión operativa y dominio relacional
 * de la entidad {@link Repartidor}.
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 25-10-2026
 * @see RepartidorService
 * @see RepartidorRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepartidorServiceImpl implements RepartidorService {

    private final RepartidorRepository repartidorRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RepartidorResponseDto registrarRepartidor(RepartidorRequestDto requestDto) {
        log.info("Iniciando registro de nuevo repartidor con DNI: {}", requestDto.getDni());

        if (repartidorRepository.existsByDni(requestDto.getDni())) {
            throw new RecursoDuplicadoException("Ya existe un repartidor registrado con el DNI: " + requestDto.getDni());
        }

        if (repartidorRepository.existsByTelefono(requestDto.getTelefono())) {
            throw new RecursoDuplicadoException("Ya existe un repartidor registrado con el teléfono: " + requestDto.getTelefono());
        }

        Repartidor nuevoRepartidor = RepartidorMapper.toEntity(requestDto);
        Repartidor guardado = repartidorRepository.save(nuevoRepartidor);

        log.info("Repartidor registrado exitosamente con ID: {}", guardado.getIdRepartidor());
        return RepartidorMapper.toResponseDto(guardado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RepartidorResponseDto actualizarRepartidor(String dniRepartidor, RepartidorRequestDto requestDto) {
        log.info("Iniciando actualización de repartidor con DNI: {}", dniRepartidor);

        Repartidor repartidorExistente = repartidorRepository.findByDni(dniRepartidor)
                .orElseThrow(()-> {
                    log.error("Fallo operativo de actualización: Repartidor inexistente con el DNI: {}", dniRepartidor);
                    return new RecursoNoEncontradoException("No es posible realizar la actualización. Repartidor no encontrado con DNI: " + dniRepartidor);
                });
        if (requestDto.getTelefono() != null && !repartidorRepository.existsByTelefono(requestDto.getTelefono())){
            repartidorExistente.setTelefono(requestDto.getTelefono());
        }else {
            throw  new RecursoDuplicadoException("No es posible realizar la actualización. El teléfono ya está registrado.");
        }
        if (requestDto.getTipoVehiculo() != null){
            repartidorExistente.setTipoVehiculo(requestDto.getTipoVehiculo());
        }

        Repartidor repartidorActualizado = repartidorRepository.save(repartidorExistente);
        return RepartidorMapper.toResponseDto(repartidorActualizado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean estaDisponible(UUID idRepartidor) {
        Repartidor repartidorEncontrado = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> {
                    log.error("No se encontró ningún repartidor con ID: {}", idRepartidor);
                    return  new RecursoNoEncontradoException("No existe ningún repartidor con el ID: " + idRepartidor);
                });
        return repartidorEncontrado.getEstadoRepartidor() == EstadoRepartidor.ACTIVO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RepartidorResponseDto obtenerPorDni(String dniRepartidor) {
        log.info("Buscando repartidor por DNI: {}", dniRepartidor);

        Repartidor repartidor = repartidorRepository.findByDni(dniRepartidor)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el repartidor con DNI: " + dniRepartidor));

        return RepartidorMapper.toResponseDto(repartidor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RepartidorResponseDto gestionarEstado(String dniRepartidor) {
        Repartidor repartidorEncontrado = repartidorRepository.findByDni(dniRepartidor)
                .orElseThrow(() -> {
                    log.error("No se encontró ningún repartidor con DNI: {}", dniRepartidor);
                    return  new RecursoNoEncontradoException("No existe ningún repartidor con el DNI: " + dniRepartidor);
                });
        if (repartidorEncontrado.getEstadoRepartidor() == EstadoRepartidor.ACTIVO){
            repartidorEncontrado.setEstadoRepartidor(EstadoRepartidor.INACTIVO);
        }else {
            repartidorEncontrado.setEstadoRepartidor(EstadoRepartidor.ACTIVO);
        }
        Repartidor repartidorActualizado = repartidorRepository.save(repartidorEncontrado);
        return RepartidorMapper.toResponseDto(repartidorActualizado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public RepartidorResponseDto obtenerPorId(UUID idRepartidor) {
        log.info("Buscando repartidor por ID: {}", idRepartidor);

        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el repartidor con ID: " + idRepartidor));

        return RepartidorMapper.toResponseDto(repartidor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<RepartidorResponseDto> listarRepartidores(EstadoRepartidor estado) {
        log.info("Consultando lista de repartidores con filtro de estado: {}", estado);

        List<Repartidor> repartidores = (estado != null)
                ? repartidorRepository.findByEstadoRepartidor(estado)
                : repartidorRepository.findAll();

        return repartidores.stream()
                .map(RepartidorMapper::toResponseDto)
                .toList();
    }
}