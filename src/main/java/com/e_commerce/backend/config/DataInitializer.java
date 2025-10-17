package com.e_commerce.backend.config;

import com.e_commerce.backend.models.Role;
import com.e_commerce.backend.models.User;
import com.e_commerce.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@sovannarithshop.com");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            userRepository.save(admin);
            System.out.println("Admin user created with username: admin");
        }

        // Create test user if not exists
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("user@sovannarithshop.com");
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setRole(Role.ROLE_USER);
            testUser.setEnabled(true);
            testUser.setAccountNonExpired(true);
            testUser.setAccountNonLocked(true);
            testUser.setCredentialsNonExpired(true);
            userRepository.save(testUser);
            System.out.println("Test user created with username: testuser");
        }
    }
}
