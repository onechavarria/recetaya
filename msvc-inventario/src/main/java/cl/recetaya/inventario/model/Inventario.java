package cl.recetaya.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

// Entidad JPA que representa el inventario de medicamentos.
@Entity
@Table(name = "inventarios", uniqueConstraints = @UniqueConstraint(columnNames = { "medicamento_id", "sucursal" }))
public class Inventario {

    // Identificador autogenerado.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador usado por el microservicio Receta.
    @NotNull
    @Column(name = "medicamento_id", nullable = false)
    private Long medicamentoId;

    // Nombre descriptivo del medicamento.
    @NotBlank
    @Column(nullable = false)
    private String medicamento;

    // Sucursal donde se encuentra el medicamento.
    @NotBlank
    @Column(nullable = false)
    private String sucursal;

    // Unidades disponibles para nuevas reservas.
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stockDisponible;

    // Unidades reservadas para recetas.
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stockReservado = 0;

    // Constructor requerido por JPA.
    public Inventario() {
    }

    // Getters y setters de los atributos.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Integer getStockReservado() {
        return stockReservado;
    }

    public void setStockReservado(Integer stockReservado) {
        this.stockReservado = stockReservado;
    }
}
