package com.koroFoods.qualificationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.qualificationService.dto.ResultadoResponse;

@FeignClient(name = "menuService")
public interface PlatoFeignClient {
	@GetMapping("/menu/feign/{id}")
    ResultadoResponse<PlatoFeign> getDishById(@PathVariable Integer id);
}
