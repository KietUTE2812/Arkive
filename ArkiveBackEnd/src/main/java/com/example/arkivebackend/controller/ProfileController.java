package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.request.ProfileRequest;
import com.example.arkivebackend.dto.response.ProfileResponse;
import com.example.arkivebackend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    ProfileService profileService;

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile() {
        ProfileResponse profile = profileService.getMyProfile();
        return ApiResponse.<ProfileResponse>builder()
                .success(true)
                .data(profile)
                .build();
    }

    @PostMapping("")
    public ApiResponse<ProfileResponse> createProfile(@RequestBody ProfileRequest profileRequest) {
        ProfileResponse profile = profileService.create(profileRequest);
        return ApiResponse.<ProfileResponse>builder()
                .success(true)
                .data(profile)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProfileResponse> updateProfile(@RequestBody ProfileRequest profileRequest, @PathVariable String id) {
        ProfileResponse profile = profileService.update(id, profileRequest);
        return ApiResponse.<ProfileResponse>builder()
                .success(true)
                .data(profile)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProfile(@PathVariable String id) {
        profileService.delete(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }
}
