package cl.recetaya.dispensacion.dto;

import java.time.LocalDateTime;

// Estructura común de respuestas JSON.
public record ApiResponse<T>(
        String mensaje,
        T datos,
        LocalDateTime fecha
) {
    // Agrega automáticamente la fecha actual.
    public ApiResponse(String mensaje, T datos) {
        this(mensaje, datos, LocalDateTime.now());
    }
}
