package com.jkim.lets_play.exception;

import org.springframework.web.bind.*;
import org.springframework.http.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err ->err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request data");
        return ResponseEntity.badRequest().body(error);
    }
    // 400
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    
    
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuth(AuthException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    
}