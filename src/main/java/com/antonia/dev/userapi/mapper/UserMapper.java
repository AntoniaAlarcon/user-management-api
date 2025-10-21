package com.antonia.dev.userapi.mapper;

import com.antonia.dev.userapi.dto.user.CreateUserRequest;
import com.antonia.dev.userapi.dto.user.UserDTO;
import com.antonia.dev.userapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleName", source = "role.name")
    UserDTO toDTO(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "admin", ignore = true)
    User toEntity(CreateUserRequest request);
}