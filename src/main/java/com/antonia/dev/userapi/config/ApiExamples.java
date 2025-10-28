package com.antonia.dev.userapi.config;

/**
 * Centralized storage for API documentation examples.
 * This class contains reusable JSON examples for OpenAPI/Swagger documentation.
 */
public class ApiExamples {
    
    // ============================================
    // Authentication Examples
    // ============================================
    
    public static final String LOGIN_REQUEST = """
            {
              "username": "admin",
              "password": "admin123"
            }
            """;
    
    public static final String LOGIN_RESPONSE = """
            {
              "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTYxMDAwMDAwMCwiZXhwIjoxNjEwMDg2NDAwfQ...",
              "type": "Bearer",
              "username": "admin",
              "email": "admin@example.com",
              "role": "ADMIN",
              "userId": 1
            }
            """;
    
    public static final String VALIDATION_RESPONSE_VALID = """
            {
              "valid": true,
              "username": "admin",
              "role": "ADMIN"
            }
            """;
    
    public static final String VALIDATION_RESPONSE_INVALID = """
            {
              "valid": false,
              "username": null,
              "role": null
            }
            """;
    
    // ============================================
    // User Examples
    // ============================================
    
    public static final String CREATE_USER_REQUEST = """
            {
              "name": "Antonia Alarcon",
              "username": "antoniaa",
              "email": "antonia.alarcon@example.com",
              "password": "password123",
              "roleName": "USER"
            }
            """;
    
    public static final String USER_RESPONSE = """
            {
              "id": 1,
              "name": "Antonia Alarcon",
              "username": "antoniaa",
              "email": "antonia.alarcon@example.com",
              "roleName": "USER"
            }
            """;
    
    public static final String UPDATE_SELF_REQUEST = """
            {
              "name": "Antonia Alarcon Updated",
              "username": "antoniaa_updated",
              "email": "antonia.updated@example.com",
              "password": "newpassword123"
            }
            """;
    
    public static final String UPDATE_USER_REQUEST = """
            {
              "name": "Antonia Alarcon Updated",
              "username": "antoniaa_updated",
              "email": "antonia.updated@example.com",
              "roleName": "ADMIN"
            }
            """;
    
    public static final String USER_LIST_RESPONSE = """
            [
              {
                "id": 1,
                "name": "Admin User",
                "username": "admin",
                "email": "admin@example.com",
                "roleName": "ADMIN"
              },
              {
                "id": 2,
                "name": "Antonia Alarcon",
                "username": "antoniaa",
                "email": "antonia.alarcon@example.com",
                "roleName": "USER"
              }
            ]
            """;
    
    // ============================================
    // Role Examples
    // ============================================
    
    public static final String ROLE_RESPONSE = """
            {
              "id": 1,
              "name": "ADMIN",
              "description": "Administrator with full access"
            }
            """;
    
    public static final String CREATE_ROLE_REQUEST = """
            {
              "name": "SUPERVISOR",
              "description": "Supervisor with limited admin access"
            }
            """;
    
    public static final String ROLE_LIST_RESPONSE = """
            [
              {
                "id": 1,
                "name": "ADMIN",
                "description": "Administrator with full access"
              },
              {
                "id": 2,
                "name": "USER",
                "description": "Standard user with limited access"
              },
              {
                "id": 3,
                "name": "MANAGER",
                "description": "Manager with elevated permissions"
              }
            ]
            """;
    
    // ============================================
    // Common Response Examples
    // ============================================
    
    public static final String DELETE_USER_RESPONSE = """
            {
              "message": "User deleted successfully",
              "id": 1,
              "name": "Antonia Alarcon"
            }
            """;
    
    public static final String DELETE_ROLE_RESPONSE = """
            {
              "message": "Role deleted successfully",
              "id": 1,
              "name": "SUPERVISOR"
            }
            """;
    
    // ============================================
    // Error Examples
    // ============================================
    
    public static final String ERROR_EMAIL_EXISTS = """
            {
              "field": "email",
              "message": "Email already exists"
            }
            """;
    
    public static final String ERROR_USERNAME_EXISTS = """
            {
              "field": "username",
              "message": "Username already exists"
            }
            """;
    
    public static final String ERROR_VALIDATION = """
            {
              "field": "password",
              "message": "Password must be at least 6 characters long"
            }
            """;
    
    // ============================================
    // Private constructor to prevent instantiation
    // ============================================
    
    private ApiExamples() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}



