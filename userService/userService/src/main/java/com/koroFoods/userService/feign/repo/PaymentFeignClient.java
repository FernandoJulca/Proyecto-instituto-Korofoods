package com.koroFoods.userService.feign.repo;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.feign.dto.GraficoTresDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "paymentService")
public interface PaymentFeignClient {

    @GetMapping("/pago/feign/graficoTres")
    ResultadoResponse<GraficoTresDto>getGraficoTres(@RequestParam Integer mes);

}
