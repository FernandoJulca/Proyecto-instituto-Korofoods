package com.koroFoods.userService.controller;

/*para todo lo relacionado con webSockets*/

import com.koroFoods.userService.dto.request.ChatMessageRequest;
import com.koroFoods.userService.model.document.Mensaje;
import com.koroFoods.userService.service.ChatService;
import com.koroFoods.userService.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class SocketController {


    private final MensajeService mensajeService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat/{chatId}")
    public void sendMessage(
            @DestinationVariable String chatId,
            @Payload ChatMessageRequest request
    ) {
        //Obtenemos quien manda el mensaje
        Integer emisorId = request.getEmisorId();

        //obtenemos a quien va a mandar el mensaje
        Integer receptorId = chatService.obtenerReceptor(chatId, emisorId);

        Mensaje guardado = mensajeService.guardarMensaje(
                request,
                chatId,
                emisorId,
                receptorId
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId,
                guardado
        );

        messagingTemplate.convertAndSendToUser(
                receptorId.toString(),
                "/queue/messages",
                guardado
        );

    }
}
