package com.antonia.dev.userapi.entity;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Administrator - full system access"),
    USER("Regular user - basic access"),
    MANAGER("Manager - limited administrative access");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
