package com.example.challenge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.challenge.dto.request.CreateUserDto;
import com.example.challenge.entity.User;
import com.example.challenge.repository.UsersRepository;

import jakarta.transaction.Transactional;

@RestController
public class UsersController {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsersController(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostMapping("/users")
    public ResponseEntity<String> newUser(@RequestBody CreateUserDto dto) {

        var userFromDb = usersRepository.findByUsername(dto.username());

        if (userFromDb.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("Username already exists");
        }

        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));

        usersRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully");
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        var users = usersRepository.findAll();
        return ResponseEntity.ok(users);
    }

}