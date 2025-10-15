package com.antonia.dev.userapi.repository;

import com.antonia.dev.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role")
    List<User> findAll();
    
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.role r WHERE r.name = :roleName")
    List<User> findByRoleName(String roleName);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
