package com.example.arkivebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedCollectionDetailResponse {

    private String collectionName;
    private String collectionDescription;
    private String ownerName;
    private Integer assetCount;
    private List<AssetResponse> assets;
}

