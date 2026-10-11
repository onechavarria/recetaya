package cl.recetaya.inventario.controller;

import cl.recetaya.inventario.dto.ApiResponse;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de negocio: 404, 409, etc.
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

    // Maneja campos incorrectos o cantidades negativas.
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

    // Maneja registros duplicados en la base de datos.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> duplicado(
            DataIntegrityViolationException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        "Conflicto con los datos del inventario",
                        null));
    }
}
