package com.andres.curso.springboot.app.springbootcrud.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andres.curso.springboot.app.springbootcrud.entities.Post;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Método para encontrar posts por el propietario
    List<Post> findByOwner(User owner);

    // Puedes agregar más métodos personalizados aquí si es necesario
}
