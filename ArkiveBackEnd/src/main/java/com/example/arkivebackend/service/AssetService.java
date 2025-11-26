package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.AssetUpdateRequest;
import com.example.arkivebackend.dto.request.AssetUploadCompleteRequest;
import com.example.arkivebackend.dto.request.AssetUploadRequest;
import com.example.arkivebackend.dto.response.AssetResponse;
import com.example.arkivebackend.dto.response.PresignedUrlResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssetService {

    PresignedUrlResponse requestUploadUrl(AssetUploadRequest request);
    AssetResponse completeUpload(AssetUploadCompleteRequest request);
    PresignedUrlResponse requestThumbnailUploadUrl(AssetUploadRequest request);
    List<AssetResponse> getAssetsByCollectionId(String collectionId);
    void hardDeleteAsset(String assetId);
    void softDeleteAsset(String assetId);

    // Phân trang và lọc asset
    Page<AssetResponse> getAssetsPaginated(String collectionId, String keyword,
                                           String sortBy, String sortOrder, int page, int size);

    /**
     * Lấy chi tiết một asset
     */
    AssetResponse getAssetById(String assetId);

    /**
     * Cập nhật thông tin asset (filename, tags)
     */
    AssetResponse updateAsset(String assetId, AssetUpdateRequest request);

    /**
     * Lấy presigned URL để download asset
     */
    PresignedUrlResponse getDownloadUrl(String assetId);

    /**
     * Lấy presigned URL để preview asset
     */
    PresignedUrlResponse getPreviewUrl(String assetId);

    /**
     * Lấy tất cả assets đã xóa
     */
    List<AssetResponse> getDeletedAssets();

    /**
     * Khôi phục asset đã xóa
     */
    void restoreAsset(String assetId);
}
