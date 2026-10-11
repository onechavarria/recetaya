package cl.recetaya.receta.repository;

import cl.recetaya.receta.model.Receta;
import cl.recetaya.receta.model.EstadoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Repositorio JPA que proporciona operaciones CRUD sobre recetas.
public interface RecetaRepository
        extends JpaRepository<Receta, Long> {

    // Consulta las recetas asociadas a un médico.
    List<Receta> findByMedicoId(Long medicoId);

    // Consulta las recetas según su estado.
    List<Receta> findByEstado(EstadoReceta estado);
}
