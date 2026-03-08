package com.sojus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sojus.dto.HardwareRequest;
import com.sojus.dto.LoginRequest;
import com.sojus.dto.SoftwareRequest;
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
 * Tests de integración para el módulo de Inventario (Hardware + Software).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Inventario — Tests de Integración")
@SuppressWarnings("null")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String tecnicoToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = loginAndGetToken("admin", "admin123");
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
    // HARDWARE
    // ================================================================
    @Nested
    @DisplayName("GET /api/inventory/hardware")
    class ListarHardware {

        @Test
        @DisplayName("Admin puede listar todo el hardware")
        void listarHardware() throws Exception {
            mockMvc.perform(get("/api/inventory/hardware")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("Obtener hardware por ID existente")
        void obtenerHardwarePorId() throws Exception {
            mockMvc.perform(get("/api/inventory/hardware/1")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.inventarioPatrimonial").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/inventory/hardware")
    class CrearHardware {

        @Test
        @DisplayName("Admin crea hardware exitosamente — 201")
        void crearHardware() throws Exception {
            HardwareRequest request = new HardwareRequest();
            request.setInventarioPatrimonial("INV-TEST-001");
            request.setClase("PC");
            request.setTipo("Desktop");
            request.setMarca("Lenovo");
            request.setModelo("ThinkCentre M70q");
            request.setEstado("ACTIVO");

            mockMvc.perform(post("/api/inventory/hardware")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.inventarioPatrimonial").value("INV-TEST-001"));
        }

        @Test
        @DisplayName("Inventario patrimonial duplicado — 409")
        void crearHardwareDuplicado() throws Exception {
            HardwareRequest request = new HardwareRequest();
            request.setInventarioPatrimonial("INV-001-0001"); // Ya existe en DataInitializer
            request.setClase("PC");

            mockMvc.perform(post("/api/inventory/hardware")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Sin campo 'clase' obligatorio — 400")
        void crearHardwareSinClase() throws Exception {
            HardwareRequest request = new HardwareRequest();
            request.setInventarioPatrimonial("INV-TEST-002");
            // clase is missing

            mockMvc.perform(post("/api/inventory/hardware")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/inventory/hardware")
    class EliminarHardware {

        @Test
        @DisplayName("Admin elimina hardware (soft delete) — 204")
        void eliminarHardware() throws Exception {
            // Crear un hardware para eliminar (evitar interferencia con otros tests)
            HardwareRequest request = new HardwareRequest();
            request.setInventarioPatrimonial("INV-DELETE-HW");
            request.setClase("PC");
            request.setEstado("ACTIVO");

            MvcResult created = mockMvc.perform(post("/api/inventory/hardware")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Long createdId = objectMapper.readTree(created.getResponse().getContentAsString())
                    .get("id").asLong();

            mockMvc.perform(delete("/api/inventory/hardware/" + createdId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Técnico NO puede eliminar hardware — 403")
        void tecnicoNoEliminaHardware() throws Exception {
            mockMvc.perform(delete("/api/inventory/hardware/1")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isForbidden());
        }
    }

    // ================================================================
    // SOFTWARE
    // ================================================================
    @Nested
    @DisplayName("GET /api/inventory/software")
    class ListarSoftware {

        @Test
        @DisplayName("Admin puede listar todo el software")
        void listarSoftware() throws Exception {
            mockMvc.perform(get("/api/inventory/software")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("Obtener software por ID existente")
        void obtenerSoftwarePorId() throws Exception {
            mockMvc.perform(get("/api/inventory/software/1")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/inventory/software")
    class CrearSoftware {

        @Test
        @DisplayName("Admin crea software exitosamente — 201")
        void crearSoftware() throws Exception {
            SoftwareRequest request = new SoftwareRequest();
            request.setNombre("Test Software");
            request.setVersion("1.0");
            request.setFabricante("Test Corp");

            mockMvc.perform(post("/api/inventory/software")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Sin campo 'nombre' obligatorio — 400")
        void crearSoftwareSinNombre() throws Exception {
            SoftwareRequest request = new SoftwareRequest();
            request.setVersion("1.0");

            mockMvc.perform(post("/api/inventory/software")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/inventory/software")
    class EliminarSoftware {

        @Test
        @DisplayName("Admin elimina software (soft delete) — 204")
        void eliminarSoftware() throws Exception {
            // Crear un software para eliminar
            SoftwareRequest request = new SoftwareRequest();
            request.setNombre("Software Para Eliminar");

            MvcResult created = mockMvc.perform(post("/api/inventory/software")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Long createdId = objectMapper.readTree(created.getResponse().getContentAsString())
                    .get("id").asLong();

            mockMvc.perform(delete("/api/inventory/software/" + createdId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNoContent());
        }
    }
}
