package com.antonia.dev.userapi.dto;

import com.antonia.dev.userapi.entity.Role;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Nickname is required")
    String nickname,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    String password,
    
    Role role
) {}
