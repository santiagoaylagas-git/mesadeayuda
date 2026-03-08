package com.sojus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sojus.dto.ContractRequest;
import com.sojus.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para el módulo de Contratos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Contratos — Tests de Integración")
@SuppressWarnings("null")
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String operadorToken;
    private String tecnicoToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = loginAndGetToken("admin", "admin123");
        operadorToken = loginAndGetToken("operador", "oper123");
        tecnicoToken = loginAndGetToken("tecnico", "tec123");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername(username);
        login.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Nested
    @DisplayName("GET /api/contracts")
    class ListarContratos {

        @Test
        @DisplayName("Admin puede listar contratos")
        void listarComoAdmin() throws Exception {
            mockMvc.perform(get("/api/contracts")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("Operador puede listar contratos")
        void listarComoOperador() throws Exception {
            mockMvc.perform(get("/api/contracts")
                    .header("Authorization", "Bearer " + operadorToken))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Técnico NO puede listar contratos — 403")
        void tecnicoSinAcceso() throws Exception {
            mockMvc.perform(get("/api/contracts")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Obtener contrato por ID")
        void obtenerPorId() throws Exception {
            mockMvc.perform(get("/api/contracts/1")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.proveedor").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/contracts")
    class CrearContrato {

        @Test
        @DisplayName("Admin crea contrato exitosamente — 201")
        void crearContrato() throws Exception {
            ContractRequest request = new ContractRequest();
            request.setNombre("Contrato Test");
            request.setProveedor("Test SRL");
            request.setNumeroContrato("CNT-TEST-001");
            request.setFechaInicio(LocalDate.of(2026, 1, 1));
            request.setFechaFin(LocalDate.of(2027, 12, 31));

            mockMvc.perform(post("/api/contracts")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nombre").value("Contrato Test"));
        }

        @Test
        @DisplayName("Sin campo 'nombre' obligatorio — 400")
        void crearContratoSinNombre() throws Exception {
            ContractRequest request = new ContractRequest();
            request.setProveedor("Test SRL");

            mockMvc.perform(post("/api/contracts")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Operador NO puede crear contratos — 403")
        void operadorNoCrea() throws Exception {
            ContractRequest request = new ContractRequest();
            request.setNombre("No debería crearse");
            request.setProveedor("Test");

            mockMvc.perform(post("/api/contracts")
                    .header("Authorization", "Bearer " + operadorToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/contracts/expiring")
    class ContratosProximosVencer {

        @Test
        @DisplayName("Buscar contratos próximos a vencer")
        void buscarExpiringConDays() throws Exception {
            mockMvc.perform(get("/api/contracts/expiring?days=365")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("DELETE /api/contracts")
    class DesactivarContrato {

        @Test
        @DisplayName("Admin desactiva contrato — 204")
        void desactivarContrato() throws Exception {
            // Crear un contrato para desactivar
            ContractRequest request = new ContractRequest();
            request.setNombre("Contrato Para Desactivar");
            request.setProveedor("Test SRL");

            MvcResult created = mockMvc.perform(post("/api/contracts")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Long createdId = objectMapper.readTree(created.getResponse().getContentAsString())
                    .get("id").asLong();

            mockMvc.perform(delete("/api/contracts/" + createdId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNoContent());
        }
    }
}
