package com.antonia.dev.userapi.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank(message = "{user.name.required}")
    String name,

    @NotBlank(message = "{user.username.required}")
    String username,

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    String email,

    @NotBlank(message = "{user.password.required}")
    @Size(min = 6, message = "{user.password.size}")
    String password,

    String roleName
) {}
