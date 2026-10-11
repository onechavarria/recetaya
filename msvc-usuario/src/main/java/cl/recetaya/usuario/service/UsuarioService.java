package cl.recetaya.usuario.service;

import cl.recetaya.usuario.model.Usuario;
import cl.recetaya.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Identifica esta clase como un servicio de Spring Boot.
@Service
public class UsuarioService {

    // Repositorio utilizado para acceder a H2 Database.
    private final UsuarioRepository repository;

    // Inyección de dependencias mediante el constructor.
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    // obtiene la lista de todos los usuarios registrados.
    public List<Usuario> listar() {
        return repository.findAll();
    }

    // busca un usuario por su identificador.
    public Usuario buscar(Long id) {

        // Devuelve 404 cuando el usuario no existe.
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuario no encontrado"));
    }

    // registra un nuevo usuario en la base de datos.
    public Usuario guardar(Usuario usuario) {

        usuario.setId(null);

        // Activa al usuario si no se especificó su estado.
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        // Impide registrar un correo electrónico duplicado.
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado");
        }

        // Guarda y devuelve el usuario registrado.
        return repository.save(usuario);
    }

    // actualiza los datos de un usuario existente.
    public Usuario actualizar(Long id, Usuario datos) {

        // Comprueba que el usuario exista antes de actualizar.
        Usuario usuario = buscar(id);

        // Verifica que el correo no pertenezca a otro usuario.
        repository.findByEmail(datos.getEmail())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "El correo ya está registrado");
                });

        // Actualiza los atributos con los nuevos datos recibidos.
        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        usuario.setRol(datos.getRol());
        usuario.setActivo(datos.getActivo());

        // Persiste los cambios y devuelve el usuario actualizado.
        return repository.save(usuario);
    }

    // elimina un usuario mediante su identificador.
    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        repository.delete(usuario);
    }
}
