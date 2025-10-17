package com.antonia.dev.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "{user.username.required}")
    private String username;
    
    @NotBlank(message = "{user.password.required}")
    private String password;
}


