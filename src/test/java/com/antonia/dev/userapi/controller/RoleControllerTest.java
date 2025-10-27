package com.antonia.dev.userapi.controller;

import com.antonia.dev.userapi.dto.common.DeleteResponse;
import com.antonia.dev.userapi.dto.role.RoleDTO;
import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.security.JwtUtil;
import com.antonia.dev.userapi.security.CustomUserDetailsService;
import com.antonia.dev.userapi.service.role.RoleService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = RoleController.class,
    excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        SpringDocConfiguration.class
    }
)
@DisplayName("RoleController Tests")
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private RoleDTO testRoleDTO;

    @BeforeEach
    void setUp() {
        testRoleDTO = new RoleDTO(1L, "USER", "Default user role");
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all roles successfully")
    void getAllRoles_ShouldReturnListOfRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(testRoleDTO));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("USER"))
                .andExpect(jsonPath("$[0].description").value("Default user role"));

        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    @WithMockUser
    @DisplayName("Should return no content when no roles exist")
    void getAllRoles_WithNoRoles_ShouldReturnNoContent() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get role by name successfully")
    void getRoleByName_WithValidName_ShouldReturnRole() throws Exception {
        when(roleService.getRoleByName("USER")).thenReturn(Optional.of(testRoleDTO));

        mockMvc.perform(get("/api/roles/name/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("USER"));

        verify(roleService, times(1)).getRoleByName("USER");
    }

    @Test
    @WithMockUser
    @DisplayName("Should return not found when role name doesn't exist")
    void getRoleByName_WithInvalidName_ShouldReturnNotFound() throws Exception {
        when(roleService.getRoleByName("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/name/INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get role by ID successfully")
    void getRoleById_WithValidId_ShouldReturnRole() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(testRoleDTO));

        mockMvc.perform(get("/api/roles/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("USER"));

        verify(roleService, times(1)).getRoleById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return not found when role ID doesn't exist")
    void getRoleById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.getRoleById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/id/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should create role successfully")
    void createRole_WithValidData_ShouldReturnCreatedRole() throws Exception {
        RoleDTO newRoleDTO = new RoleDTO(null, "ADMIN", "Administrator role");
        RoleDTO createdRoleDTO = new RoleDTO(2L, "ADMIN", "Administrator role");
        when(roleService.createRole(any(RoleDTO.class))).thenReturn(createdRoleDTO);

        mockMvc.perform(post("/api/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRoleDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService, times(1)).createRole(any(RoleDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should update role successfully")
    void updateRole_WithValidData_ShouldReturnUpdatedRole() throws Exception {
        RoleDTO updateDTO = new RoleDTO(1L, "SUPER_USER", "Updated description");
        when(roleService.updateRole(eq(1L), any(RoleDTO.class))).thenReturn(Optional.of(updateDTO));

        mockMvc.perform(patch("/api/roles/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPER_USER"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        verify(roleService, times(1)).updateRole(eq(1L), any(RoleDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return not found when updating non-existent role")
    void updateRole_WithInvalidId_ShouldReturnNotFound() throws Exception {
        RoleDTO updateDTO = new RoleDTO(999L, "ADMIN", "Description");
        when(roleService.updateRole(eq(999L), any(RoleDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/roles/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete role successfully")
    void deleteRole_WithValidId_ShouldReturnDeleteResponse() throws Exception {
        Role deletedRole = new Role("USER", "User role");
        deletedRole.setId(1L);
        when(roleService.deleteRole(1L)).thenReturn(Optional.of(deletedRole));

        mockMvc.perform(delete("/api/roles/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Role deleted successfully"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("USER"));

        verify(roleService, times(1)).deleteRole(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return not found when deleting non-existent role")
    void deleteRole_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.deleteRole(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/roles/999")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle multiple roles correctly")
    void getAllRoles_WithMultipleRoles_ShouldReturnAllRoles() throws Exception {
        RoleDTO adminRole = new RoleDTO(2L, "ADMIN", "Admin role");
        RoleDTO moderatorRole = new RoleDTO(3L, "MODERATOR", "Moderator role");
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(testRoleDTO, adminRole, moderatorRole));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("USER"))
                .andExpect(jsonPath("$[1].name").value("ADMIN"))
                .andExpect(jsonPath("$[2].name").value("MODERATOR"));
    }
}
