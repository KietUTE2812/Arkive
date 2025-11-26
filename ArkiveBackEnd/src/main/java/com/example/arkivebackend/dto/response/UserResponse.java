package com.example.arkivebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    String id;
    String username;
    String fullName;
    String email;
    String address;
    String phoneNumber;
    String avatarUrl;
    String bio;
    LocalDate dateOfBirth;
    Set<RoleResponse> roles; // Sử dụng DTO thay vì Entity
    boolean isVerified;
}
