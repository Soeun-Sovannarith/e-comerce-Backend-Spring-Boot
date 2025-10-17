package com.e_commerce.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserDTO user;
    private String message;

    // Constructor for successful authentication with user data
    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
        this.type = "Bearer";
    }

    // Constructor for error messages
    public AuthResponse(String message) {
        this.message = message;
    }

    // Legacy constructor for backward compatibility
    public AuthResponse(String token, String username, String email, String role) {
        this.token = token;
        this.user = new UserDTO();
        this.user.setUsername(username);
        this.user.setEmail(email);
        this.user.setRole(role);
        this.type = "Bearer";
    }
}
