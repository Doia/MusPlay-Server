package com.andres.curso.springboot.app.springbootcrud.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andres.curso.springboot.app.springbootcrud.dto.PostDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Post;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Método para encontrar posts por el propietario
    List<PostDTO> findByOwner(User owner);

    List<PostDTO> findByOwnerIn(Set<User> set);

    // Puedes agregar más métodos personalizados aquí si es necesario
}
