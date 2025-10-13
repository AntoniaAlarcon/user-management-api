package com.antonia.dev.userapi.mapper;

import com.antonia.dev.userapi.dto.CreateUserRequest;
import com.antonia.dev.userapi.dto.UserDTO;
import com.antonia.dev.userapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleName", source = "role.name")
    UserDTO toDTO(User user);
    
    User toEntity(CreateUserRequest request);
}