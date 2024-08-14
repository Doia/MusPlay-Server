package com.andres.curso.springboot.app.springbootcrud.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.andres.curso.springboot.app.springbootcrud.entities.FriendRequest;
import com.andres.curso.springboot.app.springbootcrud.services.FriendRequestService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<String> sendFriendRequest(
            @PathVariable Long receiverId) {
        try {
            friendRequestService.sendFriendRequest(receiverId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Friend request sent successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/received")
    public ResponseEntity<List<FriendRequest>> getReceivedRequests() {
        try {
            List<FriendRequest> receivedRequests = friendRequestService.getReceivedRequests();
            return ResponseEntity.ok(receivedRequests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<List<FriendRequest>> getSentRequests() {
        try {
            List<FriendRequest> sentRequests = friendRequestService.getSentRequests();
            return ResponseEntity.ok(sentRequests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(
            @PathVariable Long requestId) {
        try {
            friendRequestService.acceptFriendRequest(requestId);
            return ResponseEntity.ok("Friend request accepted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectFriendRequest(
            @PathVariable Long requestId) {
        try {
            friendRequestService.rejectFriendRequest(requestId);
            return ResponseEntity.ok("Friend request rejected");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
