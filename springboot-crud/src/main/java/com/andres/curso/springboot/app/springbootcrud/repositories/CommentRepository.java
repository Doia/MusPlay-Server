package com.andres.curso.springboot.app.springbootcrud.repositories;

import com.andres.curso.springboot.app.springbootcrud.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Puedes agregar métodos personalizados si es necesario, por ejemplo:
    // List<Comment> findByPostId(Long postId);
    void deleteByPostId(Long postId);
}
