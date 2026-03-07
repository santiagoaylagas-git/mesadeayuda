package com.sojus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para Contrato.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractResponse {
    private Long id;
    private String nombre;
    private String proveedor;
    private String numeroContrato;
    private String fechaInicio;
    private String fechaFin;
    private String coberturaHw;
    private String coberturaSw;
    private String slaDescripcion;
    private String observaciones;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
}
