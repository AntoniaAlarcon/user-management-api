package com.antonia.dev.userapi.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateSelfRequest(
        String name,
        String nickname,
        String email,
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}
