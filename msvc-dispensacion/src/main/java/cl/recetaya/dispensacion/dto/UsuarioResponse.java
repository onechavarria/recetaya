package cl.recetaya.dispensacion.dto;

// Respuesta recibida desde msvc-usuario.
public record UsuarioResponse(
        String mensaje,
        UsuarioDatos datos) {
    // Datos necesarios para validar al farmacéutico.
    public record UsuarioDatos(
        Long id,
        String nombre,
        String email,
        String rol,
        Boolean activo) {
    }
}
