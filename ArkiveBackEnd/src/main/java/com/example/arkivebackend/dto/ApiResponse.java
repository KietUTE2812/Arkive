package com.example.arkivebackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    boolean success; // Trạng thái thành công hay thất bại
    Map<String, Object> errors; // Giá trị lỗi nếu có
    T data; // Dữ liệu trả về
    String message; // Thông điệp bổ sung (nếu cần)
}
