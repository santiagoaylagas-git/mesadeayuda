package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.domain.entity.Contract;
import com.sojus.dto.ContractRequest;
import com.sojus.dto.ContractResponse;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.AuditLogRepository;
import com.sojus.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio de gestión de contratos con proveedores externos.
 * Incluye consulta de contratos próximos a vencer y registro de auditoría.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class ContractService {

    private final ContractRepository contractRepository;
    private final AuditLogRepository auditLogRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Lista todos los contratos activos.
     */
    @Transactional(readOnly = true)
    public List<ContractResponse> findAll() {
        return contractRepository.findAllByActiveTrue().stream()
                .map(this::toResponse).toList();
    }

    /**
     * Busca un contrato activo por ID.
     * 
     * @throws ResourceNotFoundException si no existe o está inactivo
     */
    @Transactional(readOnly = true)
    public ContractResponse findById(Long id) {
        Contract contract = getContractOrThrow(id);
        return toResponse(contract);
    }

    /**
     * Crea un nuevo contrato.
     */
    @Transactional
    public ContractResponse create(ContractRequest request, String username) {
        Contract contract = Contract.builder()
                .nombre(request.getNombre())
                .proveedor(request.getProveedor())
                .numeroContrato(request.getNumeroContrato())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .coberturaHw(request.getCoberturaHw())
                .coberturaSw(request.getCoberturaSw())
                .slaDescripcion(request.getSlaDescripcion())
                .observaciones(request.getObservaciones())
                .build();

        Contract saved = contractRepository.save(contract);
        log.info("Contrato creado: {} (ID: {})", saved.getNombre(), saved.getId());

        auditLogRepository.save(AuditLog.builder()
                .entityName("Contract").entityId(saved.getId())
                .action("CREAR").username(username)
                .newValue("Contrato creado: " + saved.getNombre())
                .build());

        return toResponse(saved);
    }

    /**
     * Actualiza un contrato existente.
     */
    @Transactional
    public ContractResponse update(Long id, ContractRequest request, String username) {
        Contract existing = getContractOrThrow(id);

        existing.setNombre(request.getNombre());
        existing.setProveedor(request.getProveedor());
        existing.setNumeroContrato(request.getNumeroContrato());
        existing.setFechaInicio(request.getFechaInicio());
        existing.setFechaFin(request.getFechaFin());
        existing.setCoberturaHw(request.getCoberturaHw());
        existing.setCoberturaSw(request.getCoberturaSw());
        existing.setSlaDescripcion(request.getSlaDescripcion());
        existing.setObservaciones(request.getObservaciones());

        Contract saved = contractRepository.save(existing);
        log.info("Contrato actualizado: {} (ID: {})", saved.getNombre(), saved.getId());

        auditLogRepository.save(AuditLog.builder()
                .entityName("Contract").entityId(saved.getId())
                .action("ACTUALIZAR").username(username)
                .newValue("Contrato actualizado: " + saved.getNombre())
                .build());

        return toResponse(saved);
    }

    /**
     * Desactiva un contrato (no lo elimina físicamente).
     */
    @Transactional
    public void deactivate(Long id, String username) {
        Contract contract = getContractOrThrow(id);
        contract.setActive(false);
        contractRepository.save(contract);
        log.info("Contrato desactivado: {} (ID: {})", contract.getNombre(), id);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Contract").entityId(id)
                .action("DESACTIVAR").username(username)
                .oldValue("activo").newValue("inactivo")
                .build());
    }

    /**
     * Devuelve contratos cuya fecha de fin es anterior o igual a la fecha actual +
     * días indicados.
     */
    @Transactional(readOnly = true)
    public List<ContractResponse> findExpiringSoon(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return contractRepository.findExpiringBefore(threshold).stream()
                .map(this::toResponse).toList();
    }

    // ================================================================
    // MÉTODOS AUXILIARES
    // ================================================================

    private Contract getContractOrThrow(Long id) {
        return contractRepository.findById(id)
                .filter(Contract::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", id));
    }

    private ContractResponse toResponse(Contract c) {
        return ContractResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .proveedor(c.getProveedor())
                .numeroContrato(c.getNumeroContrato())
                .fechaInicio(c.getFechaInicio() != null ? c.getFechaInicio().toString() : null)
                .fechaFin(c.getFechaFin() != null ? c.getFechaFin().toString() : null)
                .coberturaHw(c.getCoberturaHw())
                .coberturaSw(c.getCoberturaSw())
                .slaDescripcion(c.getSlaDescripcion())
                .observaciones(c.getObservaciones())
                .active(c.getActive())
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : null)
                .updatedAt(c.getUpdatedAt() != null ? c.getUpdatedAt().format(FMT) : null)
                .build();
    }
}
