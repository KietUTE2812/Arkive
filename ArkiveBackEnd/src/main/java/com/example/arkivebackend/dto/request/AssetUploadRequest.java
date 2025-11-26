package com.example.arkivebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AssetUploadRequest {
    @NotBlank(message = "FILE_NAME_REQUIRED")
    @Size(max = 255, message = "FILE_NAME_TOO_LONG")
    private String fileName;    // Ví dụ: "logo.png"

    @NotBlank(message = "CONTENT_TYPE_REQUIRED")
    private String contentType; // Ví dụ: "image/png"
}
