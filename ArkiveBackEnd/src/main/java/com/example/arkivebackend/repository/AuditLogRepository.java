package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho AuditLog entity.
 * Cung cấp các phương thức query để tìm kiếm và phân tích audit logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    /**
     * Tìm tất cả audit logs của một user
     */
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Tìm audit logs theo action
     */
    Page<AuditLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    /**
     * Tìm audit logs theo resource type và resource id
     */
    List<AuditLog> findByResourceTypeAndResourceIdOrderByCreatedAtDesc(String resourceType, String resourceId);

    /**
     * Tìm audit logs trong khoảng thời gian
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    /**
     * Tìm các failed requests (status >= 400)
     */
    @Query("SELECT a FROM AuditLog a WHERE a.statusCode >= 400 ORDER BY a.createdAt DESC")
    Page<AuditLog> findFailedRequests(Pageable pageable);

    /**
     * Tìm audit logs theo user và action
     */
    Page<AuditLog> findByUserIdAndActionOrderByCreatedAtDesc(String userId, String action, Pageable pageable);

    /**
     * Thống kê số lượng requests theo user trong khoảng thời gian
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    Long countByUserIdAndDateRange(
        @Param("userId") String userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Tìm các requests chậm (duration > threshold)
     */
    @Query("SELECT a FROM AuditLog a WHERE a.durationMs > :threshold ORDER BY a.durationMs DESC")
    Page<AuditLog> findSlowRequests(@Param("threshold") Long threshold, Pageable pageable);
}

