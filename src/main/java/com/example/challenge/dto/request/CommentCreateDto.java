package com.example.challenge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class CommentCreateDto {
    
    @NotBlank(message = "Author is required")
    @Size(min = 2, max = 255, message = "Author must be between 2 and 255 characters")
    private String author;
    
    @NotBlank(message = "Message is required")
    @Size(min = 1, max = 2000, message = "Message cannot exceed 2000 characters")
    private String message;
    
    public CommentCreateDto() {}
    
    public CommentCreateDto(String author, String message) {
        this.author = author;
        this.message = message;
    }
    

    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 