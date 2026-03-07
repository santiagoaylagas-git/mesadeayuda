package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.domain.entity.Hardware;
import com.sojus.domain.entity.Software;
import com.sojus.domain.enums.AssetStatus;
import com.sojus.dto.*;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.AuditLogRepository;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.JuzgadoRepository;
import com.sojus.repository.SoftwareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService — Tests Unitarios")
@SuppressWarnings("null")
class InventoryServiceTest {

    @Mock
    private HardwareRepository hardwareRepository;
    @Mock
    private SoftwareRepository softwareRepository;
    @Mock
    private JuzgadoRepository juzgadoRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Hardware pc;
    private Software office;

    @BeforeEach
    void setUp() {
        pc = Hardware.builder()
                .id(1L).inventarioPatrimonial("INV-001-0001").numeroSerie("SN-001")
                .clase("PC").tipo("Desktop").marca("Dell").modelo("OptiPlex 7090")
                .estado(AssetStatus.ACTIVO).ubicacionFisica("Puesto 1")
                .deleted(false).createdAt(LocalDateTime.now())
                .build();

        office = Software.builder()
                .id(1L).nombre("Microsoft Office 365").version("2024")
                .fabricante("Microsoft").tipoLicencia("Suscripción")
                .cantidadLicencias(500).deleted(false)
                .estado(AssetStatus.ACTIVO)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ================================================================
    // HARDWARE
    // ================================================================
    @Nested
    @DisplayName("CRUD de Hardware")
    class CrudHardware {

        @Test
        @DisplayName("findAllHardware devuelve solo no eliminados como DTOs")
        void findAll() {
            when(hardwareRepository.findAllByDeletedFalse()).thenReturn(List.of(pc));

            List<HardwareResponse> result = inventoryService.findAllHardware();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getInventarioPatrimonial()).isEqualTo("INV-001-0001");
        }

        @Test
        @DisplayName("findHardwareById devuelve hardware existente como DTO")
        void findById_exitoso() {
            when(hardwareRepository.findById(1L)).thenReturn(Optional.of(pc));

            HardwareResponse result = inventoryService.findHardwareById(1L);

            assertThat(result.getMarca()).isEqualTo("Dell");
        }

        @Test
        @DisplayName("findHardwareById de eliminado lanza excepción")
        void findById_eliminado() {
            Hardware deleted = Hardware.builder().id(2L).deleted(true).build();
            when(hardwareRepository.findById(2L)).thenReturn(Optional.of(deleted));

            assertThatThrownBy(() -> inventoryService.findHardwareById(2L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("createHardware exitoso con auditoría")
        void crear_exitoso() {
            HardwareRequest request = new HardwareRequest();
            request.setInventarioPatrimonial("INV-NEW-0001");
            request.setClase("Impresora");

            when(hardwareRepository.existsByInventarioPatrimonial("INV-NEW-0001")).thenReturn(false);
            when(hardwareRepository.save(any(Hardware.class))).thenAnswer(inv -> {
                Hardware h = inv.getArgument(0);
                h.setId(10L);
                h.setEstado(AssetStatus.ACTIVO);
                h.setCreatedAt(LocalDateTime.now());
                return h;
            });
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            HardwareResponse result = inventoryService.createHardware(request, "admin");

            assertThat(result.getId()).isEqualTo(10L);
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("createHardware rechaza inventario patrimonial duplicado")
        void crear_duplicado() {
            HardwareRequest request = new HardwareRequest();
            request.setInventarioPatrimonial("INV-001-0001");
            request.setClase("PC");

            when(hardwareRepository.existsByInventarioPatrimonial("INV-001-0001")).thenReturn(true);

            assertThatThrownBy(() -> inventoryService.createHardware(request, "admin"))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Inventario Patrimonial");
        }

        @Test
        @DisplayName("updateHardware actualiza campos correctamente")
        void actualizar_exitoso() {
            HardwareRequest request = new HardwareRequest();
            request.setClase("Servidor");
            request.setTipo("Rack");
            request.setMarca("HP");
            request.setModelo("ProLiant");
            request.setEstado("EN_REPARACION");
            request.setInventarioPatrimonial("INV-001-0001");

            when(hardwareRepository.findById(1L)).thenReturn(Optional.of(pc));
            when(hardwareRepository.save(any(Hardware.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            HardwareResponse result = inventoryService.updateHardware(1L, request, "admin");

            assertThat(result.getClase()).isEqualTo("Servidor");
            assertThat(result.getEstado()).isEqualTo("EN_REPARACION");
        }

        @Test
        @DisplayName("softDeleteHardware marca como eliminado y registra auditoría")
        void softDelete_exitoso() {
            when(hardwareRepository.findById(1L)).thenReturn(Optional.of(pc));
            when(hardwareRepository.save(any(Hardware.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            inventoryService.softDeleteHardware(1L, "admin");

            assertThat(pc.getDeleted()).isTrue();
            verify(auditLogRepository).save(any(AuditLog.class));
        }
    }

    // ================================================================
    // SOFTWARE
    // ================================================================
    @Nested
    @DisplayName("CRUD de Software")
    class CrudSoftware {

        @Test
        @DisplayName("findAllSoftware devuelve solo no eliminados como DTOs")
        void findAll() {
            when(softwareRepository.findAllByDeletedFalse()).thenReturn(List.of(office));

            List<SoftwareResponse> result = inventoryService.findAllSoftware();

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("createSoftware exitoso con auditoría")
        void crear_exitoso() {
            SoftwareRequest request = new SoftwareRequest();
            request.setNombre("Antivirus");

            when(softwareRepository.save(any(Software.class))).thenAnswer(inv -> {
                Software s = inv.getArgument(0);
                s.setId(10L);
                s.setEstado(AssetStatus.ACTIVO);
                s.setCreatedAt(LocalDateTime.now());
                return s;
            });
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            SoftwareResponse result = inventoryService.createSoftware(request, "admin");

            assertThat(result.getId()).isEqualTo(10L);
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("updateSoftware actualiza campos correctamente")
        void actualizar_exitoso() {
            SoftwareRequest request = new SoftwareRequest();
            request.setNombre("Microsoft Office 365 - Renovado");
            request.setVersion("2025");
            request.setFabricante("Microsoft");
            request.setTipoLicencia("Suscripción Anual");
            request.setCantidadLicencias(600);

            when(softwareRepository.findById(1L)).thenReturn(Optional.of(office));
            when(softwareRepository.save(any(Software.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            SoftwareResponse result = inventoryService.updateSoftware(1L, request, "admin");

            assertThat(result.getNombre()).isEqualTo("Microsoft Office 365 - Renovado");
        }

        @Test
        @DisplayName("softDeleteSoftware marca como eliminado y registra auditoría")
        void softDelete_exitoso() {
            when(softwareRepository.findById(1L)).thenReturn(Optional.of(office));
            when(softwareRepository.save(any(Software.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            inventoryService.softDeleteSoftware(1L, "admin");

            assertThat(office.getDeleted()).isTrue();
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("findSoftwareById de eliminado lanza excepción")
        void findById_eliminado() {
            Software deleted = Software.builder().id(5L).deleted(true).build();
            when(softwareRepository.findById(5L)).thenReturn(Optional.of(deleted));

            assertThatThrownBy(() -> inventoryService.findSoftwareById(5L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
