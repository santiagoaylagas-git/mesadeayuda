package com.sojus.service;

import com.sojus.domain.entity.Contract;
import com.sojus.dto.ContractRequest;
import com.sojus.dto.ContractResponse;
import com.sojus.domain.entity.AuditLog;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.AuditLogRepository;
import com.sojus.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractService — Tests Unitarios")
@SuppressWarnings("null")
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private ContractService contractService;

    private Contract contratoActivo;
    private Contract contratoPorVencer;

    @BeforeEach
    void setUp() {
        contratoActivo = Contract.builder()
                .id(1L).nombre("Soporte HW Dell").proveedor("Dell Argentina")
                .numeroContrato("CNT-2024-001")
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2027, 12, 31))
                .coberturaHw("PCs y Servidores Dell")
                .slaDescripcion("Respuesta 4hs hábiles")
                .active(true).createdAt(LocalDateTime.now())
                .build();

        contratoPorVencer = Contract.builder()
                .id(2L).nombre("Licencias Microsoft").proveedor("Microsoft")
                .fechaFin(LocalDate.now().plusDays(15))
                .active(true).createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("CRUD de Contratos")
    class CrudContratos {

        @Test
        @DisplayName("findAll devuelve solo contratos activos como DTOs")
        void findAll_activos() {
            when(contractRepository.findAllByActiveTrue()).thenReturn(List.of(contratoActivo));

            List<ContractResponse> result = contractService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Soporte HW Dell");
        }

        @Test
        @DisplayName("findById devuelve contrato activo como DTO")
        void findById_exitoso() {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contratoActivo));

            ContractResponse result = contractService.findById(1L);

            assertThat(result.getNombre()).isEqualTo("Soporte HW Dell");
        }

        @Test
        @DisplayName("findById de contrato inactivo lanza excepción")
        void findById_inactivo() {
            Contract inactivo = Contract.builder().id(3L).active(false).build();
            when(contractRepository.findById(3L)).thenReturn(Optional.of(inactivo));

            assertThatThrownBy(() -> contractService.findById(3L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Debe crear contrato exitosamente con auditoría")
        void crear_exitoso() {
            ContractRequest request = new ContractRequest();
            request.setNombre("Nuevo Contrato");
            request.setProveedor("Proveedor X");
            request.setFechaInicio(LocalDate.of(2026, 1, 1));
            request.setFechaFin(LocalDate.of(2027, 12, 31));

            when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
                Contract c = inv.getArgument(0);
                c.setId(10L);
                c.setCreatedAt(LocalDateTime.now());
                return c;
            });
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            ContractResponse result = contractService.create(request, "admin");

            assertThat(result.getId()).isEqualTo(10L);
            verify(contractRepository).save(any(Contract.class));
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Debe actualizar campos del contrato con auditoría")
        void actualizar_exitoso() {
            ContractRequest request = new ContractRequest();
            request.setNombre("Soporte HW Dell - Renovado");
            request.setProveedor("Dell Argentina S.A.");
            request.setFechaFin(LocalDate.of(2028, 12, 31));

            when(contractRepository.findById(1L)).thenReturn(Optional.of(contratoActivo));
            when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            ContractResponse result = contractService.update(1L, request, "admin");

            assertThat(result.getNombre()).isEqualTo("Soporte HW Dell - Renovado");
            verify(auditLogRepository).save(any(AuditLog.class));
        }
    }

    @Nested
    @DisplayName("Desactivación y Vencimiento")
    class DesactivacionVencimiento {

        @Test
        @DisplayName("deactivate marca contrato como inactivo con auditoría")
        void desactivar_exitoso() {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contratoActivo));
            when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

            contractService.deactivate(1L, "admin");

            assertThat(contratoActivo.getActive()).isFalse();
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("findExpiringSoon devuelve contratos próximos a vencer como DTOs")
        void findExpiringSoon() {
            when(contractRepository.findExpiringBefore(any(LocalDate.class)))
                    .thenReturn(List.of(contratoPorVencer));

            List<ContractResponse> result = contractService.findExpiringSoon(30);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Licencias Microsoft");
        }

        @Test
        @DisplayName("findExpiringSoon con 0 días no devuelve contratos futuros")
        void findExpiringSoon_sinResultados() {
            when(contractRepository.findExpiringBefore(any(LocalDate.class)))
                    .thenReturn(List.of());

            List<ContractResponse> result = contractService.findExpiringSoon(0);

            assertThat(result).isEmpty();
        }
    }
}
