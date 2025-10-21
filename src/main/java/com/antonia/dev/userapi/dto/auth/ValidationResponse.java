package com.antonia.dev.userapi.dto.auth;

public record ValidationResponse(
        boolean valid,
        String username,
        String role) {
}
