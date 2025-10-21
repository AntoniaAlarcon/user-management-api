package com.antonia.dev.userapi.dto.auth;

public record LoginResponse(
    String token,
    String type,
    String username,
    String email,
    String role,
    Long userId
) {
    public LoginResponse {
        if (type == null) {
            type = "Bearer";
        }
    }
    
    public LoginResponse(String token, String username, String email, String role, Long userId) {
        this(token, "Bearer", username, email, role, userId);
    }
}
