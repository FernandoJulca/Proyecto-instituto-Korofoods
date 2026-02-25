package com.koroFoods.paymentService.messaging;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.koroFoods.paymentService.config.RabbitMQConfig;
import com.koroFoods.paymentService.dtos.PagoAnuladoEvent;
import com.koroFoods.paymentService.dtos.PagoConfirmadoEvent;

@Service
@RequiredArgsConstructor
@Slf4j

public class PagoEventPublisher {

	private final RabbitTemplate rabbitTemplate;

    public void publicarPagoConfirmado(PagoConfirmadoEvent event) {
        log.info("Publicando evento: Pago Confirmado - ID: {}", event.getIdPago());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGO_EXCHANGE,
                RabbitMQConfig.PAGO_CONFIRMADO_ROUTING_KEY,
                event
        );
        log.info("Evento publicado exitosamente");
    }

    public void publicarPagoAnulado(PagoAnuladoEvent event) {
        log.info("Publicando evento: Pago Anulado - ID: {}", event.getIdPago());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGO_EXCHANGE,
                RabbitMQConfig.PAGO_ANULADO_ROUTING_KEY,
                event
        );
        log.info("Evento publicado exitosamente");
    }
}
