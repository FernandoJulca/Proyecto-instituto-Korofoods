package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.document.Mensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IRepositoryMensaje extends MongoRepository<Mensaje, String> {


    //Buscamos mensajes por el chat que estamos entrando 1,2,3 el historial de mensajes
    Page<Mensaje> findByChatIdOrderByFechaMandadoAsc(String chatId, Pageable pageable);

    //Obtenemos el ultimo mensaje que se mando ya sea cliente o recepcioitsa
    Optional<Mensaje> findFirstByChatIdOrderByFechaMandadoDesc(String chatId);
}
