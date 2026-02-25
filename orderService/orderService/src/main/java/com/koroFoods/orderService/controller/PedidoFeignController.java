package com.koroFoods.orderService.controller;

import com.koroFoods.orderService.dto.response.GraficoCincoList;
import com.koroFoods.orderService.dto.response.GraficoUnoListResponse;
import com.koroFoods.orderService.service.DetallePedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.koroFoods.orderService.dto.PedidoResumenDto;
import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.service.PedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pedido/feign")
public class PedidoFeignController {
	
	private final PedidoService pedidoService;
    private final DetallePedidoService dtService;

	@GetMapping("/reserva/{idReserva}")
	public ResponseEntity<ResultadoResponse<PedidoResumenDto>> getPedidoByReservaId(@PathVariable Integer idReserva) {
		ResultadoResponse<PedidoResumenDto> resultado = pedidoService.obtenerPedidoPorReserva(idReserva);
		if (resultado.isValor()) {
			return ResponseEntity.ok(resultado);
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}

    @GetMapping("/graficoUno")
    public ResponseEntity<ResultadoResponse<List<GraficoUnoListResponse>>> getGraficoUno(@RequestParam Integer mes){
        ResultadoResponse<List<GraficoUnoListResponse>> resultado = dtService.grafico1(mes);

        if (resultado.isValor()) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }

    @GetMapping("/graficoCinco")
    public ResponseEntity<ResultadoResponse<List<GraficoCincoList>>> getGraficoCinto(@RequestParam Integer mes){
        ResultadoResponse<List<GraficoCincoList>> response = pedidoService.graficoCincoList(mes);
        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
