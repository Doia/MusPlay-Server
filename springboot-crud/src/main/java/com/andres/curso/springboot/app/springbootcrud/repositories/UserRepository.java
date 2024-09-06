package com.andres.curso.springboot.app.springbootcrud.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findByUsernameIn(List<String> usernames);

    List<UserBasicDTO> findByUsernameContainingOrNameContaining(String username, String name);
}
