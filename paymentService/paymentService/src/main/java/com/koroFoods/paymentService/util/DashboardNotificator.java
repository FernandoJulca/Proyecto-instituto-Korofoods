package com.koroFoods.paymentService.util;

import com.koroFoods.paymentService.feign.UsuarioFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardNotificator {

    private final UsuarioFeignClient feignClient;

    @Async
    public void notificarGraficoTres(Integer mes){
        try {
            feignClient.notificarPayment(mes);
        } catch (Exception e) {
            throw new RuntimeException("Error al notificar al dashboard: " + e + " " + e.getLocalizedMessage() );
        }
    }
}
