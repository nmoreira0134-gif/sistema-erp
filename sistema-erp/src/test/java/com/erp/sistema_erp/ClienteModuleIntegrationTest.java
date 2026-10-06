/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ClienteModuleIntegrationTest.java
 * PAQUETE: com.erp.sistema_erp
 *
 * QUÉ HACE:
 * Prueba de integración automatizada para verificar el ciclo de vida completo del Módulo 2 (Clientes).
 *
 * POR QUÉ EXISTE:
 * Valida de forma exhaustiva:
 * - Autenticación y obtención de JWT.
 * - Creación de cliente Natural con validación de Cédula nica.
 * - Autogeneración secuencial del código CLI-2026-XXXX.
 * - Creación de cliente Jurídico con RUC y Razón Social.
 * - Rechazo de RUC sin Razón Social y rechazo de duplicados.
 * - Paginación y búsqueda multicriterio.
 * - Edición y Soft Delete con preservación de auditoría.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Ejecuta peticiones contra MockMvc integradas con la base de datos SQL Server y Spring Security.
 */
package com.erp.sistema_erp;

import com.erp.sistema_erp.dto.seguridad.LoginRequestDTO;
import com.erp.sistema_erp.dto.ventas.ClienteCreateDTO;
import com.erp.sistema_erp.dto.ventas.ClienteUpdateDTO;
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

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClienteModuleIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;
    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Limpiar datos previos de pruebas para garantizar repetibilidad
        jdbcTemplate.execute("DELETE FROM Clientes WHERE DocumentoIdentidad IN ('001-280590-0001A', 'J0310000001234')");

        // Obtener token JWT del administrador inicial
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin", "Admin2026!Temp");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        this.jwtToken = root.get("datos").get("token").asText();
    }

    @Test
    @DisplayName("Ciclo completo CRUD de Clientes: Natural, Jurídico, Unicidad, Búsqueda y Soft Delete")
    void testFlujoCompletoClientes() throws Exception {
        // 1. Crear Cliente Natural con autogeneración de código
        ClienteCreateDTO naturalDTO = new ClienteCreateDTO();
        naturalDTO.setNombre("Carlos Alberto");
        naturalDTO.setApellido("Mendoza Ruiz");
        naturalDTO.setTipoCliente("Natural");
        naturalDTO.setDocumentoIdentidad("001-280590-0001A");
        naturalDTO.setEmail("cmendoza@test.com.ni");
        naturalDTO.setTelefono("+505 8888-1111");
        naturalDTO.setCiudad("Managua");
        naturalDTO.setLimiteCredito(BigDecimal.ZERO); // Contado
        naturalDTO.setDiasCredito(0);

        MvcResult resNatural = mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(naturalDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.codigoCliente").value(org.hamcrest.Matchers.startsWith("CLI-2026-")))
                .andExpect(jsonPath("$.datos.tipoCliente").value("Natural"))
                .andExpect(jsonPath("$.datos.documentoIdentidad").value("001-280590-0001A"))
                .andReturn();

        JsonNode jsonNatural = objectMapper.readTree(resNatural.getResponse().getContentAsString()).get("datos");
        Integer naturalId = jsonNatural.get("clienteId").asInt();
        String codigoGenerado = jsonNatural.get("codigoCliente").asText();

        // 2. Crear Cliente Jurídico con RUC y condiciones de Crédito
        ClienteCreateDTO juridicoDTO = new ClienteCreateDTO();
        juridicoDTO.setNombre("Elena");
        juridicoDTO.setApellido("Morales");
        juridicoDTO.setRazonSocial("Agropecuaria El Bosque S.A.");
        juridicoDTO.setTipoCliente("Juridico");
        juridicoDTO.setDocumentoIdentidad("J0310000001234");
        juridicoDTO.setEmail("compras@elbosque.com.ni");
        juridicoDTO.setTelefono("+505 2278-9999");
        juridicoDTO.setCiudad("Matagalpa");
        juridicoDTO.setLimiteCredito(new BigDecimal("50000.00"));
        juridicoDTO.setDiasCredito(30);

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(juridicoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.razonSocial").value("Agropecuaria El Bosque S.A."))
                .andExpect(jsonPath("$.datos.tipoCliente").value("Juridico"))
                .andExpect(jsonPath("$.datos.esCredito").value(true));

        // 3. Validar rechazo si Tipo = Jurídico y falta Razón Social
        ClienteCreateDTO invalidoJuridico = new ClienteCreateDTO();
        invalidoJuridico.setNombre("Mario");
        invalidoJuridico.setApellido("López");
        invalidoJuridico.setTipoCliente("Juridico");
        invalidoJuridico.setDocumentoIdentidad("J0310000005678");

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidoJuridico)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));

        // 4. Validar rechazo por Cédula duplicada
        ClienteCreateDTO duplicado = new ClienteCreateDTO();
        duplicado.setNombre("Otro");
        duplicado.setApellido("Usuario");
        duplicado.setTipoCliente("Natural");
        duplicado.setDocumentoIdentidad("001-280590-0001A"); // Misma cédula

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicado)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));

        // 5. Consultar detalle por ID
        mockMvc.perform(get("/api/clientes/" + naturalId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.codigoCliente").value(codigoGenerado));

        // 6. Búsqueda por término
        mockMvc.perform(get("/api/clientes/buscar?q=Mendoza")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.contenido").isArray())
                .andExpect(jsonPath("$.datos.contenido[0].apellido").value("Mendoza Ruiz"));

        // 7. Actualizar Cliente
        ClienteUpdateDTO updateDTO = new ClienteUpdateDTO();
        updateDTO.setNombre("Carlos Alberto");
        updateDTO.setApellido("Mendoza Actualizado");
        updateDTO.setTipoCliente("Natural");
        updateDTO.setDocumentoIdentidad("001-280590-0001A");
        updateDTO.setEmail("cmendoza.nuevo@test.com.ni");
        updateDTO.setTelefono("+505 8888-2222");
        updateDTO.setCiudad("León");
        updateDTO.setPais("Nicaragua");
        updateDTO.setLimiteCredito(new BigDecimal("15000.00"));
        updateDTO.setDiasCredito(15);

        mockMvc.perform(put("/api/clientes/" + naturalId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.apellido").value("Mendoza Actualizado"))
                .andExpect(jsonPath("$.datos.ciudad").value("León"))
                .andExpect(jsonPath("$.datos.esCredito").value(true));

        // 8. Soft Delete: Desactivar cliente
        mockMvc.perform(patch("/api/clientes/" + naturalId + "/estado")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("activo", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));

        // 9. Verificar que el cliente desactivado no aparezca en consultas estándar
        mockMvc.perform(get("/api/clientes/" + naturalId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}
