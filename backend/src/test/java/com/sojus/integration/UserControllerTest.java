package com.sojus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sojus.dto.LoginRequest;
import com.sojus.dto.UserCreateRequest;
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
 * Tests de integración para el módulo de Usuarios (ADMIN only).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Usuarios — Tests de Integración")
@SuppressWarnings("null")
class UserControllerTest {

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

    @Nested
    @DisplayName("GET /api/users")
    class ListarUsuarios {

        @Test
        @DisplayName("Admin puede listar todos los usuarios")
        void listarComoAdmin() throws Exception {
            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
        }

        @Test
        @DisplayName("Técnico NO puede listar usuarios — 403")
        void tecnicoSinAcceso() throws Exception {
            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Obtener usuario por ID")
        void obtenerPorId() throws Exception {
            mockMvc.perform(get("/api/users/1")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("admin"))
                    .andExpect(jsonPath("$.role").value("ADMINISTRADOR"));
        }

        @Test
        @DisplayName("Listar usuarios por rol TECNICO")
        void listarPorRol() throws Exception {
            mockMvc.perform(get("/api/users/role/TECNICO")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].role").value("TECNICO"));
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class CrearUsuario {

        @Test
        @DisplayName("Admin crea usuario exitosamente — 201")
        void crearUsuario() throws Exception {
            UserCreateRequest request = new UserCreateRequest();
            request.setUsername("testuser");
            request.setPassword("pass123");
            request.setFullName("Test User");
            request.setEmail("test@test.com");
            request.setRole("OPERADOR");

            mockMvc.perform(post("/api/users")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.role").value("OPERADOR"));
        }

        @Test
        @DisplayName("Username duplicado — 409")
        void crearUsuarioDuplicado() throws Exception {
            UserCreateRequest request = new UserCreateRequest();
            request.setUsername("admin"); // ya existe
            request.setPassword("pass123");
            request.setFullName("Duplicado");
            request.setRole("OPERADOR");

            mockMvc.perform(post("/api/users")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Sin username obligatorio — 400")
        void crearSinUsername() throws Exception {
            UserCreateRequest request = new UserCreateRequest();
            request.setPassword("pass123");
            request.setFullName("Sin Username");
            request.setRole("OPERADOR");

            mockMvc.perform(post("/api/users")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users")
    class EliminarUsuario {

        @Test
        @DisplayName("Técnico NO puede eliminar usuarios — 403")
        void tecnicoNoElimina() throws Exception {
            mockMvc.perform(delete("/api/users/2")
                    .header("Authorization", "Bearer " + tecnicoToken))
                    .andExpect(status().isForbidden());
        }
    }
}
