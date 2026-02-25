package com.koroFoods.userService.dto.request;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    private String passwordActual;
    private String passwordNuevo;
}
