package com.sojus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para crear un nuevo usuario.
 * Incluye validaciones para username, password, fullName, email y role.
 */
@Data
@Schema(description = "Datos para crear un nuevo usuario del sistema")
public class UserCreateRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "jperez")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "pass1234")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String fullName;

    @Email(message = "El email debe ser válido")
    @Schema(description = "Email del usuario", example = "jperez@poderjudicial.gov.ar")
    private String email;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "ADMINISTRADOR|TECNICO|OPERADOR", message = "Rol inválido. Valores permitidos: ADMINISTRADOR, TECNICO, OPERADOR")
    @Schema(description = "Rol del usuario", example = "OPERADOR", allowableValues = { "ADMINISTRADOR", "TECNICO",
            "OPERADOR" })
    private String role;

    @Schema(description = "ID del juzgado asignado (opcional)", example = "1")
    private Long juzgadoId;
}
