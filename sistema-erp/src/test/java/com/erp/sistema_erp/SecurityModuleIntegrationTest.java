/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: SecurityModuleIntegrationTest.java
 * PAQUETE: com.erp.sistema_erp
 *
 * QUÉ HACE:
 * Prueba de integración automatizada para verificar el flujo completo de autenticación y autorización
 * del Módulo 1 (Seguridad).
 *
 * POR QUÉ EXISTE:
 * Valida de forma rigurosa que el login emita un JWT válido, que las peticiones no autenticadas sean
 * rechazadas con 401/403, y que los endpoints /api/usuarios y /api/roles respondan con datos válidos.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Ejecuta llamadas contra MockMvc simulando peticiones HTTP reales desde el frontend Vanilla.
 */
package com.erp.sistema_erp;

import com.erp.sistema_erp.dto.seguridad.LoginRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityModuleIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Debe autenticar al usuario admin inicial y devolver JWT con permisos y menú")
    void testLoginAdminExitoso() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("admin", "Admin2026!Temp");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.username").value("admin"))
                .andExpect(jsonPath("$.datos.rol").value("Administrador"))
                .andExpect(jsonPath("$.datos.token").isNotEmpty())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseJson);
        String token = jsonNode.get("datos").get("token").asText();

        assertThat(token).isNotBlank();

        // Probar acceso protegido a /api/usuarios con el JWT obtenido
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos").isArray());

        // Probar acceso protegido a /api/roles con el JWT obtenido
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos").isArray())
                .andExpect(jsonPath("$.datos.length()").value(6));
    }

    @Test
    @DisplayName("Debe rechazar acceso a endpoints protegidos si no se envía token")
    void testAccesoProtegidoSinToken() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Debe rechazar credenciales incorrectas con 401")
    void testLoginCredencialesInvalidas() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("admin", "PasswordIncorrecto");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false));
    }
}
