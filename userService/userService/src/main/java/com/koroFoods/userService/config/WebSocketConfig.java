package com.koroFoods.userService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //Endpoint base al que se conectan los clientes (Angular, Postman, etc)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        /* Prefijo para los mensajes que el cliente ENVÍA al backend
           todo lo que empiece con /app llega al @MessageMapping
           por ejemplo "/app/chat/123"
        */
        registry.setApplicationDestinationPrefixes("/app");

        /*
            Prefijo que usa el backend para hacer conexion entre usuarios
            cualquier tipo de cliente se puede conectar aca para poder intercambiar
            informacion por ejemplo con
            topic -> son chat grupales o salas ya que son generales o un foro en este caso
            usamos topic/chat/idChat ya que las personas relacionadas al chat se podran unir
            queue -> son para mensajes privados que la comunicacion solo sea 1 - 1
            en el ejemplo actual usamos queue para recibir solo las notificaciones del cliente
            y no lo demas por ejemplo solo queremos recibir notificacion del id usuario 5 solo
            lo relacionado a ese id recibiremos informacion
        */
        registry.enableSimpleBroker("/topic", "/queue");
    }
}
