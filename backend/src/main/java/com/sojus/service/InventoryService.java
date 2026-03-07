package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.domain.entity.Hardware;
import com.sojus.domain.entity.Juzgado;
import com.sojus.domain.entity.Software;
import com.sojus.domain.enums.AssetStatus;
import com.sojus.dto.HardwareRequest;
import com.sojus.dto.HardwareResponse;
import com.sojus.dto.SoftwareRequest;
import com.sojus.dto.SoftwareResponse;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.AuditLogRepository;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.JuzgadoRepository;
import com.sojus.repository.SoftwareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio de gestión de inventario de Hardware y Software.
 * Implementa operaciones CRUD con soft-delete, validación de reglas de negocio
 * y registro inmutable de auditoría para todas las operaciones de escritura.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class InventoryService {

    private final HardwareRepository hardwareRepository;
    private final SoftwareRepository softwareRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final AuditLogRepository auditLogRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ================================================================
    // HARDWARE
    // ================================================================

    /**
     * Lista todo el hardware no eliminado (soft-delete).
     */
    @Transactional(readOnly = true)
    public List<HardwareResponse> findAllHardware() {
        return hardwareRepository.findAllByDeletedFalse().stream()
                .map(this::toHardwareResponse).toList();
    }

    /**
     * Busca un hardware por ID, excluyendo los eliminados.
     * 
     * @throws ResourceNotFoundException si no existe o fue eliminado
     */
    @Transactional(readOnly = true)
    public HardwareResponse findHardwareById(Long id) {
        Hardware hw = getHardwareOrThrow(id);
        return toHardwareResponse(hw);
    }

    /**
     * Crea un nuevo equipo de hardware.
     * Valida que el N° Inventario Patrimonial sea único.
     */
    @Transactional
    public HardwareResponse createHardware(HardwareRequest request, String username) {
        if (hardwareRepository.existsByInventarioPatrimonial(request.getInventarioPatrimonial())) {
            throw new BusinessRuleException("Ya existe un equipo con ese N° Inventario Patrimonial");
        }

        Hardware hardware = Hardware.builder()
                .inventarioPatrimonial(request.getInventarioPatrimonial())
                .numeroSerie(request.getNumeroSerie())
                .clase(request.getClase())
                .tipo(request.getTipo())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .ubicacionFisica(request.getUbicacionFisica())
                .observaciones(request.getObservaciones())
                .build();

        if (request.getEstado() != null) {
            hardware.setEstado(AssetStatus.valueOf(request.getEstado()));
        }

        if (request.getJuzgadoId() != null) {
            Juzgado juzgado = juzgadoRepository.findById(request.getJuzgadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Juzgado", request.getJuzgadoId()));
            hardware.setJuzgado(juzgado);
        }

        Hardware saved = hardwareRepository.save(hardware);
        log.info("Hardware creado: {} (ID: {})", saved.getInventarioPatrimonial(), saved.getId());

        auditLogRepository.save(AuditLog.builder()
                .entityName("Hardware").entityId(saved.getId())
                .action("CREAR").username(username)
                .newValue("Hardware creado: " + saved.getInventarioPatrimonial())
                .build());

        return toHardwareResponse(saved);
    }

    /**
     * Actualiza un equipo de hardware existente.
     */
    @Transactional
    public HardwareResponse updateHardware(Long id, HardwareRequest request, String username) {
        Hardware existing = getHardwareOrThrow(id);

        existing.setClase(request.getClase());
        existing.setTipo(request.getTipo());
        existing.setMarca(request.getMarca());
        existing.setModelo(request.getModelo());
        existing.setNumeroSerie(request.getNumeroSerie());
        existing.setUbicacionFisica(request.getUbicacionFisica());
        existing.setObservaciones(request.getObservaciones());

        if (request.getEstado() != null) {
            existing.setEstado(AssetStatus.valueOf(request.getEstado()));
        }

        if (request.getJuzgadoId() != null) {
            Juzgado juzgado = juzgadoRepository.findById(request.getJuzgadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Juzgado", request.getJuzgadoId()));
            existing.setJuzgado(juzgado);
        }

        Hardware saved = hardwareRepository.save(existing);
        log.info("Hardware actualizado: {} (ID: {})", saved.getInventarioPatrimonial(), saved.getId());

        auditLogRepository.save(AuditLog.builder()
                .entityName("Hardware").entityId(saved.getId())
                .action("ACTUALIZAR").username(username)
                .newValue("Hardware actualizado: " + saved.getInventarioPatrimonial())
                .build());

        return toHardwareResponse(saved);
    }

    /**
     * Elimina un hardware (soft-delete).
     */
    @Transactional
    public void softDeleteHardware(Long id, String username) {
        Hardware hw = getHardwareOrThrow(id);
        hw.setDeleted(true);
        hardwareRepository.save(hw);
        log.info("Hardware eliminado (soft): {} (ID: {})", hw.getInventarioPatrimonial(), id);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Hardware").entityId(id)
                .action("ELIMINAR").username(username)
                .oldValue("activo").newValue("eliminado")
                .build());
    }

    // ================================================================
    // SOFTWARE
    // ================================================================

    /**
     * Lista todo el software no eliminado.
     */
    @Transactional(readOnly = true)
    public List<SoftwareResponse> findAllSoftware() {
        return softwareRepository.findAllByDeletedFalse().stream()
                .map(this::toSoftwareResponse).toList();
    }

    /**
     * Busca un software por ID, excluyendo los eliminados.
     */
    @Transactional(readOnly = true)
    public SoftwareResponse findSoftwareById(Long id) {
        Software sw = getSoftwareOrThrow(id);
        return toSoftwareResponse(sw);
    }

    /**
     * Crea un nuevo registro de software.
     */
    @Transactional
    public SoftwareResponse createSoftware(SoftwareRequest request, String username) {
        Software software = Software.builder()
                .nombre(request.getNombre())
                .version(request.getVersion())
                .fabricante(request.getFabricante())
                .tipoLicencia(request.getTipoLicencia())
                .numeroLicencia(request.getNumeroLicencia())
                .cantidadLicencias(request.getCantidadLicencias())
                .fechaVencimiento(request.getFechaVencimiento())
                .observaciones(request.getObservaciones())
                .build();

        Software saved = softwareRepository.save(software);
        log.info("Software creado: {} (ID: {})", saved.getNombre(), saved.getId());

        auditLogRepository.save(AuditLog.builder()
                .entityName("Software").entityId(saved.getId())
                .action("CREAR").username(username)
                .newValue("Software creado: " + saved.getNombre())
                .build());

        return toSoftwareResponse(saved);
    }

    /**
     * Actualiza un registro de software existente.
     */
    @Transactional
    public SoftwareResponse updateSoftware(Long id, SoftwareRequest request, String username) {
        Software existing = getSoftwareOrThrow(id);

        existing.setNombre(request.getNombre());
        existing.setVersion(request.getVersion());
        existing.setFabricante(request.getFabricante());
        existing.setTipoLicencia(request.getTipoLicencia());
        existing.setNumeroLicencia(request.getNumeroLicencia());
        existing.setCantidadLicencias(request.getCantidadLicencias());
        existing.setFechaVencimiento(request.getFechaVencimiento());
        existing.setObservaciones(request.getObservaciones());

        Software saved = softwareRepository.save(existing);
        log.info("Software actualizado: {} (ID: {})", saved.getNombre(), saved.getId());

        auditLogRepository.save(AuditLog.builder()
                .entityName("Software").entityId(saved.getId())
                .action("ACTUALIZAR").username(username)
                .newValue("Software actualizado: " + saved.getNombre())
                .build());

        return toSoftwareResponse(saved);
    }

    /**
     * Elimina un software (soft-delete).
     */
    @Transactional
    public void softDeleteSoftware(Long id, String username) {
        Software sw = getSoftwareOrThrow(id);
        sw.setDeleted(true);
        softwareRepository.save(sw);
        log.info("Software eliminado (soft): {} (ID: {})", sw.getNombre(), id);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Software").entityId(id)
                .action("ELIMINAR").username(username)
                .oldValue("activo").newValue("eliminado")
                .build());
    }

    // ================================================================
    // MÉTODOS AUXILIARES
    // ================================================================

    private Hardware getHardwareOrThrow(Long id) {
        return hardwareRepository.findById(id)
                .filter(h -> !h.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Hardware", id));
    }

    private Software getSoftwareOrThrow(Long id) {
        return softwareRepository.findById(id)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Software", id));
    }

    private HardwareResponse toHardwareResponse(Hardware hw) {
        return HardwareResponse.builder()
                .id(hw.getId())
                .inventarioPatrimonial(hw.getInventarioPatrimonial())
                .numeroSerie(hw.getNumeroSerie())
                .clase(hw.getClase())
                .tipo(hw.getTipo())
                .marca(hw.getMarca())
                .modelo(hw.getModelo())
                .estado(hw.getEstado().name())
                .juzgadoNombre(hw.getJuzgado() != null ? hw.getJuzgado().getNombre() : null)
                .ubicacionFisica(hw.getUbicacionFisica())
                .observaciones(hw.getObservaciones())
                .createdAt(hw.getCreatedAt() != null ? hw.getCreatedAt().format(FMT) : null)
                .updatedAt(hw.getUpdatedAt() != null ? hw.getUpdatedAt().format(FMT) : null)
                .build();
    }

    private SoftwareResponse toSoftwareResponse(Software sw) {
        return SoftwareResponse.builder()
                .id(sw.getId())
                .nombre(sw.getNombre())
                .version(sw.getVersion())
                .fabricante(sw.getFabricante())
                .tipoLicencia(sw.getTipoLicencia())
                .numeroLicencia(sw.getNumeroLicencia())
                .cantidadLicencias(sw.getCantidadLicencias())
                .fechaVencimiento(sw.getFechaVencimiento() != null ? sw.getFechaVencimiento().toString() : null)
                .estado(sw.getEstado().name())
                .observaciones(sw.getObservaciones())
                .createdAt(sw.getCreatedAt() != null ? sw.getCreatedAt().format(FMT) : null)
                .updatedAt(sw.getUpdatedAt() != null ? sw.getUpdatedAt().format(FMT) : null)
                .build();
    }
}
