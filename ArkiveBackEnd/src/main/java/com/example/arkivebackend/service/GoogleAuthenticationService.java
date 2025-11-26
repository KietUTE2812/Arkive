package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.response.AuthenticationResponse;

public interface GoogleAuthenticationService {
    AuthenticationResponse verifyAndProcessGoogleIdToken(String idToken);
}
