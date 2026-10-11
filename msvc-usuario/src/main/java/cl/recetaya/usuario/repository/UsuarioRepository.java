package cl.recetaya.usuario.repository;

import cl.recetaya.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repositorio encargado del acceso a los datos de Usuario.
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    // Busca un usuario por su correo electrónico.
    // Optional permite manejar resultados inexistentes.
    Optional<Usuario> findByEmail(String email);
}
