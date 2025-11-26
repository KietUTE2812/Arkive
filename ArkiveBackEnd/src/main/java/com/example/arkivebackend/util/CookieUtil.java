package com.example.arkivebackend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@Slf4j
public class CookieUtil {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @Value("${jwt.refreshable-duration:604800}") // Mặc định 7 ngày
    private long REFRESH_TOKEN_EXPIRATION_MILLISECONDS;

    @Value("${app.environment:development}")
    private String APP_ENVIRONMENT;

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, token)
                .maxAge(Duration.ofMillis(REFRESH_TOKEN_EXPIRATION_MILLISECONDS))
                .httpOnly(true)
                .secure(APP_ENVIRONMENT.equals("production") ? true : false) // Quan trọng trên production
                .path("/")
                .sameSite("Strict") // Chống tấn công CSRF
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .build();
    }
}
