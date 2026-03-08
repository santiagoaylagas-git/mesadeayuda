package com.sojus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para Auditoría, Dashboard y Estructura Territorial.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Auditoría, Dashboard y Locations — Tests de Integración")
@SuppressWarnings("null")
class AuditDashboardLocationTest {

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

    // ================================================================
    // AUDITORÍA
    // ================================================================
    @Nested
    @DisplayName("GET /api/audit")
    class Auditoria {

        @Test
        @DisplayName("Admin puede ver registros de auditoría")
        void listarAuditoria() throws Exception {
            mockMvc.perform(get("/api/audit")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Admin puede ver historial de una entidad")
        void historialEntidad() throws Exception {
            mockMvc.perform(get("/api/audit/entity/Ticket/1")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Técnico NO puede ver auditoría — 403")
        void tecnicoSinAcceso() throws Exception {
            mockMvc.perform(get("/api/audit")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Operador NO puede ver auditoría — 403")
        void operadorSinAcceso() throws Exception {
            mockMvc.perform(get("/api/audit")
                    .header("Authorization", "Bearer " + operadorToken))
                    .andExpect(status().isForbidden());
        }
    }

    // ================================================================
    // DASHBOARD
    // ================================================================
    @Nested
    @DisplayName("GET /api/dashboard/stats")
    class Dashboard {

        @Test
        @DisplayName("Admin puede ver estadísticas del dashboard")
        void statsComoAdmin() throws Exception {
            mockMvc.perform(get("/api/dashboard/stats")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isNotEmpty());
        }

        @Test
        @DisplayName("Operador puede ver estadísticas del dashboard")
        void statsComoOperador() throws Exception {
            mockMvc.perform(get("/api/dashboard/stats")
                    .header("Authorization", "Bearer " + operadorToken))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Técnico NO puede ver dashboard — 403")
        void tecnicoSinAcceso() throws Exception {
            mockMvc.perform(get("/api/dashboard/stats")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isForbidden());
        }
    }

    // ================================================================
    // ESTRUCTURA TERRITORIAL
    // ================================================================
    @Nested
    @DisplayName("GET /api/locations")
    class EstructuraTerritorial {

        @Test
        @DisplayName("Listar circunscripciones con jerarquía")
        void listarCircunscripciones() throws Exception {
            mockMvc.perform(get("/api/locations/circunscripciones")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
        }

        @Test
        @DisplayName("Listar todos los juzgados activos")
        void listarJuzgados() throws Exception {
            mockMvc.perform(get("/api/locations/juzgados")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
        }

        @Test
        @DisplayName("Listar juzgados de un edificio")
        void juzgadosPorEdificio() throws Exception {
            mockMvc.perform(get("/api/locations/edificios/1/juzgados")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Técnico puede ver ubicaciones (acceso para todos)")
        void tecnicoVeUbicaciones() throws Exception {
            mockMvc.perform(get("/api/locations/juzgados")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isOk());
        }
    }
}
