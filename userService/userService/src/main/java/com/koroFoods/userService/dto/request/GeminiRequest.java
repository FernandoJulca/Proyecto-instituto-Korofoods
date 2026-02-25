package com.koroFoods.userService.dto.request;

import java.util.List;

public record GeminiRequest(
        SystemInstruction systemInstruction,
        List<Content> contents,
        GenerationConfig generationConfig
) {

    public static record SystemInstruction(List<Part> parts) {}

    public static record Content(
            String role, // "user" | "model"
            List<Part> parts
    ) {}

    public static record Part(String text) {}

    public static record GenerationConfig(
            Integer maxOutputTokens,
            Double temperature
    ) {}
}


