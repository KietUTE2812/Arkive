package com.example.arkivebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Entity để lưu trữ Audit Log.
 * Hiện tại chỉ log ra console, nhưng có thể mở rộng để lưu vào database.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_audit_status", columnList = "status_code")
})
@Entity
public class AuditLog extends BaseEntity {

    /**
     * User ID của người thực hiện action (null nếu anonymous)
     */
    @Column(name = "user_id", length = 36)
    String userId;

    /**
     * Username của người thực hiện (null nếu anonymous)
     */
    @Column(name = "username", length = 100)
    String username;

    /**
     * HTTP Method (GET, POST, PUT, DELETE, PATCH)
     */
    @Column(name = "http_method", nullable = false, length = 10)
    String httpMethod;

    /**
     * Request URI
     */
    @Column(name = "request_uri", nullable = false, length = 500)
    String requestUri;

    /**
     * Query String (nếu có)
     */
    @Column(name = "query_string", length = 1000)
    String queryString;

    /**
     * Action/Operation được thực hiện (ví dụ: CREATE_USER, DELETE_ASSET)
     */
    @Column(name = "action", length = 100)
    String action;

    /**
     * Resource Type (USER, ASSET, COLLECTION, etc.)
     */
    @Column(name = "resource_type", length = 50)
    String resourceType;

    /**
     * Resource ID (ID của entity bị tác động)
     */
    @Column(name = "resource_id", length = 36)
    String resourceId;

    /**
     * HTTP Status Code của response
     */
    @Column(name = "status_code", nullable = false)
    Integer statusCode;

    /**
     * IP Address của client
     */
    @Column(name = "ip_address", length = 45) // IPv6 max length
    String ipAddress;

    /**
     * User Agent
     */
    @Column(name = "user_agent", length = 500)
    String userAgent;

    /**
     * Request Body (chỉ lưu nếu cần, có thể null để tiết kiệm space)
     */
    @Column(name = "request_body", columnDefinition = "TEXT")
    String requestBody;

    /**
     * Response Body (chỉ lưu nếu cần, có thể null để tiết kiệm space)
     */
    @Column(name = "response_body", columnDefinition = "TEXT")
    String responseBody;

    /**
     * Error Message (nếu có lỗi xảy ra)
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    String errorMessage;

    /**
     * Thời gian xử lý request (milliseconds)
     */
    @Column(name = "duration_ms")
    Long durationMs;

    /**
     * Success/Failure flag
     */
    @Column(name = "is_success", nullable = false)
    Boolean isSuccess;

    /**
     * Additional metadata (JSON format)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    String metadata;
}

