package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.request.RoleRequest;
import com.example.arkivebackend.dto.response.RoleResponse;
import com.example.arkivebackend.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasRole('ADMIN')") // trả về exception nếu dạng AccessDeniedException
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleController {

    RoleService roleService;

    @PostMapping("")
    public ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleRequest request) {
        var role = roleService.create(request);
        return ApiResponse.<RoleResponse>builder()
                .success(true)
                .data(role)
                .build();
    }
    @GetMapping("")
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        var roles = roleService.getAll();
        return ApiResponse.<List<RoleResponse>>builder()
                .success(true)
                .data(roles)
                .build();
    }

    @DeleteMapping("/{roleName}")
    public ApiResponse<Void> deleteRole(@PathVariable String roleName) {
        roleService.delete(roleName);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable String id, @RequestBody @Valid RoleRequest request) {
        var role = roleService.update(id, request);
        return ApiResponse.<RoleResponse>builder()
                .success(true)
                .data(role)
                .build();
    }

}
