package com.sojus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO de entrada para cambiar el estado de un ticket.
 * Incluye validación de transiciones y asignación de técnico.
 */
@Data
@Schema(description = "Datos para cambiar el estado de un ticket de soporte")
public class StatusChangeRequest {
    @NotBlank(message = "El nuevo estado es obligatorio")
    @Pattern(regexp = "SOLICITADO|ASIGNADO|EN_CURSO|CERRADO", message = "Estado inválido. Valores permitidos: SOLICITADO, ASIGNADO, EN_CURSO, CERRADO")
    @Schema(description = "Nuevo estado del ticket", example = "ASIGNADO", allowableValues = { "SOLICITADO", "ASIGNADO",
            "EN_CURSO", "CERRADO" })
    private String status;

    @Schema(description = "ID del técnico a asignar (obligatorio si el estado es ASIGNADO)", example = "3")
    private Long tecnicoId;

    @Schema(description = "Comentario para la bitácora del ticket", example = "Asignado a técnico de guardia")
    private String comentario;
}
