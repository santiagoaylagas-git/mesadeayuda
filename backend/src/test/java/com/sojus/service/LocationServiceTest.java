package com.sojus.service;

import com.sojus.domain.entity.Circunscripcion;
import com.sojus.domain.entity.Juzgado;
import com.sojus.repository.CircunscripcionRepository;
import com.sojus.repository.JuzgadoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationService — Tests Unitarios")
class LocationServiceTest {

    @Mock
    private CircunscripcionRepository circunscripcionRepository;
    @Mock
    private JuzgadoRepository juzgadoRepository;

    @InjectMocks
    private LocationService locationService;

    @Test
    @DisplayName("findAllCircunscripciones devuelve lista completa")
    void findAllCircunscripciones_devuelveTodas() {
        Circunscripcion c1 = Circunscripcion.builder().id(1L).nombre("Primera").codigo("C-001").build();
        Circunscripcion c2 = Circunscripcion.builder().id(2L).nombre("Segunda").codigo("C-002").build();

        when(circunscripcionRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Circunscripcion> result = locationService.findAllCircunscripciones();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("Primera");
        verify(circunscripcionRepository).findAll();
    }

    @Test
    @DisplayName("findAllCircunscripciones devuelve lista vacía si no hay datos")
    void findAllCircunscripciones_sinDatos() {
        when(circunscripcionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Circunscripcion> result = locationService.findAllCircunscripciones();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllJuzgados devuelve solo juzgados activos")
    void findAllJuzgados_soloActivos() {
        Juzgado j1 = Juzgado.builder().id(1L).nombre("Juzgado Civil N°1").active(true).build();

        when(juzgadoRepository.findAllByActiveTrue()).thenReturn(List.of(j1));

        List<Juzgado> result = locationService.findAllJuzgados();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Juzgado Civil N°1");
        verify(juzgadoRepository).findAllByActiveTrue();
    }

    @Test
    @DisplayName("findAllJuzgados devuelve lista vacía si no hay activos")
    void findAllJuzgados_sinActivos() {
        when(juzgadoRepository.findAllByActiveTrue()).thenReturn(Collections.emptyList());

        List<Juzgado> result = locationService.findAllJuzgados();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findJuzgadosByEdificio devuelve juzgados del edificio indicado")
    void findJuzgadosByEdificio_conResultados() {
        Juzgado j1 = Juzgado.builder().id(1L).nombre("Juzgado Penal N°3").build();

        when(juzgadoRepository.findAllByEdificioId(2L)).thenReturn(List.of(j1));

        List<Juzgado> result = locationService.findJuzgadosByEdificio(2L);

        assertThat(result).hasSize(1);
        verify(juzgadoRepository).findAllByEdificioId(2L);
    }

    @Test
    @DisplayName("findJuzgadosByEdificio devuelve lista vacía si el edificio no tiene juzgados")
    void findJuzgadosByEdificio_sinResultados() {
        when(juzgadoRepository.findAllByEdificioId(999L)).thenReturn(Collections.emptyList());

        List<Juzgado> result = locationService.findJuzgadosByEdificio(999L);

        assertThat(result).isEmpty();
    }
}
