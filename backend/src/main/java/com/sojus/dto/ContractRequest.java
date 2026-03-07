package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de entrada para crear o actualizar un Contrato.
 */
@Data
public class ContractRequest {

    @NotBlank(message = "El nombre del contrato es obligatorio")
    private String nombre;

    @NotBlank(message = "El proveedor es obligatorio")
    private String proveedor;

    private String numeroContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String coberturaHw;
    private String coberturaSw;
    private String slaDescripcion;
    private String observaciones;
}
