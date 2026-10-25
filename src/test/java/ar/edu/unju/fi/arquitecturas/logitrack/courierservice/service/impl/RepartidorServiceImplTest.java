package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.model.Repartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.repository.RepartidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para la clase {@link RepartidorServiceImpl}.
 *
 * <p>
 * Valida la lógica de negocio, control de unicidad de DNI y teléfono,
 * filtrado por estado y gestión de excepciones institucionales.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 25-10-2026
 * @see RepartidorServiceImpl
 * @see RepartidorRepository
 */
@ExtendWith(MockitoExtension.class)
class RepartidorServiceImplTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private RepartidorServiceImpl repartidorService;

    private Repartidor repartidorMock;
    private RepartidorRequestDto requestDto;
    private UUID idRepartidor;

    @BeforeEach
    void setUp() {
        idRepartidor = UUID.randomUUID();

        repartidorMock = Repartidor.builder()
                .idRepartidor(idRepartidor)
                .dni("35123456")
                .nombre("Carlos")
                .apellido("Gomez")
                .telefono("3884567890")
                .tipoVehiculo("Motocicleta")
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
                .build();

        requestDto = RepartidorRequestDto.builder()
                .dni("35123456")
                .nombre("Carlos")
                .apellido("Gomez")
                .telefono("3884567890")
                .tipoVehiculo("Motocicleta")
                .build();
    }

    /**
     * Valida el registro exitoso de un repartidor cuando el DNI y teléfono no están duplicados.
     */
    @Test
    @DisplayName("Debe registrar un repartidor exitosamente cuando los datos son válidos")
    void registrarRepartidor_Exitoso() {
        // Given
        when(repartidorRepository.existsByDni(requestDto.getDni())).thenReturn(false);
        when(repartidorRepository.existsByTelefono(requestDto.getTelefono())).thenReturn(false);
        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidorMock);

        // When
        RepartidorResponseDto response = repartidorService.registrarRepartidor(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(repartidorMock.getDni(), response.getDni());
        assertEquals(EstadoRepartidor.ACTIVO, response.getEstadoRepartidor());
        verify(repartidorRepository, times(1)).existsByDni(requestDto.getDni());
        verify(repartidorRepository, times(1)).existsByTelefono(requestDto.getTelefono());
        verify(repartidorRepository, times(1)).save(any(Repartidor.class));
    }

    /**
     * Valida que se lance una excepción {@link RecursoDuplicadoException} al intentar registrar un repartidor con un DNI ya existente.
     */
    @Test
    @DisplayName("Debe lanzar RecursoDuplicadoException si el DNI ya se encuentra registrado")
    void registrarRepartidor_LanzaExcepcion_DniDuplicado() {
        // Given
        when(repartidorRepository.existsByDni(requestDto.getDni())).thenReturn(true);

        // When & Then
        assertThrows(RecursoDuplicadoException.class, () -> repartidorService.registrarRepartidor(requestDto));

        verify(repartidorRepository, times(1)).existsByDni(requestDto.getDni());
        verify(repartidorRepository, never()).existsByTelefono(anyString());
        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }

    /**
     * Valida que se lance una excepción {@link RecursoDuplicadoException} al intentar registrar un repartidor con un teléfono ya existente.
     */
    @Test
    @DisplayName("Debe lanzar RecursoDuplicadoException si el teléfono ya se encuentra registrado")
    void registrarRepartidor_LanzaExcepcion_TelefonoDuplicado() {
        // Given
        when(repartidorRepository.existsByDni(requestDto.getDni())).thenReturn(false);
        when(repartidorRepository.existsByTelefono(requestDto.getTelefono())).thenReturn(true);

        // When & Then
        assertThrows(RecursoDuplicadoException.class, () -> repartidorService.registrarRepartidor(requestDto));

        verify(repartidorRepository, times(1)).existsByDni(requestDto.getDni());
        verify(repartidorRepository, times(1)).existsByTelefono(requestDto.getTelefono());
        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }

    /**
     * Valida la consulta exitosa de un repartidor a partir de su ID.
     */
    @Test
    @DisplayName("Debe obtener un repartidor por su ID correctamente")
    void obtenerPorId_Exitoso() {
        // Given
        when(repartidorRepository.findById(idRepartidor)).thenReturn(Optional.of(repartidorMock));

        // When
        RepartidorResponseDto response = repartidorService.obtenerPorId(idRepartidor);

        // Then
        assertNotNull(response);
        assertEquals(idRepartidor, response.getIdRepartidor());
        verify(repartidorRepository, times(1)).findById(idRepartidor);
    }

    /**
     * Valida que se lance una excepción {@link RecursoNoEncontradoException} al buscar un ID de repartidor inexistente.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al buscar un ID de repartidor inexistente")
    void obtenerPorId_LanzaExcepcion_SiNoExiste() {
        // Given
        when(repartidorRepository.findById(idRepartidor)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> repartidorService.obtenerPorId(idRepartidor));
        verify(repartidorRepository, times(1)).findById(idRepartidor);
    }

    /**
     * Valida el listado de repartidores aplicando un filtro de estado específico (ej. ACTIVO).
     */
    @Test
    @DisplayName("Debe listar repartidores filtrados por estado operativo")
    void listarRepartidores_ConEstadoEspecifico() {
        // Given
        when(repartidorRepository.findByEstadoRepartidor(EstadoRepartidor.ACTIVO)).thenReturn(List.of(repartidorMock));

        // When
        List<RepartidorResponseDto> resultado = repartidorService.listarRepartidores(EstadoRepartidor.ACTIVO);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repartidorRepository, times(1)).findByEstadoRepartidor(EstadoRepartidor.ACTIVO);
        verify(repartidorRepository, never()).findAll();
    }

    /**
     * Valida que se devuelvan todos los repartidores si el parámetro de estado es nulo.
     */
    @Test
    @DisplayName("Debe listar todos los repartidores si el filtro de estado es nulo")
    void listarRepartidores_EstadoNull_DevuelveTodos() {
        // Given
        when(repartidorRepository.findAll()).thenReturn(List.of(repartidorMock));

        // When
        List<RepartidorResponseDto> resultado = repartidorService.listarRepartidores(null);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repartidorRepository, times(1)).findAll();
        verify(repartidorRepository, never()).findByEstadoRepartidor(any());
    }
}