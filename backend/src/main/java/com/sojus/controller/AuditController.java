package com.sojus.controller;

import com.sojus.domain.entity.AuditLog;
import com.sojus.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Auditoría", description = "Logs inmutables de cambios en el sistema")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "Últimos 100 registros de auditoría")
    @ApiResponse(responseCode = "200", description = "Registros de auditoría obtenidos exitosamente")
    public ResponseEntity<List<AuditLog>> findRecent() {
        return ResponseEntity.ok(auditService.findRecent());
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    @Operation(summary = "Historial de una entidad específica", description = "Devuelve todos los cambios realizados sobre una entidad (Ticket, Hardware, etc.) por su ID")
    @ApiResponse(responseCode = "200", description = "Historial de la entidad obtenido exitosamente")
    public ResponseEntity<List<AuditLog>> findByEntity(
            @PathVariable String entityName,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.findByEntity(entityName, entityId));
    }
}
