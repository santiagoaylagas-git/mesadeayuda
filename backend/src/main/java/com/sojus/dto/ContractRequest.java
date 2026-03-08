package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de entrada para crear o actualizar un Contrato.
 */
@Data
@Schema(description = "Datos para crear o actualizar un contrato con proveedor")
public class ContractRequest {

    @NotBlank(message = "El nombre del contrato es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
    @Schema(description = "Nombre descriptivo del contrato", example = "Soporte HW Dell")
    private String nombre;

    @NotBlank(message = "El proveedor es obligatorio")
    @Size(max = 150, message = "El proveedor no puede superar los 150 caracteres")
    @Schema(description = "Nombre del proveedor", example = "Dell Argentina S.A.")
    private String proveedor;

    @Schema(description = "Número de contrato", example = "CNT-2024-001")
    private String numeroContrato;

    @Schema(description = "Fecha de inicio del contrato", example = "2024-01-01")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de fin del contrato", example = "2026-12-31")
    private LocalDate fechaFin;

    @Schema(description = "Cobertura de hardware")
    private String coberturaHw;

    @Schema(description = "Cobertura de software")
    private String coberturaSw;

    @Schema(description = "Descripción del SLA")
    private String slaDescripcion;

    @Schema(description = "Observaciones adicionales")
    private String observaciones;
}
