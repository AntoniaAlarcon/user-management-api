package com.antonia.dev.userapi.dto.user;

import jakarta.validation.constraints.Size;

public record UserUpdateSelfRequest(
        String name,
        String username,
        String email,
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}
