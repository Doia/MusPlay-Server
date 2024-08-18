package com.andres.curso.springboot.app.springbootcrud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private UserRepository userRepository;
    
    private final Path rootLocation = Paths.get("resources/images");

    public String storeImage(MultipartFile file, String folder) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Path folderPath = rootLocation.resolve(rootLocation.toAbsolutePath() + "/" + folder);
        Files.createDirectories(folderPath.toAbsolutePath());  // Crear los directorios si no existen

        if (authUser.getImagePath() != null) {
            // Si el usuario ya tiene una imagen de perfil, eliminarla
            this.deleteImage(authUser.getImagePath(), folder);
        }
        
        String fileExtension = "";
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = System.currentTimeMillis() + "_" + authUser.getUsername() + fileExtension;

        System.out.println(rootLocation.resolve(folderPath.toAbsolutePath() + "/" + filename).toAbsolutePath());

        
        Files.copy(file.getInputStream(), rootLocation.resolve(folderPath.toAbsolutePath() + "/" + filename).toAbsolutePath());

        
        // Actualizar el imagePath del usuario
        authUser.setImagePath(filename);
        userRepository.save(authUser);

        return filename;
    }

    public void deleteImage(String filename, String folder) throws IOException {
        Path folderPath = rootLocation.resolve(rootLocation.toAbsolutePath() + "/" + folder);
        Path file = rootLocation.resolve(folderPath.toAbsolutePath() + "/" + filename);
        Files.deleteIfExists(file.toAbsolutePath());
    }
}
