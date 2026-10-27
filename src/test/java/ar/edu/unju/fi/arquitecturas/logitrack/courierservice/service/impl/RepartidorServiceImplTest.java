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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para la clase {@link RepartidorServiceImpl}.
 * <p>
 * Valida de forma aislada la lógica de negocio de la capa de servicio, abarcando:
 * </p>
 * <ul>
 *     <li>Registro de repartidores y control de unicidad de DNI y teléfono.</li>
 *     <li>Actualización de datos de contacto y de vehículo.</li>
 *     <li>Consulta de disponibilidad operativa.</li>
 *     <li>Consultas por DNI y por identificador único (UUID).</li>
 *     <li>Alternancia del estado operativo (ACTIVO / INACTIVO).</li>
 *     <li>Listado con y sin filtro de estado.</li>
 *     <li>Eliminación de repartidores.</li>
 * </ul>
 * <p>
 * Se emplean mocks de Mockito sobre {@link RepartidorRepository} para aislar la
 * persistencia y verificar tanto los resultados como las interacciones con el repositorio.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.2.0
 * @since 26-10-2026
 * @see RepartidorServiceImpl
 * @see RepartidorRepository
 */
@ExtendWith(MockitoExtension.class)
class RepartidorServiceImplTest {

    /** DNI de prueba reutilizado en los escenarios de consulta, actualización y eliminación. */
    private static final String DNI = "35123456";

    /** Mock del repositorio de repartidores para aislar la persistencia en memoria. */
    @Mock
    private RepartidorRepository repartidorRepository;

    /** Instancia de la clase de servicio bajo prueba, con las dependencias simuladas inyectadas. */
    @InjectMocks
    private RepartidorServiceImpl repartidorService;

    /** Entidad simulada de un repartidor persistido, en estado ACTIVO, para las validaciones. */
    private Repartidor repartidorMock;

    /** DTO de solicitud simulado para las pruebas de registro y actualización. */
    private RepartidorRequestDto requestDto;

    /** Identificador único universal reutilizable en las pruebas de consulta por ID. */
    private UUID idRepartidor;

    /**
     * Inicializa los datos base y objetos de prueba requeridos antes de la ejecución de cada test.
     */
    @BeforeEach
    void setUp() {
        idRepartidor = UUID.randomUUID();

        repartidorMock = Repartidor.builder()
                .idRepartidor(idRepartidor)
                .dni(DNI)
                .nombre("Carlos")
                .apellido("Gomez")
                .telefono("3884567890")
                .tipoVehiculo("Motocicleta")
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
                .build();

        requestDto = RepartidorRequestDto.builder()
                .dni(DNI)
                .nombre("Carlos")
                .apellido("Gomez")
                .telefono("3884567890")
                .tipoVehiculo("Motocicleta")
                .build();
    }


    /**
     * Valida el registro exitoso de un repartidor cuando el DNI y el teléfono
     * proporcionados no se encuentran previamente registrados en el sistema.
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
     * Valida que la entidad enviada a persistir al registrar un repartidor tenga
     * asignado el estado operativo {@link EstadoRepartidor#ACTIVO} y una fecha de creación,
     * utilizando un {@link ArgumentCaptor} para inspeccionar el objeto guardado.
     */
    @Test
    @DisplayName("Debe guardar el repartidor con estado ACTIVO y fecha de creación asignada")
    void registrarRepartidor_AsignaEstadoActivoYFechaCreacion() {
        // Given
        when(repartidorRepository.existsByDni(anyString())).thenReturn(false);
        when(repartidorRepository.existsByTelefono(anyString())).thenReturn(false);
        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidorMock);

        // When
        repartidorService.registrarRepartidor(requestDto);

