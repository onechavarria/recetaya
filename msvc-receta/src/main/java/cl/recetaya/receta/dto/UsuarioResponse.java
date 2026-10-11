package cl.recetaya.receta.dto;

// Representa la respuesta JSON del microservicio Usuario.
public record UsuarioResponse(
        String mensaje,
        UsuarioDatos datos) {

    // Contiene la información necesaria para validar al médico.
    public record UsuarioDatos(
            Long id,
            String nombre,
            String email,
            String rol,
            Boolean activo) {
    }
}
