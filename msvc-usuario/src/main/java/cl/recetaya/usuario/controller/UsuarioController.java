
package cl.recetaya.usuario.controller;

import cl.recetaya.usuario.dto.ApiResponse;
import cl.recetaya.usuario.model.Usuario;
import cl.recetaya.usuario.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // GET - Listar usuarios
    @GetMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> listar() {
        List<Usuario> usuarios = service.listar();

        return ResponseEntity.ok(
            new ApiResponse<>(
                "Usuarios consultados correctamente",
                usuarios));
    }

    // GET - Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> buscar(
        @PathVariable Long id) {

        Usuario usuario = service.buscar(id);

        return ResponseEntity.ok(
            new ApiResponse<>(
            "Usuario encontrado correctamente",
            usuario));
    }

    // POST - Crear usuario
    @PostMapping
    public ResponseEntity<ApiResponse<Usuario>> guardar(
            @Valid @RequestBody Usuario usuario) {

        Usuario nuevo = service.guardar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(
            "Usuario creado correctamente",
            nuevo));
    }

    // PUT - Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        Usuario actualizado = service.actualizar(id, usuario);

        return ResponseEntity.ok(
            new ApiResponse<>(
                "Usuario actualizado correctamente",
                actualizado));
    }

    // DELETE - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
        @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.ok(
            new ApiResponse<>(
                "Usuario eliminado correctamente",
                null));
    }
}
