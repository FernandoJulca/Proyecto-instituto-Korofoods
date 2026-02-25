package com.koroFoods.reservationService.util;

import com.koroFoods.reservationService.feign.UsuarioFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardNotificador {

    private final UsuarioFeignClient usuarioFeignClient;


    @Async
    public void notificarGraficoDos(Integer mes){
        try {
            usuarioFeignClient.notificarReserva(mes);
        } catch (Exception e) {
            throw new RuntimeException("Error al notificar al dashboard: " + e + " " + e.getLocalizedMessage() );
        }
    }

    @Async
    public void notificarGraficoCuatro(Integer mes){
        try {
            usuarioFeignClient.notificarReservaEvento(mes);
        } catch (Exception e) {
            throw new RuntimeException("Error al notificar al dashboard: " + e + " " + e.getLocalizedMessage() );
        }
    }
}
