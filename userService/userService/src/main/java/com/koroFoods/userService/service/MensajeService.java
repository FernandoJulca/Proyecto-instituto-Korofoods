package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.request.ChatMessageRequest;
import com.koroFoods.userService.model.document.Mensaje;
import com.koroFoods.userService.repository.IRepositoryMensaje;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final IRepositoryMensaje repositoryMensaje;


    //Se lista los mensajes mandados cuando entramos al chat
    public Page<Mensaje> listarMensajePorIdChat(String chatId, Pageable pageable) {
        return repositoryMensaje.findByChatIdOrderByFechaMandadoAsc(chatId, pageable);
    }


    public Mensaje guardarMensaje(ChatMessageRequest request,
                                  String chatId, Integer emisorId,
                                  Integer receptorId) {

        if (request == null) {
            throw new IllegalArgumentException("Mensaje vacío");
        }

        Mensaje mensaje = new Mensaje(
                chatId,
                emisorId,
                receptorId,
                request.getContent()
        );

        return repositoryMensaje.save(mensaje);
    }



}
