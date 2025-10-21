package com.antonia.dev.userapi.dto.common;

public record ErrorResponse(
    String field,
    String message
) {}
