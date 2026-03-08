package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO de entrada para crear o actualizar un equipo de Hardware.
 * Evita exponer campos internos de la entidad (id, deleted, timestamps).
 */
@Data
@Schema(description = "Datos para crear o actualizar un equipo de hardware")
public class HardwareRequest {

        @NotBlank(message = "El número de inventario patrimonial es obligatorio")
        @Schema(description = "Número de inventario patrimonial único", example = "INV-001-0001")
        private String inventarioPatrimonial;

        @Schema(description = "Número de serie del equipo", example = "SN-DELL-001")
        private String numeroSerie;

        @NotBlank(message = "La clase del equipo es obligatoria (PC, Servidor, Impresora...)")
        @Schema(description = "Clase del equipo", example = "PC", allowableValues = { "PC", "Servidor", "Impresora",
                        "Monitor", "Red" })
        private String clase;

        @Schema(description = "Tipo específico", example = "Desktop")
        private String tipo;
        @Schema(description = "Marca del equipo", example = "Dell")
        private String marca;
        @Schema(description = "Modelo del equipo", example = "OptiPlex 7090")
        private String modelo;
        @Schema(description = "Estado del equipo", example = "ACTIVO", allowableValues = { "ACTIVO", "EN_REPARACION",
                        "DE_BAJA", "EN_DEPOSITO" })
        private String estado; // ACTIVO, EN_REPARACION, DE_BAJA, EN_DEPOSITO
        @Schema(description = "ID del juzgado donde se encuentra")
        private Long juzgadoId;
        @Schema(description = "Ubicación física dentro del edificio", example = "Puesto Secretario")
        private String ubicacionFisica;
        @Schema(description = "Observaciones adicionales")
        private String observaciones;
}
