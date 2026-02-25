package com.koroFoods.userService.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String correo;
    private String clave;
}
