package com.sojus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sojus.dto.HardwareRequest;
import com.sojus.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
class DebugTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void debugCreateHardware() throws Exception {
        // Login
        LoginRequest login = new LoginRequest();
        login.setUsername("admin");
        login.setPassword("admin123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andReturn();
        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();

        // Create
        HardwareRequest request = new HardwareRequest();
        request.setInventarioPatrimonial("INV-DEBUG-001");
        request.setClase("PC");
        request.setEstado("ACTIVO");

        mockMvc.perform(post("/api/inventory/hardware")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
    }
}
