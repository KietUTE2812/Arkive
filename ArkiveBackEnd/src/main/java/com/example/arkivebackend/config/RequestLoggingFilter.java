package com.example.arkivebackend.config;

import com.example.arkivebackend.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * Filter để ghi log mọi HTTP request và response.
 * Đây là tiền thân của Audit Log, có thể phát triển thêm để lưu vào database.
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Wrap request và response để có thể đọc body nhiều lần
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            // Log request trước khi xử lý
            logRequest(wrappedRequest, startTime);

            // Tiếp tục xử lý request
            filterChain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            // Log response sau khi xử lý
            long duration = System.currentTimeMillis() - startTime;
            logResponse(wrappedRequest, wrappedResponse, duration);

            // Quan trọng: Copy response body về client
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * Log thông tin request
     */
    private void logRequest(HttpServletRequest request, long startTime) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String remoteAddr = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String userId = getCurrentUserId();

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n╔═══════════════════════════════════════════════════════════════════════════════");
        logMessage.append("\n║ 📥 INCOMING REQUEST");
        logMessage.append("\n╠═══════════════════════════════════════════════════════════════════════════════");
        logMessage.append(String.format("\n║ Timestamp    : %s", timestamp));
        logMessage.append(String.format("\n║ Method       : %s", method));
        logMessage.append(String.format("\n║ URI          : %s", uri));
        if (queryString != null) {
            logMessage.append(String.format("\n║ Query String : %s", queryString));
        }
        logMessage.append(String.format("\n║ Remote IP    : %s", remoteAddr));
        if (userId != null) {
            logMessage.append(String.format("\n║ User ID      : %s", userId));
        } else {
            logMessage.append("\n║ User ID      : Anonymous");
        }

        // Log headers (optional, có thể bật/tắt)
        if (log.isDebugEnabled()) {
            logMessage.append("\n╠───────────────────────────────────────────────────────────────────────────────");
            logMessage.append("\n║ Headers:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Skip sensitive headers
                if (!headerName.equalsIgnoreCase("Authorization") &&
                    !headerName.equalsIgnoreCase("Cookie")) {
                    logMessage.append(String.format("\n║   %s: %s", headerName, request.getHeader(headerName)));
                }
            }
        }

        logMessage.append("\n╚═══════════════════════════════════════════════════════════════════════════════");

        log.info(logMessage.toString());
    }

    /**
     * Log thông tin response
     */
    private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response, long duration) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        String statusText = getStatusText(status);
        String userId = getCurrentUserId();

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n╔═══════════════════════════════════════════════════════════════════════════════");
        logMessage.append("\n║ 📤 OUTGOING RESPONSE");
        logMessage.append("\n╠═══════════════════════════════════════════════════════════════════════════════");
        logMessage.append(String.format("\n║ Timestamp    : %s", timestamp));
        logMessage.append(String.format("\n║ Method       : %s", method));
        logMessage.append(String.format("\n║ URI          : %s", uri));
        logMessage.append(String.format("\n║ Status       : %d %s", status, statusText));
        logMessage.append(String.format("\n║ Duration     : %d ms", duration));
        if (userId != null) {
            logMessage.append(String.format("\n║ User ID      : %s", userId));
        }

        // Thêm emoji dựa vào status code
        String emoji = getStatusEmoji(status);
        logMessage.append(String.format("\n║ Result       : %s", emoji));

        logMessage.append("\n╚═══════════════════════════════════════════════════════════════════════════════\n");

        // Log level khác nhau dựa vào status code
        if (status >= 500) {
            log.error(logMessage.toString());
        } else if (status >= 400) {
            log.warn(logMessage.toString());
        } else {
            log.info(logMessage.toString());
        }
    }

    /**
     * Lấy User ID hiện tại (nếu đã authenticated)
     */
    private String getCurrentUserId() {
        try {
            if (SecurityUtil.isAuthenticated()) {
                return SecurityUtil.getCurrentUserId();
            }
        } catch (Exception e) {
            // Ignore - user chưa authenticated
        }
        return null;
    }

    /**
     * Lấy IP thực của client (xử lý cả trường hợp có proxy/load balancer)
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Lấy IP đầu tiên nếu có nhiều IP (qua nhiều proxy)
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Lấy text mô tả status code
     */
    private String getStatusText(int status) {
        if (status >= 200 && status < 300) return "Success";
        if (status >= 300 && status < 400) return "Redirect";
        if (status >= 400 && status < 500) return "Client Error";
        if (status >= 500) return "Server Error";
        return "Unknown";
    }

    /**
     * Lấy emoji dựa vào status code
     */
    private String getStatusEmoji(int status) {
        if (status >= 200 && status < 300) return "✅ Success";
        if (status >= 300 && status < 400) return "↪️ Redirect";
        if (status == 400) return "❌ Bad Request";
        if (status == 401) return "🔒 Unauthorized";
        if (status == 403) return "🚫 Forbidden";
        if (status == 404) return "🔍 Not Found";
        if (status >= 400 && status < 500) return "⚠️ Client Error";
        if (status >= 500) return "💥 Server Error";
        return "❓ Unknown";
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip logging cho các static resources
        String path = request.getRequestURI();
        return path.startsWith("/static/") ||
               path.startsWith("/public/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/favicon.ico");
    }
}

