package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.ProfileRequest;
import com.example.arkivebackend.dto.response.ProfileResponse;
import com.example.arkivebackend.entity.Profile;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.repository.ProfileRepository;
import com.example.arkivebackend.repository.UserRepository;
import com.example.arkivebackend.service.ProfileService;
import com.example.arkivebackend.util.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImpl implements ProfileService {

    ProfileRepository profileRepository;
    UserRepository userRepository;

    private String getUserIdFromContext() {
        return SecurityUtil.getCurrentUserId();
    }

    @Override
    public ProfileResponse create(ProfileRequest request) {
        User user = userRepository.findById(getUserIdFromContext())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setBio(request.getBio());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setAddress(request.getAddress());
        profile.setPhoneNumber(request.getPhoneNumber());

        profileRepository.save(profile);
        return ProfileResponse.builder()
                .address(profile.getAddress())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .phoneNumber(profile.getPhoneNumber())
                .build();
    }

    @Override
    public ProfileResponse getById(String s) {
        Profile profile = profileRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return ProfileResponse.builder()
                .address(profile.getAddress())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .phoneNumber(profile.getPhoneNumber())
                .build();
    }

    @Override
    public ProfileResponse update(String s, ProfileRequest request) {
        User user = userRepository.findById(getUserIdFromContext()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Profile profile = profileRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        if (!profile.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        profile.setBio(request.getBio());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setAddress(request.getAddress());
        profile.setPhoneNumber(request.getPhoneNumber());
        profileRepository.save(profile);
        return ProfileResponse.builder()
                .address(profile.getAddress())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .phoneNumber(profile.getPhoneNumber())
                .build();
    }

    @Override
    public void delete(String s) {
        Profile profile = profileRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        User user = userRepository.findById(getUserIdFromContext()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!profile.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        profileRepository.delete(profile);
    }

    public ProfileResponse getMyProfile() {
        String userId = getUserIdFromContext();
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return ProfileResponse.builder()
                .address(profile.getAddress())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .phoneNumber(profile.getPhoneNumber())
                .build();
    }
}
