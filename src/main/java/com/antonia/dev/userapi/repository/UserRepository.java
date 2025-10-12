package com.antonia.dev.userapi.repository;

import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findByNickname(String nickname);
    List<User> findByRole(Role role);
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
}
