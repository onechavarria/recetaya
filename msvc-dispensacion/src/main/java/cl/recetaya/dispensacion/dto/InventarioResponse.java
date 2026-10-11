package cl.recetaya.dispensacion.dto;

// Respuesta recibida desde msvc-inventario.
public record InventarioResponse(
    String mensaje,
    InventarioDatos datos) {
// Permite comprobar stock reservado.
    public record InventarioDatos(
        Long id,
        Long medicamentoId,
        String medicamento,
        String sucursal,
        Integer stockDisponible,
        Integer stockReservado) {
    }
}
