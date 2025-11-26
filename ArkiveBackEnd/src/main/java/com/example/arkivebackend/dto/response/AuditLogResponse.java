package com.example.arkivebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * DTO cho Audit Log Response
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuditLogResponse {

    String id;
    String userId;
    String username;
    String httpMethod;
    String requestUri;
    String queryString;
    String action;
    String resourceType;
    String resourceId;
    Integer statusCode;
    String ipAddress;
    String userAgent;
    String errorMessage;
    Long durationMs;
    Boolean isSuccess;
    LocalDateTime createdAt;
}

