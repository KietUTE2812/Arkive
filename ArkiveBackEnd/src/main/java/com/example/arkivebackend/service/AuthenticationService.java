package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.AuthenticationRequest;
import com.example.arkivebackend.dto.request.IntrospectRequest;
import com.example.arkivebackend.dto.request.RegisterRequest;
import com.example.arkivebackend.dto.response.AuthenticationResponse;
import com.example.arkivebackend.dto.response.IntrospectResponse;
import com.example.arkivebackend.dto.response.UserResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.text.ParseException;

public interface AuthenticationService {
    // Các phương thức của dịch vụ xác thực
    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest);

    UserResponse register(RegisterRequest request);

    boolean verifyEmail(String verificationCode);

    boolean resendVerificationCode(String email) throws IOException, MessagingException;

    boolean logout(String refreshToken) throws ParseException, JOSEException;

    AuthenticationResponse refreshToken(String refreshToken, HttpServletRequest httpServletRequest) throws ParseException, JOSEException;

    Boolean forgotPassword(String email);

    Boolean resetPassword(String token, String newPassword);

    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
}
