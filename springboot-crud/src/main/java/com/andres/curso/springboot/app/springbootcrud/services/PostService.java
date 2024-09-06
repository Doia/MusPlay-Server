package com.andres.curso.springboot.app.springbootcrud.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.andres.curso.springboot.app.springbootcrud.dto.CommentDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.PostDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Comment;
import com.andres.curso.springboot.app.springbootcrud.entities.Post;

import jakarta.validation.Valid;

public interface PostService {
    PostDTO save(Post post);

    void delete(Long id);

    PostDTO findById(Long id);

    Page<PostDTO> getFeedPosts(Pageable pageable);

    Page<PostDTO> findPostsByUsername(String username, Pageable pageable);

    Page<PostDTO> findPostsByUserId(Long userId, Pageable pageable);

    CommentDTO addComment(Long postId, @Valid Comment comment);

    void removeLike(Long postId);

    void removeComment(Long postId, Long commentId);

    void addLike(Long postId);
}
