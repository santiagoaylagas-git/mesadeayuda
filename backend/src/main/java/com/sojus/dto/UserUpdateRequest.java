package com.sojus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para actualizar un usuario existente.
 * La contraseña es opcional — si se envía vacía, no se actualiza.
 */
@Data
public class UserUpdateRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El rol es obligatorio")
    private String role; // ADMINISTRADOR, TECNICO, OPERADOR

    private Boolean active;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password; // Opcional — null o vacío no actualiza
}
