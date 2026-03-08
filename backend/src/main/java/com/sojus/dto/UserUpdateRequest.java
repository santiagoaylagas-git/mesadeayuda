package com.sojus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para actualizar un usuario existente.
 * La contraseña es opcional — si se envía vacía, no se actualiza.
 */
@Data
@Schema(description = "Datos para actualizar un usuario existente")
public class UserUpdateRequest {

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

    @Schema(description = "Estado activo del usuario", example = "true")
    private Boolean active;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Nueva contraseña (opcional — null o vacío no actualiza)")
    private String password;
}
