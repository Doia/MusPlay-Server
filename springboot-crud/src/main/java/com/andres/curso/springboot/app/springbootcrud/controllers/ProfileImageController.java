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
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("/profile-images")
public class ProfileImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private final Path rootLocation = Paths.get("resources/profile-images"); // Ruta para almacenar las imágenes

    @PostMapping("/upload")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {

            // Guardar la nueva imagen
            String filename = imageService.storeImage(file, "/profile-images");

            return ResponseEntity.status(HttpStatus.CREATED).body("Profile image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile image");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProfileImage(@AuthenticationPrincipal User currentUser) {
        try {
            if (currentUser.getImagePath() != null) {
                imageService.deleteImage(currentUser.getImagePath(), "/profile-images");
                currentUser.setImagePath(null);
                userService.updateUser(currentUser);
                return ResponseEntity.ok("Profile image deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile image to delete");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete profile image");
        }
    }

    @GetMapping("/get/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
