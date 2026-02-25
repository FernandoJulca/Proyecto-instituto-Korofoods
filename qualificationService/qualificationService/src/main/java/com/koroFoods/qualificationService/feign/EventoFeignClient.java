package com.koroFoods.qualificationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.qualificationService.dto.ResultadoResponse;

@FeignClient(name = "eventService")
public interface EventoFeignClient {
	@GetMapping("/evento/feign/{id}")
    ResultadoResponse<EventoFeign> getEventById(@PathVariable Integer id);
}
