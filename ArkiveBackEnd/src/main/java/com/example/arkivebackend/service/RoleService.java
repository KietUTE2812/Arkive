package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.RoleRequest;
import com.example.arkivebackend.dto.response.RoleResponse;

import java.util.List;

public interface RoleService extends  BaseCrudService<String, RoleRequest, RoleResponse> {
    List<RoleResponse> getAll();
}
