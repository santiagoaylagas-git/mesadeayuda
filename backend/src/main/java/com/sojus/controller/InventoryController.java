package com.sojus.controller;

import com.sojus.domain.entity.User;
import com.sojus.dto.HardwareRequest;
import com.sojus.dto.HardwareResponse;
import com.sojus.dto.SoftwareRequest;
import com.sojus.dto.SoftwareResponse;
import com.sojus.service.InventoryService;
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
    public List<HardwareResponse> listHardware() {
        return inventoryService.findAllHardware();
    }

    @GetMapping("/hardware/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'TECNICO')")
    public HardwareResponse getHardware(@PathVariable Long id) {
        return inventoryService.findHardwareById(id);
    }

    @PostMapping("/hardware")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    public HardwareResponse createHardware(@Valid @RequestBody HardwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.createHardware(request, user.getUsername());
    }

    @PutMapping("/hardware/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    public HardwareResponse updateHardware(@PathVariable Long id,
            @Valid @RequestBody HardwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.updateHardware(id, request, user.getUsername());
    }

    @DeleteMapping("/hardware/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void deleteHardware(@PathVariable Long id, @AuthenticationPrincipal User user) {
        inventoryService.softDeleteHardware(id, user.getUsername());
    }

    // ================================================================
    // SOFTWARE
    // ================================================================

    @GetMapping("/software")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'TECNICO')")
    public List<SoftwareResponse> listSoftware() {
        return inventoryService.findAllSoftware();
    }

    @GetMapping("/software/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR', 'TECNICO')")
    public SoftwareResponse getSoftware(@PathVariable Long id) {
        return inventoryService.findSoftwareById(id);
    }

    @PostMapping("/software")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    public SoftwareResponse createSoftware(@Valid @RequestBody SoftwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.createSoftware(request, user.getUsername());
    }

    @PutMapping("/software/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    public SoftwareResponse updateSoftware(@PathVariable Long id,
            @Valid @RequestBody SoftwareRequest request,
            @AuthenticationPrincipal User user) {
        return inventoryService.updateSoftware(id, request, user.getUsername());
    }

    @DeleteMapping("/software/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void deleteSoftware(@PathVariable Long id, @AuthenticationPrincipal User user) {
        inventoryService.softDeleteSoftware(id, user.getUsername());
    }
}
