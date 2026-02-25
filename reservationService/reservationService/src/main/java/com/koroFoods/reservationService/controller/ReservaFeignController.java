package com.koroFoods.reservationService.controller;

import com.koroFoods.reservationService.dto.response.Grafico2Data;
import com.koroFoods.reservationService.dto.response.GraficoCuatroList;
import com.koroFoods.reservationService.feign.UsuarioFeign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.koroFoods.reservationService.model.Reserva;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.reservationService.dto.ReservaDtoFeing;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.service.ReservaService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/reserva/feign")
@RequiredArgsConstructor
public class ReservaFeignController {
	private final ReservaService reservaService;

	@GetMapping("/{codigo}")
	public ResponseEntity<ResultadoResponse<ReservaDtoFeing>> getreservationhById(@PathVariable String codigo) {
		ResultadoResponse<ReservaDtoFeing> resultado = reservaService.getReservationByID(codigo);
		if (resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
		}
	}

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<ResultadoResponse<UsuarioFeign>> obtenerUsuarioPorReserva(@PathVariable Integer idReserva){
        ResultadoResponse<UsuarioFeign> response = reservaService.obtenerUsuarioPorReserva(idReserva);
        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/mesas-ocupadas")
    public ResponseEntity<ResultadoResponse<List<Integer>>> obtenerMesasOcupadas(
            @RequestParam List<Integer> idsMesas,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        return ResponseEntity.ok(reservaService.obtenerMesasOcupadas(idsMesas, inicio, fin));
    }
    

    @GetMapping("/list/{idCliente}")
    public ResponseEntity<ResultadoResponse<List<Reserva>>> obtenerListaDeReservasPorCliente(@PathVariable Integer idCliente){
        ResultadoResponse<List<Reserva>> response = reservaService.obtenerReservaPorIdCliente(idCliente);
        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/graficoDos")
    public ResponseEntity<ResultadoResponse<Grafico2Data>> getGraficoDos(@RequestParam Integer mes){
        ResultadoResponse<Grafico2Data> response = reservaService.graficoDos(mes);
        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/graficoCuatro")
    public ResponseEntity<ResultadoResponse<List<GraficoCuatroList>>> getGraficoCuatro(@RequestParam Integer mes){
        ResultadoResponse<List<GraficoCuatroList>> response = reservaService.graficoCuatroList(mes);
        if (response.isValor()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}