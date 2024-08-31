package com.andres.curso.springboot.app.springbootcrud.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andres.curso.springboot.app.springbootcrud.entities.FollowRequest;
import com.andres.curso.springboot.app.springbootcrud.entities.FollowRequestStatus;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    boolean existsBySenderAndReceiver(User sender, User receiver);

    // Nuevo método que busca una solicitud de seguimiento en estado PENDING
    Optional<FollowRequest> findBySenderAndReceiverAndStatus(User sender, User receiver, FollowRequestStatus status);

    FollowRequest findBySenderAndReceiver(User sender, User receiver);

    // Método para obtener todas las solicitudes de follow recibidas por un usuario
    List<FollowRequest> findByReceiver(User receiver);

    // Método para obtener todas las solicitudes de follow enviadas por un usuario
    List<FollowRequest> findBySender(User sender);
}
