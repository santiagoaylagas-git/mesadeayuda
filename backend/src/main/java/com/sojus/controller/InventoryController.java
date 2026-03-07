package com.sojus.controller;

import com.sojus.dto.HardwareRequest;
import com.sojus.dto.HardwareResponse;
import com.sojus.dto.SoftwareRequest;
import com.sojus.dto.SoftwareResponse;
import com.sojus.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
            Authentication auth) {
        return inventoryService.createHardware(request, auth.getName());
    }

    @PutMapping("/hardware/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    public HardwareResponse updateHardware(@PathVariable Long id,
            @Valid @RequestBody HardwareRequest request,
            Authentication auth) {
        return inventoryService.updateHardware(id, request, auth.getName());
    }

    @DeleteMapping("/hardware/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void deleteHardware(@PathVariable Long id, Authentication auth) {
        inventoryService.softDeleteHardware(id, auth.getName());
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
            Authentication auth) {
        return inventoryService.createSoftware(request, auth.getName());
    }

    @PutMapping("/software/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNICO')")
    public SoftwareResponse updateSoftware(@PathVariable Long id,
            @Valid @RequestBody SoftwareRequest request,
            Authentication auth) {
        return inventoryService.updateSoftware(id, request, auth.getName());
    }

    @DeleteMapping("/software/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void deleteSoftware(@PathVariable Long id, Authentication auth) {
        inventoryService.softDeleteSoftware(id, auth.getName());
    }
}
