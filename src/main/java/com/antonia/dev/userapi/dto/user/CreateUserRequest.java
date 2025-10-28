package com.antonia.dev.userapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request for creating a new user")
public record CreateUserRequest(
    @Schema(description = "Full name of the user", example = "Antonia Alarcon", required = true)
    @NotBlank(message = "{user.name.required}")
    String name,

    @Schema(description = "Unique username (6-20 characters, alphanumeric and underscore only)", 
            example = "antoniaa", required = true)
    @NotBlank(message = "{user.username.required}")
    @Size(min = 6, max = 20, message = "{user.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{user.username.pattern}")
    String username,

    @Schema(description = "Valid email address", example = "antonia.alarcon@example.com", required = true)
    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    String email,

    @Schema(description = "Password (minimum 6 characters)", example = "password123", required = true)
    @NotBlank(message = "{user.password.required}")
    @Size(min = 6, message = "{user.password.size}")
    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "{user.password.pattern}")
    String password,

    @Schema(description = "Role name to assign (e.g., USER, ADMIN, MANAGER)", 
            example = "USER", defaultValue = "USER")
    String roleName
) {}
