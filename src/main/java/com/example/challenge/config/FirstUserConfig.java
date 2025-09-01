package com.example.challenge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.challenge.users.Users;
import com.example.challenge.users.UsersRepository;

@Component
public class FirstUserConfig implements CommandLineRunner {

    private UsersRepository usersRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public FirstUserConfig(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usersRepository.findByUsername("user").isEmpty()) {
            Users userUser = new Users();
            userUser.setUsername("user");
            userUser.setPassword(passwordEncoder.encode("user123"));
            
            usersRepository.save(userUser);
            System.out.println("Default user user created with username: user and password: user123");
        } else {
            System.out.println("User user already exists, skipping creation");
        }
    }
}
