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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    @Autowired
    private UserRepository userRepository;

    private final Path rootLocation = Paths.get("resources/images");

    @Override
    public String storeProfileImage(MultipartFile file, String folder) {

        if (file.isEmpty()) {
            throw new ServerException(ErrorMessages.FILE_IS_EMPTY);
        }

        User authUser = getAuthenticatedUser();

        Path folderPath = Paths.get(rootLocation.toAbsolutePath().toString(), folder);
        System.out.println(folderPath);

        try {
            Files.createDirectories(folderPath); // Crear los directorios si no existen

            // Eliminar la imagen anterior si existe
            if (authUser.getImagePath() != null) {
                this.deleteImage(authUser.getImagePath(), folder);
            }

            // Obtener la extensión del archivo
            String fileExtension = getFileExtension(file);
            // Generar un nombre único para el archivo
            String filename = System.currentTimeMillis() + "_" + authUser.getUsername() + fileExtension;

            // Definir la ruta del archivo de destino
            Path destinationFilePath = folderPath.resolve(filename);

            // Copiar el archivo al destino
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFilePath.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Actualizar el imagePath del usuario en la base de datos
            authUser.setImagePath(filename);
            userRepository.save(authUser);

            return filename;
        } catch (IOException e) {
            throw new ServerException(ErrorMessages.FILE_STORAGE_FAILED);
        }
    }

    public String storeImage(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new ServerException(ErrorMessages.FILE_IS_EMPTY);
        }

        User authUser = getAuthenticatedUser();

        Path folderPath = Paths.get(rootLocation.toAbsolutePath().toString(), folder);
        System.out.println(folderPath);

        try {
            Files.createDirectories(folderPath); // Crear los directorios si no existen

            // Obtener la extensión del archivo
            String fileExtension = getFileExtension(file);
            // Generar un nombre único para el archivo
            String filename = System.currentTimeMillis() + "_" + authUser.getUsername() + fileExtension;

            // Definir la ruta del archivo de destino
            Path destinationFilePath = folderPath.resolve(filename);

            // Copiar el archivo al destino
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFilePath.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
            }

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
