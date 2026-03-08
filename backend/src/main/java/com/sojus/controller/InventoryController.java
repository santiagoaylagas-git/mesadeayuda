package com.sojus.controller;

import com.sojus.domain.entity.User;
import com.sojus.dto.HardwareRequest;
import com.sojus.dto.HardwareResponse;
import com.sojus.dto.SoftwareRequest;
import com.sojus.dto.SoftwareResponse;
import com.sojus.service.InventoryService;
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
 * Controlador REST para la gestión de inventario de Hardware y Software.
 * Acceso restringido a ADMINISTRADOR (CRUD completo) y OPERADOR/TECNICO
 * (lectura).
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de inventario de Hardware y Software")
public class InventoryController {

    private final InventoryService inventoryService;

    // ================================================================
    // HARDWARE
    // ================================================================

    @GetMapping("/hardware")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'TECNICO')")
    @Operation(summary = "Listar todo el hardware activo")
    @ApiResponse(responseCode = "200", description = "Lista de hardware obtenida exitosamente")
    public List<HardwareResponse> listHardware() {
        return inventoryService.findAllHardware();
    }

    @GetMapping("/hardware/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'TECNICO')")
    @Operation(summary = "Obtener hardware por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hardware encontrado"),
            @ApiResponse(responseCode = "404", description = "Hardware no encontrado")
    })
    public HardwareResponse getHardware(@PathVariable Long id) {
        return inventoryService.findHardwareById(id);
    }

    @PostMapping("/hardware")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Crear nuevo equipo de hardware")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hardware creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "N° Inventario Patrimonial duplicado")
    })
    public HardwareResponse createHardware(@Valid @RequestBody HardwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.createHardware(request, user.getUsername());
    }

    @PutMapping("/hardware/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Actualizar equipo de hardware")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hardware actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Hardware no encontrado")
    })
    public HardwareResponse updateHardware(@PathVariable Long id,
            @Valid @RequestBody HardwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.updateHardware(id, request, user.getUsername());
    }

    @DeleteMapping("/hardware/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar hardware (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Hardware eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hardware no encontrado")
    })
    public void deleteHardware(@PathVariable Long id, @AuthenticationPrincipal User user) {
        inventoryService.softDeleteHardware(id, user.getUsername());
    }

    // ================================================================
    // SOFTWARE
    // ================================================================

    @GetMapping("/software")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'TECNICO')")
    @Operation(summary = "Listar todo el software activo")
    @ApiResponse(responseCode = "200", description = "Lista de software obtenida exitosamente")
    public List<SoftwareResponse> listSoftware() {
        return inventoryService.findAllSoftware();
    }

    @GetMapping("/software/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'TECNICO')")
    @Operation(summary = "Obtener software por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Software encontrado"),
            @ApiResponse(responseCode = "404", description = "Software no encontrado")
    })
    public SoftwareResponse getSoftware(@PathVariable Long id) {
        return inventoryService.findSoftwareById(id);
    }

    @PostMapping("/software")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Crear nuevo registro de software")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Software creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public SoftwareResponse createSoftware(@Valid @RequestBody SoftwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.createSoftware(request, user.getUsername());
    }

    @PutMapping("/software/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    @Operation(summary = "Actualizar registro de software")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Software actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Software no encontrado")
    })
    public SoftwareResponse updateSoftware(@PathVariable Long id,
            @Valid @RequestBody SoftwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.updateSoftware(id, request, user.getUsername());
    }

    @DeleteMapping("/software/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar software (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Software eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Software no encontrado")
    })
    public void deleteSoftware(@PathVariable Long id, @AuthenticationPrincipal User user) {
        inventoryService.softDeleteSoftware(id, user.getUsername());
    }
}
