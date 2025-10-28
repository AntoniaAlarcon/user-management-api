package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.config.ApiExamples;
import com.antonia.dev.userapi.dto.common.ErrorResponse;
import com.antonia.dev.userapi.dto.user.CreateUserRequest;
import com.antonia.dev.userapi.dto.common.DeleteResponse;
import com.antonia.dev.userapi.dto.user.UserDTO;
import com.antonia.dev.userapi.dto.user.UserUpdateSelfRequest;
import com.antonia.dev.userapi.service.user.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name = "Users", description = "User management endpoints - CRUD operations for users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Retrieve a list of all registered users. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No users found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return Optional.ofNullable(userService.getAllUsers())
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a specific user by their unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Find users by name",
            description = "Search for users by name (partial match supported)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "204", description = "No users found with that name"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/name/{name}")
    public ResponseEntity<List<UserDTO>> getUsersByName(
            @Parameter(description = "Name to search for", example = "Antonia", required = true)
            @PathVariable String name) {
        List<UserDTO> users = userService.getUsersByName(name);
        return users.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Find user by email",
            description = "Retrieve a user by their email address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(
            @Parameter(description = "Email address", example = "antonia.alarcon@example.com", required = true)
            @PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Find user by username",
            description = "Retrieve a user by their username"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(
            @Parameter(description = "Username", example = "antoniaa", required = true)
            @PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Find users by role",
            description = "Retrieve all users assigned to a specific role"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "204", description = "No users found with that role"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(
            @Parameter(description = "Role name", example = "USER", required = true)
            @PathVariable String roleName) {
        List<UserDTO> users = userService.getUsersByRole(roleName);
        return users.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Create new user (Registration)",
            description = "Register a new user. This endpoint is public and does not require authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or user already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ApiExamples.ERROR_EMAIL_EXISTS)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(
            summary = "Update user (Admin)",
            description = "Update any user's information. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "User ID to update", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody UserDTO userDTO) {
        return userService.updateByAdmin(id, userDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Update own profile",
            description = "Allows authenticated users to update their own profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/self/{id}")
    public ResponseEntity<UserDTO> updateSelf(
            @Parameter(description = "Your user ID", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateSelfRequest user) {
        return userService.updateSelf(id, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete user",
            description = "Delete a user by ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeleteResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteUser(
            @Parameter(description = "User ID to delete", example = "1", required = true)
            @PathVariable Long id) {
        return userService.delete(id)
                .map(user -> {
                    DeleteResponse response = new DeleteResponse(
                            "User deleted successfully",
                            user.getId(),
                            user.getName()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
