package com.example.arkivebackend.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ==================== GENERAL & USER (1xxx) ====================
    // --- General (10xx) ---
    USER_NOT_FOUND(1001, "User not found", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND(1002, "User profile not found", HttpStatus.NOT_FOUND),
    COLLECTION_EXISTED(1003, "Collection with the same name already exists", HttpStatus.BAD_REQUEST),
    COLLECTION_NOT_FOUND(1004, "Collection not found", HttpStatus.NOT_FOUND),
    ASSET_NOT_FOUND(1005, "Asset not found", HttpStatus.NOT_FOUND),
    ASSET_ALREADY_DELETED(1010, "Asset has already been deleted", HttpStatus.BAD_REQUEST),
    SHARED_LINK_NOT_FOUND(1006, "Shared link not found", HttpStatus.NOT_FOUND),
    SHARED_LINK_ALREADY_EXISTS(1007, "Shared link already exists for this collection", HttpStatus.BAD_REQUEST),
    SHARED_LINK_PASSWORD_REQUIRED(1008, "Password is required to access this shared link", HttpStatus.UNAUTHORIZED),
    SHARED_LINK_PASSWORD_INCORRECT(1009, "Incorrect password for shared link", HttpStatus.UNAUTHORIZED),

    // --- Input Validation (11xx) ---
    PASSWORD_INVALID(1101, "Password must be at least 8 characters long and contain at least one digit, one lowercase, one uppercase letter, and one special character.", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1102, "Invalid email format", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1103, "Username must be between 3 and 20 characters", HttpStatus.BAD_REQUEST),
    DOB_INVALID(1104, "Invalid date of birth", HttpStatus.BAD_REQUEST),
    
    // --- Authentication (12xx) ---
    UNAUTHENTICATED(1201, "Authentication failed, please login", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(1202, "Incorrect username or password", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(1203, "Refresh token is invalid or expired", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1204, "You are not authorized to access this resource", HttpStatus.UNAUTHORIZED),

    // --- Registration (13xx) ---
    USERNAME_ALREADY_EXISTS(1301, "Username already exists", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1302, "Email already exists", HttpStatus.BAD_REQUEST),
    PROFILE_ALREADY_EXISTS(1303, "User profile already exists", HttpStatus.BAD_REQUEST),
    
    // --- Email Verification (14xx) ---
    ACCOUNT_NOT_ACTIVATED(1401, "Account has not been activated. Please check your email.", HttpStatus.FORBIDDEN),
    ACCOUNT_ALREADY_ACTIVATED(1402, "Account has already been activated", HttpStatus.BAD_REQUEST),
    VERIFICATION_TOKEN_INVALID(1403, "Verification token is invalid", HttpStatus.BAD_REQUEST),
    VERIFICATION_TOKEN_EXPIRED(1404, "Verification token has expired", HttpStatus.BAD_REQUEST),

    // --- Password Reset (15xx) ---
    RESET_PASSWORD_TOKEN_INVALID(1501, "Password reset token is invalid", HttpStatus.BAD_REQUEST),
    RESET_PASSWORD_TOKEN_EXPIRED(1502, "Password reset token has expired", HttpStatus.BAD_REQUEST),

    // ==================== AUTHORIZATION (2xxx) ====================
    FORBIDDEN(2001, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(2002, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(2003, "Role already exists", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(2004, "Permission not found", HttpStatus.NOT_FOUND),
    PERMISSION_EXISTED(2005, "Permission already exists", HttpStatus.BAD_REQUEST),
    
    // ==================== SYSTEM & EXTERNAL (9xxx) ====================
    EMAIL_SENDING_FAILED(9001, "Failed to send email due to an external service error", HttpStatus.SERVICE_UNAVAILABLE),
    UPLOAD_FILE_ERROR(9002, "An error occurred while uploading the file", HttpStatus.SERVICE_UNAVAILABLE),
    UNCATEGORIZED_EXCEPTION(9999, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(9003, "File not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}