package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.dto.auth.LoginRequest;
import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.security.CustomUserDetailsService;
import com.antonia.dev.userapi.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests using @WebMvcTest.
 *
 * NOTE: These tests currently have a configuration conflict with Springdoc OpenAPI
 * and @EnableJpaAuditing. The issue is that @WebMvcTest tries to load a minimal
 * context, but Springdoc triggers JPA initialization which requires a full database
 * context. This is a known limitation of @WebMvcTest with JPA auditing enabled.
 *
 * Possible solutions (for future refactoring):
 * - Use @SpringBootTest instead of @WebMvcTest (slower but more complete)
 * - Create a separate test configuration that disables @EnableJpaAuditing
 * - Use @DataJpaTest for repository tests and keep service tests (which work fine)
 *
 * Service layer tests (UserServiceImplTest, etc.) work correctly and provide
 * comprehensive coverage of business logic.
 */
@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        SpringDocConfiguration.class
    }
)
@DisplayName("AuthController Tests")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;
    private Role testRole;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testRole = new Role("USER", "Default user role");
        testRole.setId(1L);

        testUser = new User("Test User", "testuser", "test@example.com", "password123", testRole);
        testUser.setId(1L);

        userDetails = new org.springframework.security.core.userdetails.User(
                "testuser",
                "password123",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        Authentication authentication = mock(Authentication.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetailsService.loadUserEntityByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mock-jwt-token");
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("testuser", "USER");
    }

    @Test
    @DisplayName("Should return unauthorized with invalid credentials")
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should validate token successfully")
    void validateToken_WithValidToken_ShouldReturnValidationResponse() throws Exception {
        String token = "valid-jwt-token";
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(jwtUtil.extractRole(token)).thenReturn("USER");
        when(jwtUtil.validateToken(token)).thenReturn(true);
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(jwtUtil, times(1)).validateToken(token);
    }

    @Test
    @DisplayName("Should return unauthorized with invalid token")
    void validateToken_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        String token = "invalid-jwt-token";
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(jwtUtil.extractRole(token)).thenReturn("USER");
        when(jwtUtil.validateToken(token)).thenReturn(false);
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Should return unauthorized without Bearer prefix")
    void validateToken_WithoutBearerPrefix_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "invalid-jwt-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Should return unauthorized without Authorization header")
    void validateToken_WithoutAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Should handle exception during token validation")
    void validateToken_WithException_ShouldReturnUnauthorized() throws Exception {
        String token = "problematic-token";
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Token parsing error"));
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Should require valid request body for login")
    void login_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle login with different user roles")
    void login_WithAdminRole_ShouldReturnTokenWithAdminRole() throws Exception {
        Role adminRole = new Role("ADMIN", "Admin role");
        adminRole.setId(2L);
        User adminUser = new User("Admin User", "adminuser", "admin@example.com", "adminpass", adminRole);
        adminUser.setId(2L);

        LoginRequest loginRequest = new LoginRequest("adminuser", "adminpass");
        Authentication authentication = mock(Authentication.class);
        UserDetails adminUserDetails = new org.springframework.security.core.userdetails.User(
                "adminuser",
                "adminpass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminUserDetails);
        when(userDetailsService.loadUserEntityByUsername("adminuser")).thenReturn(adminUser);
        when(jwtUtil.generateToken("adminuser", "ADMIN")).thenReturn("admin-jwt-token");
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}


