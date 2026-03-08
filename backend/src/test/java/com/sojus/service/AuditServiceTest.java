package com.sojus.service;

import com.sojus.domain.entity.AuditLog;
import com.sojus.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditService — Tests Unitarios")
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    @DisplayName("findRecent devuelve los últimos 100 registros ordenados por timestamp")
    void findRecent_devuelveRegistros() {
        AuditLog log1 = AuditLog.builder()
                .id(1L).entityName("Ticket").entityId(1L).action("CREAR")
                .username("admin").timestamp(LocalDateTime.now())
                .build();
        AuditLog log2 = AuditLog.builder()
                .id(2L).entityName("Hardware").entityId(5L).action("ACTUALIZAR")
                .username("tecnico").timestamp(LocalDateTime.now().minusHours(1))
                .build();

        when(auditLogRepository.findTop100ByOrderByTimestampDesc())
                .thenReturn(List.of(log1, log2));

        List<AuditLog> result = auditService.findRecent();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEntityName()).isEqualTo("Ticket");
        verify(auditLogRepository).findTop100ByOrderByTimestampDesc();
    }

    @Test
    @DisplayName("findRecent devuelve lista vacía cuando no hay registros")
    void findRecent_sinRegistros() {
        when(auditLogRepository.findTop100ByOrderByTimestampDesc())
                .thenReturn(Collections.emptyList());

        List<AuditLog> result = auditService.findRecent();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByEntity devuelve historial de una entidad específica")
    void findByEntity_conResultados() {
        AuditLog log1 = AuditLog.builder()
                .id(1L).entityName("Ticket").entityId(1L).action("CREAR")
                .username("operador").timestamp(LocalDateTime.now())
                .build();
        AuditLog log2 = AuditLog.builder()
                .id(2L).entityName("Ticket").entityId(1L).action("CAMBIO_ESTADO")
                .username("admin").field("status").oldValue("SOLICITADO").newValue("ASIGNADO")
                .timestamp(LocalDateTime.now().minusMinutes(30))
                .build();

        when(auditLogRepository.findAllByEntityNameAndEntityIdOrderByTimestampDesc("Ticket", 1L))
                .thenReturn(List.of(log1, log2));

        List<AuditLog> result = auditService.findByEntity("Ticket", 1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(1).getAction()).isEqualTo("CAMBIO_ESTADO");
        verify(auditLogRepository).findAllByEntityNameAndEntityIdOrderByTimestampDesc("Ticket", 1L);
    }

    @Test
    @DisplayName("findByEntity devuelve lista vacía para entidad sin historial")
    void findByEntity_sinResultados() {
        when(auditLogRepository.findAllByEntityNameAndEntityIdOrderByTimestampDesc("Unknown", 999L))
                .thenReturn(Collections.emptyList());

        List<AuditLog> result = auditService.findByEntity("Unknown", 999L);

        assertThat(result).isEmpty();
    }
}
