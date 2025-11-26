package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.response.AuthenticationResponse;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.enums.AuthProvider;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.repository.UserRepository;
import com.example.arkivebackend.service.AuthenticationService;
import com.example.arkivebackend.service.GoogleAuthenticationService;
import com.example.arkivebackend.util.TokenUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthenticationServiceImpl implements GoogleAuthenticationService {

    UserRepository userRepository;
    AuthenticationService authenticationService;
    PasswordEncoder passwordEncoder;

    @Value("${app.google.client-id}")
    @NonFinal
    String googleClientId;

    @Value("${app.google.client-secret}")
    @NonFinal
    String googleClientSecret;

    TokenUtil tokenUtil;

    @Override
    public AuthenticationResponse verifyAndProcessGoogleIdToken(String idToken) {
        GoogleIdToken.Payload payload = verifyGoogleIdToken(idToken);
        String email = payload.getEmail();

        // Tìm người dùng theo email và nếu không tồn tại thì tạo mới
        User userOpt = userRepository.findByEmail(email).orElseGet(() -> createUserFromGoogle(payload));

        // Tạo access token và refresh token cho người dùng
        String token = tokenUtil.generateToken(userOpt, false);
        String refreshToken = tokenUtil.generateToken(userOpt, true);

        // Lưu refresh token vào cơ sở dữ liệu
        tokenUtil.saveRefreshToken(refreshToken, userOpt, false, "Google Login", "Google Login");
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    private User createUserFromGoogle(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        // Kiểm tra lại lần nữa để tránh race condition
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User newUser = User.builder()
                .email(email)
                .username(email) // Hoặc tạo username duy nhất từ email
                .fullName((String) payload.get("name"))
                .authProvider(AuthProvider.GOOGLE)
                .isVerified(true) // Người dùng từ Google được coi là đã xác thực email
                // Tạo mật khẩu ngẫu nhiên vì người dùng sẽ không dùng mật khẩu này để đăng nhập
                .password(passwordEncoder.encode(Long.toHexString(Double.doubleToLongBits(Math.random())))) 
                .build();

        log.info("Creating a new user from Google login: {}", email);
        return userRepository.save(newUser);
    }

    private GoogleIdToken.Payload verifyGoogleIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // issuer phải là Google
                if (!payload.getIssuer().equals("accounts.google.com") 
                    && !payload.getIssuer().equals("https://accounts.google.com")) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                // token chưa hết hạn
                if (payload.getExpirationTimeSeconds() < System.currentTimeMillis() / 1000) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                return payload;
            }
            else {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error verifying Google ID token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
}
