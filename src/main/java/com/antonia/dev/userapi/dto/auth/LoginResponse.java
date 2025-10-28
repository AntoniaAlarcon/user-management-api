package com.antonia.dev.userapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response with JWT token and user details")
public record LoginResponse(
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzUxMiJ9...")
    String token,
    
    @Schema(description = "Token type", example = "Bearer")
    String type,
    
    @Schema(description = "Username of the authenticated user", example = "admin")
    String username,
    
    @Schema(description = "Email of the authenticated user", example = "admin@example.com")
    String email,
    
    @Schema(description = "Role assigned to the user", example = "ADMIN")
    String role,
    
    @Schema(description = "Unique identifier of the user", example = "1")
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
