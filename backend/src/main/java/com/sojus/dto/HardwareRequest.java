package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de entrada para crear o actualizar un equipo de Hardware.
 * Evita exponer campos internos de la entidad (id, deleted, timestamps).
 */
@Data
public class HardwareRequest {

    @NotBlank(message = "El número de inventario patrimonial es obligatorio")
    private String inventarioPatrimonial;

    private String numeroSerie;

    @NotBlank(message = "La clase del equipo es obligatoria (PC, Servidor, Impresora...)")
    private String clase;

    private String tipo;
    private String marca;
    private String modelo;
    private String estado; // ACTIVO, EN_REPARACION, DE_BAJA, EN_DEPOSITO
    private Long juzgadoId;
    private String ubicacionFisica;
    private String observaciones;
}
