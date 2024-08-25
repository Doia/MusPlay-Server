package com.andres.curso.springboot.app.springbootcrud.services;

import java.util.List;

import com.andres.curso.springboot.app.springbootcrud.dto.CommentDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.PostDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Comment;
import com.andres.curso.springboot.app.springbootcrud.entities.Post;

import jakarta.validation.Valid;

public interface PostService {
    PostDTO save(Post post);

    void delete(Long id);

    PostDTO findById(Long id);

    List<PostDTO> findPostsByUsername(String username);

    CommentDTO addComment(Long postId, @Valid Comment comment);

    void removeLike(Long postId);

    void removeComment(Long postId, Long commentId);

    void addLike(Long postId);
}
