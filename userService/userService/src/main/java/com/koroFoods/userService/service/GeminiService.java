package com.koroFoods.userService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.koroFoods.userService.dto.request.PromptRequest;
import com.koroFoods.userService.dto.response.GeminiResponse;
import com.koroFoods.userService.util.ChatbotPrompt;
import com.koroFoods.userService.util.ChatbotResponseFormatter;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestClient restClient;

    public GeminiService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String model;

    public GeminiResponse getChatResponse(PromptRequest promptRequest) {

        Map<String, Object> requestBody = Map.of(
            "systemInstruction", Map.of(
                "parts", List.of(
                    Map.of("text", ChatbotPrompt.BASE_PROMPT)
                )
            ),
            "contents", List.of(
                Map.of(
                    "role", "user",
                    "parts", List.of(
                        Map.of("text", promptRequest.prompt())
                    )
                )
            ),
            "generationConfig", Map.of(
                "maxOutputTokens", 400,
                "temperature", 0.6
            )
        );

        return restClient.post()
                .uri("/models/" + model + ":generateContent?key=" + apiKey)
                .body(requestBody)
                .retrieve()
                .body(GeminiResponse.class);
    }
    
    public String getFormattedChatResponse(PromptRequest promptRequest) {

        GeminiResponse response = getChatResponse(promptRequest);
        String rawText = response.getText();
        return ChatbotResponseFormatter.format(rawText);
    }



}

