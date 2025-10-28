package com.antonia.dev.userapi.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after successful deletion")
public record DeleteResponse(
    @Schema(description = "Deletion confirmation message", example = "User deleted successfully")
    String message,
    
    @Schema(description = "ID of the deleted entity", example = "1")
    Long id,
    
    @Schema(description = "Name of the deleted entity", example = "Antonia Alarcon")
    String name
) {}
