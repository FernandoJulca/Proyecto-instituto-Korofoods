package com.koroFoods.userService.dto.request;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {


    private String content;
    private String sender;
    private String avatar;
    private Integer emisorId;
}
