package com.e_commerce.backend.services;

import com.e_commerce.backend.dto.AuthResponse;
import com.e_commerce.backend.dto.LoginRequest;
import com.e_commerce.backend.dto.RegisterRequest;
import com.e_commerce.backend.dto.UserDTO;
import com.e_commerce.backend.models.Role;
import com.e_commerce.backend.models.User;
import com.e_commerce.backend.repositories.UserRepository;
import com.e_commerce.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_DURATION = 24; // hours

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ROLE_USER); // Default role

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String jwt = jwtUtil.generateToken(savedUser);

        // Return response with token and user data
        return new AuthResponse(jwt, new UserDTO(savedUser));
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Check if account is locked
        if (!user.isAccountNonLocked()) {
            throw new RuntimeException("Account is locked due to multiple failed login attempts. Please try again later.");
        }

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );

            // Reset failed attempts on successful login
            if (user.getFailedLoginAttempts() > 0) {
                resetFailedAttempts(user.getUsername());
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            // Fetch fresh user data to return
            User freshUser = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Return response with token and complete user data
            return new AuthResponse(jwt, new UserDTO(freshUser));

        } catch (BadCredentialsException e) {
            // Handle failed login attempt
            handleFailedLogin(user);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Transactional
    public void handleFailedLogin(User user) {
        int newFailAttempts = user.getFailedLoginAttempts() + 1;
        userRepository.updateFailedAttempts(user.getUsername(), newFailAttempts, LocalDateTime.now());

        if (newFailAttempts >= MAX_FAILED_ATTEMPTS) {
            userRepository.lockUser(user.getUsername(), LocalDateTime.now().plusHours(LOCK_TIME_DURATION));
        }
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        userRepository.updateFailedAttempts(username, 0, null);
    }

    public AuthResponse logout() {
        // For stateless JWT, we just return a success message
        // In a more advanced implementation, you could maintain a blacklist of tokens
        return new AuthResponse("Logged out successfully");
    }
}
