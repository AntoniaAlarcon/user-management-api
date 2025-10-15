package com.antonia.dev.userapi.service;

import com.antonia.dev.userapi.dto.CreateUserRequest;
import com.antonia.dev.userapi.dto.UserDTO;
import com.antonia.dev.userapi.dto.UserUpdateSelfRequest;
import com.antonia.dev.userapi.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();

    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByEmail(String email);
    Optional<UserDTO> getUserByUsername(String username);

    List<UserDTO> getUsersByName(String name);
    List<UserDTO> getUsersByRole(String roleName);

    UserDTO createUser(CreateUserRequest user);
    Optional<UserDTO> updateSelf(Long id, UserUpdateSelfRequest request);
    Optional<UserDTO> updateByAdmin(Long id, UserDTO userDTO);

    Optional<User> delete(Long id);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
