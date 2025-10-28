package com.antonia.dev.userapi.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response with field-level validation details")
public record ErrorResponse(
    @Schema(description = "Field that caused the error", example = "email")
    String field,
    
    @Schema(description = "Error message", example = "Email already exists")
    String message
) {}
