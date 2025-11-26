package com.example.arkivebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {
    private String url;        // URL để upload file lên
    private String storageKey; // Tên file duy nhất trên R2
}
