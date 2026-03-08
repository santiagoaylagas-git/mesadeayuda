package com.sojus.service;

import com.sojus.dto.DashboardStats;
import com.sojus.domain.enums.Priority;
import com.sojus.domain.enums.TicketStatus;
import com.sojus.repository.ContractRepository;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.SoftwareRepository;
import com.sojus.repository.TicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService — Tests Unitarios")
class DashboardServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private HardwareRepository hardwareRepository;
    @Mock
    private SoftwareRepository softwareRepository;
    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("getStats devuelve métricas correctas del dashboard")
    void getStats_metricsCorrection() {
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.SOLICITADO)).thenReturn(5L);
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.ASIGNADO)).thenReturn(3L);
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.EN_CURSO)).thenReturn(2L);
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.CERRADO)).thenReturn(10L);
        when(ticketRepository.countByPrioridadAndStatusNotAndDeletedFalse(
                eq(Priority.ALTA), eq(TicketStatus.CERRADO))).thenReturn(4L);
        when(hardwareRepository.countByDeletedFalse()).thenReturn(50L);
        when(softwareRepository.countByDeletedFalse()).thenReturn(30L);
        when(contractRepository.countByActiveTrue()).thenReturn(8L);
        when(contractRepository.countByFechaFinBeforeAndActiveTrue(any(LocalDate.class))).thenReturn(2L);

        DashboardStats stats = dashboardService.getStats();

        assertThat(stats).isNotNull();
        assertThat(stats.getTicketsAbiertos()).isEqualTo(10L); // 5+3+2
        assertThat(stats.getTicketsCerradosMes()).isEqualTo(10L);
        assertThat(stats.getTicketsPrioridadAlta()).isEqualTo(4L);
        assertThat(stats.getTotalHardware()).isEqualTo(50L);
        assertThat(stats.getTotalSoftware()).isEqualTo(30L);
        assertThat(stats.getContratosVigentes()).isEqualTo(8L);
        assertThat(stats.getContratosProximosVencer()).isEqualTo(2L);
    }

    @Test
    @DisplayName("getStats devuelve ceros cuando no hay datos")
    void getStats_sinDatos() {
        when(ticketRepository.countByStatusAndDeletedFalse(any())).thenReturn(0L);
        when(ticketRepository.countByPrioridadAndStatusNotAndDeletedFalse(any(), any())).thenReturn(0L);
        when(hardwareRepository.countByDeletedFalse()).thenReturn(0L);
        when(softwareRepository.countByDeletedFalse()).thenReturn(0L);
        when(contractRepository.countByActiveTrue()).thenReturn(0L);
        when(contractRepository.countByFechaFinBeforeAndActiveTrue(any())).thenReturn(0L);

        DashboardStats stats = dashboardService.getStats();

        assertThat(stats.getTicketsAbiertos()).isZero();
        assertThat(stats.getTotalHardware()).isZero();
        assertThat(stats.getTotalSoftware()).isZero();
    }

    @Test
    @DisplayName("getStats calcula tickets abiertos como suma de SOLICITADO + ASIGNADO + EN_CURSO")
    void getStats_ticketsAbiertosCalculoCompuesto() {
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.SOLICITADO)).thenReturn(10L);
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.ASIGNADO)).thenReturn(5L);
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.EN_CURSO)).thenReturn(3L);
        when(ticketRepository.countByStatusAndDeletedFalse(TicketStatus.CERRADO)).thenReturn(0L);
        when(ticketRepository.countByPrioridadAndStatusNotAndDeletedFalse(any(), any())).thenReturn(0L);
        when(hardwareRepository.countByDeletedFalse()).thenReturn(0L);
        when(softwareRepository.countByDeletedFalse()).thenReturn(0L);
        when(contractRepository.countByActiveTrue()).thenReturn(0L);
        when(contractRepository.countByFechaFinBeforeAndActiveTrue(any())).thenReturn(0L);

        DashboardStats stats = dashboardService.getStats();

        assertThat(stats.getTicketsAbiertos()).isEqualTo(18L);
    }
}
