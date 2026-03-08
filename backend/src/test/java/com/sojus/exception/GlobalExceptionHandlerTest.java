package com.sojus.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

import java.time.format.DateTimeParseException;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para GlobalExceptionHandler.
 * Verifica que cada handler retorna el status code y formato JSON correcto.
 */
@DisplayName("GlobalExceptionHandler — Tests Unitarios")
@SuppressWarnings("null")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ResourceNotFoundException retorna 404")
    void handleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Ticket", 99L);
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message").toString()).contains("Ticket");
    }

    @Test
    @DisplayName("AuthenticationFailedException retorna 401")
    void handleAuthFailed() {
        AuthenticationFailedException ex = new AuthenticationFailedException("Credenciales inválidas");
        ResponseEntity<Map<String, Object>> response = handler.handleAuthFailed(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().get("status")).isEqualTo(401);
        assertThat(response.getBody().get("message")).isEqualTo("Credenciales inválidas");
    }

    @Test
    @DisplayName("BusinessRuleException retorna 409")
    void handleBusinessRule() {
        BusinessRuleException ex = new BusinessRuleException("El equipo ya tiene un ticket activo");
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessRule(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().get("status")).isEqualTo(409);
        assertThat(response.getBody().get("message")).isEqualTo("El equipo ya tiene un ticket activo");
    }

    @Test
    @DisplayName("IllegalArgumentException con enum retorna mensaje amigable")
    void handleIllegalArgument_enumError() {
        IllegalArgumentException ex = new IllegalArgumentException(
                "No enum constant com.sojus.domain.enums.TicketStatus.INVALIDO");
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message").toString()).contains("INVALIDO");
        assertThat(response.getBody().get("message").toString()).contains("no es válido");
    }

    @Test
    @DisplayName("IllegalArgumentException genérico retorna mensaje original")
    void handleIllegalArgument_generic() {
        IllegalArgumentException ex = new IllegalArgumentException("Parámetro inválido");
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Parámetro inválido");
    }

    @Test
    @DisplayName("DateTimeParseException retorna 400 con mensaje de formato")
    void handleDateTimeParse() {
        DateTimeParseException ex = new DateTimeParseException("parse error", "not-a-date", 0);
        ResponseEntity<Map<String, Object>> response = handler.handleDateTimeParse(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message").toString()).contains("formato");
    }

    @Test
    @DisplayName("AccessDeniedException retorna 403")
    void handleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Denegado");
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().get("status")).isEqualTo(403);
        assertThat(response.getBody().get("message").toString()).contains("permisos");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException retorna 400")
    void handleNotReadable() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "bad json", (HttpInputMessage) null);
        ResponseEntity<Map<String, Object>> response = handler.handleNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message").toString()).contains("JSON");
    }

    @Test
    @DisplayName("Exception genérica retorna 500")
    void handleGeneric() {
        Exception ex = new RuntimeException("Error inesperado");
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("message")).isEqualTo("Error interno del servidor");
    }

    @Test
    @DisplayName("Todas las respuestas tienen timestamp, status, error y message")
    void responseFormat() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Entidad", 1L);
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertThat(response.getBody()).containsKeys("timestamp", "status", "error", "message");
    }
}
