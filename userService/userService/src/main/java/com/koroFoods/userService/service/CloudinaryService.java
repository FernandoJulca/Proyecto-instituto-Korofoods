package com.koroFoods.userService.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String carpeta) throws IOException {

        try {
            Map uploadCloud = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", carpeta));
            return uploadCloud.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
