package com.antonia.dev.userapi.dto.user;

public record UserDTO(
    Long id,
    String name,
    String username,
    String email,
    String roleName
) {}
