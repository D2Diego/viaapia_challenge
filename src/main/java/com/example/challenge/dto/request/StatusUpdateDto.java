package com.example.challenge.dto.request;

import com.example.challenge.entity.Status;
import jakarta.validation.constraints.NotNull;


public class StatusUpdateDto {
    
    @NotNull(message = "Status is required")
    private Status status;
    
    public StatusUpdateDto() {}
    
    public StatusUpdateDto(Status status) {
        this.status = status;
    }
    

    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
} 