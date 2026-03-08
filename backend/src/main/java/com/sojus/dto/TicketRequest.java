package com.sojus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos para crear un nuevo ticket de soporte")
public class TicketRequest {
    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede superar los 200 caracteres")
    @Schema(description = "Asunto breve del ticket", example = "PC no enciende en Secretaría")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    @Schema(description = "Descripción detallada del problema")
    private String descripcion;

    @Schema(description = "Nivel de prioridad", example = "MEDIA", allowableValues = { "ALTA", "MEDIA", "BAJA" })
    private String prioridad; // ALTA, MEDIA, BAJA

    @NotNull(message = "El juzgado es obligatorio")
    @Schema(description = "ID del juzgado solicitante", example = "1")
    private Long juzgadoId;

    @Schema(description = "ID del hardware afectado (opcional)")
    private Long hardwareId;

    @Schema(description = "Canal de ingreso", example = "WEB", allowableValues = { "WEB", "PORTAL", "EMAIL" })
    private String canal; // WEB, PORTAL, EMAIL
}
