package cl.recetaya.dispensacion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

// Entidad JPA para registrar entregas de medicamentos.
@Entity
@Table(name = "dispensaciones", uniqueConstraints = @UniqueConstraint(columnNames = "receta_id"))
public class Dispensacion {

    // Identificador autogenerado.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Receta dispensada. No permite duplicados.
    @NotNull
    @Column(name = "receta_id", nullable = false, unique = true)
    private Long recetaId;

    // Farmacéutico que entrega el medicamento.
    @NotNull
    @Column(nullable = false)
    private Long farmaceuticoId;

    // Fecha y hora de la entrega.
    @Column(nullable = false)
    private LocalDateTime fecha;

    // Estado de la dispensación.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDispensacion estado;

    // Constructor requerido por JPA.
    public Dispensacion() {
    }

    // Métodos de acceso a los atributos.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }

    public Long getFarmaceuticoId() {
        return farmaceuticoId;
    }

    public void setFarmaceuticoId(Long farmaceuticoId) {
        this.farmaceuticoId = farmaceuticoId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoDispensacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoDispensacion estado) {
        this.estado = estado;
    }
}
