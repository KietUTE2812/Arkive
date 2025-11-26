package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.PermissionRequest;
import com.example.arkivebackend.dto.response.PermissionResponse;
import com.example.arkivebackend.entity.Permission;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.mapper.PermissionMapper;
import com.example.arkivebackend.repository.PermissionRepository;
import com.example.arkivebackend.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)

public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        var exist = permissionRepository.existsByName(request.getName());
        if (exist) {
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    public PermissionResponse getById(String s) {
        return null;
    }

    @Override
    public List<PermissionResponse> getAll(String filter) {
        var permissions = permissionRepository.findByNameContaining(filter);
        return permissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    @Override
    public void delete(String permissionName) {
        permissionRepository.deleteById(permissionName);
    }

    @Override
    public PermissionResponse update(String id, PermissionRequest request) {
        var permission = permissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        permission.setDescription(request.getDescription());
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }
}
