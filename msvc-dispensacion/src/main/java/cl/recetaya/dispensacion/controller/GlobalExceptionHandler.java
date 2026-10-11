package cl.recetaya.dispensacion.controller;

import cl.recetaya.dispensacion.dto.ApiResponse;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Responde errores controlados de los servicios.
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

    // Responde errores de campos inválidos.
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

    // Responde conflictos por registros duplicados.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> duplicado(
            DataIntegrityViolationException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        "La dispensacion ya existe o presenta un conflicto",
                        null));
    }
}
