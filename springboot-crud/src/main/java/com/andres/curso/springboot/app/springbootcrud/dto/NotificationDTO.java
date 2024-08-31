package com.andres.curso.springboot.app.springbootcrud.dto;

import java.time.LocalDateTime;

import com.andres.curso.springboot.app.springbootcrud.entities.FollowRequestStatus;
import com.andres.curso.springboot.app.springbootcrud.entities.NotificationType;

public interface NotificationDTO {

    Long getId();

    NotificationType getType();

    String getMessage();

    boolean getIsRead();

    LocalDateTime getCreatedAt();

    PostSummaryDTO getPost();

    CommentSummaryDTO getComment();

    FollowRequestSummaryDTO getFollowRequest();

    UserBasicDTO getReceiver();

    UserBasicDTO getSender();

    interface PostSummaryDTO {
        Long getId();
    }

    interface CommentSummaryDTO {
        Long getId();

        PostSummaryDTO getPost();
    }

    interface FollowRequestSummaryDTO {
        Long getId();

        UserBasicDTO getSender();

        FollowRequestStatus getStatus();
    }

}
