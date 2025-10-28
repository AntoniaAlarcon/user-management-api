package com.antonia.dev.userapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token validation response")
public record ValidationResponse(
        @Schema(description = "Indicates if the token is valid", example = "true")
        boolean valid,
        
        @Schema(description = "Username extracted from token", example = "admin")
        String username,
        
        @Schema(description = "Role extracted from token", example = "ADMIN")
        String role) {
}
