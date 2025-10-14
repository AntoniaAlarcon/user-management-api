package com.antonia.dev.userapi.service;

import com.antonia.dev.userapi.dto.CreateUserRequest;
import com.antonia.dev.userapi.dto.UserDTO;
import com.antonia.dev.userapi.dto.ErrorResponse;
import com.antonia.dev.userapi.dto.UserUpdateSelfRequest;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.exception.RoleNotFoundException;
import com.antonia.dev.userapi.exception.UserNotFoundException;
import com.antonia.dev.userapi.exception.UserValidationException;
import com.antonia.dev.userapi.mapper.UserMapper;
import com.antonia.dev.userapi.repository.RoleRepository;
import com.antonia.dev.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("id", "User not found whit ID " + id)));
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("email", "User not found whit email: " + email)));
    }

    @Override
    public Optional<UserDTO> getUserByNickname(String nickname) {
        return Optional.ofNullable(userRepository.findByNickname(nickname)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("nickname", "User not found with nickname: " + nickname)));
    }

    @Override
    public List<UserDTO> getUsersByName(String name) {
        return userRepository.findByName(name)
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public List<UserDTO> getUsersByRole(String roleName) {
        roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("roleName", "Role not found with name: " + roleName));
        
        return userRepository.findByRoleName(roleName)
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        validateAndSetupUser(user, request.roleName());

        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public Optional<UserDTO> updateSelf(Long id, UserUpdateSelfRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", "User not found with id: " + id));

        validateAndApplySelfUpdate(existing, request);

        User saved = userRepository.save(existing);
        return Optional.of(userMapper.toDTO(saved));
    }

    @Override
    @Transactional
    public Optional<UserDTO> updateByAdmin(Long id, UserDTO request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", "User not found with id: " + id));

        validateAndApplyAdminUpdate(existing, request);

        User saved = userRepository.save(existing);
        return Optional.of(userMapper.toDTO(saved));
    }

    @Override
    @Transactional
    public Optional<User> delete(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return user;
                });
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private void validateAndSetupUser(User user, String requestedRoleName) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (existsByEmail(user.getEmail())) {
            errors.add(new ErrorResponse("email", "Email already exists"));
        }
        if (existsByNickname(user.getNickname())) {
            errors.add(new ErrorResponse("nickname", "Nickname already exists"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String roleName = (requestedRoleName != null && !requestedRoleName.isBlank())
                ? requestedRoleName 
                : "USER";

        roleRepository.findByName(roleName)
                .ifPresentOrElse(
                        user::setRole,
                        () -> errors.add(new ErrorResponse("roleName", "Role not found with name: " + roleName))
                );

        if (!errors.isEmpty()) {
            throw new UserValidationException(errors);
        }
    }

    private void validateAndApplySelfUpdate(User existing, UserUpdateSelfRequest request) {
        List<ErrorResponse> errors = new ArrayList<>();

        applyCommonFields(existing, request.name(), request.email(), request.nickname(), errors);

        if (request.password() != null && !request.password().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.password()));
        }

        if (!errors.isEmpty()) {
            throw new UserValidationException(errors);
        }
    }

    private void validateAndApplyAdminUpdate(User existing, UserDTO request) {
        List<ErrorResponse> errors = new ArrayList<>();

        applyCommonFields(existing, request.name(), request.email(), request.nickname(), errors);

        Optional.ofNullable(request.roleName())
                .ifPresent(roleName -> {
                    roleRepository.findByName(roleName)
                            .ifPresentOrElse(
                                    existing::setRole,
                                    () -> errors.add(new ErrorResponse(
                                            "roleName", "Role not found with name: " + roleName))
                            );
                });

        if (!errors.isEmpty()) {
            throw new UserValidationException(errors);
        }
    }

    private void applyCommonFields(User existing, String name, String email, String nickname, List<ErrorResponse> errors) {
        if (name != null && !name.isBlank()) {
            existing.setName(name);
        }

        if (email != null && !email.isBlank()) {
            if (!email.equals(existing.getEmail()) && userRepository.existsByEmail(email)) {
                errors.add(new ErrorResponse("email", "Email already exists"));
            } else {
                existing.setEmail(email);
            }
        }

        if (nickname != null && !nickname.isBlank()) {
            if (!nickname.equals(existing.getNickname()) && userRepository.existsByNickname(nickname)) {
                errors.add(new ErrorResponse("nickname", "Nickname already exists"));
            } else {
                existing.setNickname(nickname);
            }
        }
    }
}
