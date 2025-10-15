package com.antonia.dev.userapi.dto;

public record UserDTO(
    Long id,
    String name,
    String username,
    String email,
    String roleName
) {}
