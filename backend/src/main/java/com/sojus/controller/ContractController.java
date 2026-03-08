package com.sojus.controller;

import com.sojus.domain.entity.User;
import com.sojus.dto.ContractRequest;
import com.sojus.dto.ContractResponse;
import com.sojus.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de contratos con proveedores.
 * Acceso restringido a ADMINISTRADOR (CRUD completo) y OPERADOR (lectura).
 */
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contratos", description = "Gestión de contratos con proveedores")
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Listar todos los contratos activos")
    @ApiResponse(responseCode = "200", description = "Lista de contratos obtenida exitosamente")
    public List<ContractResponse> listAll() {
        return contractService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Obtener contrato por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrato encontrado"),
            @ApiResponse(responseCode = "404", description = "Contrato no encontrado o inactivo")
    })
    public ContractResponse getById(@PathVariable Long id) {
        return contractService.findById(id);
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    @Operation(summary = "Listar contratos próximos a vencer", description = "Devuelve contratos cuya fecha de fin es anterior a hoy + días indicados")
    @ApiResponse(responseCode = "200", description = "Lista de contratos próximos a vencer")
    public List<ContractResponse> findExpiring(@RequestParam(defaultValue = "30") int days) {
        return contractService.findExpiringSoon(days);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear nuevo contrato")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ContractResponse create(@Valid @RequestBody ContractRequest request,
            @AuthenticationPrincipal User user) {
        return contractService.create(request, user.getUsername());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar contrato existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrato actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    })
    public ContractResponse update(@PathVariable Long id,
            @Valid @RequestBody ContractRequest request,
            @AuthenticationPrincipal User user) {
        return contractService.update(id, request, user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Desactivar contrato")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrato desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    })
    public void deactivate(@PathVariable Long id, @AuthenticationPrincipal User user) {
        contractService.deactivate(id, user.getUsername());
    }
}
