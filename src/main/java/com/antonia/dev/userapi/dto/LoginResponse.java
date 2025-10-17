package com.antonia.dev.userapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
    private Long userId;
    
    public LoginResponse(String token, String username, String email, String role, Long userId) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
        this.email = email;
        this.role = role;
        this.userId = userId;
    }
}

