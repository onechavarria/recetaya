package cl.recetaya.usuario.controller;

import cl.recetaya.usuario.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores 404, 409 y otros definidos en el Service
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> manejarError(
            ResponseStatusException ex) {

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ApiResponse<>(
                        ex.getReason() != null
                                ? ex.getReason()
                                : "No se pudo completar la operación",
                        null));
    }

    // Errores de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> "Campo " + error.getField()
                        + ": " + error.getDefaultMessage())
                .orElse("Datos inválidos");

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(mensaje, null));
    }
}
