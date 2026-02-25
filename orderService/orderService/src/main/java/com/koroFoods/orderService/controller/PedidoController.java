package com.koroFoods.orderService.controller;

import java.time.LocalDate;
import java.util.List;

import com.koroFoods.orderService.dto.request.DetallePedidoRequest;
import com.koroFoods.orderService.dto.response.DetallePedidoMeseroResponse;
import com.koroFoods.orderService.dto.response.DetallePedidoPagar;
import com.koroFoods.orderService.dto.response.DetallePedidoResponse;
import com.koroFoods.orderService.dto.response.DetallePedidoUsuarioResponse;
import com.koroFoods.orderService.model.DetallePedido;
import com.koroFoods.orderService.service.DetallePedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.koroFoods.orderService.dto.PedidoRequestDTO;
import com.koroFoods.orderService.dto.PedidoResumenDto;
import com.koroFoods.orderService.dto.PlatosMasVendidosDTO;
import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.dto.VentasPorFechaMesaDTO;
import com.koroFoods.orderService.enums.EstadoPedido;
import com.koroFoods.orderService.model.Pedido;
import com.koroFoods.orderService.service.PedidoService;
import com.koroFoods.orderService.service.ReporteService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pedido")
public class PedidoController {

	private final PedidoService pedidoService;
	private final DetallePedidoService detallePedidoService;
	  private final ReporteService reporteService;

	@GetMapping
	public ResponseEntity<ResultadoResponse<List<PedidoResumenDto>>> list(
			@RequestParam(required = false) EstadoPedido estado) {
		ResultadoResponse<List<PedidoResumenDto>> resultado = pedidoService.listarPedidos(estado);

		if (resultado.isValor()) {
			return ResponseEntity.ok(resultado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
		}
	}

	@PostMapping
	public ResponseEntity<ResultadoResponse<Pedido>> crearPedido(@RequestBody PedidoRequestDTO dto) {
		ResultadoResponse<Pedido> resultado = pedidoService.crearPedido(dto);
		if (resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
		}
	}

	@PostMapping("/newPlato")
	public ResponseEntity<ResultadoResponse<DetallePedido>> agregarPlatoOrden(
			@RequestBody DetallePedidoRequest request) {
		ResultadoResponse<DetallePedido> resultado = detallePedidoService.registrarPlato(request);
		if (resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
		}
	}

	@GetMapping("/list/{pedidoId}")
	public ResponseEntity<ResultadoResponse<List<DetallePedidoResponse>>> obtenerListaDetallePorPedido(
			@PathVariable Integer pedidoId) {
		ResultadoResponse<List<DetallePedidoResponse>> lista = detallePedidoService.obtenerDetallePorPedido(pedidoId);
		if (lista.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(lista);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(lista);
		}
	}

	@PutMapping("/ent/{idDetalle}")
	public ResponseEntity<ResultadoResponse<DetallePedido>> cambiarEstadoEntregado(@PathVariable Integer idDetalle) {
		ResultadoResponse<DetallePedido> cambiado = detallePedidoService.cambiarEstadoAEntregado(idDetalle);
		if (cambiado.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(cambiado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cambiado);
		}
	}

	@PutMapping("/can/{idDetalle}")
	public ResponseEntity<ResultadoResponse<DetallePedido>> cambiarEstadoCancelado(@PathVariable Integer idDetalle) {
		ResultadoResponse<DetallePedido> cambiado = detallePedidoService.cambiarEstadoACancelado(idDetalle);
		if (cambiado.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(cambiado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cambiado);
		}
	}

	@GetMapping("/cliente/{idPedido}")
	public ResponseEntity<ResultadoResponse<DetallePedidoUsuarioResponse>> obtenerCliente(
			@PathVariable Integer idPedido) {
		ResultadoResponse<DetallePedidoUsuarioResponse> cliente = detallePedidoService
				.obtenerUsuarioPorPedidoReserva(idPedido);

		if (cliente.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cliente);
		}
	}

	@GetMapping("/mesero/{idPedido}")
	public ResponseEntity<ResultadoResponse<DetallePedidoMeseroResponse>> obtenerMesero(
			@PathVariable Integer idPedido) {
		ResultadoResponse<DetallePedidoMeseroResponse> cliente = detallePedidoService.obtenerUsuarioPorPedido(idPedido);

		if (cliente.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cliente);
		}
	}

	@GetMapping("/{idUsuario}/list")
	public ResponseEntity<ResultadoResponse<List<Pedido>>> obtenerPedidosDelCliente(@PathVariable Integer idUsuario) {
		ResultadoResponse<List<Pedido>> pedidos = pedidoService.obtenerPedidoDelCliente(idUsuario);

		if (pedidos.isValor()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(pedidos);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pedidos);
		}
	}

    @GetMapping("/procederPago")
    public ResponseEntity<ResultadoResponse<DetallePedidoPagar>> pagar(
            @RequestParam Integer idPedido
    ){
        ResultadoResponse<DetallePedidoPagar> response = detallePedidoService.procederAlPago(idPedido);

        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/estadoPagado")
    public ResponseEntity<ResultadoResponse<Pedido>> estadoPagado(
            @RequestParam Integer idPedido
    ){
        ResultadoResponse<Pedido> response = detallePedidoService.cambiarAPagadoLaOrder(idPedido);
        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/obtener/{idPedido}")
    public ResponseEntity<ResultadoResponse<Pedido>> obtener(
            @PathVariable Integer idPedido
    ){
        ResultadoResponse<Pedido> response = detallePedidoService.obtenerPedidoPorId(idPedido);
        if (response != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
  

    
    @GetMapping("reporte/ventas-por-mesa")
    public ResponseEntity<ResultadoResponse<List<VentasPorFechaMesaDTO>>> ventasPorFechaMesa(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer idMesa) {

        ResultadoResponse<List<VentasPorFechaMesaDTO>> resultado =
                reporteService.ventasPorFechaMesa(fechaInicio, fechaFin, idMesa);

        return resultado.isValor()
                ? ResponseEntity.ok(resultado)
                : ResponseEntity.badRequest().body(resultado);
    }

     @GetMapping("reporte/platos-mas-vendidos")
    public ResponseEntity<ResultadoResponse<List<PlatosMasVendidosDTO>>> platosMasVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        ResultadoResponse<List<PlatosMasVendidosDTO>> resultado =
                reporteService.platosMasVendidos(fechaInicio, fechaFin);

        return resultado.isValor()
                ? ResponseEntity.ok(resultado)
                : ResponseEntity.badRequest().body(resultado);
    }
}
