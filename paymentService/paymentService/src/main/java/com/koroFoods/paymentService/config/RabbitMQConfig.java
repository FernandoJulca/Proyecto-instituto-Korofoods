package com.koroFoods.paymentService.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class RabbitMQConfig {

	 // Nombres de exchanges
    public static final String PAGO_EXCHANGE = "pago.exchange";

    // Nombres de queues
    public static final String PAGO_CONFIRMADO_QUEUE = "pago.confirmado.queue";
    public static final String PAGO_ANULADO_QUEUE = "pago.anulado.queue";

    // Routing keys
    public static final String PAGO_CONFIRMADO_ROUTING_KEY = "pago.confirmado";
    public static final String PAGO_ANULADO_ROUTING_KEY = "pago.anulado";

    // Exchange
    @Bean
    public TopicExchange pagoExchange() {
        return new TopicExchange(PAGO_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue pagoConfirmadoQueue() {
        return new Queue(PAGO_CONFIRMADO_QUEUE, true); // durable = true
    }

    @Bean
    public Queue pagoAnuladoQueue() {
        return new Queue(PAGO_ANULADO_QUEUE, true);
    }

    // Bindings
    @Bean
    public Binding pagoConfirmadoBinding() {
        return BindingBuilder
                .bind(pagoConfirmadoQueue())
                .to(pagoExchange())
                .with(PAGO_CONFIRMADO_ROUTING_KEY);
    }

    @Bean
    public Binding pagoAnuladoBinding() {
        return BindingBuilder
                .bind(pagoAnuladoQueue())
                .to(pagoExchange())
                .with(PAGO_ANULADO_ROUTING_KEY);
    }

    // Message Converter (para enviar objetos como JSON)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate con el converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
