package cl.recetaya.dispensacion.dto;

// Respuesta recibida desde msvc-receta.
public record RecetaResponse(
    String mensaje,
    RecetaDatos datos) {
    // Información necesaria para dispensar la receta.
    public record RecetaDatos(
        Long id,
        Long medicoId,
        Long pacienteId,
        Long medicamentoId,
        String sucursal,
        String estado) {
    }
}
