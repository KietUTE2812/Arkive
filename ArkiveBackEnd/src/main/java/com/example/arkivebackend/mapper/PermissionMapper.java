package com.example.arkivebackend.mapper;

import com.example.arkivebackend.dto.request.PermissionRequest;
import com.example.arkivebackend.dto.response.PermissionResponse;
import com.example.arkivebackend.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
