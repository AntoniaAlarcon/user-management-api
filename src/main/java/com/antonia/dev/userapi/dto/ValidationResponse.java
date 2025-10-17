package com.antonia.dev.userapi.dto;

public record ValidationResponse(
        boolean valid,
        String username,
        String role) {
}
