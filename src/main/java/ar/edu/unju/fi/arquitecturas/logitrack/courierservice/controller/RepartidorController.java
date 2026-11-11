package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.security.UsuarioActual;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.service.interfaces.RepartidorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST encargado de exponer las operaciones CRUD y de consulta
 * para la gestión de repartidores en el sistema.
 *
 * @author Logitrack-Penguin
 * @version 1.1.0
 * @since 25-10-2026
 * @see RepartidorService
 */
@RestController
@RequestMapping("/api/v1/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

    private final RepartidorService repartidorService;

    /**
     * Endpoint para registrar un nuevo repartidor en el sistema.
     *
     * @param requestDto Payload con los datos requeridos.
     * @return {@link ResponseEntity} con HTTP 201 (CREATED) y el DTO creado.
     */
    @PostMapping
    public ResponseEntity<RepartidorResponseDto> registrarRepartidor(@Valid @RequestBody RepartidorRequestDto requestDto) {
        RepartidorResponseDto creado = repartidorService.registrarRepartidor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Endpoint para actualizar un repartidor ya registrado en el sistema.
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor.
     * @param requestDto Payload con los datos actualizados del repartidor.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y el DTO actualizado.
     */
    @PutMapping("/{dniRepartidor}")
    public ResponseEntity<RepartidorResponseDto> actualizarRepartidor(@PathVariable String dniRepartidor, @Valid @RequestBody RepartidorRequestDto requestDto) {
        return ResponseEntity.ok(repartidorService.actualizarRepartidor(dniRepartidor, requestDto));
    }

    /**
     * Endpoint para gestionar el estado de un repartidor basándose en su DNI.
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor a buscar.
     * @param roles Cabecera {@code X-User-Roles} del gateway (nula en llamadas internas).
     * @param dniUsuario Cabecera {@code X-User-Dni} del gateway: un REPARTIDOR solo accede a su DNI.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y el DTO con el estado actualizado; 403 si un
     *         REPARTIDOR intenta cambiar el estado de otro.
     */
    @PutMapping("/{dniRepartidor}/estado")
    public ResponseEntity<RepartidorResponseDto> gestionarEstado(@PathVariable String dniRepartidor,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario){
        UsuarioActual.desde(roles, dniUsuario).verificarAcceso(dniRepartidor);
        return ResponseEntity.ok(repartidorService.gestionarEstado(dniRepartidor));
    }

    /**
     * Endpoint para consultar el detalle de un repartidor mediante su DNI.
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor a consultar.
     * @param roles Cabecera {@code X-User-Roles} del gateway (nula en llamadas internas).
     * @param dniUsuario Cabecera {@code X-User-Dni} del gateway: un REPARTIDOR solo accede a su DNI.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y el DTO del repartidor; 403 si un REPARTIDOR
     *         pide los datos de otro.
     */
    @GetMapping("/{dniRepartidor}")
    public ResponseEntity<RepartidorResponseDto> obtenerPorDni(@PathVariable String dniRepartidor,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        UsuarioActual.desde(roles, dniUsuario).verificarAcceso(dniRepartidor);
        RepartidorResponseDto repartidor = repartidorService.obtenerPorDni(dniRepartidor);
        return ResponseEntity.ok(repartidor);
    }

    /**
     * Endpoint para listar repartidores, permitiendo filtrar por su estado operativo.
     *
     * @param estado Parámetro opcional para filtrar por {@link EstadoRepartidor}.
     * @param roles Cabecera {@code X-User-Roles} del gateway (nula en llamadas internas).
     * @param dniUsuario Cabecera {@code X-User-Dni} del gateway: un REPARTIDOR solo accede a su DNI.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y la lista de repartidores. Un REPARTIDOR solo
     *         recibe su propia ficha (o una lista vacía si su usuario no tiene DNI).
     */
    @GetMapping
    public ResponseEntity<List<RepartidorResponseDto>> listarRepartidores(@RequestParam(required = false) EstadoRepartidor estado,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        UsuarioActual usuario = UsuarioActual.desde(roles, dniUsuario);
        List<RepartidorResponseDto> repartidores = repartidorService.listarRepartidores(estado);
        if (usuario.esRepartidorRestringido()) {
            repartidores = repartidores.stream().filter(r -> usuario.puedeAccederA(r.getDni())).toList();
        }
        return ResponseEntity.ok(repartidores);
    }

    /**
     * Endpoint para la baja de repartidores a partir de su DNI.
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor a eliminar.
     * @return {@link ResponseEntity} con HTTP 204(No Content) tras la baja exitosa del repartidor.
     */
    @DeleteMapping("/{dniRepartidor}")
    public ResponseEntity<Void> eliminarRepartidor (@PathVariable String dniRepartidor) {
        repartidorService.eliminarRepartidor(dniRepartidor);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/{dniRepartidor}/estaDisponible")
    public boolean verificarDisponibilidad (@PathVariable String dniRepartidor,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario) {
        UsuarioActual.desde(roles, dniUsuario).verificarAcceso(dniRepartidor);
        return repartidorService.estaDisponible(repartidorService.obtenerPorDni(dniRepartidor).getIdRepartidor());
    }

    @PutMapping("/{dniRepartidor}/cambiarEstado")
    public ResponseEntity<RepartidorResponseDto> cambiarEstado(@PathVariable String dniRepartidor,
            @RequestHeader(value = UsuarioActual.HEADER_ROLES, required = false) String roles,
            @RequestHeader(value = UsuarioActual.HEADER_DNI, required = false) String dniUsuario){
        UsuarioActual.desde(roles, dniUsuario).verificarAcceso(dniRepartidor);
        RepartidorResponseDto repartidorActualizado = repartidorService.gestionarEstado(dniRepartidor);
        return ResponseEntity.ok(repartidorActualizado);
    }
}