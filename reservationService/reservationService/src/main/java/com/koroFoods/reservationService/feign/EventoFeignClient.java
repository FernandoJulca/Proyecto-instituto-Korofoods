package com.koroFoods.reservationService.feign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.koroFoods.reservationService.dto.EventoConMesaDto;
import com.koroFoods.reservationService.dto.EventoDtoFeign;
import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "eventService")
public interface EventoFeignClient {
	
	@GetMapping("/evento/feign")
	ResultadoResponse<List<EventoDtoFeign>> listarEventos();

	@GetMapping("/evento/feign/validar/{id}")
	ResultadoResponse<EventoFeign> obtenerEvento(@PathVariable Integer id);

	@GetMapping("/evento/feign/ocupaciones")
	ResultadoResponse<Boolean> validarHorariosParaReservaConEvento(@RequestParam Integer mesaId,
			@RequestParam Integer eventoId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta);

	@GetMapping("/evento/feign/mesas/{idEvento}")
	ResultadoResponse<List<EventoConMesaDto>> listarMesasPorEvento(@PathVariable Integer idEvento);

	
}
