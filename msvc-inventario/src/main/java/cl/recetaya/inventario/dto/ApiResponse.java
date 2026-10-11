package cl.recetaya.inventario.dto;

import java.time.LocalDateTime;

// Formato común para los mensajes JSON de la API.
public record ApiResponse<T>(
        String mensaje,
        T datos,
        LocalDateTime fecha
) {
    // Agrega automáticamente la fecha a cada respuesta.
    public ApiResponse(String mensaje, T datos) {
        this(mensaje, datos, LocalDateTime.now());
    }
}
