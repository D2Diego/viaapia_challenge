package com.example.challenge.controller;

import com.example.challenge.dto.request.UserCreateDto;
import com.example.challenge.dto.response.UserResponseDto;
import com.example.challenge.service.UserService;
import com.example.challenge.shared.dto.ApiResponse;
import com.example.challenge.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody UserCreateDto createDto) {
        UserResponseDto user = userService.createUser(createDto);
        ApiResponse<UserResponseDto> response = ApiResponse.success("User created successfully", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable UUID id) {
        UserResponseDto user = userService.findById(id);
        ApiResponse<UserResponseDto> response = ApiResponse.success(user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username,asc") String sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        PageResponse<UserResponseDto> users = userService.findAll(pageable);
        
        ApiResponse<PageResponse<UserResponseDto>> response = ApiResponse.success(users);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable UUID id, 
            @Valid @RequestBody UserCreateDto updateDto) {
        
        UserResponseDto user = userService.updateUser(id, updateDto);
        ApiResponse<UserResponseDto> response = ApiResponse.success("User updated successfully", user);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = ApiResponse.success("User deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUsername(@RequestParam String username) {
        UserResponseDto user = userService.findByUsername(username);
        ApiResponse<UserResponseDto> response = ApiResponse.success(user);
        return ResponseEntity.ok(response);
    }
    
    private Pageable createPageable(int page, int size, String sort) {
        try {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            String direction = sortParams.length > 1 ? sortParams[1] : "asc";
            
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            
            return PageRequest.of(page, size, Sort.by(sortDirection, field));
            
        } catch (Exception e) {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));
        }
    }
} 