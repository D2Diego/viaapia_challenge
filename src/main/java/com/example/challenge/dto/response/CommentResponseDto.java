package com.example.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;


public class CommentResponseDto {
    
    private UUID id;
    private UUID incidentId;
    private String author;
    private String message;
    private LocalDateTime createdAt;
    
    public CommentResponseDto() {}
    
    public CommentResponseDto(UUID id, UUID incidentId, String author, 
                             String message, LocalDateTime createdAt) {
        this.id = id;
        this.incidentId = incidentId;
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }
    

    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getIncidentId() {
        return incidentId;
    }
    
    public void setIncidentId(UUID incidentId) {
        this.incidentId = incidentId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 