package com.antonia.dev.userapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User data transfer object")
public record UserDTO(
    @Schema(description = "Unique identifier of the user", example = "1")
    Long id,
    
    @Schema(description = "Full name of the user", example = "Antonia Alarcon")
    String name,
    
    @Schema(description = "Username", example = "antoniaa")
    String username,
    
    @Schema(description = "Email address", example = "antonia.alarcon@example.com")
    String email,
    
    @Schema(description = "Role assigned to the user", example = "USER")
    String roleName
) {}
