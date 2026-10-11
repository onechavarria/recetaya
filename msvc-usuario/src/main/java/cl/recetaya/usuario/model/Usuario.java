package cl.recetaya.usuario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


// Lombok genera automáticamente getters y setters.
// Entidad JPA que representa la tabla de usuarios.
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    // Identificador único generado automáticamente.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre obligatorio del usuario.
    @NotBlank
    @Column(nullable = false)
    private String nombre;

    // Correo obligatorio, válido y único en la base de datos.
    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    // Rol obligatorio almacenado como texto.
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Indica si el usuario está activo; por defecto es true.
    @Column(nullable = false)
    private Boolean activo = true;
}
