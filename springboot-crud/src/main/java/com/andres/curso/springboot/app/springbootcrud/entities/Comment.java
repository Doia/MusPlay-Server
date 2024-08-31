package com.andres.curso.springboot.app.springbootcrud.entities;

import java.time.LocalDateTime;

import com.andres.curso.springboot.app.springbootcrud.dto.CommentDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.CommentDTOImpl;
import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @NotNull
    @Size(max = 256)
    private String content;

    private LocalDateTime createdAt;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedDate() {
        return createdAt;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdAt = createdDate;
    }

    // Dtos

    // Método para convertir la entidad Comment a su CommentDTO
    public CommentDTO toCommentDTO() {
        // Convertimos el autor a UserBasicDTO usando el método definido en User
        UserBasicDTO ownerDTO = owner.toUserBasicDTO();
        // Creamos un PostSummaryDTO a partir de la información de la entidad Post
        CommentDTO.PostSummaryDTO postSummaryDTO = new CommentDTOImpl.PostSummaryDTOImpl(post.getId());

        // Retornamos la implementación de CommentDTO
        return new CommentDTOImpl(id, content, createdAt, postSummaryDTO, ownerDTO);
    }
}
