package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.dto.common.DeleteResponse;
import com.antonia.dev.userapi.dto.role.RoleDTO;
import com.antonia.dev.userapi.service.role.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name = "Roles", description = "Role management endpoints - CRUD operations for roles (ADMIN only)")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @Operation(
            summary = "Get all roles",
            description = "Retrieve a list of all available roles. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No roles found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return Optional.ofNullable(roleService.getAllRoles())
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(
            summary = "Get role by name",
            description = "Retrieve a specific role by its name. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("name/{roleName}")
    public ResponseEntity<RoleDTO> getRoleByName(
            @Parameter(description = "Role name", example = "ADMIN", required = true)
            @PathVariable String roleName) {
        return roleService.getRoleByName(roleName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get role by ID",
            description = "Retrieve a specific role by its unique identifier. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("id/{id}")
    public ResponseEntity<RoleDTO> getRoleById(
            @Parameter(description = "Role ID", example = "1", required = true)
            @PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create new role",
            description = "Create a new role in the system. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Role created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        RoleDTO createdRole = roleService.createRole(roleDTO);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRole.id())
                .toUri();
        
        return ResponseEntity.created(location).body(createdRole);
    }

    @Operation(
            summary = "Update role",
            description = "Update an existing role's information. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @Parameter(description = "Role ID to update", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody RoleDTO roleDTO) {
        return roleService.updateRole(id, roleDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete role",
            description = "Delete a role by ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeleteResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteRole(
            @Parameter(description = "Role ID to delete", example = "1", required = true)
            @PathVariable Long id) {
        return roleService.deleteRole(id)
                .map(role -> {
                    DeleteResponse response = new DeleteResponse(
                            "Role deleted successfully",
                            role.getId(),
                            role.getName()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
