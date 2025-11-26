package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.PermissionRequest;
import com.example.arkivebackend.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService extends BaseCrudService<String, PermissionRequest, PermissionResponse> {
    List<PermissionResponse> getAll(String filter);
}
