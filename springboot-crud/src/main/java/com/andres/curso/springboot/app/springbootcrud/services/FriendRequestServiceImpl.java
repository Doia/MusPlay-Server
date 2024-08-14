package com.andres.curso.springboot.app.springbootcrud.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.andres.curso.springboot.app.springbootcrud.repositories.FriendRequestRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;
import com.andres.curso.springboot.app.springbootcrud.entities.FriendRequest;
import com.andres.curso.springboot.app.springbootcrud.entities.FriendRequestStatus;
import com.andres.curso.springboot.app.springbootcrud.entities.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendRequestServiceImpl(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    @Override
    public void sendFriendRequest(Long receiverId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User sender = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (sender.getId().equals(receiverId)) {
            throw new RuntimeException("You cannot send a friend request to yourself");
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new RuntimeException("Friend request already sent");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);

        friendRequestRepository.save(friendRequest);
    }

    @Override
    public List<FriendRequest> getReceivedRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return friendRequestRepository.findByReceiver(user);
    }

    @Override
    public List<FriendRequest> getSentRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return friendRequestRepository.findBySender(user);
    }

    @Override
    public void acceptFriendRequest(Long requestId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("authenticatedUser not found"));

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("You are not authorized to accept this request");
        }

        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new RuntimeException("You are not authorized to accept this request");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        request.getSender().addFriend(request.getReceiver());
        request.getReceiver().addFriend(request.getSender());

        userRepository.save(request.getSender());
        userRepository.save(request.getReceiver());
    }

    @Override
    public void rejectFriendRequest(Long requestId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("authenticatedUser not found"));

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("You are not authorized to reject this request");
        }

        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new RuntimeException("You are not authorized to accept this request");
        }

        request.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }
}
