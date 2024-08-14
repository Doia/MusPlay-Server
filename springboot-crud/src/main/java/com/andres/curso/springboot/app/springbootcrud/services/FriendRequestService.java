package com.andres.curso.springboot.app.springbootcrud.services;

import java.util.List;
import com.andres.curso.springboot.app.springbootcrud.entities.FriendRequest;

public interface FriendRequestService {
    void sendFriendRequest(Long receiverId);

    void acceptFriendRequest(Long requestId);

    List<FriendRequest> getReceivedRequests();

    List<FriendRequest> getSentRequests();

    void rejectFriendRequest(Long requestId);
}