        // Then
        ArgumentCaptor<Repartidor> captor = ArgumentCaptor.forClass(Repartidor.class);
        verify(repartidorRepository).save(captor.capture());
        assertEquals(EstadoRepartidor.ACTIVO, captor.getValue().getEstadoRepartidor());
        assertNotNull(captor.getValue().getFechaCreacion());
    }

    /**
     * Valida que se lance una excepción de tipo {@link RecursoDuplicadoException}
     * al intentar registrar un repartidor con un DNI que ya existe en la base de datos,
     * sin consultar el teléfono ni persistir información.
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
     * Valida que se lance una excepción de tipo {@link RecursoDuplicadoException}
     * al intentar registrar un repartidor con un número de teléfono que ya existe,
     * sin persistir información.
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
     * Valida la actualización exitosa del teléfono y del tipo de vehículo de un repartidor
     * existente cuando el nuevo teléfono no está registrado, verificando además que se
     * registre la fecha de última modificación.
     */
    @Test
    @DisplayName("Debe actualizar teléfono y tipo de vehículo cuando el nuevo teléfono está libre")
    void actualizarRepartidor_Exitoso() {
        // Given
        RepartidorRequestDto cambios = RepartidorRequestDto.builder()
                .telefono("3885551111")
                .tipoVehiculo("Camioneta")
                .build();

        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.of(repartidorMock));
        when(repartidorRepository.existsByTelefono("3885551111")).thenReturn(false);
        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidorMock);

        // When
        RepartidorResponseDto response = repartidorService.actualizarRepartidor(DNI, cambios);

        // Then
        assertNotNull(response);
        assertEquals("3885551111", repartidorMock.getTelefono());
        assertEquals("Camioneta", repartidorMock.getTipoVehiculo());
        assertNotNull(repartidorMock.getUltimaModificacion());
        verify(repartidorRepository, times(1)).save(repartidorMock);
    }

    /**
     * Valida que se lance una excepción de tipo {@link RecursoNoEncontradoException}
     * al intentar actualizar un repartidor cuyo DNI no existe en el sistema.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al actualizar un DNI inexistente")
    void actualizarRepartidor_LanzaExcepcion_SiNoExiste() {
        // Given
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class,
                () -> repartidorService.actualizarRepartidor(DNI, requestDto));

        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }

    /**
     * Valida que se lance una excepción de tipo {@link RecursoDuplicadoException}
     * al intentar actualizar un repartidor con un teléfono que ya pertenece a otro registro.
     */
    @Test
    @DisplayName("Debe lanzar RecursoDuplicadoException al actualizar con un teléfono ya registrado")
    void actualizarRepartidor_LanzaExcepcion_TelefonoDuplicado() {
        // Given
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.of(repartidorMock));
        when(repartidorRepository.existsByTelefono(requestDto.getTelefono())).thenReturn(true);

        // When & Then
        assertThrows(RecursoDuplicadoException.class,
                () -> repartidorService.actualizarRepartidor(DNI, requestDto));

        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }



    /**
     * Valida que se indique disponibilidad ({@code true}) cuando el repartidor
     * se encuentra en estado {@link EstadoRepartidor#ACTIVO}.
     */
    @Test
    @DisplayName("Debe retornar true si el repartidor está ACTIVO")
    void estaDisponible_RetornaTrue_SiActivo() {
        // Given
        when(repartidorRepository.findById(idRepartidor)).thenReturn(Optional.of(repartidorMock));

        // When
        boolean disponible = repartidorService.estaDisponible(idRepartidor);

        // Then
        assertTrue(disponible);
        verify(repartidorRepository, times(1)).findById(idRepartidor);
    }

    /**
     * Valida que se indique no disponibilidad ({@code false}) cuando el repartidor
     * se encuentra en estado {@link EstadoRepartidor#INACTIVO}.
     */
    @Test
    @DisplayName("Debe retornar false si el repartidor está INACTIVO")
    void estaDisponible_RetornaFalse_SiInactivo() {
        // Given
        repartidorMock.setEstadoRepartidor(EstadoRepartidor.INACTIVO);
        when(repartidorRepository.findById(idRepartidor)).thenReturn(Optional.of(repartidorMock));

        // When
        boolean disponible = repartidorService.estaDisponible(idRepartidor);

        // Then
        assertFalse(disponible);
    }

    /**
     * Valida que se lance una excepción de tipo {@link RecursoNoEncontradoException}
     * al consultar la disponibilidad de un repartidor con un ID inexistente.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al consultar disponibilidad de un ID inexistente")
    void estaDisponible_LanzaExcepcion_SiNoExiste() {
        // Given
        when(repartidorRepository.findById(idRepartidor)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> repartidorService.estaDisponible(idRepartidor));
    }

    // ======================================================================
    // obtenerPorDni
    // ======================================================================

    /**
     * Valida la correcta recuperación y mapeo de los datos de un repartidor
     * a partir de su DNI.
     */
    @Test
    @DisplayName("Debe obtener un repartidor por su DNI correctamente")
    void obtenerPorDni_Exitoso() {
        // Given
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.of(repartidorMock));

        // When
        RepartidorResponseDto response = repartidorService.obtenerPorDni(DNI);

        // Then
        assertNotNull(response);
        assertEquals(DNI, response.getDni());
        verify(repartidorRepository, times(1)).findByDni(DNI);
    }

    /**
     * Valida que se lance una excepción de tipo {@link RecursoNoEncontradoException}
     * al buscar un repartidor mediante un DNI que no existe en el sistema.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al buscar un DNI inexistente")
    void obtenerPorDni_LanzaExcepcion_SiNoExiste() {
        // Given
        when(repartidorRepository.findByDni("99999999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> repartidorService.obtenerPorDni("99999999"));
    }




    /**
     * Valida que al gestionar el estado de un repartidor ACTIVO, este pase
     * a {@link EstadoRepartidor#INACTIVO} y se persista el cambio.
     */
    @Test
    @DisplayName("Debe cambiar el estado de ACTIVO a INACTIVO")
    void gestionarEstado_ActivoAInactivo() {
        // Given
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.of(repartidorMock));
        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidorMock);

        // When
        RepartidorResponseDto response = repartidorService.gestionarEstado(DNI);

        // Then
        assertEquals(EstadoRepartidor.INACTIVO, response.getEstadoRepartidor());
        verify(repartidorRepository, times(1)).save(repartidorMock);
    }

    /**
     * Valida que al gestionar el estado de un repartidor INACTIVO, este pase
     * a {@link EstadoRepartidor#ACTIVO}.
     */
    @Test
    @DisplayName("Debe cambiar el estado de INACTIVO a ACTIVO")
    void gestionarEstado_InactivoAActivo() {
        // Given
        repartidorMock.setEstadoRepartidor(EstadoRepartidor.INACTIVO);
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.of(repartidorMock));
        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidorMock);

        // When
        RepartidorResponseDto response = repartidorService.gestionarEstado(DNI);

        // Then
        assertEquals(EstadoRepartidor.ACTIVO, response.getEstadoRepartidor());
    }

    /**
     * Valida que se lance una excepción de tipo {@link RecursoNoEncontradoException}
     * al gestionar el estado de un repartidor cuyo DNI no existe, sin persistir cambios.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al gestionar el estado de un DNI inexistente")
    void gestionarEstado_LanzaExcepcion_SiNoExiste() {
        // Given
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> repartidorService.gestionarEstado(DNI));

        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }




    /**
     * Valida la correcta recuperación y mapeo de los datos de un repartidor
     * a partir de su identificador único universal (UUID).
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
     * Valida que se lance una excepción de tipo {@link RecursoNoEncontradoException}
     * al intentar buscar un repartidor mediante un ID que no existe en el sistema.
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
     * Valida el listado correcto de repartidores aplicando un filtro
     * de estado operativo específico (por ejemplo, ACTIVO), sin recurrir
     * a la consulta completa.
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
     * Valida que se devuelva el listado completo de todos los repartidores
     * cuando el parámetro de filtrado por estado es nulo.
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




    /**
     * Valida que el proceso de eliminación se ejecute correctamente
     * cuando existe un repartidor con el DNI indicado.
     */
    @Test
    @DisplayName("Debe eliminar un repartidor existente")
    void eliminarRepartidor_Exitoso() {
        // Given
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.of(repartidorMock));

        // When
        repartidorService.eliminarRepartidor(DNI);

        // Then
        verify(repartidorRepository, times(1)).delete(repartidorMock);
    }

    /**
     * Valida que se lance una excepción de tipo {@link RecursoNoEncontradoException}
     * al intentar eliminar un repartidor con un DNI inexistente, sin ejecutar la baja.
     */
    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException al eliminar un DNI inexistente")
    void eliminarRepartidor_LanzaExcepcion_SiNoExiste() {
        // Given
        when(repartidorRepository.findByDni(DNI)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () -> repartidorService.eliminarRepartidor(DNI));

        verify(repartidorRepository, never()).delete(any(Repartidor.class));
    }
}