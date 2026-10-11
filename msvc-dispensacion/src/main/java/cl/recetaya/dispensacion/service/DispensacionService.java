package cl.recetaya.dispensacion.service;

import cl.recetaya.dispensacion.dto.*;
import cl.recetaya.dispensacion.model.*;
import cl.recetaya.dispensacion.repository.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DispensacionService {

    private final DispensacionRepository repository;

    // Clientes HTTP para comunicarse con los microservicios.
    private final RestClient usuarioClient;
    private final RestClient recetaClient;
    private final RestClient inventarioClient;

    // Configura las direcciones de los servicios.
    public DispensacionService(
            DispensacionRepository repository,
            @Value("${servicios.usuario.url}") String usuarioUrl,
            @Value("${servicios.receta.url}") String recetaUrl,
            @Value("${servicios.inventario.url}") String inventarioUrl) {

        this.repository = repository;

        this.usuarioClient = RestClient.builder()
                .baseUrl(usuarioUrl).build();

        this.recetaClient = RestClient.builder()
                .baseUrl(recetaUrl).build();

        this.inventarioClient = RestClient.builder()
                .baseUrl(inventarioUrl).build();
    }

    // GET: consulta todas las dispensaciones.
    public List<Dispensacion> listar() {
        return repository.findAll();
    }

    // GET: busca una dispensación por ID.
    public Dispensacion buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dispensacion no encontrada"));
    }

    // GET: consulta una dispensación por receta.
    public Dispensacion buscarPorReceta(Long recetaId) {
        return repository.findByRecetaId(recetaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe dispensacion para esta receta"));
    }

    // GET: consulta dispensaciones de un farmacéutico.
    public List<Dispensacion> porFarmaceutico(Long id) {
        return repository.findByFarmaceuticoId(id);
    }

    // Valida que el usuario sea un farmacéutico activo.
    private void validarFarmaceutico(Long id) {
        try {
            UsuarioResponse respuesta = usuarioClient.get()
                    .uri("/api/usuarios/{id}", id)
                    .retrieve()
                    .body(UsuarioResponse.class);

            if (respuesta == null || respuesta.datos() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Respuesta invalida de Usuario");
            }

            var usuario = respuesta.datos();

            if (!"FARMACEUTICO".equals(usuario.rol())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El usuario no tiene rol FARMACEUTICO");
            }

            if (!Boolean.TRUE.equals(usuario.activo())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El farmaceutico esta inactivo");
            }

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El farmaceutico no existe");

        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar msvc-usuario");
        }
    }

    // Consulta una receta en msvc-receta.
    private RecetaResponse.RecetaDatos consultarReceta(Long id) {
        try {
            RecetaResponse respuesta = recetaClient.get()
                    .uri("/api/recetas/{id}", id)
                    .retrieve()
                    .body(RecetaResponse.class);

            if (respuesta == null || respuesta.datos() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Respuesta invalida de Receta");
            }

            return respuesta.datos();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Receta no encontrada");

        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar msvc-receta");
        }
    }

    // Consulta el inventario del medicamento y sucursal.
    private InventarioResponse.InventarioDatos consultarInventario(
            Long medicamentoId, String sucursal) {

        try {
            InventarioResponse respuesta = inventarioClient.get()
                    .uri(uri -> uri
                            .path("/api/inventarios/medicamento/{id}")
                            .queryParam("sucursal", sucursal)
                            .build(medicamentoId))
                    .retrieve()
                    .body(InventarioResponse.class);

            if (respuesta == null || respuesta.datos() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Respuesta invalida de Inventario");
            }

            return respuesta.datos();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Medicamento no encontrado en la sucursal");

        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar msvc-inventario");
        }
    }

    // POST: registra la entrega de una receta reservada.
    public Dispensacion guardar(CrearDispensacionRequest request) {

        // Evita dispensar dos veces la misma receta.
        if (repository.findByRecetaId(request.recetaId()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La receta ya fue dispensada");
        }

        // Verifica que el responsable sea farmacéutico.
        validarFarmaceutico(request.farmaceuticoId());

        // Verifica el estado de la receta.
        var receta = consultarReceta(request.recetaId());

        if (!"RESERVADA".equals(receta.estado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La receta debe tener estado RESERVADA");
        }

        // Busca el medicamento en la sucursal indicada.
        var inventario = consultarInventario(
                receta.medicamentoId(),
                receta.sucursal());

        // Comprueba que exista stock reservado.
        if (inventario.stockReservado() == null
                || inventario.stockReservado() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No existe stock reservado para dispensar");
        }

        // Descuenta una unidad reservada en Inventario.
        try {
            inventarioClient.patch()
                    .uri("/api/inventarios/{id}/dispensar",
                            inventario.id())
                    .body(Map.of("cantidad", 1))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo confirmar la entrega en Inventario");
        }

        // Cambia el estado de Receta a DISPENSADA.
        try {
            recetaClient.patch()
                    .uri(uri -> uri
                            .path("/api/recetas/{id}/estado")
                            .queryParam("estado", "DISPENSADA")
                            .build(request.recetaId()))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Inventario actualizado, pero fallo el estado de Receta");
        }

        // Registra la dispensación en su propia H2.
        Dispensacion nueva = new Dispensacion();
        nueva.setRecetaId(request.recetaId());
        nueva.setFarmaceuticoId(request.farmaceuticoId());
        nueva.setFecha(LocalDateTime.now());
        nueva.setEstado(EstadoDispensacion.COMPLETADA);

        return repository.save(nueva);
    }

    // PUT: corrige el farmacéutico responsable.
    public Dispensacion actualizar(
            Long id, Long farmaceuticoId) {

        Dispensacion dispensacion = buscar(id);

        // Verifica el nuevo responsable.
        validarFarmaceutico(farmaceuticoId);

        dispensacion.setFarmaceuticoId(farmaceuticoId);
        return repository.save(dispensacion);
    }

    // DELETE: elimina el registro administrativo.
    public void eliminar(Long id) {
        Dispensacion dispensacion = buscar(id);
        repository.delete(dispensacion);
    }
}
