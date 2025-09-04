package com.example.challenge.dto.response;

import java.util.Set;
import java.util.UUID;


public class UserResponseDto {
    
    private UUID id;
    private String username;
    private Set<String> roles; // Nomes dos roles
    
    public UserResponseDto() {}
    
    public UserResponseDto(UUID id, String username, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
    

    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
} 