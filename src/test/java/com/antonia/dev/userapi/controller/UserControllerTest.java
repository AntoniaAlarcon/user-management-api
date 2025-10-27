package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.dto.common.DeleteResponse;
import com.antonia.dev.userapi.dto.user.CreateUserRequest;
import com.antonia.dev.userapi.dto.user.UserDTO;
import com.antonia.dev.userapi.dto.user.UserUpdateSelfRequest;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.security.JwtUtil;
import com.antonia.dev.userapi.security.CustomUserDetailsService;
import com.antonia.dev.userapi.service.user.UserService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        SpringDocConfiguration.class
    }
)
@DisplayName("UserController Tests")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private UserDTO testUserDTO;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO(1L, "Test User", "testuser", "test@example.com", "USER");
        createUserRequest = new CreateUserRequest("Test User", "testuser", "test@example.com", "password123", "USER");
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser
    @DisplayName("Should return no content when no users exist")
    void getAllUsers_WithNoUsers_ShouldReturnNoContent() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get user by ID successfully")
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUserDTO));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return not found when user ID doesn't exist")
    void getUserById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get users by name successfully")
    void getUsersByName_WithValidName_ShouldReturnUsers() throws Exception {
        when(userService.getUsersByName("Test User")).thenReturn(Arrays.asList(testUserDTO));

        mockMvc.perform(get("/api/users/name/Test User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"));

        verify(userService, times(1)).getUsersByName("Test User");
    }

    @Test
    @WithMockUser
    @DisplayName("Should get user by email successfully")
    void getUserByEmail_WithValidEmail_ShouldReturnUser() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUserDTO));

        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    @WithMockUser
    @DisplayName("Should get user by username successfully")
    void getUserByUsername_WithValidUsername_ShouldReturnUser() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUserDTO));

        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    @WithMockUser
    @DisplayName("Should get users by role successfully")
    void getUsersByRole_WithValidRole_ShouldReturnUsers() throws Exception {
        when(userService.getUsersByRole("USER")).thenReturn(Arrays.asList(testUserDTO));

        mockMvc.perform(get("/api/users/role/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleName").value("USER"));

        verify(userService, times(1)).getUsersByRole("USER");
    }

    @Test
    @WithMockUser
    @DisplayName("Should create user successfully")
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUserDTO);

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should update user by admin successfully")
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        UserDTO updateDTO = new UserDTO(1L, "Updated User", "updateduser", "updated@example.com", "ADMIN");
        when(userService.updateByAdmin(eq(1L), any(UserDTO.class))).thenReturn(Optional.of(updateDTO));

        mockMvc.perform(patch("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));

        verify(userService, times(1)).updateByAdmin(eq(1L), any(UserDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should update self successfully")
    void updateSelf_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateSelfRequest updateRequest = new UserUpdateSelfRequest(
                "Updated Name", 
                "updated@example.com", 
                "updateduser", 
                "newpassword"
        );
        UserDTO updatedDTO = new UserDTO(1L, "Updated Name", "updateduser", "updated@example.com", "USER");
        when(userService.updateSelf(eq(1L), any(UserUpdateSelfRequest.class))).thenReturn(Optional.of(updatedDTO));

        mockMvc.perform(patch("/api/users/self/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(userService, times(1)).updateSelf(eq(1L), any(UserUpdateSelfRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete user successfully")
    void deleteUser_WithValidId_ShouldReturnDeleteResponse() throws Exception {
        User deletedUser = new User("Test User", "testuser", "test@example.com", "password", new Role("USER", "User role"));
        deletedUser.setId(1L);
        when(userService.delete(1L)).thenReturn(Optional.of(deletedUser));

        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return not found when deleting non-existent user")
    void deleteUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(userService.delete(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/999")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should validate required fields when creating user")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateUserRequest invalidRequest = new CreateUserRequest("", "", "", "", "");

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
