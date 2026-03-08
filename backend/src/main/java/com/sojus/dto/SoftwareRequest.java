package com.sojus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de entrada para crear o actualizar un registro de Software.
 * Evita exponer campos internos de la entidad (id, deleted, timestamps).
 */
@Data
@Schema(description = "Datos para crear o actualizar un registro de software")
public class SoftwareRequest {

    @NotBlank(message = "El nombre del software es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Schema(description = "Nombre del software", example = "Microsoft Office")
    private String nombre;

    @Size(max = 50, message = "La versión no puede superar los 50 caracteres")
    @Schema(description = "Versión del software", example = "365 v2024")
    private String version;

    @Size(max = 100, message = "El fabricante no puede superar los 100 caracteres")
    @Schema(description = "Fabricante o desarrollador", example = "Microsoft")
    private String fabricante;

    @Schema(description = "Tipo de licencia", example = "Suscripción anual")
    private String tipoLicencia;

    @Schema(description = "Número o clave de licencia", example = "XXXXX-XXXXX-XXXXX")
    private String numeroLicencia;

    @Schema(description = "Cantidad de licencias adquiridas", example = "50")
    private Integer cantidadLicencias;

    @Schema(description = "Fecha de vencimiento de la licencia", example = "2026-12-31")
    private LocalDate fechaVencimiento;

    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    @Schema(description = "Observaciones adicionales")
    private String observaciones;
}
