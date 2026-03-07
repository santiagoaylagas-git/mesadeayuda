package com.sojus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para Software. No expone campos internos como deleted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoftwareResponse {
    private Long id;
    private String nombre;
    private String version;
    private String fabricante;
    private String tipoLicencia;
    private String numeroLicencia;
    private Integer cantidadLicencias;
    private String fechaVencimiento;
    private String estado;
    private String observaciones;
    private String createdAt;
    private String updatedAt;
}
