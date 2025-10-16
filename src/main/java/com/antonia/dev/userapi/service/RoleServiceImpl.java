package com.antonia.dev.userapi.service;

import com.antonia.dev.userapi.dto.RoleDTO;
import com.antonia.dev.userapi.entity.Role;
import com.antonia.dev.userapi.exception.RoleNotFoundException;
import com.antonia.dev.userapi.mapper.RoleMapper;
import com.antonia.dev.userapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDTO> getRoleById(Long id) {
        return Optional.ofNullable(roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElseThrow(() -> new RoleNotFoundException("id", "Role not found whit ID " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDTO> getRoleByName(String roleName) {
        return Optional.ofNullable(roleRepository.findByName(roleName)
                .map(roleMapper::toDTO)
                .orElseThrow(() -> new RoleNotFoundException("id", "Role not found whit name " + roleName)));
    }

    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public Optional<RoleDTO> updateRole(Long id, RoleDTO roleDTO) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("id", "Role not found with ID: " + id));

        if (roleDTO.name() != null && !roleDTO.name().isBlank()) {
            existing.setName(roleDTO.name());
        }
        if (roleDTO.description() != null && !roleDTO.description().isBlank()) {
            existing.setDescription(roleDTO.description());
        }

        Role saved = roleRepository.save(existing);
        return Optional.of(roleMapper.toDTO(saved));
    }

    @Override
    @Transactional
    public Optional<Role> deleteRole(Long id) {
        return roleRepository.findById(id)
                .map(role -> {
                    roleRepository.delete(role);
                    return role;
                });
    }
}
