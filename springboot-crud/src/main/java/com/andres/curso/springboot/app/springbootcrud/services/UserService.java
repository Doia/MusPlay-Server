package com.andres.curso.springboot.app.springbootcrud.services;

import java.util.List;
import java.util.Set;

import com.andres.curso.springboot.app.springbootcrud.dto.NotificationDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.UserDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

public interface UserService {

    List<User> findAll();

    UserDTO update(User user);

    User privateSave(User user);

    boolean existsByUsername(String username);

    User findById(Long id);

    User findByUsername(String username);

    List<UserBasicDTO> searchUsersByText(String username);

    UserDTO getUserDTO(User user);

    void delete(Long id);

    void delete(String username);

    List<NotificationDTO> getAllNotificationsById(Long id);

    // follow
    public void followUser(Long userIdToFollow);

    public void unfollowUser(Long userIdToUnfollow);

    public void acceptFollowRequest(Long requestId);

    public void rejectFollowRequest(Long requestId);

    Set<User> getFollowsById(Long id);

    Set<User> getFollowersById(Long id);

}
