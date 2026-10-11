package cl.recetaya.dispensacion.repository;

import cl.recetaya.dispensacion.model.Dispensacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

// Acceso a la tabla de dispensaciones.
public interface DispensacionRepository
    extends JpaRepository<Dispensacion, Long> {

    // Busca una dispensación por su receta.
    Optional<Dispensacion> findByRecetaId(Long recetaId);

    // Consulta entregas realizadas por un farmacéutico.
    List<Dispensacion> findByFarmaceuticoId(Long farmaceuticoId);
}

