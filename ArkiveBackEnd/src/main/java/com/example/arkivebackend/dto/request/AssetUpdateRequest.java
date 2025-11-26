package com.example.arkivebackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetUpdateRequest {

    private String filename;
    private String thumbnailUrl;
    private Set<String> tags;
}

