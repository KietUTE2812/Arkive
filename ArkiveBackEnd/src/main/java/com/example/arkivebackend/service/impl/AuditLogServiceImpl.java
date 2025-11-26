package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.entity.AuditLog;
import com.example.arkivebackend.repository.AuditLogRepository;
import com.example.arkivebackend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation của AuditLogService.
 * Hiện tại lưu vào database một cách async để không ảnh hưởng performance.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    AuditLogRepository auditLogRepository;

    @Value("${app.audit-log.enabled:true}")
    @NonFinal
    boolean auditLogEnabled;

    @NonFinal
    @Value("${app.audit-log.save-to-database:false}")
    boolean saveToDatabase;

    /**
     * Lưu audit log một cách async để không block request
     */
    @Override
    @Async
    @Transactional
    public void saveAuditLog(AuditLog auditLog) {
        if (!auditLogEnabled) {
            return;
        }

        try {
            if (saveToDatabase) {
                auditLogRepository.save(auditLog);
                log.debug("Audit log saved to database: {} {} {}",
                    auditLog.getHttpMethod(),
                    auditLog.getRequestUri(),
                    auditLog.getStatusCode());
            } else {
                log.debug("Audit log save-to-database is disabled. Only console logging is active.");
            }
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUserId(String userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsForResource(String resourceType, String resourceId) {
        return auditLogRepository.findByResourceTypeAndResourceIdOrderByCreatedAtDesc(resourceType, resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByDateRange(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getFailedRequests(Pageable pageable) {
        return auditLogRepository.findFailedRequests(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getSlowRequests(Long thresholdMs, Pageable pageable) {
        return auditLogRepository.findSlowRequests(thresholdMs, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countRequestsByUserAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.countByUserIdAndDateRange(userId, startDate, endDate);
    }
}

