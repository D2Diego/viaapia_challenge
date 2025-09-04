package com.example.challenge.shared.mapper;

import com.example.challenge.dto.request.UserCreateDto;
import com.example.challenge.dto.response.UserResponseDto;
import com.example.challenge.entity.Role;
import com.example.challenge.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class UserMapper {
    

    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }
        
        Set<String> roleNames = user.getRoles()
            .stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        
        return new UserResponseDto(
            user.getUserId(),
            user.getUsername(),
            roleNames
        );
    }
    

    public User toEntity(UserCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(createDto.getUsername());
        user.setPassword(createDto.getPassword());
        
        return user;
    }
    

    public void updateEntityFromDto(User user, UserCreateDto updateDto) {
        if (user == null || updateDto == null) {
            return;
        }
        
        if (updateDto.getUsername() != null) {
            user.setUsername(updateDto.getUsername());
        }
        
        if (updateDto.getPassword() != null) {
            user.setPassword(updateDto.getPassword());
        }
    }
} 