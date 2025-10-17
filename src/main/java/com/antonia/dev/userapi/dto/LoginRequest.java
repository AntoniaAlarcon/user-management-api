package com.antonia.dev.userapi.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "{user.username.required}")
    String username,
    
    @NotBlank(message = "{user.password.required}")
    String password
) {}


