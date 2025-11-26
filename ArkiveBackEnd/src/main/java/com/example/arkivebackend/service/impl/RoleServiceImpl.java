package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.RoleRequest;
import com.example.arkivebackend.dto.response.RoleResponse;
import com.example.arkivebackend.entity.Role;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.mapper.RoleMapper;
import com.example.arkivebackend.repository.PermissionRepository;
import com.example.arkivebackend.repository.RoleRepository;
import com.example.arkivebackend.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @Override
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toRole(request);
        var exist = roleRepository.existsByName(request.getName());
        if (exist) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        var permissions = request.getPermissions().stream().map(permissionRepository::findByName)
                .filter(Optional::isPresent).map(Optional::get).toList();
        if (permissions.isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    public RoleResponse getById(String s) {
        return null;
    }

    @Override
    public List<RoleResponse> getAll() {
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    @Override
    public void delete(String roleName) {
        roleRepository.deleteById(roleName);
    }

    @Override
    public RoleResponse update(String id, RoleRequest request) {
        var role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        var permissions = request.getPermissions().stream().map(permissionRepository::findByName)
                .filter(Optional::isPresent).map(Optional::get).toList();
        if (permissions.isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
}
