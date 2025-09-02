package com.example.challenge.shared.exception;


public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(String resource, Object id) {
        super(String.format("%s not found with id: %s", resource, id));
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 