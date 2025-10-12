package com.antonia.dev.userapi.dto;

public record ErrorResponse(
    String field,
    String message
) {}
