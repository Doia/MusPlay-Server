package com.andres.curso.springboot.app.springbootcrud.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorMessages {

        // General Error Messages
        public static final ErrorDetail INVALID_REFRESH_TOKEN  = new ErrorDetail(
                        "Refresh token is expired.", HttpStatus.FORBIDDEN);

        public static final ErrorDetail INTERNAL_SERVER_ERROR = new ErrorDetail(
                        "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail VALIDATION_FAILED = new ErrorDetail("Validation failed for one or more fields.",
                        HttpStatus.BAD_REQUEST);

        public static final ErrorDetail UNAUTHORIZED_ACCESS = new ErrorDetail(
                        "You do not have the necessary permissions to access this resource.", HttpStatus.FORBIDDEN);

        public static final ErrorDetail RESOURCE_NOT_FOUND = new ErrorDetail("The requested resource was not found.",
                        HttpStatus.NOT_FOUND);

        // User-related Error Messages
        public static final ErrorDetail AUTHENTICATED_USER_NOT_FOUND = new ErrorDetail("Authenticated user not found.",
                        HttpStatus.FORBIDDEN);

        public static final ErrorDetail USER_NOT_FOUND = new ErrorDetail("User not found.", HttpStatus.NOT_FOUND);

        public static final ErrorDetail USER_ALREADY_EXISTS = new ErrorDetail(
                        "A user with the same username or email already exists.", HttpStatus.CONFLICT);

        public static final ErrorDetail USER_CREATION_FAILED = new ErrorDetail("Failed to create a new user.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail USER_UPDATE_FAILED = new ErrorDetail("Failed to update the user information.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail USER_DELETION_FAILED = new ErrorDetail("Failed to delete the user.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail USER_IS_PRIVATE = new ErrorDetail(
                        "This user is private.", HttpStatus.OK);

        public static final ErrorDetail JSON_PROCESSING_ERROR = new ErrorDetail(
                        "Error processing JSON data.", HttpStatus.INTERNAL_SERVER_ERROR);

        // Post-related Error Messages
        public static final ErrorDetail POST_NOT_FOUND = new ErrorDetail("Post not found.", HttpStatus.NOT_FOUND);

        public static final ErrorDetail POST_CREATION_FAILED = new ErrorDetail("Failed to create a new post.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail POST_UPDATE_FAILED = new ErrorDetail("Failed to update the post.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail POST_DELETION_FAILED = new ErrorDetail("Failed to delete the post.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail POST_UNAUTHORIZED = new ErrorDetail(
                        "You are not authorized to modify or delete this post.", HttpStatus.FORBIDDEN);

        // Follow-related Error Messages
        public static final ErrorDetail FOLLOW_REQUEST_ALREADY_SENT = new ErrorDetail(
                        "A follow request has already been sent to this user.", HttpStatus.CONFLICT);

        public static final ErrorDetail FOLLOW_REQUEST_NOT_FOUND = new ErrorDetail(
                        "The follow request could not be found.", HttpStatus.NOT_FOUND);

        public static final ErrorDetail FOLLOW_REQUEST_NOT_PENDING = new ErrorDetail(
                        "The follow request is not pending.", HttpStatus.CONFLICT);

        public static final ErrorDetail FOLLOW_REQUEST_ALREADY_ACCEPTED = new ErrorDetail(
                        "The follow request has already been accepted.", HttpStatus.CONFLICT);

        public static final ErrorDetail FOLLOW_REQUEST_ALREADY_REJECTED = new ErrorDetail(
                        "The follow request has already been rejected.", HttpStatus.CONFLICT);

        public static final ErrorDetail FOLLOW_OPERATION_FAILED = new ErrorDetail(
                        "Failed to follow the user. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail UNFOLLOW_OPERATION_FAILED = new ErrorDetail(
                        "Failed to unfollow the user. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail ACCEPT_FOLLOW_OPERATION_FAILED = new ErrorDetail(
                        "Failed to accept follow request. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail REJECT_FOLLOW_OPERATION_FAILED = new ErrorDetail(
                        "Failed to reject follow request. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail CANNOT_FOLLOW_YOURSELF = new ErrorDetail(
                        "You cannot follow yourself.", HttpStatus.BAD_REQUEST);

        public static final ErrorDetail ALREADY_FOLLOWING_USER = new ErrorDetail(
                        "You are already following this user.", HttpStatus.CONFLICT);

        // Comment-related Error Messages
        public static final ErrorDetail COMMENT_NOT_FOUND = new ErrorDetail("Comment not found.", HttpStatus.NOT_FOUND);

        public static final ErrorDetail COMMENT_CREATION_FAILED = new ErrorDetail("Failed to create a new comment.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail COMMENT_DELETION_FAILED = new ErrorDetail("Failed to delete the comment.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail COMMENT_UNAUTHORIZED = new ErrorDetail(
                        "You are not authorized to modify or delete this comment.", HttpStatus.FORBIDDEN);

        // Like-related Error Messages
        public static final ErrorDetail LIKE_CREATION_FAILED = new ErrorDetail("Failed to add a like to the post.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail LIKE_REMOVAL_FAILED = new ErrorDetail(
                        "Failed to remove the like from the post.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail LIKE_UNAUTHORIZED = new ErrorDetail(
                        "You are not authorized to like or unlike this post.", HttpStatus.FORBIDDEN);

        // Image-related Error Messages
        public static final ErrorDetail FILE_STORAGE_FAILED = new ErrorDetail(
                        "Failed to store the image file.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail FILE_IS_EMPTY = new ErrorDetail(
                        "File is empty.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail FILE_DELETION_FAILED = new ErrorDetail(
                        "Failed to delete the image file.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail INVALID_FILE_FORMAT = new ErrorDetail(
                        "Invalid file format. Please upload a valid image file.", HttpStatus.BAD_REQUEST);

        // Notifications

        // Otros mensajes relacionados
        public static final ErrorDetail DATABASE_ERROR = new ErrorDetail(
                        "A database error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        public static final ErrorDetail SERVICE_UNAVAILABLE = new ErrorDetail(
                        "The service is currently unavailable. Please try again later.",
                        HttpStatus.SERVICE_UNAVAILABLE);

        private ErrorMessages() {
                // Constructor privado para prevenir instanciación
        }
}
