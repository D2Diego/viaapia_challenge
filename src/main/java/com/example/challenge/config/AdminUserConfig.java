package com.example.challenge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.example.challenge.role.Role;
import com.example.challenge.users.User;
import com.example.challenge.role.RoleRepository;
import com.example.challenge.users.UsersRepository;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UsersRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository,
                           UsersRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("admin ja existe");
                },
                () -> {
                    if (roleAdmin != null) {
                        var user = new User();
                        user.setUsername("admin");
                        user.setPassword(passwordEncoder.encode("123"));
                        user.setRoles(Set.of(roleAdmin));
                        userRepository.save(user);
                        System.out.println("Admin user created successfully");
                    } else {
                        System.err.println("Admin role not found! Cannot create admin user.");
                    }
                }
        );
    }
}