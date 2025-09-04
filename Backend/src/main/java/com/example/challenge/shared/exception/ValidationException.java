package com.example.challenge.shared.exception;


public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String value, String reason) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", field, value, reason));
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 