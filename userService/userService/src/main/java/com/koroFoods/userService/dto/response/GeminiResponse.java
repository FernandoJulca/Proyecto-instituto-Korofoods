package com.koroFoods.userService.dto.response;

import java.util.List;

public record GeminiResponse(
        List<Candidate> candidates,
        UsageMetadata usageMetadata
) {
    public static record Candidate(
            Content content,
            String finishReason,
            int index
    ) {}
    
    public static record Content(
            List<Part> parts,
            String role
    ) {}
    
    public static record Part(String text) {}
    
    public static record UsageMetadata(
            int promptTokenCount,
            int candidatesTokenCount,
            int totalTokenCount
    ) {}
    
    // Método auxiliar para extraer el texto fácilmente
    public String getText() {
        if (candidates != null && !candidates.isEmpty()) {
            var content = candidates.get(0).content();
            if (content != null && content.parts() != null && !content.parts().isEmpty()) {
                return content.parts().get(0).text();
            }
        }
        return "";
    }
}