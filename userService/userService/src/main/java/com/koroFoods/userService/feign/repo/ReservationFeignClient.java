package com.koroFoods.userService.feign.repo;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.feign.dto.GraficoCuatroDto;
import com.koroFoods.userService.feign.dto.GraficoDosDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "reservationService")
public interface ReservationFeignClient {

    @GetMapping("/reserva/feign/graficoDos")
    ResultadoResponse<GraficoDosDto>getGraficoDos(@RequestParam("mes")Integer mes);
    @GetMapping("/reserva/feign/graficoCuatro")
    ResultadoResponse<List<GraficoCuatroDto>>getGraficoCuatro(@RequestParam("mes")Integer mes);
}
