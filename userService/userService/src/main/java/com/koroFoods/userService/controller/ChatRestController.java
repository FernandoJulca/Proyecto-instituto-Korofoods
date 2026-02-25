package com.koroFoods.userService.controller;

//para operaciones CRUD

import com.koroFoods.userService.dto.request.StarChatRequest;
import com.koroFoods.userService.dto.response.HistorialUsuarioResponse;
import com.koroFoods.userService.dto.response.RecepcionistaResponse;
import com.koroFoods.userService.model.document.Chat;
import com.koroFoods.userService.model.document.Mensaje;
import com.koroFoods.userService.service.ChatService;
import com.koroFoods.userService.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {


    private final ChatService chatService;
    private final MensajeService mensajeService;


    @PostMapping("/start")
    public Chat start(@RequestBody StarChatRequest req){
        return chatService.iniciarChat(req.getEmisorId(), req.getReceptorId());
    }


    //Obtenemos el historial de chat segun el Id del usuario
    @GetMapping("/user/{idUsuario}")
    public ResponseEntity<?> historialDeChats(
            @PathVariable Integer idUsuario
    ){

        try {
            List<HistorialUsuarioResponse> historal = chatService.listaDeChatsDto(idUsuario);
            return ResponseEntity.ok(historal);
        } catch (Exception e) {
            throw new RuntimeException("Error encontrado: " + e + " " + e.getMessage() + " " + e.getLocalizedMessage());
        }

    }

    //Se obtiene el historial de mensajes asociados a un chat
    @GetMapping("/{chatId}/mensajes")
    public Page<Mensaje> historialDeMensajes(
            @PathVariable String chatId,
            Pageable pageable
    ){
        return mensajeService.listarMensajePorIdChat(chatId, pageable);
    }

    //Obtener la lista de recepcionistas para elegir a quien mandar mensaje
    @GetMapping("/recepcionistas")
    public ResponseEntity<?> listaRecepcionistas(){

        List<RecepcionistaResponse> lista = chatService.listaDeRecepcionistas();

        return ResponseEntity.ok(lista);
    }



}
