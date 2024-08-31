package com.andres.curso.springboot.app.springbootcrud.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

public interface PostDTO {

    Long getId();

    String getContent();

    String getImagePath();

    UserBasicDTO getOwner();

    @NotNull
    LocalDateTime getCreatedDate();

    Set<UserBasicDTO> getLikes();

    List<CommentDTO> getComments();
}
