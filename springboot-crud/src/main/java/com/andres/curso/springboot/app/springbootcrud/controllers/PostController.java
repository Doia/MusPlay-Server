package com.andres.curso.springboot.app.springbootcrud.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.andres.curso.springboot.app.springbootcrud.dto.CommentDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.PostDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Comment;
import com.andres.curso.springboot.app.springbootcrud.entities.Post;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ErrorMessages;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ServerException;
import com.andres.curso.springboot.app.springbootcrud.services.ImageService;
import com.andres.curso.springboot.app.springbootcrud.services.PostService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RestController
@RequestMapping("/posts")
public class PostController {

    int pageSize = 10;

    @Autowired
    private PostService postService;

    @Autowired
    private ImageService imageService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestParam("text") String text,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        // Validar los datos (puedes añadir más validaciones según sea necesario)
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("msg", "El texto del post no puede estar vacío"));
        }

        // Inicializar imagePath
        String imagePath = null;

        // Si se ha proporcionado un archivo, procesarlo
        if (file != null && !file.isEmpty()) {
            try {
                imagePath = imageService.storeImage(file, "/post");
            } catch (IOException e) {
                throw new ServerException(ErrorMessages.POST_CREATION_FAILED);
            }
        }

        // Crear el objeto Post
        Post post = new Post();
        post.setContent(text);
        post.setImagePath(imagePath);
        PostDTO savedPost;
        try {
            // Guardar el post usando el servicio correspondiente
            savedPost = postService.save(post);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.POST_CREATION_FAILED);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Post creado con éxito");
        response.put("post", savedPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id) {
        postService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "Post deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> modifyPost(@PathVariable Long id, @Valid @RequestBody Post post,
            BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        try {
            PostDTO existingPost = postService.findById(id);
            if (existingPost == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Post not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            post.setId(id);
            PostDTO updatedPost = postService.save(post);

            Map<String, Object> response = new HashMap<>();
            response.put("msg", "Post updated successfully");
            response.put("post", updatedPost);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while updating the post");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/feed")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getFeedPosts(
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<PostDTO> feedPosts = postService.getFeedPosts(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Feed posts retrieved successfully");
        response.put("posts", feedPosts.getContent());
        response.put("totalPages", feedPosts.getTotalPages());
        response.put("totalElements", feedPosts.getTotalElements());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/userId/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPostByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<PostDTO> posts = postService.findPostsByUserId(userId, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Posts retrieved successfully");
        response.put("posts", posts.getContent());
        response.put("totalPages", posts.getTotalPages());
        response.put("totalElements", posts.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getPostByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<PostDTO> posts = postService.findPostsByUsername(username, pageable);
        if (posts.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "No posts found for user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Posts retrieved successfully");
        response.put("posts", posts.getContent());
        response.put("totalPages", posts.getTotalPages());
        response.put("totalElements", posts.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // Create a Comment
    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createComment(@PathVariable Long postId,
            @Valid @RequestBody Comment comment, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        CommentDTO commentDTO = postService.addComment(postId, comment);
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Comment added successfully");
        response.put("comment", commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // Delete a Comment
    @DeleteMapping("/{postId}/comments/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.removeComment(postId, commentId);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "Comment deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Add a Like
    @PostMapping("/{postId}/likes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addLike(@PathVariable Long postId) {
        postService.addLike(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Like added successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Remove a Like
    @DeleteMapping("/{postId}/likes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> removeLike(@PathVariable Long postId) {
        postService.removeLike(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Like removed successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private ResponseEntity<Map<String, Object>> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
