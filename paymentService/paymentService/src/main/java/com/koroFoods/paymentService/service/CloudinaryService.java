package com.koroFoods.paymentService.service;

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

	public String subirImagenBase64(String base64Image, String referenciaPago) throws IOException {
		try {
			// Limpiar el prefijo data:image/... si existe
			String base64Data = base64Image;
			if (base64Image.contains(",")) {
				base64Data = base64Image.split(",")[1];
			}

			// Decodificar Base64
			byte[] imageBytes = Base64.getDecoder().decode(base64Data);

			// Subir a Cloudinary
			Map<String, Object> uploadResult = cloudinary.uploader().upload(imageBytes,
					ObjectUtils.asMap("folder", "korofood/pagos", "public_id", "pago_" + referenciaPago,
							"resource_type", "image", "overwrite", true));

			String url = (String) uploadResult.get("secure_url");
			log.info("Imagen subida exitosamente a Cloudinary: {}", url);

			return url;

		} catch (Exception e) {
			log.error("Error al subir imagen a Cloudinary", e);
			throw new IOException("Error al subir imagen a Cloudinary: " + e.getMessage());
		}
	}
}
