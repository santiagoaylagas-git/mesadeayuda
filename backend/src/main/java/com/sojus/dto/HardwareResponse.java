package com.sojus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para Hardware. No expone campos internos como deleted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HardwareResponse {
    private Long id;
    private String inventarioPatrimonial;
    private String numeroSerie;
    private String clase;
    private String tipo;
    private String marca;
    private String modelo;
    private String estado;
    private String juzgadoNombre;
    private String ubicacionFisica;
    private String observaciones;
    private String createdAt;
    private String updatedAt;
}
