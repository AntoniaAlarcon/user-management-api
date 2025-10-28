package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.config.ApiExamples;
import com.antonia.dev.userapi.dto.auth.LoginRequest;
import com.antonia.dev.userapi.dto.auth.LoginResponse;
import com.antonia.dev.userapi.dto.auth.ValidationResponse;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.security.CustomUserDetailsService;
import com.antonia.dev.userapi.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Authentication and JWT token management endpoints")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "User login",
            description = "Authenticate user with username and password. Returns a JWT token valid for 24 hours."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(value = ApiExamples.LOGIN_RESPONSE)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
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

    @Operation(
            summary = "Validate JWT token",
            description = "Validates the provided JWT token and returns user information if valid"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token is valid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponse.class),
                            examples = @ExampleObject(value = ApiExamples.VALIDATION_RESPONSE_VALID)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = ApiExamples.VALIDATION_RESPONSE_INVALID)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @Parameter(description = "JWT token in format: Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader) {
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


