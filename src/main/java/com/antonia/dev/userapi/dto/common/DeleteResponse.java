package com.antonia.dev.userapi.dto.common;

public record DeleteResponse(
    String message,
    Long id,
    String name
) {}
