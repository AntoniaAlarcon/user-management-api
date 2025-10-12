package com.antonia.dev.userapi.dto;

import com.antonia.dev.userapi.entity.Role;

public record UserDTO(
    Long id,
    String name,
    String nickname,
    String email,
    Role role
) {}
