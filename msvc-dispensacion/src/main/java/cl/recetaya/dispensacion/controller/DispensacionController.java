package cl.recetaya.dispensacion.controller;

import cl.recetaya.dispensacion.dto.*;
import cl.recetaya.dispensacion.model.Dispensacion;
import cl.recetaya.dispensacion.service.DispensacionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispensaciones")
public class DispensacionController {

    private final DispensacionService service;

    // Inyección del servicio por constructor.
    public DispensacionController(DispensacionService service) {
        this.service = service;
    }

    // GET: obtiene todas las dispensaciones.
    @GetMapping
    public ApiResponse<List<Dispensacion>> listar() {
        return new ApiResponse<>(
                "Dispensaciones consultadas correctamente",
                service.listar());
    }

    // GET: consulta una dispensación por su ID.
    @GetMapping("/{id}")
    public ApiResponse<Dispensacion> buscar(
            @PathVariable Long id) {

        return new ApiResponse<>(
                "Dispensacion encontrada correctamente",
                service.buscar(id));
    }

    // GET: consulta la entrega asociada a una receta.
    @GetMapping("/receta/{recetaId}")
    public ApiResponse<Dispensacion> porReceta(
            @PathVariable Long recetaId) {

        return new ApiResponse<>(
                "Dispensacion de receta consultada correctamente",
                service.buscarPorReceta(recetaId));
    }

    // GET: consulta entregas realizadas por un farmacéutico.
    @GetMapping("/farmaceutico/{id}")
    public ApiResponse<List<Dispensacion>> porFarmaceutico(
            @PathVariable Long id) {

        return new ApiResponse<>(
                "Dispensaciones del farmaceutico consultadas",
                service.porFarmaceutico(id));
    }

    // POST: registra la dispensación de una receta.
    @PostMapping
    public ResponseEntity<ApiResponse<Dispensacion>> guardar(
            @Valid @RequestBody CrearDispensacionRequest request) {

        Dispensacion nueva = service.guardar(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "Medicamento dispensado correctamente",
                        nueva));
    }

    // PUT: actualiza el farmacéutico responsable.
    @PutMapping("/{id}")
    public ApiResponse<Dispensacion> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarFarmaceuticoRequest request) {

        return new ApiResponse<>(
                "Dispensacion actualizada correctamente",
                service.actualizar(id, request.farmaceuticoId()));
    }

    // DELETE: elimina el registro local.
    @DeleteMapping("/{id}")
    public ApiResponse<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);

        return new ApiResponse<>(
                "Registro de dispensacion eliminado correctamente",
                null);
    }

    // DTO para actualizar al farmacéutico.
    public record ActualizarFarmaceuticoRequest(
            @NotNull Long farmaceuticoId) {
    }
}
