package com.example.arkivebackend.exception;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @Value("${app.environment:development}")
        private String projectEnv;

        @ExceptionHandler(AppException.class) // Bắt lỗi AppException
        @ResponseBody
        public ResponseEntity<Object> handleAppException(AppException ex) {

                if (projectEnv.equals("development")) {
                        log.error("AppException: ", ex);
                } else {
                        ex.printStackTrace();
                }
                ErrorCode errorCode = ex.getErrorCode();
                ApiResponse<Object> response = ApiResponse.builder()
                        .success(false)
                        .errors(Map.of("code", errorCode.getCode(), "message", errorCode.getMessage()))
                        .build();   
                return ResponseEntity.status(errorCode.getStatusCode())
                        .body(response);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class) // Bắt lỗi validation
        @ResponseBody
        public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
                if (projectEnv.equals("development")) {
                        log.error("Validation error: ", ex);
                }

                Map<String, String> validationErrors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error ->
                        validationErrors.put(error.getField(), error.getDefaultMessage())
                );

                ApiResponse<Object> response = ApiResponse.builder()
                        .success(false)
                        .errors(Map.of("validation", validationErrors))
                        .message("Validation failed")
                        .build();

                return ResponseEntity.badRequest().body(response);
        }

        @ExceptionHandler(AuthorizationDeniedException.class) // Bắt lỗi AuthorizationDeniedException
        @ResponseBody
        public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
                if (projectEnv.equals("development")) {
                        log.error("AuthorizationDeniedException: ", ex);
                } else {
                        ex.printStackTrace();
                }
                ErrorCode errorCode = ErrorCode.FORBIDDEN;
                ApiResponse<Object> response = ApiResponse.builder()
                        .success(false)
                        .errors(Map.of("code", errorCode.getCode(), "message", errorCode.getMessage()))
                        .build();   
                return ResponseEntity.status(errorCode.getStatusCode())
                        .body(response);
        }

        @ExceptionHandler(Exception.class) // Bắt tất cả các lỗi khác
        @ResponseBody
        public ResponseEntity<Object> handleException(Exception ex) {
            if (projectEnv.equals("development")) {
                log.error("Exception: ", ex);
            } else {
                ex.printStackTrace();
            }
            ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
            ApiResponse<Object> response = ApiResponse.builder()
                    .success(false)
                    .errors(Map.of("code", errorCode.getCode(), "message", errorCode.getMessage()))
                    .build();
            return ResponseEntity.status(errorCode.getStatusCode())
                    .body(response);
        }
}
