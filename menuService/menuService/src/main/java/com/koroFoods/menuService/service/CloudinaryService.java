package com.koroFoods.menuService.service;

import com.cloudinary.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j

public class CloudinaryService {

	private final Cloudinary cloudinary;

    /**
     * Sube una imagen en Base64 a Cloudinary
     * @param base64Image Imagen en formato Base64 (con o sin prefijo data:image/...)
     * @param folder Carpeta en Cloudinary (ej: "korofood/platos", "korofood/eventos")
     * @param publicId ID público para la imagen (ej: "plato_1", "evento_5")
     * @return URL pública de la imagen subida
     */
    public String subirImagen(String base64Image, String folder, String publicId) throws IOException {
        try {
            // Limpiar el prefijo data:image/... si existe
            String base64Data = base64Image;
            if (base64Image.contains(",")) {
                base64Data = base64Image.split(",")[1];
            }

            // Decodificar Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // Subir a Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", publicId,
                    "resource_type", "image",
                    "overwrite", true
            ));

            String url = (String) uploadResult.get("secure_url");
            log.info("✅ Imagen subida exitosamente a Cloudinary: {}", url);
            
            return url;
            
        } catch (Exception e) {
            log.error("❌ Error al subir imagen a Cloudinary", e);
            throw new IOException("Error al subir imagen a Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen de Cloudinary
     * @param publicId ID público de la imagen (ej: "korofood/platos/plato_1")
     */
    public void eliminarImagen(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("✅ Imagen eliminada de Cloudinary: {}", publicId);
        } catch (Exception e) {
            log.error("❌ Error al eliminar imagen de Cloudinary", e);
            throw new IOException("Error al eliminar imagen: " + e.getMessage());
        }
    }

    /**
     * Extrae el public_id desde una URL de Cloudinary
     * @param url URL completa (ej: https://res.cloudinary.com/dvacublsz/image/upload/v123/korofood/platos/plato_1.jpg)
     * @return public_id (ej: korofood/platos/plato_1)
     */
    public String extraerPublicId(String url) {
        if (url == null || !url.contains("cloudinary.com")) {
            return null;
        }

        try {
            // Extraer la parte después de /upload/
            String[] parts = url.split("/upload/");
            if (parts.length < 2) return null;

            String path = parts[1];
            
            // Remover la versión si existe (v123456/)
            path = path.replaceFirst("v\\d+/", "");
            
            // Remover la extensión
            int lastDot = path.lastIndexOf('.');
            if (lastDot > 0) {
                path = path.substring(0, lastDot);
            }

            return path;
        } catch (Exception e) {
            log.error("Error al extraer publicId de URL: {}", url, e);
            return null;
        }
    }
}
