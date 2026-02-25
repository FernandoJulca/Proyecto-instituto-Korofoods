package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.document.Chat;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IRepositoryChat extends MongoRepository<Chat, String> {

    //Obtenemos el historial de chats que se manda 1-1
    List<Chat> findByEmisorIdOrReceptorId(Integer idEmisor, Integer idReceptor);

    //Buscamos si hay un chat creado o si es complementamente nuevo
    Optional<Chat>findByEmisorIdAndReceptorIdOrEmisorIdAndReceptorId(Integer idEmisor1, Integer idReceptor1,
                                                                     Integer idEmisor2, Integer idReceptor2);


}
