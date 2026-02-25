package com.koroFoods.userService.service;

import com.koroFoods.userService.feign.repo.OrderFeignClient;
import com.koroFoods.userService.feign.repo.PaymentFeignClient;
import com.koroFoods.userService.feign.repo.QualificationFeignClient;
import com.koroFoods.userService.feign.repo.ReservationFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderFeignClient orderFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final QualificationFeignClient qualificationFeignClient;
    private final ReservationFeignClient reservationFeignClient;
    private final SimpMessagingTemplate messagingTemplate;


    public void ActualizarGraficosCompletos(Integer mes) {
        enviarGraficoUno(mes);
        enviarGraficoDos(mes);
        enviarGraficoTres(mes);
        enviarGraficoCuatro(mes);
        enviarGraficoCinco(mes);
        enviarGraficoSeis(mes);
    }


    public void enviarGraficoUno(Integer mes) {
        log.info("Ejecutando metodo Grafico 1 ");
        try {
            var data = orderFeignClient.getGraficoUno(mes);
            log.info("Grafico 1 cambiando con datos: {}", data);
            messagingTemplate.convertAndSend("/topic/dashboard/grafico-uno", data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarGraficoDos(Integer mes) {
        log.info("Ejecutando metodo Grafico 2 ");
        try {
            var data = reservationFeignClient.getGraficoDos(mes);
            log.info("Grafico 2 cambiando con datos: {}", data);
            messagingTemplate.convertAndSend("/topic/dashboard/grafico-dos", data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarGraficoTres(Integer mes) {
        log.info("Ejecutando metodo Grafico 3 ");
        try {
            var data = paymentFeignClient.getGraficoTres(mes);
            log.info("Grafico 3 cambiando con datos: {}", data);
            messagingTemplate.convertAndSend("/topic/dashboard/grafico-tres", data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarGraficoCuatro(Integer mes) {
        log.info("Ejecutando metodo Grafico 4 ");
        try {
            var data = reservationFeignClient.getGraficoCuatro(mes);
            log.info("Grafico 4 cambiando con datos: {}", data);
            messagingTemplate.convertAndSend("/topic/dashboard/grafico-cuatro", data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarGraficoCinco(Integer mes) {
        log.info("Ejecutando metodo Grafico 5 ");
        try {
            var data = orderFeignClient.getGraficoCinto(mes);
            log.info("Grafico 5 cambiando con datos: {}", data);
            messagingTemplate.convertAndSend("/topic/dashboard/grafico-cinco", data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarGraficoSeis(Integer mes) {
        log.info("Ejecutando metodo Grafico 6 ");
        try {
            var data = qualificationFeignClient.graficoSeisList(mes);
            log.info("Grafico 6 cambiando con datos: {}", data);
            messagingTemplate.convertAndSend("/topic/dashboard/grafico-seis", data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
