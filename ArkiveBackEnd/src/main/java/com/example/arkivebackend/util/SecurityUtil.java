package com.example.arkivebackend.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utility class để xử lý các thao tác liên quan đến Security Context.
 */
@Component
public class SecurityUtil {

    /**
     * Lấy User ID từ JWT token của user hiện tại.
     *
     * @return User ID (String)
     */
    public static String getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaimAsString("userId");
    }

    /**
     * Lấy username từ JWT token của user hiện tại.
     *
     * @return Username (String)
     */
    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Kiểm tra xem user hiện tại có authenticated không.
     *
     * @return true nếu authenticated, false nếu không
     */
    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }
}

