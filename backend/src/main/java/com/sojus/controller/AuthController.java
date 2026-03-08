package com.sojus.controller;

import com.sojus.dto.LoginRequest;
import com.sojus.dto.LoginResponse;
import com.sojus.domain.entity.User;
import com.sojus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login y gestión de sesión JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica con username/password y retorna un token JWT válido por 24hs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, token JWT retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario desactivado")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Retorna datos del usuario autenticado a partir del token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos del usuario autenticado"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o expirado")
    })
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", user.getRole().name(),
                "email", user.getEmail() != null ? user.getEmail() : ""));
    }
}
