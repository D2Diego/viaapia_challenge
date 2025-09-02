package com.example.challenge.service;

import com.example.challenge.dto.request.UserCreateDto;
import com.example.challenge.dto.response.UserResponseDto;
import com.example.challenge.entity.Role;
import com.example.challenge.entity.User;
import com.example.challenge.repository.RoleRepository;
import com.example.challenge.repository.UsersRepository;
import com.example.challenge.shared.dto.PageResponse;
import com.example.challenge.shared.exception.BusinessException;
import com.example.challenge.shared.exception.NotFoundException;
import com.example.challenge.shared.exception.ValidationException;
import com.example.challenge.shared.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Service
@Transactional
public class UserService {
    
    private final UsersRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UsersRepository userRepository, 
                       RoleRepository roleRepository,
                       UserMapper userMapper,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    

    public UserResponseDto createUser(UserCreateDto createDto) {

        if (userRepository.findByUsername(createDto.getUsername()).isPresent()) {
            throw new BusinessException("create user", "Username already exists: " + createDto.getUsername());
        }
        

        User user = userMapper.toEntity(createDto);
        

        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        

        Set<Role> roles = processRoles(createDto.getRoleNames());
        user.setRoles(roles);
        

        User savedUser = userRepository.save(user);
        

        return userMapper.toResponseDto(savedUser);
    }
    

    @Transactional(readOnly = true)
    public UserResponseDto findById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
            
        return userMapper.toResponseDto(user);
    }
    

    @Transactional(readOnly = true)
    public PageResponse<UserResponseDto> findAll(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        
        Page<UserResponseDto> dtoPage = userPage.map(userMapper::toResponseDto);
        
        return PageResponse.of(dtoPage);
    }
    

    public UserResponseDto updateUser(UUID id, UserCreateDto updateDto) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
        

        if (!existingUser.getUsername().equals(updateDto.getUsername())) {
            if (userRepository.findByUsername(updateDto.getUsername()).isPresent()) {
                throw new BusinessException("update user", "Username already exists: " + updateDto.getUsername());
            }
        }
        
        userMapper.updateEntityFromDto(existingUser, updateDto);
        
        if (updateDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        
        if (updateDto.getRoleNames() != null) {
            Set<Role> newRoles = processRoles(updateDto.getRoleNames());
            existingUser.setRoles(newRoles);
        }
        
        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponseDto(savedUser);
    }
    

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User", id);
        }
        
        userRepository.deleteById(id);
    }
    

    @Transactional(readOnly = true)
    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
            
        return userMapper.toResponseDto(user);
    }
    

    private Set<Role> processRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            Role basicRole = roleRepository.findByName(Role.Values.BASIC.name());
            if (basicRole == null) {
                throw new ValidationException("Default role BASIC not found in database");
            }
            return Set.of(basicRole);
        }
        
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new ValidationException("role", roleName, "Role not found");
            }
            roles.add(role);
        }
        
        return roles;
    }
} 