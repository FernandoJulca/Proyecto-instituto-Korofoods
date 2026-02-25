package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.response.HistorialUsuarioResponse;
import com.koroFoods.userService.dto.response.RecepcionistaResponse;
import com.koroFoods.userService.model.Usuario;
import com.koroFoods.userService.model.document.Chat;
import com.koroFoods.userService.model.document.Mensaje;
import com.koroFoods.userService.repository.IRepositoryChat;
import com.koroFoods.userService.repository.IRepositoryMensaje;
import com.koroFoods.userService.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final IRepositoryChat repositoryChat;
    private final IRepositoryMensaje repositoryMensaje;
    private final IUsuarioRepository usuarioRepository;

    //Lista de chats que hay ya sea cliente/recepcionista o recepcionista/cliente
    public List<HistorialUsuarioResponse> listaDeChatsDto(Integer idUsuario) {

        List<Chat> chats = repositoryChat
                .findByEmisorIdOrReceptorId(idUsuario, idUsuario);


        return chats.stream().map(chat -> {
            Integer receptorId = chat.getEmisorId().equals(idUsuario)
                    ? chat.getReceptorId() // si es igual al emisor(cliente) capturamos del chat el id del receptor
                    : chat.getEmisorId(); // si no es igual y somos el receptor(recepcionista) capturamos del chat el id del emisor

            //Obtener al usuario
            Usuario u = usuarioRepository.findById(receptorId)
                    .orElseThrow(() -> new RuntimeException("No se encontro al usuario con ID: " + idUsuario));

            //Obtener el ultimo mensaje que se mando
            Mensaje ultimoMensaje = repositoryMensaje.findFirstByChatIdOrderByFechaMandadoDesc(chat.getId()).orElse(null);


            return new HistorialUsuarioResponse(
                    chat.getId(),
                    receptorId,
                    u.getNombres(),
                    u.getApePaterno(),
                    u.getImagen(),
                    ultimoMensaje != null ? ultimoMensaje.getContenido() : " ",
                    chat.getFechaUltimoMensaje()
            );
        }).toList();
    }


    //Obtener ID del usuario a quien queremos enviar el mensaje(receptor)
    public Integer obtenerReceptor(String chatId, Integer emisorId) {

        Chat chat = repositoryChat.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat con ID: " + chatId + " no existe"));

        //Si soy el cliente obtengo el Id del recepcionista
        if (chat.getEmisorId().equals(emisorId)) {
            return chat.getReceptorId();
        }

        //Si soy el recepcionista obtengo el Id del usuario
        if (chat.getReceptorId().equals(emisorId)) {
            return chat.getEmisorId();
        }

        throw new RuntimeException("Usuario no pertenece al chat");
    }


    public Chat iniciarChat(Integer emisorId, Integer receptorId) {
        return repositoryChat.findByEmisorIdAndReceptorIdOrEmisorIdAndReceptorId(
                emisorId, receptorId,
                receptorId, emisorId
        ).orElseGet(() -> {
            Chat nuevoChat = new Chat();
            nuevoChat.setEmisorId(emisorId);
            nuevoChat.setReceptorId(receptorId);
            nuevoChat.setUltimoMensaje("");
            nuevoChat.setFechaUltimoMensaje(LocalDateTime.now());

            return repositoryChat.save(nuevoChat);
        });
    }


    public List<RecepcionistaResponse> listaDeRecepcionistas() {

        List<Usuario> recepcionistas =
                usuarioRepository.findByRol_IdRol(2);

        return recepcionistas.stream().map(u ->
                new RecepcionistaResponse(
                        u.getIdUsuario(),
                        u.getNombres(),
                        u.getApePaterno(),
                        u.getImagen()
                )
        ).toList();


    }


}
