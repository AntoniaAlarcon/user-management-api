package com.antonia.dev.userapi.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank(message = "{user.name.required}")
    String name,

    @NotBlank(message = "{user.username.required}")
    @Size(min = 6, max = 20, message = "{user.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{user.username.pattern}")
    String username,

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    String email,

    @NotBlank(message = "{user.password.required}")
    @Size(min = 6, message = "{user.password.size}")
    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "{user.password.pattern}")
    String password,

    String roleName
) {}
