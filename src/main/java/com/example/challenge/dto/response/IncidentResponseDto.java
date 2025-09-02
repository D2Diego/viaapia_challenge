package com.example.challenge.dto.response;

import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class IncidentResponseDto {
    
    private UUID id;
    private String title;
    private String description;
    private IncidentPriority priority;
    private Status status;
    private String responsibleEmail;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public IncidentResponseDto() {}
    
    public IncidentResponseDto(UUID id, String title, String description, 
                             IncidentPriority priority, Status status, 
                             String responsibleEmail, List<String> tags,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.responsibleEmail = responsibleEmail;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    

    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public IncidentPriority getPriority() {
        return priority;
    }
    
    public void setPriority(IncidentPriority priority) {
        this.priority = priority;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getResponsibleEmail() {
        return responsibleEmail;
    }
    
    public void setResponsibleEmail(String responsibleEmail) {
        this.responsibleEmail = responsibleEmail;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 