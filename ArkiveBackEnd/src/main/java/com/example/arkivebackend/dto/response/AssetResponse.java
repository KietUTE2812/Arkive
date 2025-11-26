package com.example.arkivebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {
    String id;
    String assetId;
    String filename;
    String fileType;
    Long fileSize;
    String thumbnailUrl;
    Set<String> tags;
    String collectionId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
