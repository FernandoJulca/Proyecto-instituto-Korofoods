package com.koroFoods.userService.util;

public class ChatbotResponseFormatter {

    public static String format(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "Lo siento, no pude generar una respuesta en este momento.";
        }

        return rawText
                .replace("**", "")                // quitar markdown
                .replaceAll("\\n{3,}", "\n\n")    // limpiar saltos
                .trim();
    }
}

