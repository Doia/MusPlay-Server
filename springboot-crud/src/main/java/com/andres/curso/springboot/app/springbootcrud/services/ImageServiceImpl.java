package com.andres.curso.springboot.app.springbootcrud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ErrorMessages;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ServerException;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private UserRepository userRepository;

    private final Path rootLocation = Paths.get("resources/images");

    @Override
    @Transactional
    public String storeProfileImage(MultipartFile file, String folder) {
        User authUser = getAuthenticatedUser();

        Path folderPath = rootLocation.resolve(folder);
        try {
            Files.createDirectories(folderPath); // Crear los directorios si no existen

            if (authUser.getImagePath() != null) {
                // Si el usuario ya tiene una imagen de perfil, eliminarla
                this.deleteImage(authUser.getImagePath(), folder);
            }

            String fileExtension = getFileExtension(file);
            String filename = System.currentTimeMillis() + "_" + authUser.getUsername() + fileExtension;

            Path destinationFilePath = folderPath.resolve(filename);
            Files.copy(file.getInputStream(), destinationFilePath);

            // Actualizar el imagePath del usuario
            authUser.setImagePath(filename);
            userRepository.save(authUser);

            return filename;
        } catch (IOException e) {
            throw new ServerException(ErrorMessages.FILE_STORAGE_FAILED);
        }
    }

    @Override
    public void deleteImage(String filename, String folder) {
        Path folderPath = rootLocation.resolve(folder);
        Path file = folderPath.resolve(filename);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new ServerException(ErrorMessages.FILE_DELETION_FAILED);
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ServerException(ErrorMessages.AUTHENTICATED_USER_NOT_FOUND));
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            throw new ServerException(ErrorMessages.INVALID_FILE_FORMAT);
        }
    }
}
