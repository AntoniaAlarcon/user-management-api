package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.dto.LoginRequest;
import com.antonia.dev.userapi.dto.LoginResponse;
import com.antonia.dev.userapi.dto.ValidationResponse;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.security.CustomUserDetailsService;
import com.antonia.dev.userapi.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());

        LoginResponse response = new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName(),
                user.getId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                
                if (jwtUtil.validateToken(token)) {
                    return ResponseEntity.ok().body(new ValidationResponse(true, username, role));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidationResponse(false, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidationResponse(false, null, null));
        }
    }

}


