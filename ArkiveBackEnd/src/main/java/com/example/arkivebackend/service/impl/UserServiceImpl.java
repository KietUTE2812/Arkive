package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.CreateUserRequest;
import com.example.arkivebackend.dto.request.UpdateUserRequest;
import com.example.arkivebackend.dto.response.UserResponse;
import com.example.arkivebackend.entity.Role;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.mapper.UserMapper;
import com.example.arkivebackend.repository.RoleRepository;
import com.example.arkivebackend.repository.UserRepository;
import com.example.arkivebackend.service.RoleService;
import com.example.arkivebackend.service.UserService;
import com.example.arkivebackend.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleService roleService;
    RoleRepository roleRepository;

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserResponse createUser (CreateUserRequest request) {
        var user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set default USER role
        var defaultRole = roleRepository.findByName("USER")
            .orElse(Role.builder().name("USER").description("Default user role").build());
        user.setRoles(Set.of(defaultRole));
        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            return null;
        }
        return userMapper.toUserResponse(user);
    }   

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return null;
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser (UpdateUserRequest request, String userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(request, user);
        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserById(String userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> searchUsers(String query) {
        List<User> users = userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_FOUND));
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse getCurrentUser() {
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    /*public UserResponse getCurrentUser() {
        var context = SecurityContextHolder.getContext();// Get the security context
        var authentication = context.getAuthentication(); // Get the authentication object
        var username = authentication.getName(); // Get the principal (the logged-in user)

        var user = userRepository.findByUsernameWithProfile(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var userResponse = userMapper.toUserResponse(user);
        return userResponse;
    }*/
}
