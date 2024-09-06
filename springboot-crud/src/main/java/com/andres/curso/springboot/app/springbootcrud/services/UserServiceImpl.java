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

import com.andres.curso.springboot.app.springbootcrud.dto.NotificationDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.PrivacyLevel;
import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.UserDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.FollowRequest;
import com.andres.curso.springboot.app.springbootcrud.entities.FollowRequestStatus;
import com.andres.curso.springboot.app.springbootcrud.entities.Notification;
import com.andres.curso.springboot.app.springbootcrud.entities.NotificationType;
import com.andres.curso.springboot.app.springbootcrud.entities.Role;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ErrorMessages;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ServerException;
import com.andres.curso.springboot.app.springbootcrud.repositories.FollowRequestRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.NotificationRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.RoleRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User privateSave(User user) {
        List<Role> roles = new ArrayList<>();

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        optionalRoleUser.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDTO update(User user) {
        User authenticatedUser = getAuthenticatedUser();

        User oldUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        if (!authenticatedUser.isAdmin() && authenticatedUser.getId() != user.getId()) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        // Solo modificamos Nombre, Descripcion, Telefono y nivel de privacidad
        oldUser.setName(user.getName());
        oldUser.setDescription(user.getDescription());
        oldUser.setPhone(user.getPhone());
        oldUser.setPrivacyLevel(user.getPrivacyLevel());

        return getUserDTO(userRepository.save(oldUser));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
    }

    @Override
    public List<UserBasicDTO> searchUsersByText(String searchText) {
        User authenticatedUser = getAuthenticatedUser();

        List<UserBasicDTO> users = userRepository.findByUsernameContainingOrNameContaining(searchText, searchText);

        users = users.stream()
                .sorted((user1, user2) -> {
                    // Manejo de null en username y name
                    String username1 = user1.getUsername() != null ? user1.getUsername() : "";
                    String username2 = user2.getUsername() != null ? user2.getUsername() : "";
                    String name1 = user1.getName() != null ? user1.getName() : "";
                    String name2 = user2.getName() != null ? user2.getName() : "";

                    // Comparación por coincidencia en username y name
                    int usernameMatch1 = username1.indexOf(searchText);
                    int usernameMatch2 = username2.indexOf(searchText);
                    int nameMatch1 = name1.indexOf(searchText);
                    int nameMatch2 = name2.indexOf(searchText);

                    // Selecciona la mejor coincidencia entre username y name para cada usuario
                    int match1 = Math.min(usernameMatch1 == -1 ? Integer.MAX_VALUE : usernameMatch1,
                            nameMatch1 == -1 ? Integer.MAX_VALUE : nameMatch1);
                    int match2 = Math.min(usernameMatch2 == -1 ? Integer.MAX_VALUE : usernameMatch2,
                            nameMatch2 == -1 ? Integer.MAX_VALUE : nameMatch2);

                    // Ordenar por coincidencia
                    if (match1 != match2) {
                        return Integer.compare(match1, match2);
                    }

                    // Si la coincidencia es igual, ordenar si authenticatedUser sigue al usuario
                    boolean follows1 = authenticatedUser.getFollows().contains(user1);
                    boolean follows2 = authenticatedUser.getFollows().contains(user2);

                    if (follows1 != follows2) {
                        return Boolean.compare(follows2, follows1);
                    }
                    return 0;
                    // Finalmente, ordenar por la cantidad de seguidores
                    // return Integer.compare(user2.getFollowers().size(),
                    // user1.getFollowers().size());
                })
                .collect(Collectors.toList());

        return users;
    }

    @Override
    public void delete(Long id) {
        User user = null;
        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getId() != id && !authenticatedUser.isAdmin()) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        if (authenticatedUser.getId() != id) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
        } else {
            user = authenticatedUser;
        }

        try {
            userRepository.delete(user);
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
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
        } else {
            user = authenticatedUser;
        }

        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.USER_DELETION_FAILED);
        }
    }

    @Override
    public List<NotificationDTO> getAllNotificationsById(Long id) {

        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        // No puedes comentar si no tienes acceso a ver ese post
        if (!user.equals(authenticatedUser) && !authenticatedUser.isAdmin()) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        try {
            return notificationRepository.findByReceiverOrderByCreatedAtDesc(user);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }
    }
    // Follows code

    @Override
    public void followUser(Long userIdToFollow) {
        User currentUser = getAuthenticatedUser();
        User userToFollow = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        if (currentUser.equals(userToFollow)) {
            throw new ServerException(ErrorMessages.CANNOT_FOLLOW_YOURSELF);
        }

        if (currentUser.getFollows().contains(userToFollow)) {
            throw new ServerException(ErrorMessages.ALREADY_FOLLOWING_USER);
        }

        if (userToFollow.getPrivacyLevel() == PrivacyLevel.PRIVATE) {
            Optional<FollowRequest> existingRequest = followRequestRepository
                    .findBySenderAndReceiverAndStatus(currentUser, userToFollow, FollowRequestStatus.PENDING);

            if (existingRequest.isPresent()) {
                throw new ServerException(ErrorMessages.FOLLOW_REQUEST_ALREADY_SENT);
            }

            FollowRequest followRequest = new FollowRequest();
            followRequest.setSender(currentUser);
            followRequest.setReceiver(userToFollow);
            followRequest.setStatus(FollowRequestStatus.PENDING);

            try {
                followRequestRepository.save(followRequest);

                Notification notification = new Notification(userToFollow, currentUser, NotificationType.FOLLOW_REQUEST,
                        followRequest);
                notificationRepository.save(notification);
            } catch (Exception e) {
                throw new ServerException(ErrorMessages.FOLLOW_OPERATION_FAILED);
            }

        } else {
            try {

                FollowRequest followRequest = new FollowRequest();
                followRequest.setSender(currentUser);
                followRequest.setReceiver(userToFollow);
                followRequest.setStatus(FollowRequestStatus.ACCEPTED);

                currentUser.getFollows().add(userToFollow);
                userToFollow.getFollowers().add(currentUser);

                userRepository.save(currentUser);
                userRepository.save(userToFollow);

                Notification notification = new Notification(userToFollow, currentUser, NotificationType.FOLLOW_REQUEST,
                        followRequest);
                notificationRepository.save(notification);

            } catch (Exception e) {
                throw new ServerException(ErrorMessages.FOLLOW_OPERATION_FAILED);
            }

        }
    }

    @Override
    public void unfollowUser(Long userIdToUnfollow) {
        User currentUser = getAuthenticatedUser();
        User userToUnfollow = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        if (!currentUser.getFollows().contains(userToUnfollow)) {
            throw new ServerException(ErrorMessages.UNFOLLOW_OPERATION_FAILED);
        }

        try {
            currentUser.getFollows().remove(userToUnfollow);
            userToUnfollow.getFollowers().remove(currentUser);

            userRepository.save(currentUser);
            userRepository.save(userToUnfollow);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.UNFOLLOW_OPERATION_FAILED);
        }

    }

    @Override
    public void acceptFollowRequest(Long requestId) {

        User authenticatedUser = getAuthenticatedUser();

        FollowRequest followRequest = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ServerException(ErrorMessages.FOLLOW_REQUEST_NOT_FOUND));

        // Verificar que el usuario autenticado es el receptor de la solicitud o un
        // administrador
        if (!authenticatedUser.equals(followRequest.getReceiver()) && !authenticatedUser.isAdmin()) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        if (followRequest.getStatus() != FollowRequestStatus.PENDING) {
            throw new ServerException(ErrorMessages.FOLLOW_REQUEST_NOT_PENDING);
        }

        try {
            followRequest.setStatus(FollowRequestStatus.ACCEPTED);

            User sender = followRequest.getSender();
            User receiver = followRequest.getReceiver();

            sender.getFollows().add(receiver);
            receiver.getFollowers().add(sender);

            userRepository.save(sender);
            userRepository.save(receiver);
            followRequestRepository.save(followRequest);

        } catch (Exception e) {
            throw new ServerException(ErrorMessages.ACCEPT_FOLLOW_OPERATION_FAILED);
        }

    }

    @Override
    public void rejectFollowRequest(Long requestId) {

        User authenticatedUser = getAuthenticatedUser();

        FollowRequest followRequest = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ServerException(ErrorMessages.FOLLOW_REQUEST_NOT_FOUND));

        // Verificar que el usuario autenticado es el receptor de la solicitud o un
        // administrador
        if (!authenticatedUser.equals(followRequest.getReceiver()) && !authenticatedUser.isAdmin()) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        if (followRequest.getStatus() != FollowRequestStatus.PENDING) {
            throw new ServerException(ErrorMessages.FOLLOW_REQUEST_NOT_PENDING);
        }

        try {
            followRequest.setStatus(FollowRequestStatus.REJECTED);
            followRequestRepository.save(followRequest);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.REJECT_FOLLOW_OPERATION_FAILED);
        }

    }

    @Override
    public Set<User> getFollowsById(Long id) {

        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        if (!user.equals(authenticatedUser) && !authenticatedUser.isAdmin() &&
                !authenticatedUser.getFollows().contains(user)) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        return user.getFollows();
    }

    @Override
    public Set<User> getFollowersById(Long id) {

        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        if (!user.equals(authenticatedUser) && !authenticatedUser.isAdmin() &&
                !authenticatedUser.getFollows().contains(user)) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        return user.getFollowers();
    }

    // utils
    @Override
    public UserDTO getUserDTO(User user) {
        User authenticateUser = getAuthenticatedUser();

        // Verificar si es el propietario o un administrador
        if (authenticateUser.getRoles().contains("ROLE_ADMIN") || authenticateUser.getId().equals(user.getId())) {
            return createUserDTOWithFullPrivacy(user);
        }

        // Verificar si authenticate user sigue al usuario o si el perfil es público
        if (user.getPrivacyLevel() == PrivacyLevel.PUBLIC
                || authenticateUser.getFollows().contains(user)) {
            return new UserDTO(user, PrivacyLevel.PUBLIC);
        }

        // Si el perfil es privado y no son amigos
        return new UserDTO(user, PrivacyLevel.PRIVATE);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ServerException(ErrorMessages.AUTHENTICATED_USER_NOT_FOUND));
    }

    private UserDTO createUserDTOWithFullPrivacy(User user) {
        UserDTO userDTO = new UserDTO(user, PrivacyLevel.FULL);
        return userDTO;
    }

}
