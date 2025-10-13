package com.antonia.dev.userapi.dto;

public record UserDTO(
    Long id,
    String name,
    String nickname,
    String email,
    String roleName
) {}
