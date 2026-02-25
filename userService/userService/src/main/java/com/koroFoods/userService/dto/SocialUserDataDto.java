package com.koroFoods.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserDataDto {

    private String nombre;
    private String email;
    private String avatar;
    private String provider; //si es github o google
}
