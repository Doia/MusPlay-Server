package com.andres.curso.springboot.app.springbootcrud.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andres.curso.springboot.app.springbootcrud.dto.PrivacyLevel;
import com.andres.curso.springboot.app.springbootcrud.dto.UserDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Role;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ErrorMessages;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ServerException;
import com.andres.curso.springboot.app.springbootcrud.repositories.RoleRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Override
    public User save(User user) {
        List<Role> roles = new ArrayList<>();

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        optionalRoleUser.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
    }

    @Override
    public List<UserDTO> findUserFriends() {
        User authenticationUser = getAuthenticatedUser();

        Set<String> friendsUsernames = authenticationUser.getFriends();
        if (friendsUsernames.isEmpty()) {
            return List.of(); // Devuelve una lista vacía si no tiene amigos
        }

        List<User> friends = repository.findByUsernameIn(friendsUsernames.stream().collect(Collectors.toList()));

        return friends.stream()
                .map(friend -> new UserDTO(friend, PrivacyLevel.SAMPLE))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findFriendsOfUser(String username) {
        User authenticationUser = getAuthenticatedUser();

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        Set<String> friendsUsernames = user.getFriends();
        if (friendsUsernames.isEmpty()) {
            return List.of(); // Devuelve una lista vacía si no tiene amigos
        }

        List<User> friends = repository.findByUsernameIn(friendsUsernames.stream().collect(Collectors.toList()));

        return friends.stream()
                .map(friend -> {
                    UserDTO resp = new UserDTO(friend, PrivacyLevel.SAMPLE);
                    resp.setIsAuthenticationUserFriend(authenticationUser.getFriends().contains(friend.getUsername()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(User user) {
        repository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = null;
        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getId() != id && !authenticatedUser.isAdmin()) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        if (authenticatedUser.getId() != id) {
            user = repository.findById(id)
                    .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
        } else {
            user = authenticatedUser;
        }

        try {
            repository.delete(user);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.USER_DELETION_FAILED);
        }
    }

    @Override
    public void delete(String username) {
        User user = null;
        User authenticatedUser = getAuthenticatedUser();

        if (!authenticatedUser.getUsername().equals(username) && !authenticatedUser.isAdmin()) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        if (authenticatedUser.getUsername() != username) {
            user = repository.findByUsername(username)
                    .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
        } else {
            user = authenticatedUser;
        }

        try {
            repository.delete(user);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.USER_DELETION_FAILED);
        }
    }

    @Override
    public UserDTO getUserDTO(User user) {
        User currentUser = getAuthenticatedUser();

        // Verificar si es el propietario o un administrador
        if (currentUser.getRoles().contains("ROLE_ADMIN") || currentUser.getId().equals(user.getId())) {
            return createUserDTOWithFullPrivacy(user);
        }

        // Verificar si son amigos o si el perfil es público
        if (user.getPrivacyLevel() == PrivacyLevel.PUBLIC || currentUser.getFriends().contains(user.getUsername())) {
            return new UserDTO(user, PrivacyLevel.PUBLIC);
        }

        // Si el perfil es privado y no son amigos
        return new UserDTO(user, PrivacyLevel.PRIVATE);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ServerException(ErrorMessages.AUTHENTICATED_USER_NOT_FOUND));
    }

    private UserDTO createUserDTOWithFullPrivacy(User user) {
        UserDTO userDTO = new UserDTO(user, PrivacyLevel.FULL);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(user);
            jsonString = objectMapper.writeValueAsString(userDTO);
        } catch (JsonProcessingException e) {
            throw new ServerException(ErrorMessages.JSON_PROCESSING_ERROR);
        }
        return userDTO;
    }
}
