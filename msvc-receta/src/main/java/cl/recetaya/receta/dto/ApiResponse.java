package cl.recetaya.receta.dto;

import java.time.LocalDateTime;

// DTO genérico para estandarizar las respuestas REST.
public record ApiResponse<T>(
        String mensaje,
        T datos,
        LocalDateTime fecha) {

    // Agrega automáticamente la fecha y hora de la respuesta.
    public ApiResponse(String mensaje, T datos) {
        this(mensaje, datos, LocalDateTime.now());
    }
}
