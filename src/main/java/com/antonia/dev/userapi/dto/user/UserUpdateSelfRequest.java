package com.antonia.dev.userapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for user to update their own profile")
public record UserUpdateSelfRequest(
        @Schema(description = "Updated full name", example = "Antonia Alarcon Updated")
        String name,
        
        @Schema(description = "Updated username", example = "antoniaa_updated")
        String username,
        
        @Schema(description = "Updated email address", example = "antonia.updated@example.com")
        String email,
        
        @Schema(description = "New password (minimum 6 characters)", example = "newpassword123")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}
