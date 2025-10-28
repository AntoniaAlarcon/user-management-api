package com.antonia.dev.userapi.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Role data transfer object")
public record RoleDTO(
        @Schema(description = "Unique identifier of the role", example = "1")
        Long id,
        
        @Schema(description = "Role name", example = "ADMIN")
        String name,
        
        @Schema(description = "Description of the role", example = "Administrator with full access")
        String description
) {}
