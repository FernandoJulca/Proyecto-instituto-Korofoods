package com.koroFoods.eventService.feign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.koroFoods.eventService.dtos.ResultadoResponse;

@FeignClient(name = "reservationService")
public interface IReservaFeignClient {

	@GetMapping("/reserva/feign/mesas-ocupadas")
	ResultadoResponse<List<Integer>> obtenerMesasOcupadas(
	        @RequestParam("idsMesas") List<Integer> idsMesas,
	        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
	        @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
	);
}