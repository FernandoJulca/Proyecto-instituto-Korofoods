package com.koroFoods.userService.model.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    private String id;
    private Integer emisorId;
    private Integer receptorId;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
}
