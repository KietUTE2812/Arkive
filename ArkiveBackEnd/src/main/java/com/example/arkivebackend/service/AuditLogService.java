package com.example.arkivebackend.service;

import com.example.arkivebackend.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface cho Audit Log.
 * Hiện tại chỉ log ra console, có thể mở rộng để lưu vào database.
 */
public interface AuditLogService {

    /**
     * Lưu audit log (có thể bật/tắt qua configuration)
     */
    void saveAuditLog(AuditLog auditLog);

    /**
     * Lấy audit logs của một user
     */
    Page<AuditLog> getAuditLogsByUserId(String userId, Pageable pageable);

    /**
     * Lấy audit logs theo action
     */
    Page<AuditLog> getAuditLogsByAction(String action, Pageable pageable);

    /**
     * Lấy audit logs cho một resource cụ thể
     */
    List<AuditLog> getAuditLogsForResource(String resourceType, String resourceId);

    /**
     * Lấy audit logs trong khoảng thời gian
     */
    Page<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Lấy các failed requests
     */
    Page<AuditLog> getFailedRequests(Pageable pageable);

    /**
     * Lấy các slow requests (duration > threshold ms)
     */
    Page<AuditLog> getSlowRequests(Long thresholdMs, Pageable pageable);

    /**
     * Thống kê số lượng requests của user trong khoảng thời gian
     */
    Long countRequestsByUserAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate);
}

