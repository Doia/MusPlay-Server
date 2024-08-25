package com.andres.curso.springboot.app.springbootcrud.exceptions;

import org.springframework.http.HttpStatus;

public class ServerException extends RuntimeException {
    private final ErrorDetail errorDetail;

    public ServerException(ErrorDetail errorDetail) {
        super(errorDetail.getMessage());
        this.errorDetail = errorDetail;
    }

    public HttpStatus getHttpStatus() {
        return errorDetail.getHttpStatus();
    }

    public String getErrorMessage() {
        return errorDetail.getMessage();
    }

    public ErrorDetail getErrorDetail() {
        return errorDetail;
    }
}
