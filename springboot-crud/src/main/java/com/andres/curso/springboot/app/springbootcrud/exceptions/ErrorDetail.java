package com.andres.curso.springboot.app.springbootcrud.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorDetail {
    private final String message;
    private final HttpStatus httpStatus;

    public ErrorDetail(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
