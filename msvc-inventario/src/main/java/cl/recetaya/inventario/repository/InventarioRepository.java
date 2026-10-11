package cl.recetaya.inventario.repository;

import cl.recetaya.inventario.model.Inventario;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository
        extends JpaRepository<Inventario, Long> {

    // Consulta los medicamentos de una sucursal.
    List<Inventario> findBySucursal(String sucursal);

    // Busca un medicamento en una sucursal específica.
    Optional<Inventario> findByMedicamentoIdAndSucursal(
            Long medicamentoId, String sucursal);

    // Bloquea el registro mientras se modifica el stock.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventario i WHERE i.id = :id")
    Optional<Inventario> buscarParaMovimiento(
            @Param("id") Long id);
}
