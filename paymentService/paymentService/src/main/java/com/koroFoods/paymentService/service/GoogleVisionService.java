package com.koroFoods.paymentService.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class GoogleVisionService {

	@Value("${google.vision.api-key}")
    private String apiKey;

    public String extraerTextoDeURL(String imageUrl) throws IOException {
        try {
            // Llamar a la REST API de Google Vision directamente
            String endpoint = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;

            String requestBody = String.format("""
                {
                  "requests": [
                    {
                      "image": {
                        "source": {
                          "imageUri": "%s"
                        }
                      },
                      "features": [
                        {
                          "type": "TEXT_DETECTION"
                        }
                      ]
                    }
                  ]
                }
                """, imageUrl);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Error en Google Vision API: {}", response.body());
                throw new IOException("Error en Google Vision: " + response.statusCode());
            }

            // Parsear respuesta JSON (simple parsing)
            String responseBody = response.body();
            
            // Buscar el texto extraído en el JSON
            int textIndex = responseBody.indexOf("\"text\":");
            if (textIndex == -1) {
                log.warn("No se detectó texto en la imagen");
                return "";
            }

            // Extraer el texto (simple parsing)
            int startQuote = responseBody.indexOf("\"", textIndex + 7);
            int endQuote = responseBody.indexOf("\"", startQuote + 1);
            
            if (startQuote != -1 && endQuote != -1) {
                String texto = responseBody.substring(startQuote + 1, endQuote);
                // Decodificar \n a saltos de línea reales
                texto = texto.replace("\\n", "\n");
                
                log.info("✅ Texto extraído exitosamente ({} caracteres)", texto.length());
                return texto;
            }

            return "";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupción al llamar a Google Vision", e);
        } catch (Exception e) {
            log.error("Error al procesar imagen con Google Vision", e);
            throw new IOException("Error al procesar imagen: " + e.getMessage());
        }
    }
}
