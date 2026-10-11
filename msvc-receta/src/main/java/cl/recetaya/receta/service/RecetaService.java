package cl.recetaya.receta.service;

import cl.recetaya.receta.model.*;
import cl.recetaya.receta.dto.UsuarioResponse;
import cl.recetaya.receta.repository.RecetaRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Servicio encargado de la lógica de negocio de las recetas.
@Service
public class RecetaService {

    private final RecetaRepository repository;
    private final RestClient usuarioClient;

    // Inyecta el repositorio y configura la conexión con msvc-usuario.
    public RecetaService(
            RecetaRepository repository,
            @Value("${servicios.usuario.url}") String usuarioUrl) {

        this.repository = repository;
        this.usuarioClient = RestClient.builder()
                .baseUrl(usuarioUrl)
                .build();
    }

    // obtiene todas las recetas almacenadas.
    public List<Receta> listar() {
        return repository.findAll();
    }

    // busca una receta por ID y devuelve 404 si no existe.
    public Receta buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Receta no encontrada"));
    }

    // obtiene las recetas emitidas por un médico.
    public List<Receta> buscarPorMedico(Long medicoId) {
        return repository.findByMedicoId(medicoId);
    }

    // obtiene las recetas filtradas por estado.
    public List<Receta> buscarPorEstado(EstadoReceta estado) {
        return repository.findByEstado(estado);
    }

    // Valida mediante RestClient que el médico exista y esté activo.
    private void validarMedico(Long medicoId) {
        UsuarioResponse respuesta;

        try {
            respuesta = usuarioClient.get()
                    .uri("/api/usuarios/{id}", medicoId)
                    .retrieve()
                    .body(UsuarioResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El medico indicado no existe");

        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar msvc-usuario");
        }

        // Comprueba que Usuario haya devuelto información válida.
        if (respuesta == null || respuesta.datos() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Respuesta invalida de msvc-usuario");
        }

        var medico = respuesta.datos();

        // Verifica que el usuario tenga el rol MEDICO.
        if (!"MEDICO".equals(medico.rol())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario no tiene rol MEDICO");
        }

        // Impide emitir recetas a médicos inactivos.
        if (!Boolean.TRUE.equals(medico.activo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El medico se encuentra inactivo");
        }
    }

    // registra una receta después de validar al médico.
    public Receta guardar(Receta receta) {
        validarMedico(receta.getMedicoId());

        receta.setId(null);
        receta.setEstado(EstadoReceta.PENDIENTE_RESERVA);

        return repository.save(receta);
    }

    // actualiza únicamente recetas pendientes de reserva.
    public Receta actualizar(Long id, Receta datos) {
        Receta receta = buscar(id);

        if (receta.getEstado() != EstadoReceta.PENDIENTE_RESERVA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden editar recetas pendientes");
        }

        // Verifica al médico y actualiza los datos de la receta.
        validarMedico(datos.getMedicoId());

        receta.setMedicoId(datos.getMedicoId());
        receta.setMedicamentoId(datos.getMedicamentoId());
        receta.setSucursal(datos.getSucursal());

        return repository.save(receta);
    }

    // modifica el estado siguiendo las transiciones permitidas.
    public Receta cambiarEstado(Long id, EstadoReceta estado) {

        Receta receta = buscar(id);
        EstadoReceta actual = receta.getEstado();

        // Permite cambiar de PENDIENTE_RESERVA a RESERVADA.
        boolean reservar = actual == EstadoReceta.PENDIENTE_RESERVA
                && estado == EstadoReceta.RESERVADA;

        // Permite cambiar de RESERVADA a DISPENSADA.
        boolean dispensar = actual == EstadoReceta.RESERVADA
                && estado == EstadoReceta.DISPENSADA;

        // Rechaza las transiciones que no estén permitidas.
        if (!reservar && !dispensar) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cambio de estado no permitido");
        }

        // Guarda el nuevo estado de la receta en H2.
        receta.setEstado(estado);
        return repository.save(receta);
    }

    // elimina una receta si aún está pendiente de reserva.
    public void eliminar(Long id) {
        Receta receta = buscar(id);

        if (receta.getEstado() != EstadoReceta.PENDIENTE_RESERVA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden eliminar recetas pendientes");
        }

        repository.delete(receta);
    }
}
