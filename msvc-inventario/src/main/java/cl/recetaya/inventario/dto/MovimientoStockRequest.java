package cl.recetaya.inventario.dto;

import jakarta.validation.constraints.*;

// Solicitud para realizar movimientos de stock.
public record MovimientoStockRequest(

	// Solo se permiten cantidades mayores que cero.
	@NotNull @Min(1) Integer cantidad

) {
}
