package com.koroFoods.userService.model.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "mensajes")
@Getter
@Setter

public class Mensaje {

    @Id
    private String id;
    private String chatId;
    private Integer emisorId;
    private Integer receptorId;
    private String contenido;
    private LocalDateTime fechaMandado;
    private boolean leido;


    public Mensaje(String chatId, Integer emisorId, Integer receptorId, String contenido) {
        this.chatId = chatId;
        this.emisorId = emisorId;
        this.receptorId = receptorId;
        this.contenido = contenido;
        this.fechaMandado = LocalDateTime.now();
        this.leido = false;
    }

}
