package com.antonia.dev.userapi.service.user;

import com.antonia.dev.userapi.dto.common.ErrorResponse;
import com.antonia.dev.userapi.dto.user.CreateUserRequest;
import com.antonia.dev.userapi.dto.user.UserDTO;
import com.antonia.dev.userapi.dto.user.UserUpdateSelfRequest;
import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.entity.User;
import com.antonia.dev.userapi.exception.RoleNotFoundException;
import com.antonia.dev.userapi.exception.UserNotFoundException;
import com.antonia.dev.userapi.exception.UserValidationException;
import com.antonia.dev.userapi.mapper.UserMapper;
import com.antonia.dev.userapi.repository.RoleRepository;
import com.antonia.dev.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;
    private Role testRole;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        testRole = new Role("USER", "Default user role");
        testRole.setId(1L);

        testUser = new User("Test User", "testuser", "test@example.com", "password123", testRole);
        testUser.setId(1L);

        testUserDTO = new UserDTO(1L, "Test User", "testuser", "test@example.com", "USER");

        createUserRequest = new CreateUserRequest("Test User", "testuser", "test@example.com", "password123", "USER");
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testUserDTO);
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTO(testUser);
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_WithValidId_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        Optional<UserDTO> result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUserDTO);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user ID not found")
    void getUserById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found whit ID 999");
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void getUserByEmail_WithValidEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        Optional<UserDTO> result = userService.getUserByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when email not found")
    void getUserByEmail_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found whit email");
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void getUserByUsername_WithValidUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        Optional<UserDTO> result = userService.getUserByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().username()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should get users by name successfully")
    void getUsersByName_WithValidName_ShouldReturnUsers() {
        when(userRepository.findByName("Test User")).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        List<UserDTO> result = userService.getUsersByName("Test User");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test User");
        verify(userRepository, times(1)).findByName("Test User");
    }

    @Test
    @DisplayName("Should get users by role successfully")
    void getUsersByRole_WithValidRole_ShouldReturnUsers() {
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));
        when(userRepository.findByRoleName("USER")).thenReturn(Arrays.asList(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        List<UserDTO> result = userService.getUsersByRole("USER");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).roleName()).isEqualTo("USER");
        verify(roleRepository, times(1)).findByName("USER");
        verify(userRepository, times(1)).findByRoleName("USER");
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when role not found")
    void getUsersByRole_WithInvalidRole_ShouldThrowException() {
        when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUsersByRole("INVALID_ROLE"))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("Role not found with name: INVALID_ROLE");
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_WithValidData_ShouldReturnCreatedUser() {
        User newUser = new User("Test User", "testuser", "test@example.com", "encodedPassword", testRole);
        
        when(userMapper.toEntity(createUserRequest)).thenReturn(testUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        UserDTO result = userService.createUser(createUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    @DisplayName("Should throw UserValidationException when email already exists")
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        when(userMapper.toEntity(createUserRequest)).thenReturn(testUser);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));

        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(UserValidationException.class);
    }

    @Test
    @DisplayName("Should throw UserValidationException when username already exists")
    void createUser_WithDuplicateUsername_ShouldThrowException() {
        when(userMapper.toEntity(createUserRequest)).thenReturn(testUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));

        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(UserValidationException.class);
    }

    @Test
    @DisplayName("Should update self successfully")
    void updateSelf_WithValidData_ShouldReturnUpdatedUser() {
        UserUpdateSelfRequest updateRequest = new UserUpdateSelfRequest(
                "Updated Name", 
                "updated@example.com", 
                "newusername", 
                "newpassword"
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        Optional<UserDTO> result = userService.updateSelf(1L, updateRequest);

        assertThat(result).isPresent();
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existent user")
    void updateSelf_WithInvalidId_ShouldThrowException() {
        UserUpdateSelfRequest updateRequest = new UserUpdateSelfRequest(
                "Updated Name", 
                "updated@example.com", 
                "newusername", 
                null
        );
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateSelf(999L, updateRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");
    }

    @Test
    @DisplayName("Should update user by admin successfully")
    void updateByAdmin_WithValidData_ShouldReturnUpdatedUser() {
        UserDTO updateRequest = new UserDTO(1L, "Admin Updated", "adminuser", "admin@example.com", "ADMIN");
        Role adminRole = new Role("ADMIN", "Admin role");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(updateRequest);

        Optional<UserDTO> result = userService.updateByAdmin(1L, updateRequest);

        assertThat(result).isPresent();
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByName("ADMIN");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void delete_WithValidId_ShouldReturnDeletedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        Optional<User> result = userService.delete(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should return empty when deleting non-existent user")
    void delete_WithInvalidId_ShouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.delete(999L);

        assertThat(result).isEmpty();
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return true when username exists")
    void existsByUsername_WithExistingUsername_ShouldReturnTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsByUsername("testuser");
    }
}
