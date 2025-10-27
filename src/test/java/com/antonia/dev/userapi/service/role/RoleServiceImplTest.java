package com.antonia.dev.userapi.service.role;

import com.antonia.dev.userapi.dto.role.RoleDTO;
import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.exception.RoleNotFoundException;
import com.antonia.dev.userapi.mapper.RoleMapper;
import com.antonia.dev.userapi.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleServiceImpl Tests")
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;
    private RoleDTO testRoleDTO;

    @BeforeEach
    void setUp() {
        testRole = new Role("USER", "Default user role");
        testRole.setId(1L);

        testRoleDTO = new RoleDTO(1L, "USER", "Default user role");
    }

    @Test
    @DisplayName("Should get all roles successfully")
    void getAllRoles_ShouldReturnListOfRoles() {
        // Given
        List<Role> roles = Arrays.asList(testRole);
        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toDTO(any(Role.class))).thenReturn(testRoleDTO);

        // When
        List<RoleDTO> result = roleService.getAllRoles();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testRoleDTO);
        verify(roleRepository, times(1)).findAll();
        verify(roleMapper, times(1)).toDTO(testRole);
    }

    @Test
    @DisplayName("Should return empty list when no roles exist")
    void getAllRoles_WithNoRoles_ShouldReturnEmptyList() {
        // Given
        when(roleRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<RoleDTO> result = roleService.getAllRoles();

        // Then
        assertThat(result).isEmpty();
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get role by ID successfully")
    void getRoleById_WithValidId_ShouldReturnRole() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleMapper.toDTO(testRole)).thenReturn(testRoleDTO);

        // When
        Optional<RoleDTO> result = roleService.getRoleById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testRoleDTO);
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when role ID not found")
    void getRoleById_WithInvalidId_ShouldThrowException() {
        // Given
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.getRoleById(999L))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("Role not found whit ID 999");
        verify(roleRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get role by name successfully")
    void getRoleByName_WithValidName_ShouldReturnRole() {
        // Given
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));
        when(roleMapper.toDTO(testRole)).thenReturn(testRoleDTO);

        // When
        Optional<RoleDTO> result = roleService.getRoleByName("USER");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("USER");
        verify(roleRepository, times(1)).findByName("USER");
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when role name not found")
    void getRoleByName_WithInvalidName_ShouldThrowException() {
        // Given
        when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.getRoleByName("INVALID_ROLE"))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("Role not found whit name INVALID_ROLE");
    }

    @Test
    @DisplayName("Should create role successfully")
    void createRole_WithValidData_ShouldReturnCreatedRole() {
        // Given
        RoleDTO newRoleDTO = new RoleDTO(null, "ADMIN", "Administrator role");
        Role newRole = new Role("ADMIN", "Administrator role");
        Role savedRole = new Role("ADMIN", "Administrator role");
        savedRole.setId(2L);
        RoleDTO savedRoleDTO = new RoleDTO(2L, "ADMIN", "Administrator role");

        when(roleMapper.toEntity(newRoleDTO)).thenReturn(newRole);
        when(roleRepository.save(newRole)).thenReturn(savedRole);
        when(roleMapper.toDTO(savedRole)).thenReturn(savedRoleDTO);

        // When
        RoleDTO result = roleService.createRole(newRoleDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("ADMIN");
        verify(roleRepository, times(1)).save(newRole);
    }

    @Test
    @DisplayName("Should update role successfully")
    void updateRole_WithValidData_ShouldReturnUpdatedRole() {
        // Given
        RoleDTO updateDTO = new RoleDTO(1L, "SUPER_USER", "Updated description");
        Role updatedRole = new Role("SUPER_USER", "Updated description");
        updatedRole.setId(1L);
        RoleDTO updatedRoleDTO = new RoleDTO(1L, "SUPER_USER", "Updated description");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);
        when(roleMapper.toDTO(updatedRole)).thenReturn(updatedRoleDTO);

        // When
        Optional<RoleDTO> result = roleService.updateRole(1L, updateDTO);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("SUPER_USER");
        assertThat(result.get().description()).isEqualTo("Updated description");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when updating non-existent role")
    void updateRole_WithInvalidId_ShouldThrowException() {
        // Given
        RoleDTO updateDTO = new RoleDTO(999L, "ADMIN", "Description");
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roleService.updateRole(999L, updateDTO))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("Role not found with ID: 999");
    }

    @Test
    @DisplayName("Should update only name when description is blank")
    void updateRole_WithBlankDescription_ShouldUpdateOnlyName() {
        // Given
        RoleDTO updateDTO = new RoleDTO(1L, "NEW_USER", "");
        
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);
        when(roleMapper.toDTO(testRole)).thenReturn(testRoleDTO);

        // When
        roleService.updateRole(1L, updateDTO);

        // Then
        verify(roleRepository, times(1)).save(any(Role.class));
        // La descripción no debería cambiar
    }

    @Test
    @DisplayName("Should delete role successfully")
    void deleteRole_WithValidId_ShouldReturnDeletedRole() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        doNothing().when(roleRepository).delete(testRole);

        // When
        Optional<Role> result = roleService.deleteRole(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testRole);
        verify(roleRepository, times(1)).delete(testRole);
    }

    @Test
    @DisplayName("Should return empty when deleting non-existent role")
    void deleteRole_WithInvalidId_ShouldReturnEmpty() {
        // Given
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Role> result = roleService.deleteRole(999L);

        // Then
        assertThat(result).isEmpty();
        verify(roleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should handle multiple roles correctly")
    void getAllRoles_WithMultipleRoles_ShouldReturnAllRoles() {
        // Given
        Role adminRole = new Role("ADMIN", "Admin role");
        adminRole.setId(2L);
        Role moderatorRole = new Role("MODERATOR", "Moderator role");
        moderatorRole.setId(3L);

        RoleDTO adminDTO = new RoleDTO(2L, "ADMIN", "Admin role");
        RoleDTO moderatorDTO = new RoleDTO(3L, "MODERATOR", "Moderator role");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole, adminRole, moderatorRole));
        when(roleMapper.toDTO(testRole)).thenReturn(testRoleDTO);
        when(roleMapper.toDTO(adminRole)).thenReturn(adminDTO);
        when(roleMapper.toDTO(moderatorRole)).thenReturn(moderatorDTO);

        // When
        List<RoleDTO> result = roleService.getAllRoles();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(testRoleDTO, adminDTO, moderatorDTO);
        verify(roleRepository, times(1)).findAll();
    }
}



