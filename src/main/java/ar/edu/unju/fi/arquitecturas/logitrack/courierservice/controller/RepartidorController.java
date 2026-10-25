package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
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
     * @return {@link ResponseEntity} con HTTP 200 (OK) y el DTO con el estado actualizado.
     */
    @PutMapping("/{dniRepartidor}")
    public ResponseEntity<RepartidorResponseDto> gestionarEstado(@PathVariable String dniRepartidor){
        return ResponseEntity.ok(repartidorService.gestionarEstado(dniRepartidor));
    }

    /**
     * Endpoint para consultar el detalle de un repartidor mediante su DNI.
     *
     * @param dniRepartidor Documento de Identificación Nacional del repartidor a consultar.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y el DTO del repartidor.
     */
    @GetMapping("/{dniRepartidor}")
    public ResponseEntity<RepartidorResponseDto> obtenerPorDni(@PathVariable String dniRepartidor) {
        RepartidorResponseDto repartidor = repartidorService.obtenerPorDni(dniRepartidor);
        return ResponseEntity.ok(repartidor);
    }

    /**
     * Endpoint para listar repartidores, permitiendo filtrar por su estado operativo.
     *
     * @param estado Parámetro opcional para filtrar por {@link EstadoRepartidor}.
     * @return {@link ResponseEntity} con HTTP 200 (OK) y la lista de repartidores.
     */
    @GetMapping
    public ResponseEntity<List<RepartidorResponseDto>> listarRepartidores(@RequestParam(required = false) EstadoRepartidor estado) {
        List<RepartidorResponseDto> repartidores = repartidorService.listarRepartidores(estado);
        return ResponseEntity.ok(repartidores);
    }
}