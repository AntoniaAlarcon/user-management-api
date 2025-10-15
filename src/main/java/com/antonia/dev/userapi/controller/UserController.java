package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.dto.CreateUserRequest;
import com.antonia.dev.userapi.dto.DeleteResponse;
import com.antonia.dev.userapi.dto.UserDTO;
import com.antonia.dev.userapi.dto.UserUpdateSelfRequest;
import com.antonia.dev.userapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return Optional.ofNullable(userService.getAllUsers())
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<UserDTO>> getUsersByName(@PathVariable String name) {
        List<UserDTO> users = userService.getUsersByName(name);
        return users.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String roleName) {
        List<UserDTO> users = userService.getUsersByRole(roleName);
        return users.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.updateByAdmin(id, userDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PatchMapping("/self/{id}")
    public ResponseEntity<UserDTO> updateSelf(@PathVariable Long id, @Valid @RequestBody UserUpdateSelfRequest user) {
        return userService.updateSelf(id, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable Long id) {
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
