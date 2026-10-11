package cl.recetaya.receta.controller;

import cl.recetaya.receta.dto.ApiResponse;
import cl.recetaya.receta.model.*;
import cl.recetaya.receta.service.RecetaService;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST que expone los endpoints de recetas.
@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService service;

    // Inyección de dependencias mediante constructor.
    public RecetaController(RecetaService service) {
        this.service = service;
    }

    // GET: obtiene todas las recetas registradas.
    @GetMapping
    public ResponseEntity<ApiResponse<List<Receta>>> listar() {
        return ResponseEntity.ok(new ApiResponse<>(
                "Recetas consultadas correctamente",
                service.listar()));
    }

    // GET: busca una receta mediante su identificador.
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Receta>> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(new ApiResponse<>(
                "Receta encontrada correctamente",
                service.buscar(id)));
    }

    // GET: consulta todas las recetas emitidas por un médico.
    @GetMapping("/medico/{id}")
    public ResponseEntity<ApiResponse<List<Receta>>> porMedico(
            @PathVariable Long id) {

        return ResponseEntity.ok(new ApiResponse<>(
                "Recetas del medico consultadas correctamente",
                service.buscarPorMedico(id)));
    }

    // GET: filtra las recetas según su estado.
    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<List<Receta>>> porEstado(
            @PathVariable EstadoReceta estado) {

        return ResponseEntity.ok(new ApiResponse<>(
                "Recetas filtradas correctamente",
                service.buscarPorEstado(estado)));
    }

    // POST: registra una nueva receta y devuelve HTTP 201.
    @PostMapping
    public ResponseEntity<ApiResponse<Receta>> guardar(
            @Valid @RequestBody Receta receta) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "Receta creada correctamente",
                        service.guardar(receta)));
    }

    // PUT: actualiza los datos de una receta existente.
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Receta>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Receta receta) {

        return ResponseEntity.ok(new ApiResponse<>(
                "Receta actualizada correctamente",
                service.actualizar(id, receta)));
    }

    // PATCH: modifica el estado de una receta.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Receta>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoReceta estado) {

        return ResponseEntity.ok(new ApiResponse<>(
                "Estado de receta actualizado correctamente",
                service.cambiarEstado(id, estado)));
    }

    // DELETE: elimina una receta y confirma la operación.
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.ok(new ApiResponse<>(
                "Receta eliminada correctamente",
                null));
    }
}
