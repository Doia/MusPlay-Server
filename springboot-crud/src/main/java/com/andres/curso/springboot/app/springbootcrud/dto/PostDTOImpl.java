package com.andres.curso.springboot.app.springbootcrud.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

public class PostDTOImpl implements PostDTO {

    private Long id;
    private String content;
    private String imagePath;
    private UserBasicDTO owner;
    private LocalDateTime createdDate;
    private Set<UserBasicDTO> likes;
    private List<CommentDTO> comments;

    // Constructor
    public PostDTOImpl() {
    }

    public PostDTOImpl(Long id, String content, String imagePath, UserBasicDTO owner,
            @NotNull LocalDateTime createdDate, Set<UserBasicDTO> likes,
            List<CommentDTO> comments) {
        this.id = id;
        this.content = content;
        this.imagePath = imagePath;
        this.owner = owner;
        this.createdDate = createdDate;
        this.likes = likes;
        this.comments = comments;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public UserBasicDTO getOwner() {
        return owner;
    }

    public void setOwner(UserBasicDTO owner) {
        this.owner = owner;
    }

    @Override
    @NotNull
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Set<UserBasicDTO> getLikes() {
        return likes;
    }

    public void setLikes(Set<UserBasicDTO> likes) {
        this.likes = likes;
    }

    @Override
    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
