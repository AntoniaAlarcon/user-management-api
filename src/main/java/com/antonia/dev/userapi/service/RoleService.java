package com.antonia.dev.userapi.service;

import com.antonia.dev.userapi.dto.RoleDTO;
import com.antonia.dev.userapi.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleDTO> getAllRoles();
    Optional<RoleDTO> getRoleById(Long id);
    Optional<RoleDTO> getRoleByName(String name);
    RoleDTO createRole(RoleDTO roleDTO);
    Optional<RoleDTO> updateRole(Long id, RoleDTO roleDTO);
    Optional<Role> deleteRole(Long id);
}
