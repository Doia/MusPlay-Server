package com.andres.curso.springboot.app.springbootcrud.repositories;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andres.curso.springboot.app.springbootcrud.dto.PostDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Post;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Método para encontrar posts por el propietario
    Page<PostDTO> findByOwner(User owner, Pageable pageable);

    Page<PostDTO> findByOwnerIn(Set<User> set, Pageable pageable);

    // Puedes agregar más métodos personalizados aquí si es necesario
}
