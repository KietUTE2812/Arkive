package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.request.*;
import com.example.arkivebackend.dto.response.AuthenticationResponse;
import com.example.arkivebackend.dto.response.IntrospectResponse;
import com.example.arkivebackend.dto.response.UserResponse;
import com.example.arkivebackend.service.AuthenticationService;
import com.example.arkivebackend.service.GoogleAuthenticationService;
import com.example.arkivebackend.util.CookieUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    GoogleAuthenticationService googleAuthenticationService;

    CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest bodyRequest, HttpServletRequest httpServletRequest) {
        var response = authenticationService.authenticate(bodyRequest, httpServletRequest);
        var cookie = cookieUtil.createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .data(AuthenticationResponse.builder()
                                .token(response.getToken())
                                .authenticated(true)
                                .build())
                        .success(true)
                        .build());
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
        throws ParseException, JOSEException {
        var response = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .data(response)
                .success(true)
                .build();   
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout(HttpServletRequest httpServletRequest, 
    @CookieValue(value = CookieUtil.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) 
    throws ParseException, JOSEException {
        var response = authenticationService.logout(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie().toString())
                .body(ApiResponse.<Boolean>builder()
                        .data(response)
                        .success(true)
                        .build());
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {
        var response = authenticationService.register(request);
        return ApiResponse.<UserResponse>builder()
                .data(response)
                .success(true)
                .message("User registered successfully. Please check your email for verification code.")
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(HttpServletRequest httpServletRequest, 
    @CookieValue(value = CookieUtil.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken)
    throws ParseException, JOSEException {
        log.info("Refresh Token: {}", refreshToken);
        var response = authenticationService.refreshToken(refreshToken, httpServletRequest);
        var cookie = cookieUtil.createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .data(AuthenticationResponse.builder()
                                .token(response.getToken())
                                .authenticated(true)
                                .build())
                        .success(true)
                        .build());
    }

    @PostMapping("/verify-email")
    public ApiResponse<Boolean> verifyEmail(@RequestBody VerificationRequest request) {
        var response = authenticationService.verifyEmail(request.getVerificationCode());
        return ApiResponse.<Boolean>builder()
                .data(response)
                .success(true)
                .message("Email verified successfully.")
                .build();
    }

    @PostMapping("/resend-verification-code")
    public ApiResponse<Boolean> resendVerificationCode(@RequestBody ResendVerifyRequest request)
    throws ParseException, JOSEException, IOException, MessagingException {
        var response = authenticationService.resendVerificationCode(request.getEmail());
        return ApiResponse.<Boolean>builder()
                .data(response)
                .success(true)
                .message("Verification code resent successfully.")
                .build();
    }
    
    @PostMapping("/forgot-password")
    public ApiResponse<Boolean> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        var response = authenticationService.forgotPassword(request.getEmail());
        return ApiResponse.<Boolean>builder()
                .data(response)
                .success(true)
                .message("If the email is registered, a password reset link has been sent.")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
        var response = authenticationService.resetPassword(request.getToken(), request.getNewPassword());
        return ApiResponse.<Boolean>builder()
                .data(response)
                .success(true)
                .message("Password reset successfully.")
                .build();
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> googleLogin(@RequestBody GoogleLoginRequest bodyRequest, HttpServletRequest httpServletRequest) throws Exception {
        var response = googleAuthenticationService.verifyAndProcessGoogleIdToken(bodyRequest.getIdToken());
        var cookie = cookieUtil.createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .data(AuthenticationResponse.builder()
                                .token(response.getToken())
                                .authenticated(true)
                                .build())
                        .success(true)
                        .build());
    }
        

}
