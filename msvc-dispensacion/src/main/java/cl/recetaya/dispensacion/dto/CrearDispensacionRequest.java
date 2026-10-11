package cl.recetaya.dispensacion.dto;

import jakarta.validation.constraints.*;

// Datos necesarios para registrar una dispensación.
public record CrearDispensacionRequest(

    @NotNull Long recetaId,

    @NotNull Long farmaceuticoId) {
}