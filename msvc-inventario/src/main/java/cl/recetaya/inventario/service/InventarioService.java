package cl.recetaya.inventario.service;

import cl.recetaya.inventario.model.Inventario;
import cl.recetaya.inventario.repository.InventarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InventarioService {

	private final InventarioRepository repository;

	// Inyección de dependencias por constructor.
	public InventarioService(InventarioRepository repository) {
		this.repository = repository;
	}

	// Obtiene todos los medicamentos registrados.
	public List<Inventario> listar() {
		return repository.findAll();
	}

	// Busca un registro mediante su ID.
	public Inventario buscar(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Inventario no encontrado"));
	}

	// Consulta medicamentos de una sucursal.
	public List<Inventario> buscarPorSucursal(String sucursal) {
		return repository.findBySucursal(sucursal);
	}

	// Busca un medicamento por su ID y sucursal.
	public Inventario buscarMedicamento(
			Long medicamentoId, String sucursal) {

		return repository.findByMedicamentoIdAndSucursal(
				medicamentoId, sucursal)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Medicamento no encontrado en la sucursal"));
	}

	// Registra un nuevo medicamento en el inventario.
	@Transactional
	public Inventario guardar(Inventario inventario) {
		inventario.setId(null);
		inventario.setStockReservado(0);

		boolean existe = repository
				.findByMedicamentoIdAndSucursal(
						inventario.getMedicamentoId(),
						inventario.getSucursal())
				.isPresent();

		if (existe) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"El medicamento ya existe en esta sucursal");
		}

		return repository.save(inventario);
	}

	// Actualiza los datos generales y el stock disponible.
	@Transactional
	public Inventario actualizar(Long id, Inventario datos) {
		Inventario inventario = repository
				.buscarParaMovimiento(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Inventario no encontrado"));

		boolean duplicado = repository
				.findByMedicamentoIdAndSucursal(
						datos.getMedicamentoId(),
						datos.getSucursal())
				.filter(otro -> !otro.getId().equals(id))
				.isPresent();

		if (duplicado) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"El medicamento ya existe en esta sucursal");
		}

		inventario.setMedicamentoId(datos.getMedicamentoId());
		inventario.setMedicamento(datos.getMedicamento());
		inventario.setSucursal(datos.getSucursal());
		inventario.setStockDisponible(datos.getStockDisponible());

		// El stock reservado no se modifica mediante PUT.
		return repository.save(inventario);
	}

	// Reserva unidades disponibles para una receta.
	@Transactional
	public Inventario reservar(Long id, Integer cantidad) {
		Inventario inventario = bloquear(id);

		if (inventario.getStockDisponible() < cantidad) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Stock insuficiente para realizar la reserva");
		}

		inventario.setStockDisponible(
				inventario.getStockDisponible() - cantidad);

		inventario.setStockReservado(
				inventario.getStockReservado() + cantidad);

		return repository.save(inventario);
	}

	// Devuelve unidades reservadas al stock disponible.
	@Transactional
	public Inventario liberar(Long id, Integer cantidad) {
		Inventario inventario = bloquear(id);

		if (inventario.getStockReservado() < cantidad) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"No existe suficiente stock reservado");
		}

		inventario.setStockReservado(
				inventario.getStockReservado() - cantidad);

		inventario.setStockDisponible(
				inventario.getStockDisponible() + cantidad);

		return repository.save(inventario);
	}

	// Descuenta las unidades reservadas al dispensarlas.
	@Transactional
	public Inventario dispensar(Long id, Integer cantidad) {
		Inventario inventario = bloquear(id);

		if (inventario.getStockReservado() < cantidad) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"No existe suficiente stock reservado");
		}

		inventario.setStockReservado(
				inventario.getStockReservado() - cantidad);

		// El stock disponible no cambia:
		// las unidades ya fueron retiradas al reservar.
		return repository.save(inventario);
	}

	// Obtiene un registro bloqueado para evitar cambios simultáneos.
	private Inventario bloquear(Long id) {
		return repository.buscarParaMovimiento(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Inventario no encontrado"));
	}

	// Elimina registros sin reservas pendientes.
	@Transactional
	public void eliminar(Long id) {
		Inventario inventario = bloquear(id);

		if (inventario.getStockReservado() > 0) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"No se puede eliminar inventario con reservas");
		}

		repository.delete(inventario);
	}
}
