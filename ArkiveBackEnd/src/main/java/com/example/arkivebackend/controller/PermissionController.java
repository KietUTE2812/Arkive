package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.request.PermissionRequest;
import com.example.arkivebackend.dto.response.PermissionResponse;
import com.example.arkivebackend.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionController {
    PermissionService permissionService;

    @PostMapping("")
    public ApiResponse<PermissionResponse> createPermission(@RequestBody @Valid PermissionRequest request) {
        var permission = permissionService.create(request);
        return ApiResponse.<PermissionResponse>builder()
                .success(true)
                .data(permission)
                .build();
    }
    @GetMapping("")
    public ApiResponse<List<PermissionResponse>> getAllPermissions(@RequestParam(required = false) String filter) {
        var permissions = permissionService.getAll(filter);
        return ApiResponse.<List<PermissionResponse>>builder()
                .success(true)
                .data(permissions)
                .build();
    }
    @DeleteMapping("/{permissionName}")
    public ApiResponse<Void> deletePermission(@PathVariable String permissionName) {
        permissionService.delete(permissionName);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> updatePermission(@PathVariable String id, @RequestBody @Valid PermissionRequest request) {
        var permission = permissionService.update(id, request);
        return ApiResponse.<PermissionResponse>builder()
                .success(true)
                .data(permission)
                .build();
    }

}
