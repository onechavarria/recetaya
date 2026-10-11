package cl.recetaya.usuario.dto;

import java.time.LocalDateTime;

// DTO genérico para estandarizar las respuestas de la API REST.
public record ApiResponse<T>(
        String mensaje,
        T datos,
        LocalDateTime fecha) {

    // Constructor que asigna automáticamente la fecha y hora actual.
    public ApiResponse(String mensaje, T datos) {
        this(mensaje, datos, LocalDateTime.now());
    }
}
