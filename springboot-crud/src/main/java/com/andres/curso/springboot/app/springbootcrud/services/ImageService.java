package com.andres.curso.springboot.app.springbootcrud.services;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageService {
    public String storeProfileImage(MultipartFile file, String folder) throws IOException;

    public void deleteImage(String filename, String folder) throws IOException;
}