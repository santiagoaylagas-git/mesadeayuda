package com.sojus.repository;

import com.sojus.domain.entity.Circunscripcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.lang.NonNull;

import java.util.List;

public interface CircunscripcionRepository extends JpaRepository<Circunscripcion, Long> {

    @Override
    @EntityGraph(attributePaths = { "distritos", "distritos.edificios", "distritos.edificios.juzgados" })
    @NonNull
    List<Circunscripcion> findAll();
}
