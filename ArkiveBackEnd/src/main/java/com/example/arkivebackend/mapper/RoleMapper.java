package com.example.arkivebackend.mapper;

import com.example.arkivebackend.dto.request.RoleRequest;
import com.example.arkivebackend.dto.response.RoleResponse;
import com.example.arkivebackend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {   
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);

}
