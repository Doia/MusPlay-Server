package com.andres.curso.springboot.app.springbootcrud.dto;

import java.time.LocalDateTime;

public class CommentDTO {

    private Long id;
    private String content;
    private String authorUsername; // Nombre de usuario del autor del comentario
    private LocalDateTime createdDate; // Fecha de creación del comentario

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
