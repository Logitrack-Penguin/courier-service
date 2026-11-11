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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // --- Errores 3 y 4 de LT4-40: un REPARTIDOR solo ve y cambia su propia ficha ---

    private static final String DNI_PROPIO = "30111222";
    private static final String DNI_AJENO = "30333444";

    private static RepartidorResponseDto repartidor(String dni) {
        return RepartidorResponseDto.builder()
                .idRepartidor(UUID.randomUUID())
                .dni(dni)
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("GET lista: un REPARTIDOR solo recibe su propia ficha")
    void listar_Repartidor_SoloRecibeLaSuya() throws Exception {
        when(repartidorService.listarRepartidores(null)).thenReturn(List.of(repartidor(DNI_PROPIO), repartidor(DNI_AJENO)));

        mockMvc.perform(get(BASE).header("X-User-Roles", "REPARTIDOR").header("X-User-Dni", DNI_PROPIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dni").value(DNI_PROPIO));
    }

    @Test
    @DisplayName("GET lista: un REPARTIDOR sin DNI en el token recibe una lista vacía")
    void listar_RepartidorSinDni_ListaVacia() throws Exception {
        when(repartidorService.listarRepartidores(null)).thenReturn(List.of(repartidor(DNI_PROPIO)));

        mockMvc.perform(get(BASE).header("X-User-Roles", "REPARTIDOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET lista: el ADMIN recibe todos")
    void listar_Admin_RecibeTodos() throws Exception {
        when(repartidorService.listarRepartidores(null)).thenReturn(List.of(repartidor(DNI_PROPIO), repartidor(DNI_AJENO)));

        mockMvc.perform(get(BASE).header("X-User-Roles", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET detalle: un REPARTIDOR ve el suyo (200) y no el de otro (403)")
    void obtener_Repartidor_PropioSiAjenoNo() throws Exception {
        when(repartidorService.obtenerPorDni(DNI_PROPIO)).thenReturn(repartidor(DNI_PROPIO));

        mockMvc.perform(get(BASE + "/" + DNI_PROPIO).header("X-User-Roles", "REPARTIDOR").header("X-User-Dni", DNI_PROPIO))
                .andExpect(status().isOk());
        mockMvc.perform(get(BASE + "/" + DNI_AJENO).header("X-User-Roles", "REPARTIDOR").header("X-User-Dni", DNI_PROPIO))
                .andExpect(status().isForbidden());

        verify(repartidorService, never()).obtenerPorDni(DNI_AJENO);
    }

    @Test
    @DisplayName("PUT estado: un REPARTIDOR cambia el suyo (200) y no el de otro (403)")
    void estado_Repartidor_PropioSiAjenoNo() throws Exception {
        when(repartidorService.gestionarEstado(DNI_PROPIO)).thenReturn(repartidor(DNI_PROPIO));

        mockMvc.perform(put(BASE + "/" + DNI_PROPIO + "/estado").header("X-User-Roles", "REPARTIDOR").header("X-User-Dni", DNI_PROPIO))
                .andExpect(status().isOk());
        mockMvc.perform(put(BASE + "/" + DNI_AJENO + "/estado").header("X-User-Roles", "REPARTIDOR").header("X-User-Dni", DNI_PROPIO))
                .andExpect(status().isForbidden());

        verify(repartidorService, never()).gestionarEstado(DNI_AJENO);
    }

    @Test
    @DisplayName("PUT estado: el ADMIN puede cambiar el de cualquiera")
    void estado_Admin_Cualquiera() throws Exception {
        when(repartidorService.gestionarEstado(DNI_AJENO)).thenReturn(repartidor(DNI_AJENO));

        mockMvc.perform(put(BASE + "/" + DNI_AJENO + "/estado").header("X-User-Roles", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Sin cabeceras (llamada interna de shipment por Feign) no se restringe")
    void sinCabeceras_NoSeRestringe() throws Exception {
        when(repartidorService.obtenerPorDni(DNI_AJENO)).thenReturn(repartidor(DNI_AJENO));
        when(repartidorService.estaDisponible(any())).thenReturn(true);

        mockMvc.perform(get(BASE + "/" + DNI_AJENO)).andExpect(status().isOk());
        mockMvc.perform(get(BASE + "/" + DNI_AJENO + "/estaDisponible")).andExpect(status().isOk());
    }
}
