package com.andres.curso.springboot.app.springbootcrud.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andres.curso.springboot.app.springbootcrud.dto.NotificationDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.PrivacyLevel;
import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.UserDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.services.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/id/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return userService.getUserDTO(user);
    }

    @GetMapping("/username/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return userService.getUserDTO(user);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/search/{username}")
    public ResponseEntity<Map<String, Object>> findAllUsersByUsername(@PathVariable String username) {
        List<UserBasicDTO> usersDTO = userService.searchUsersByText(username);
        // List<UserDTO> usersDTO = new ArrayList<>();
        // for (User user : users) {
        // usersDTO.add(userService.getUserDTO(user));
        // }

        Map<String, Object> response = new HashMap<>();
        response.put("data", usersDTO);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        user.setAdmin(false);
        user.setPrivacyLevel(PrivacyLevel.PUBLIC);
        return create(user, result);
    }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    @DeleteMapping("/{username}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String username) {

        userService.delete(username);

        Map<String, Object> response = new HashMap<>();
        response.put("msg", "User deleted successfully");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/notifications/{id}")
    public ResponseEntity<Map<String, Object>> getAllNotificationsByUsername(@PathVariable Long id) {
        List<NotificationDTO> notifications = userService.getAllNotificationsById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("data", notifications);
        return ResponseEntity.ok(response);
    }

    // Follow api

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/follow/{userIdToFollow}")
    public ResponseEntity<?> followUser(@PathVariable Long userIdToFollow) {
        userService.followUser(userIdToFollow);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "Follow request sent successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/unfollow/{userIdToUnfollow}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userIdToUnfollow) {
        userService.unfollowUser(userIdToUnfollow);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "Unfollowed successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/followRequest/accept/{requestId}")
    public ResponseEntity<?> acceptFollowRequest(@PathVariable Long requestId) {
        userService.acceptFollowRequest(requestId);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "Follow request accepted successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/followRequest/reject/{requestId}")
    public ResponseEntity<?> rejectFollowRequest(@PathVariable Long requestId) {
        userService.rejectFollowRequest(requestId);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "Follow request rejected successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/follows/{id}")
    public ResponseEntity<Map<String, Object>> getFollowsById(@PathVariable Long id) {
        Set<User> follows = userService.getFollowsById(id);
        Set<UserDTO> followsDTO = new HashSet<>();
        for (User user : follows) {
            followsDTO.add(userService.getUserDTO(user));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", followsDTO);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/followers/{id}")
    public ResponseEntity<Map<String, Object>> getFollowersById(@PathVariable Long id) {
        Set<User> followers = userService.getFollowersById(id);
        Set<UserDTO> followersDTO = new HashSet<>();
        for (User user : followers) {
            followersDTO.add(userService.getUserDTO(user));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", followersDTO);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
