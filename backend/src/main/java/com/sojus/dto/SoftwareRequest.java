package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de entrada para crear o actualizar un registro de Software.
 */
@Data
public class SoftwareRequest {

    @NotBlank(message = "El nombre del software es obligatorio")
    private String nombre;

    private String version;
    private String fabricante;
    private String tipoLicencia;
    private String numeroLicencia;
    private Integer cantidadLicencias;
    private LocalDate fechaVencimiento;
    private String observaciones;
}
