package com.antonia.dev.userapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request containing user credentials")
public record LoginRequest(
    @Schema(description = "Username for authentication", example = "admin", required = true)
    @NotBlank(message = "{user.username.required}")
    String username,
    
    @Schema(description = "User password", example = "admin123", required = true)
    @NotBlank(message = "{user.password.required}")
    String password
) {}
