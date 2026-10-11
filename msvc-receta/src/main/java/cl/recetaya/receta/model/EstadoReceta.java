package cl.recetaya.receta.model;

// Estados disponibles para gestionar el ciclo de vida de una receta.
public enum EstadoReceta {
    PENDIENTE_RESERVA,
    RESERVADA,
    SIN_STOCK,
    DISPENSADA
}
