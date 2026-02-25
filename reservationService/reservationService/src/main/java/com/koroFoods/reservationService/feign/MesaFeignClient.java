package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "tableService")
public interface MesaFeignClient {

	@GetMapping("/mesa/feign/{id}")
	ResultadoResponse<MesaFeign> obtenerMesaPorId(@PathVariable Integer id);
	
	@PutMapping("/mesa/feign/estado-ocupada/{id}")
	ResultadoResponse<MesaFeign> cambiarEstadoMesaOcupada(@PathVariable Integer id);
}
