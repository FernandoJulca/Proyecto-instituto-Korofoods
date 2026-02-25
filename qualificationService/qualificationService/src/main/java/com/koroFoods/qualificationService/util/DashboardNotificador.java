package com.koroFoods.qualificationService.util;

import com.koroFoods.qualificationService.feign.UsuarioFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardNotificador {

    private final UsuarioFeignClient usuarioFeignClient;


    @Async
    public void notificarGraficoSeis(Integer mes){
        try{
            usuarioFeignClient.notificarQualification(mes);
        } catch (Exception e) {
            throw new RuntimeException("Error al notificar al dashboard: " + e + " " + e.getLocalizedMessage() );
        }
    }
}
