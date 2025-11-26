package com.example.arkivebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessSharedLinkRequest {

    @NotBlank(message = "Public ID is required")
    private String publicId;

    private String password; // Optional: Mật khẩu nếu link được bảo vệ
}

