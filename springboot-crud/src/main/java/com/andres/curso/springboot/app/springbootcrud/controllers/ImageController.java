package com.andres.curso.springboot.app.springbootcrud.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;

import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.services.UserService;
import com.andres.curso.springboot.app.springbootcrud.services.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("")
public class ImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private final Path rootLocationProfileImages = Paths.get("resources/images/profile-images"); // Ruta para almacenar
                                                                                                 // las imágenes
    private final Path rootLocationPostImages = Paths.get("resources/images/post"); // Ruta para almacenar las
                                                                                    // imágenes

    @GetMapping("/post/{filename:.+}")
    public ResponseEntity<Resource> getPostImage(@PathVariable String filename) {
        try {
            Path filePath = rootLocationPostImages.resolve(filename).normalize();
            System.out.println(filePath.toAbsolutePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile-images/{filename:.+}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            Path filePath = rootLocationProfileImages.resolve(filename).normalize();
            System.out.println(filePath.toAbsolutePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/profile-images/upload")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Entroo!!!");
            // Guardar la nueva imagen
            String filename = imageService.storeProfileImage(file, "/profile-images");

            // Crear un mapa para la respuesta JSON
            Map<String, String> response = new HashMap<>();
            response.put("msg", "Profile image uploaded successfully");
            response.put("profileImagePath", filename);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload profile image");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/profile-images/delete")
    public ResponseEntity<String> deleteProfileImage(@AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser.getImagePath() != null) {
                imageService.deleteImage(currentUser.getImagePath(), "/profile-images");
                currentUser.setImagePath(null);
                userService.privateSave(currentUser);
                return ResponseEntity.ok("Profile image deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile image to delete");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete profile image");
        }
    }
}
