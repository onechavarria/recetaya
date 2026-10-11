package cl.recetaya.receta.controller;

import cl.recetaya.receta.dto.ApiResponse;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// Maneja globalmente las excepciones de la API REST.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Devuelve el código HTTP y mensaje de los errores de negocio.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> manejarError(
            ResponseStatusException ex) {

        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApiResponse<>(
                        ex.getReason() != null
                                ? ex.getReason()
                                : "Error al procesar la solicitud",
                        null));
    }

    // Valida los campos recibidos y devuelve un error HTTP 400.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> validar(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> "Campo " + error.getField()
                        + ": " + error.getDefaultMessage())
                .orElse("Datos invalidos");

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(mensaje, null));
    }
}
