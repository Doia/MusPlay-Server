package com.andres.curso.springboot.app.springbootcrud.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andres.curso.springboot.app.springbootcrud.entities.FriendRequest;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsBySenderAndReceiver(User sender, User receiver);

    // Método para obtener todas las solicitudes de amistad recibidas por un usuario
    List<FriendRequest> findByReceiver(User receiver);

    // Método para obtener todas las solicitudes de amistad enviadas por un usuario
    List<FriendRequest> findBySender(User sender);
}
