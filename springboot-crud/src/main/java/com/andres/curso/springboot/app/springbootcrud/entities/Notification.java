package com.andres.curso.springboot.app.springbootcrud.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // El usuario que recibe la notificación

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // El usuario que genera la notificación

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type; // Tipo de notificación (LIKE, FOLLOW, COMMENT, FOLLOW_REQUEST, etc.)

    @Column(length = 100)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "follow_request_id")
    private FollowRequest followRequest;

    // Constructores
    public Notification() {
        // Constructor por defecto
    }

    public Notification(User receiver, User sender, NotificationType type, Object entity) {
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.createdAt = LocalDateTime.now();

        validateNotification(entity);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        throw new IllegalArgumentException("You cannot change the type of notification");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
        validateNotification(post);
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        validateNotification(comment);
    }

    public FollowRequest getFollowRequest() {
        return followRequest;
    }

    public void setFollowRequest(FollowRequest followRequest) {
        this.followRequest = followRequest;
        validateNotification(followRequest);
    }

    // Método de validación
    private void validateNotification(Object entity) {
        this.followRequest = null;
        this.post = null;
        this.comment = null;

        switch (type) {
            case FOLLOW:
                this.message = "@" + this.sender.getUsername() + " ha comenzado a seguirte.";
                break;
            case LIKE:
                if (!(entity instanceof Post)) {
                    throw new IllegalArgumentException("Post must be provided for LIKE notifications");
                }
                if (entity instanceof Comment || entity instanceof FollowRequest) {
                    throw new IllegalArgumentException("Comment and FollowRequest must be null for LIKE notifications");
                }
                this.message = "@" + this.sender.getUsername() + " ha dado like a tu post.";
                this.post = (Post) entity;
                break;
            case COMMENT:
                if (!(entity instanceof Comment)) {
                    throw new IllegalArgumentException("Comment must be provided for COMMENT notifications");
                }
                if (entity instanceof Post || entity instanceof FollowRequest) {
                    throw new IllegalArgumentException("Post and FollowRequest must be null for COMMENT notifications");
                }
                this.message = "@" + this.sender.getUsername() + " te ha comentado.";
                this.comment = (Comment) entity;
                break;
            case FOLLOW_REQUEST:
                if (!(entity instanceof FollowRequest)) {
                    throw new IllegalArgumentException(
                            "FollowRequest must be provided for FOLLOW_REQUEST notifications");
                }
                if (entity instanceof Comment || entity instanceof Post) {
                    throw new IllegalArgumentException(
                            "Post and Comment must be null for FOLLOW_REQUEST notifications");
                }
                this.message = "@" + this.sender.getUsername() + " quiere seguirte.";
                this.followRequest = (FollowRequest) entity;
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification type");
        }
    }
}
