package com.example.arkivebackend.dto.request;

import lombok.Data;

@Data
public class ProfileRequest {
    String phoneNumber;
    String address;
    String avatarUrl;
    String bio;
}
