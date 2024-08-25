package com.andres.curso.springboot.app.springbootcrud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andres.curso.springboot.app.springbootcrud.entities.FriendRequest;
import com.andres.curso.springboot.app.springbootcrud.entities.FriendRequestStatus;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ErrorMessages;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ServerException;
import com.andres.curso.springboot.app.springbootcrud.repositories.FriendRequestRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;

import java.util.List;

@Service
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FriendRequestRepository friendRequestRepository;

    public FriendRequestServiceImpl(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    @Override
    public void sendFriendRequest(Long receiverId) {
        User sender = getAuthenticatedUser();

        if (sender.getId().equals(receiverId)) {
            throw new ServerException(ErrorMessages.VALIDATION_FAILED);
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new ServerException(ErrorMessages.VALIDATION_FAILED);
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);

        try {
            friendRequestRepository.save(friendRequest);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }
    }

    @Override
    public List<FriendRequest> getReceivedRequests() {
        User user = getAuthenticatedUser();
        try {
            return friendRequestRepository.findByReceiver(user);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }
    }

    @Override
    public List<FriendRequest> getSentRequests() {
        User user = getAuthenticatedUser();
        try {
            return friendRequestRepository.findBySender(user);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }
    }

    @Override
    public void acceptFriendRequest(Long requestId) {
        User authenticatedUser = getAuthenticatedUser();

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ServerException(ErrorMessages.RESOURCE_NOT_FOUND));

        if (!request.getReceiver().getId().equals(authenticatedUser.getId())) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new ServerException(ErrorMessages.VALIDATION_FAILED);
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);

        try {
            friendRequestRepository.save(request);
            request.getSender().addFriend(request.getReceiver());
            request.getReceiver().addFriend(request.getSender());
            userRepository.save(request.getSender());
            userRepository.save(request.getReceiver());
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }
    }

    @Override
    public void rejectFriendRequest(Long requestId) {
        User authenticatedUser = getAuthenticatedUser();

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ServerException(ErrorMessages.RESOURCE_NOT_FOUND));

        if (!request.getReceiver().getId().equals(authenticatedUser.getId())) {
            throw new ServerException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new ServerException(ErrorMessages.VALIDATION_FAILED);
        }

        request.setStatus(FriendRequestStatus.REJECTED);

        try {
            friendRequestRepository.save(request);
        } catch (Exception e) {
            throw new ServerException(ErrorMessages.DATABASE_ERROR);
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ServerException(ErrorMessages.AUTHENTICATED_USER_NOT_FOUND));
    }
}
