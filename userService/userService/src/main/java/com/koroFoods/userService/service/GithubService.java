package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.GithubUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GithubService {

    @Value("${github.clientId}")
    private String clientId;

    @Value("${github.clientSecret}")
    private String clientSecret;

    private final WebClient webClient;

    public GithubService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://github.com")
                .build();
    }

    public GithubUserDto loginWithGithub(String code) {
        try {
            Map tokenResponse = webClient.post()
                    .uri("https://github.com/login/oauth/access_token")
                    .header("Accept", "application/json")
                    .bodyValue(Map.of(
                            "client_id", clientId,
                            "client_secret", clientSecret,
                            "code", code
                    ))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> {
                                return response.bodyToMono(String.class).flatMap(errorBody -> {
                                    return Mono.error(new RuntimeException("GitHub token error: " + errorBody));
                                });
                            }
                    )
                    .bodyToMono(Map.class)
                    .block();

            if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                throw new RuntimeException("No se pudo obtener el access token de GitHub");
            }
            String accessToken = (String) tokenResponse.get("access_token");

            Map user = webClient.get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            var emails = webClient.get()
                    .uri("https://api.github.com/user/emails")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            String email = null;
            if (emails != null && !emails.isEmpty()) {
                Map e = (Map) emails.get(0);
                email = (String) e.get("email");
            }

            return new GithubUserDto(
                    (String) user.get("name"),
                    email,
                    (String) user.get("avatar_url")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
