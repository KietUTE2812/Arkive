package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.ProfileRequest;
import com.example.arkivebackend.dto.response.ProfileResponse;

public interface ProfileService extends BaseCrudService<String, ProfileRequest, ProfileResponse> {
    ProfileResponse getMyProfile();
}
