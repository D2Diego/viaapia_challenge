package com.example.challenge.shared.exception;


public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String operation, String reason) {
        super(String.format("Business rule violation in '%s': %s", operation, reason));
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 