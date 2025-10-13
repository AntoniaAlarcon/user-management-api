package com.antonia.dev.userapi.mapper;

import com.antonia.dev.userapi.dto.RoleDTO;
import com.antonia.dev.userapi.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    Role toEntity(RoleDTO roleDTO);
}
