package com.sojus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciales de login con verificación reCAPTCHA")
public class LoginRequest {
    @NotBlank(message = "El usuario es obligatorio")
    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "admin123")
    private String password;

    @Schema(description = "Token de Google reCAPTCHA v2 (requerido si captcha está habilitado)")
    private String captchaToken;
}
