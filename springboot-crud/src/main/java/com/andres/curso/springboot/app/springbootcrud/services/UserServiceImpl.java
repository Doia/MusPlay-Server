package com.andres.curso.springboot.app.springbootcrud.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andres.curso.springboot.app.springbootcrud.dto.PrivacyLevel;
import com.andres.curso.springboot.app.springbootcrud.dto.UserDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Role;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.repositories.RoleRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;

import org.springframework.security.core.Authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
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
    @Transactional
    public User save(User user) {

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();

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
        Optional<User> resp = repository.findById(id);
        if (resp.isPresent()) {
            return resp.get();
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> resp = repository.findByUsername(username);
        if (resp.isPresent()) {
            return resp.get();
        }
        return null;
    }

    @Override
    public List<UserDTO> findUserFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticationUser = repository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticationUser = repository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

    // Método para obtener el DTO basado en el nivel de acceso
    public UserDTO getUserDTO(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = repository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Verificar si es el propietario o un administrador
        if (currentUser.getRoles().contains("ROLE_ADMIN") || currentUser.getId().equals(user.getId())) {

            UserDTO userDTO = new UserDTO(user, PrivacyLevel.FULL);

            ObjectMapper objectMapper = new ObjectMapper();

            try {
                String jsonString = objectMapper.writeValueAsString(user);
                System.out.println(jsonString);
                jsonString = objectMapper.writeValueAsString(userDTO);
                System.out.println(jsonString);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return userDTO;

        }

        // Verificar si son amigos o si el perfil es público
        if (user.getPrivacyLevel() == PrivacyLevel.PUBLIC || currentUser.getFriends().contains(user.getUsername())) {
            return new UserDTO(user, PrivacyLevel.PUBLIC);
        }

        // Si el perfil es privado y no son amigos
        return new UserDTO(user, PrivacyLevel.PRIVATE);
    }

}
