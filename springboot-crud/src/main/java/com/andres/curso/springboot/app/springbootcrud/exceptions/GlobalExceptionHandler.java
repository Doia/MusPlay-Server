package com.andres.curso.springboot.app.springbootcrud.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(ServerException ex) {

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getErrorMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    // Otros manejadores de excepciones si es necesario
}
