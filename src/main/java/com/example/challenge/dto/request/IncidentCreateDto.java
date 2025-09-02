package com.example.challenge.dto.request;

import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class IncidentCreateDto {
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 120, message = "Title must be between 5 and 120 characters")
    private String title;
    
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;
    
    @NotNull(message = "Priority is required")
    private IncidentPriority priority;
    
    @NotNull(message = "Status is required")
    private Status status;
    
    @NotBlank(message = "Responsible email is required")
    @Email(message = "Email must be valid")
    private String responsibleEmail;
    
    private List<String> tags;
    
    public IncidentCreateDto() {}
    
    public IncidentCreateDto(String title, String description, IncidentPriority priority, 
                           Status status, String responsibleEmail, List<String> tags) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.responsibleEmail = responsibleEmail;
        this.tags = tags;
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
} 