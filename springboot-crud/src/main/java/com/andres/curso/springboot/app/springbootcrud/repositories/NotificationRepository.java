package com.andres.curso.springboot.app.springbootcrud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andres.curso.springboot.app.springbootcrud.dto.NotificationDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Notification;
import com.andres.curso.springboot.app.springbootcrud.entities.NotificationType;
import com.andres.curso.springboot.app.springbootcrud.entities.Post;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<NotificationDTO> findByReceiverOrderByCreatedAtDesc(User receiver);

    // Método para obtener una notificación donde coincida el usuario, post y el
    // tipo sea LIKE
    Optional<Notification> findBySenderAndPostAndType(User sender, Post post, NotificationType type);
}