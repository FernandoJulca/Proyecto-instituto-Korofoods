package com.koroFoods.userService.feign.repo;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.feign.dto.GraficoCincoDto;
import com.koroFoods.userService.feign.dto.GraficoUnoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "orderService")
public interface OrderFeignClient {

    @GetMapping("/pedido/feign/graficoUno")
    ResultadoResponse<List<GraficoUnoDto>> getGraficoUno(@RequestParam("mes")Integer mes);

    @GetMapping("/pedido/feign/graficoCinco")
    ResultadoResponse<List<GraficoCincoDto>> getGraficoCinto(@RequestParam("mes") Integer mes);
}
