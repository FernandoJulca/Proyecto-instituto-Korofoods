package com.koroFoods.userService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class StarChatRequest {

    private Integer emisorId;
    private Integer receptorId;
}
