package com.andres.curso.springboot.app.springbootcrud.services;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.andres.curso.springboot.app.springbootcrud.dto.CommentDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.PostDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.PostDTOImpl;
import com.andres.curso.springboot.app.springbootcrud.dto.PrivacyLevel;
import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Comment;
import com.andres.curso.springboot.app.springbootcrud.entities.Post;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ErrorMessages;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ServerException;
import com.andres.curso.springboot.app.springbootcrud.repositories.CommentRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.PostRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public PostDTO save(Post post) {

        User authenticatedUser = getAuthenticatedUser();

        post.setOwner(authenticatedUser); // Asigna el usuario autenticado como el propietario del post
        post.setCreatedDate(LocalDateTime.now()); // Set the date to the current time

        try {
            return convertToDTO(postRepository.save(post));
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.POST_CREATION_FAILED);
        }
    }

    @Override
    public void delete(Long id) {
        User authenticatedUser = getAuthenticatedUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorMessages.POST_NOT_FOUND));

        // Verifica que el usuario autenticado es el propietario del post o tiene
        // permisos de administrador
        if (!post.getOwner().equals(authenticatedUser) && !authenticatedUser.isAdmin()) {
            throw new ServerException(ErrorMessages.POST_UNAUTHORIZED);
        }

        try {
            // Eliminar todos los comentarios asociados al post
            commentRepository.deleteByPostId(id);

            // Luego eliminar el post
            postRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.POST_DELETION_FAILED);
        }
    }

    @Override
    public PostDTO findById(Long id) {
        User authenticatedUser = getAuthenticatedUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorMessages.POST_NOT_FOUND));

        // Verifica que el usuario autenticado es el propietario del post, tiene
        // permisos de administrador, o el post es público
        if (!post.getOwner().equals(authenticatedUser) && !authenticatedUser.isAdmin() &&
                !authenticatedUser.isFollowing(post.getOwner().getUsername())) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        return convertToDTO(post);
    }

    @Override
    public List<PostDTO> getFeedPosts() {
        User authenticatedUser = getAuthenticatedUser();

        try {
            // Obtén los posts del usuario
            List<PostDTO> userPosts = postRepository.findByOwner(authenticatedUser);

            // Obtén los posts de los amigos del usuario
            List<PostDTO> friendsPosts = postRepository.findByOwnerIn(authenticatedUser.getFollows());

            // Combina las listas de posts y ordénalas por fecha
            List<PostDTO> allPosts = Stream.concat(userPosts.stream(), friendsPosts.stream())
                    .sorted(Comparator.comparing(PostDTO::getCreatedDate).reversed())
                    .collect(Collectors.toList());

            return allPosts;// .stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }
    }

    @Override
    public List<PostDTO> findPostsByUsername(String username) {
        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        // El usuario debe ser público, un amigo, o el remitente
        if (user.getPrivacyLevel() != PrivacyLevel.PUBLIC && !user.equals(authenticatedUser)
                && !authenticatedUser.isFollowing(user.getUsername())) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        try {
            List<PostDTO> posts = postRepository.findByOwner(user);
            return posts;// .stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }

    }

    // Comentarios

    @Override
    public CommentDTO addComment(Long postId, @Valid Comment comment) {
        User authenticatedUser = getAuthenticatedUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServerException(ErrorMessages.POST_NOT_FOUND));

        // No puedes comentar si no tienes acceso a ver ese post
        if (!post.getOwner().equals(authenticatedUser) && !authenticatedUser.isAdmin() &&
                !authenticatedUser.isFollowing(post.getOwner().getUsername())) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        // Asignar el post y el autor al comentario
        comment.setPost(post);
        comment.setOwner(authenticatedUser);
        comment.setCreatedDate(LocalDateTime.now());

        try {
            // Guardar el comentario
            commentRepository.save(comment);
            // Añadir el comentario al post y actualizar
            post.getComments().add(comment);
            postRepository.save(post);
        } catch (Exception e) {
            // volvemos al estado anterior
            throw new ServerException(ErrorMessages.COMMENT_CREATION_FAILED);
        }
        return comment.toCommentDTO();
    }

    @Override
    public void removeComment(Long postId, Long commentId) {
        User authenticatedUser = getAuthenticatedUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServerException(ErrorMessages.POST_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ServerException(ErrorMessages.COMMENT_NOT_FOUND));

        // Si el comentario es tuyo o eres admin o eres el dueño del post

        if (!post.getOwner().equals(authenticatedUser) && !authenticatedUser.isAdmin() &&
                !comment.getOwner().equals(authenticatedUser)) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        try {
            commentRepository.delete(comment);
            // Añadir el comentario al post y actualizar
            post.getComments().remove(comment);
            postRepository.save(post);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.COMMENT_DELETION_FAILED);
        }
    }

    // Likes

    @Override
    public void addLike(Long postId) {
        User authenticatedUser = getAuthenticatedUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServerException(ErrorMessages.POST_NOT_FOUND));

        // No puedes dar like si no tienes acceso a ver ese post
        if (!post.getOwner().equals(authenticatedUser) && !authenticatedUser.isAdmin() &&
                !authenticatedUser.isFollowing(post.getOwner().getUsername())) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        try {
            // Añadir el like si no existe ya
            if (!post.getLikes().contains(authenticatedUser)) {
                post.getLikes().add(authenticatedUser);
                postRepository.save(post);
            }
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.LIKE_CREATION_FAILED);
        }

    }

    @Override
    public void removeLike(Long postId) {
        User authenticatedUser = getAuthenticatedUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServerException(ErrorMessages.POST_NOT_FOUND));

        try {
            // Añadir el like si no existe ya
            if (post.getLikes().contains(authenticatedUser)) {
                post.getLikes().remove(authenticatedUser);
                postRepository.save(post);
            }
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.LIKE_REMOVAL_FAILED);
        }
    }

    private PostDTO convertToDTO(Post post) {
        if (post == null) {
            return null; // O lanzar una excepción si prefieres manejar el caso de `null`
        }

        return new PostDTOImpl(
                post.getId(),
                post.getContent(),
                post.getImagePath(),
                post.getOwner().toUserBasicDTO(), // Utilizamos el método que creamos en User
                post.getCreatedDate(),
                post.getLikes().stream()
                        .map(User::toUserBasicDTO) // Utilizamos el método directamente desde la clase User
                        .collect(Collectors.toSet()),
                post.getComments().stream()
                        .map(Comment::toCommentDTO) // Convertir cada comentario a CommentDTO
                        .collect(Collectors.toList()));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ServerException(ErrorMessages.AUTHENTICATED_USER_NOT_FOUND));
    }
}
