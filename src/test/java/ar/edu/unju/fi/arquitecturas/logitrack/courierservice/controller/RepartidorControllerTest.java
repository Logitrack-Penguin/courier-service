package ar.edu.unju.fi.arquitecturas.logitrack.courierservice.controller;

import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.courierservice.service.interfaces.RepartidorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web de {@link RepartidorController}.
 * <p>
 * Verifican que la validación del {@link RepartidorRequestDto} (Bean Validation, activada con
 * {@code spring-boot-starter-validation} en LT4-22) rechace los datos inválidos con un 400 y el
 * detalle por campo que arma el {@code GlobalExceptionHandler}, sin llegar a la capa de servicio.
 * </p>
 * <p>
 * {@code spring.config.name} apunta a un nombre inexistente para que este test no cargue
 * {@code application.yaml}: su {@code spring.config.import: configserver:} no es opcional y haría
 * fallar el contexto si el config-server no está levantado. La capa web no necesita esa configuración.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 * @see RepartidorController
 */
@WebMvcTest(
        controllers = RepartidorController.class,
        properties = {
                "spring.config.name=repartidor-controller-test",
                "spring.cloud.config.enabled=false",
                "spring.cloud.config.import-check.enabled=false",
                "eureka.client.enabled=false"
        }
)
class RepartidorControllerTest {

    private static final String BASE = "/api/v1/repartidores";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepartidorService repartidorService;

    /**
     * Arma el JSON de un repartidor. Se escribe a mano para no depender de la versión de Jackson.
     */
    private static String json(String dni, String telefono) {
        String telefonoJson = telefono == null ? "" : ", \"telefono\": \"" + telefono + "\"";
        return """
                {"dni": "%s", "nombre": "Esteban", "apellido": "Quito", "tipoVehiculo": "MOTO"%s}
                """.formatted(dni, telefonoJson);
    }

    @Test
    @DisplayName("POST con DNI no numérico debe responder 400 con el detalle del campo dni")
    void registrar_DniInvalido_Responde400() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("abc", "3881112222")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.dni").exists());

        verify(repartidorService, never()).registrarRepartidor(any());
    }

    @Test
    @DisplayName("POST con teléfono de 9 dígitos debe responder 400 con el detalle del campo telefono")
    void registrar_TelefonoDe9Digitos_Responde400() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("28111222", "388111222")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.telefono").exists());

        verify(repartidorService, never()).registrarRepartidor(any());
    }

    @Test
    @DisplayName("PUT sin teléfono debe responder 400 con el detalle del campo telefono")
    void actualizar_SinTelefono_Responde400() throws Exception {
        mockMvc.perform(put(BASE + "/28111222")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("28111222", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.telefono").exists());

        verify(repartidorService, never()).actualizarRepartidor(anyString(), any());
    }

    @Test
    @DisplayName("POST con datos válidos debe responder 201")
    void registrar_DatosValidos_Responde201() throws Exception {
        RepartidorResponseDto respuesta = RepartidorResponseDto.builder()
                .idRepartidor(UUID.randomUUID())
                .dni("28111222")
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
                .build();
        when(repartidorService.registrarRepartidor(any(RepartidorRequestDto.class))).thenReturn(respuesta);

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("28111222", "3881112222")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("28111222"));
    }
}
