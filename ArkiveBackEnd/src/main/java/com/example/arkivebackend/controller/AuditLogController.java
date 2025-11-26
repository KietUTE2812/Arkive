    package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.response.AuditLogResponse;
import com.example.arkivebackend.entity.AuditLog;
import com.example.arkivebackend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller để query Audit Logs.
 * Chỉ ADMIN mới có quyền truy cập.
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogController {

    AuditLogService auditLogService;

    /**
     * Lấy audit logs của một user cụ thể
     * Chỉ ADMIN hoặc chính user đó mới được xem
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['userId']")
    public ApiResponse<Page<AuditLogResponse>> getAuditLogsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditLogService.getAuditLogsByUserId(userId, pageable);
        Page<AuditLogResponse> response = auditLogs.map(this::toResponse);

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Lấy audit logs theo action
     * Chỉ ADMIN
     */
    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AuditLogResponse>> getAuditLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditLogService.getAuditLogsByAction(action, pageable);
        Page<AuditLogResponse> response = auditLogs.map(this::toResponse);

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Lấy audit logs cho một resource cụ thể
     * Chỉ ADMIN
     */
    @GetMapping("/resource/{resourceType}/{resourceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AuditLogResponse>> getAuditLogsForResource(
            @PathVariable String resourceType,
            @PathVariable String resourceId) {

        List<AuditLog> auditLogs = auditLogService.getAuditLogsForResource(resourceType, resourceId);
        List<AuditLogResponse> response = auditLogs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Lấy audit logs trong khoảng thời gian
     * Chỉ ADMIN
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AuditLogResponse>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditLogService.getAuditLogsByDateRange(startDate, endDate, pageable);
        Page<AuditLogResponse> response = auditLogs.map(this::toResponse);

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Lấy các failed requests
     * Chỉ ADMIN
     */
    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AuditLogResponse>> getFailedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditLogService.getFailedRequests(pageable);
        Page<AuditLogResponse> response = auditLogs.map(this::toResponse);

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Lấy các slow requests (> threshold ms)
     * Chỉ ADMIN
     */
    @GetMapping("/slow")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AuditLogResponse>> getSlowRequests(
            @RequestParam(defaultValue = "1000") Long thresholdMs,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditLogService.getSlowRequests(thresholdMs, pageable);
        Page<AuditLogResponse> response = auditLogs.map(this::toResponse);

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Thống kê số lượng requests của user trong khoảng thời gian
     * Chỉ ADMIN hoặc chính user đó
     */
    @GetMapping("/stats/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['userId']")
    public ApiResponse<Long> countRequestsByUser(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Long count = auditLogService.countRequestsByUserAndDateRange(userId, startDate, endDate);

        return ApiResponse.<Long>builder()
                .success(true)
                .data(count)
                .message("Total requests: " + count)
                .build();
    }

    /**
     * Helper method để convert Entity sang DTO
     */
    private AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .username(auditLog.getUsername())
                .httpMethod(auditLog.getHttpMethod())
                .requestUri(auditLog.getRequestUri())
                .queryString(auditLog.getQueryString())
                .action(auditLog.getAction())
                .resourceType(auditLog.getResourceType())
                .resourceId(auditLog.getResourceId())
                .statusCode(auditLog.getStatusCode())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .errorMessage(auditLog.getErrorMessage())
                .durationMs(auditLog.getDurationMs())
                .isSuccess(auditLog.getIsSuccess())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}

