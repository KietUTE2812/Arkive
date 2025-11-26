package com.example.arkivebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration để enable async execution cho Audit Log service.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Spring sẽ tự động tạo ThreadPoolTaskExecutor mặc định
    // Có thể custom executor nếu cần
}

