package com.andres.curso.springboot.app.springbootcrud.services;

import java.util.List;

import com.andres.curso.springboot.app.springbootcrud.dto.UserDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

public interface UserService {

    List<User> findAll();

    User save(User user);

    void updateUser(User user);

    boolean existsByUsername(String username);

    User findById(Long id);

    User findByUsername(String username);

    List<UserDTO> findUserFriends();

    UserDTO getUserDTO(User user);

    List<UserDTO> findFriendsOfUser(String username);

    void delete(Long id);

    void delete(String username);
}
