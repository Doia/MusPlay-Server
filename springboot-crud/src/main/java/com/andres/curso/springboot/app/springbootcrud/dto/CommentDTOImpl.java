package com.andres.curso.springboot.app.springbootcrud.dto;

import java.time.LocalDateTime;

public class CommentDTOImpl implements CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private PostSummaryDTO post;
    private UserBasicDTO owner;

    public CommentDTOImpl(Long id, String content, LocalDateTime createdAt, PostSummaryDTO post,
            UserBasicDTO owner) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.post = post;
        this.owner = owner;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public PostSummaryDTO getPost() {
        return post;
    }

    @Override
    public UserBasicDTO getOwner() {
        return owner;
    }

    public static class PostSummaryDTOImpl implements PostSummaryDTO {
        private Long id;

        public PostSummaryDTOImpl(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }
    }
}
