package com.sojus.controller;

import com.sojus.dto.ContractRequest;
import com.sojus.dto.ContractResponse;
import com.sojus.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de contratos con proveedores.
 * Acceso restringido a ADMINISTRADOR (CRUD completo) y OPERADOR (lectura).
 */
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    public List<ContractResponse> listAll() {
        return contractService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    public ContractResponse getById(@PathVariable Long id) {
        return contractService.findById(id);
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'OPERADOR')")
    public List<ContractResponse> findExpiring(@RequestParam(defaultValue = "30") int days) {
        return contractService.findExpiringSoon(days);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ContractResponse create(@Valid @RequestBody ContractRequest request,
            Authentication auth) {
        return contractService.create(request, auth.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ContractResponse update(@PathVariable Long id,
            @Valid @RequestBody ContractRequest request,
            Authentication auth) {
        return contractService.update(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void deactivate(@PathVariable Long id, Authentication auth) {
        contractService.deactivate(id, auth.getName());
    }
}
