package com.example.challenge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;


public class UserCreateDto {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private Set<String> roleNames;
    
    public UserCreateDto() {}
    
    public UserCreateDto(String username, String password, Set<String> roleNames) {
        this.username = username;
        this.password = password;
        this.roleNames = roleNames;
    }
    

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<String> getRoleNames() {
        return roleNames;
    }
    
    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = roleNames;
    }
} 