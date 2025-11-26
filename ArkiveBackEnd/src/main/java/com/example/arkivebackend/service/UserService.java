package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.CreateUserRequest;
import com.example.arkivebackend.dto.request.UpdateUserRequest;
import com.example.arkivebackend.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser (CreateUserRequest request);
    UserResponse getUserByUsername(String username);
    UserResponse updateUser (UpdateUserRequest request, String userId);
    UserResponse getUserById(String userId);
    List<UserResponse> searchUsers(String query);
    UserResponse getCurrentUser();
}
