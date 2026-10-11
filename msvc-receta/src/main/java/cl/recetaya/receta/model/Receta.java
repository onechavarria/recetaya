package cl.recetaya.receta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

// Entidad JPA que representa una receta médica.
@Entity
@Table(name = "recetas")
public class Receta {

    // Identificador autogenerado de la receta.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificadores del médico, paciente y medicamento.
    @NotNull
    private Long medicoId;



    @NotNull
    private Long medicamentoId;

    // Sucursal donde se dispensará el medicamento.
    @NotBlank
    private String sucursal;

    // Estado inicial de la receta, almacenado como texto.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReceta estado = EstadoReceta.PENDIENTE_RESERVA;

    // Constructor vacío requerido por JPA.
    public Receta() {
    }

    // Getters y setters para acceder y modificar los atributos.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }



    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public EstadoReceta getEstado() {
        return estado;
    }

    public void setEstado(EstadoReceta estado) {
        this.estado = estado;
    }
}
