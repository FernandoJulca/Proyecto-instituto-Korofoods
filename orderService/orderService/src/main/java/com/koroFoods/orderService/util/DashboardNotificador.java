package com.koroFoods.orderService.util;

import com.koroFoods.orderService.feign.UsuarioFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardNotificador {

        private final UsuarioFeignClient usuarioFeignClient;

        @Async
        public void notificarGraficoUno(Integer mes){
            try {
                usuarioFeignClient.notificarOrder(mes);
            } catch (Exception e) {
                throw new RuntimeException("Error al notificar al dashboard: " + e + " " + e.getLocalizedMessage() );
            }
        }

        @Async
        public  void notificarGraficoCinco(Integer mes){
            try {
                usuarioFeignClient.notificarOrder(mes);
            } catch (Exception e) {
                throw new RuntimeException("Error al notificar al dashboard: " + e + " " + e.getLocalizedMessage() );
            }
        }
}
