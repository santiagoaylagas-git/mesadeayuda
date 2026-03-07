package com.sojus.controller;

import com.sojus.domain.enums.RoleName;
import com.sojus.dto.UserCreateRequest;
import com.sojus.dto.UserResponse;
import com.sojus.dto.UserUpdateRequest;
import com.sojus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para ABM de Usuarios.
 * Acceso restringido exclusivamente al rol ADMINISTRADOR.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema (ADMIN)")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public List<UserResponse> listAll() {
        return userService.findAllAsDto();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public UserResponse getById(@PathVariable Long id) {
        return userService.findByIdAsDto(id);
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Listar usuarios por rol")
    public List<UserResponse> listByRole(@PathVariable String role) {
        return userService.findByRoleAsDto(RoleName.valueOf(role));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo usuario")
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return userService.createFromRequest(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario existente")
    public UserResponse update(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateFromRequest(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar usuario (soft delete)")
    public void delete(@PathVariable Long id) {
        userService.softDelete(id);
    }
}
