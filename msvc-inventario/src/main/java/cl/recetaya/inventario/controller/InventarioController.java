package cl.recetaya.inventario.controller;

import cl.recetaya.inventario.dto.*;
import cl.recetaya.inventario.model.Inventario;
import cl.recetaya.inventario.service.InventarioService;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventarios")
public class InventarioController {

    private final InventarioService service;

    // Inyección del servicio.
    public InventarioController(InventarioService service) {
        this.service = service;
    }

    // GET - Listar todos los inventarios.
    @GetMapping
    public ApiResponse<List<Inventario>> listar() {
        return new ApiResponse<>(
        "Inventarios consultados correctamente",
        service.listar());
    }

    // GET - Buscar un inventario por ID.
    @GetMapping("/{id}")
    public ApiResponse<Inventario> buscar(@PathVariable Long id) {
        return new ApiResponse<>(
        "Inventario encontrado correctamente",
        service.buscar(id));
    }

    // GET - Consultar medicamentos por sucursal.
    @GetMapping("/sucursal")
    public ApiResponse<List<Inventario>> porSucursal(
        @RequestParam String nombre) {

        return new ApiResponse<>(
            "Inventarios de la sucursal consultados correctamente",
            service.buscarPorSucursal(nombre));
    }

    // GET - Buscar medicamento y sucursal.
    @GetMapping("/medicamento/{medicamentoId}")
    public ApiResponse<Inventario> buscarMedicamento(
        @PathVariable Long medicamentoId,
        @RequestParam String sucursal) {

        return new ApiResponse<>(
            "Medicamento encontrado correctamente",
            service.buscarMedicamento(medicamentoId, sucursal));
    }

    // POST - Registrar un medicamento.
    @PostMapping
    public ResponseEntity<ApiResponse<Inventario>> guardar(
        @Valid @RequestBody Inventario inventario) {

        Inventario nuevo = service.guardar(inventario);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(
                    "Inventario creado correctamente",
                    nuevo));
    }

    // PUT - Actualizar un medicamento.
    @PutMapping("/{id}")
    public ApiResponse<Inventario> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody Inventario inventario) {

        return new ApiResponse<>(
            "Inventario actualizado correctamente",
            service.actualizar(id, inventario));
    }

    // PATCH - Reservar stock.
    @PatchMapping("/{id}/reservar")
    public ApiResponse<Inventario> reservar(
        @PathVariable Long id,
        @Valid @RequestBody MovimientoStockRequest request) {

        return new ApiResponse<>(
            "Stock reservado correctamente",
            service.reservar(id, request.cantidad()));
    }

    // PATCH - Liberar stock reservado.
    @PatchMapping("/{id}/liberar")
    public ApiResponse<Inventario> liberar(
        @PathVariable Long id,
        @Valid @RequestBody MovimientoStockRequest request) {

        return new ApiResponse<>(
            "Stock liberado correctamente",
            service.liberar(id, request.cantidad()));
    }

    // PATCH - Confirmar entrega del medicamento.
    @PatchMapping("/{id}/dispensar")
    public ApiResponse<Inventario> dispensar(
        @PathVariable Long id,
        @Valid @RequestBody MovimientoStockRequest request) {

        return new ApiResponse<>(
            "Medicamento dispensado correctamente",
            service.dispensar(id, request.cantidad()));
    }

    // DELETE - Eliminar un registro.
    @DeleteMapping("/{id}")
    public ApiResponse<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);

        return new ApiResponse<>(
            "Inventario eliminado correctamente",
            null);
    }
}

