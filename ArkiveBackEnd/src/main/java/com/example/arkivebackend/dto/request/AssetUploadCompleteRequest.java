package com.example.arkivebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Set;

@Data
public class AssetUploadCompleteRequest {
    @NotBlank(message = "STORAGE_KEY_REQUIRED")
    private String storageKey;  // Tên file trên R2

    @NotBlank(message = "FILE_NAME_REQUIRED")
    private String fileName;    // Tên file gốc

    @NotBlank(message = "CONTENT_TYPE_REQUIRED")
    private String contentType; // Loại file

    @NotNull(message = "FILE_SIZE_REQUIRED")
    @Positive(message = "FILE_SIZE_MUST_BE_POSITIVE")
    private Long fileSize;      // Kích thước (bytes)

    private String thumbnailUrl; // URL của thumbnail (nếu có)
    private Set<String> tags;      // Tập hợp các tag liên quan đến asset

    @NotBlank(message = "COLLECTION_ID_REQUIRED")
    private String collectionId; // Collection để gán vào
}
