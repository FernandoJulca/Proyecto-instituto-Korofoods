package com.koroFoods.userService.dto.response;

import com.koroFoods.userService.dto.SocialUserDataDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialAuthResponse {

    private boolean usuarioExistente;
    private String token;
    private String tempToken;
    private SocialUserDataDto socialUserDataDto;
}
