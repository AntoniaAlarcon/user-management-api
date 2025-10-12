package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.dto.RoleDTO;
import com.antonia.dev.userapi.entity.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(
                Arrays.stream(Role.values())
                        .map(role -> new RoleDTO(role.name(), role.getDescription()))
                        .toList()
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {
        return Arrays.stream(Role.values())
                .filter(role -> role.name().equalsIgnoreCase(name))
                .map(role -> new RoleDTO(role.name(), role.getDescription()))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
