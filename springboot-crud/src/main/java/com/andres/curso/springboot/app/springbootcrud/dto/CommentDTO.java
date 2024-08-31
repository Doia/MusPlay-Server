package com.andres.curso.springboot.app.springbootcrud.dto;

import java.time.LocalDateTime;

public interface CommentDTO {

    Long getId();

    String getContent();

    LocalDateTime getCreatedAt();

    // Métodos para obtener solo los atributos específicos de Post y User
    PostSummaryDTO getPost();

    UserBasicDTO getOwner();

    interface PostSummaryDTO {
        Long getId();
    }

}
