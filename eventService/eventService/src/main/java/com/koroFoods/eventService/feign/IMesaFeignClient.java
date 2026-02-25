package com.koroFoods.eventService.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.koroFoods.eventService.dtos.ResultadoResponse;


@FeignClient(name = "tableService")
public interface IMesaFeignClient {

	@GetMapping("/mesa/feign/{id}")
	ResultadoResponse<MesaFeign> obtenerMesaPorId(@PathVariable Integer id);
	
    @PostMapping("/mesa/feign/por-ids")
    ResultadoResponse<List<MesaFeign>> obtenerMesasPorIds(@RequestBody List<Integer> ids);
}
