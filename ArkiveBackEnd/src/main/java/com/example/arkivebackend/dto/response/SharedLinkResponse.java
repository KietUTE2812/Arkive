package com.example.arkivebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedLinkResponse {

    private String id;
    private String publicId;
    private boolean hasPassword;
    private String collectionId;
    private String collectionName;
    private String collectionDescription;
    private Integer assetCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shareUrl; // Full URL để share: /api/v1/shared/{publicId}
}

