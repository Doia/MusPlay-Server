package com.andres.curso.springboot.app.springbootcrud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andres.curso.springboot.app.springbootcrud.dto.NotificationDTO;
import com.andres.curso.springboot.app.springbootcrud.entities.Notification;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<NotificationDTO> findByReceiverOrderByCreatedAtDesc(User receiver);
}