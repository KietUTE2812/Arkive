package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.request.AssetUpdateRequest;
import com.example.arkivebackend.dto.request.AssetUploadCompleteRequest;
import com.example.arkivebackend.dto.request.AssetUploadRequest;
import com.example.arkivebackend.dto.response.AssetResponse;
import com.example.arkivebackend.dto.response.PresignedUrlResponse;
import com.example.arkivebackend.service.AssetService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetController {
    AssetService assetService;

    /**
     * BƯỚC 1: Frontend gọi để lấy URL.
     */
    @PostMapping("/upload-request")
    public ApiResponse<PresignedUrlResponse> requestUploadUrl(@RequestBody @Valid AssetUploadRequest request) {
        var presignedUrl = assetService.requestUploadUrl(request);
        return ApiResponse.<PresignedUrlResponse>builder()
                .success(true)
                .data(presignedUrl)
                .build();
    }

    // BƯỚC 2: Frontend tự upload lên URL

    /**
     * BƯỚC 3: Frontend gọi sau khi upload xong.
     */
    @PostMapping("/upload-complete")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AssetResponse> completeUpload(@RequestBody @Valid AssetUploadCompleteRequest request) {
        var asset = assetService.completeUpload(request);
        return ApiResponse.<AssetResponse>builder()
                .success(true)
                .data(asset)
                .build();
    }

    /**
     * Endpoint để lấy thông tin Asset theo ID.
     * Chỉ owner của asset mới được phép gọi.
     */
    @GetMapping("/{assetId}")
    @PreAuthorize("@assetSecurity.isOwner(#assetId, authentication.name)")
    public ApiResponse<AssetResponse> getAssetById(@PathVariable String assetId) {
        var asset = assetService.getAssetById(assetId);
        return ApiResponse.<AssetResponse>builder()
                .success(true)
                .data(asset)
                .build();
    }

    /**
     * Tạo presigned URL để upload thumbnail cho Asset.
     */
    @PostMapping("/upload-thumbnail-request")
    public ApiResponse<PresignedUrlResponse> requestThumbnailUploadUrl(@RequestBody @Valid AssetUploadRequest request) {
        var presignedUrl = assetService.requestThumbnailUploadUrl(request);
        return ApiResponse.<PresignedUrlResponse>builder()
                .success(true)
                .data(presignedUrl)
                .build();
    }

    /**
     * Endpoint để xóa Asset.
     * Ở đây chỉ thực hiện xóa mềm (soft delete).
     * Owner của asset mới được phép xóa.
     */
    @DeleteMapping("/{assetId}")
    @PreAuthorize("@assetService.isOwner(#assetId, authentication.name)")
    public ApiResponse<Void> deleteAsset(@PathVariable String assetId) {
        assetService.softDeleteAsset(assetId);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    @DeleteMapping("/{assetId}/hard")
    @PreAuthorize("@assetSecurity.isOwner(#assetId, authentication.name)")
    public ApiResponse<Void> hardDeleteAsset(@PathVariable String assetId) {
        assetService.hardDeleteAsset(assetId);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    /**
     * Endpoint để cập nhật thông tin Asset (filename, tags, thumbnail).
     */
    @PatchMapping("/{assetId}")
    @PreAuthorize("@assetSecurity.isOwner(#assetId, authentication.name)")
    public ApiResponse<AssetResponse> updateAsset(
            @PathVariable String assetId,
            @RequestBody @Valid AssetUpdateRequest request) {
        var asset = assetService.updateAsset(assetId, request);
        return ApiResponse.<AssetResponse>builder()
                .success(true)
                .data(asset)
                .build();
    }

    /**
     * Endpoint để lấy presigned URL để download asset.
     * Chỉ owner của asset mới được phép gọi.
     */
    @GetMapping("/{assetId}/download")
    @PreAuthorize("@assetSecurity.isOwner(#assetId, authentication.name)")
    public ApiResponse<PresignedUrlResponse> getDownloadUrl(@PathVariable String assetId) {
        var downloadUrl = assetService.getDownloadUrl(assetId);
        return ApiResponse.<PresignedUrlResponse>builder()
                .success(true)
                .data(downloadUrl)
                .build();
    }

    /**
     * Endpoint để lấy presigned URL để xem trước asset.
     * Chỉ owner của asset mới được phép gọi.
     */
    @GetMapping("/{assetId}/preview")
    @PreAuthorize("@assetSecurity.isOwner(#assetId, authentication.name)")
    public ApiResponse<PresignedUrlResponse> getPreviewUrl(@PathVariable String assetId) {
        var previewUrl = assetService.getPreviewUrl(assetId);
        return ApiResponse.<PresignedUrlResponse>builder()
                .success(true)
                .data(previewUrl)
                .build();
    }

    /**
     * Endpoint để lấy danh sách assets có phân trang và tìm kiếm
     * * Cách gọi: GET /api/v1/assets?collectionId=...&keyword=...&page=...&size=...
     */
    @GetMapping("")
    @PreAuthorize("@assetSecurity.isCollectionOwner(#collectionId, authentication.name)")
    public ApiResponse<Page<AssetResponse>> getAssets(
            @RequestParam String collectionId, // Lấy từ query param
            @RequestParam(required = false, defaultValue = "") String keyword, // Không bắt buộc, mặc định là chuỗi rỗng
            @RequestParam(required = false, defaultValue = "0") int page,     // Không bắt buộc, mặc định là trang 0
            @RequestParam(required = false, defaultValue = "20") int size,     // Không bắt buộc, mặc định là 20
            @RequestParam(required = false, defaultValue = "false") String sortBy,
            @RequestParam(required = false, defaultValue = "false") String sortOrder
    ) {
        // 1. Gọi phương thức Service mà bạn vừa viết
        Page<AssetResponse> assetPage = assetService.getAssetsPaginated(collectionId, keyword, sortBy, sortOrder, page, size);

        // 2. Trả về đối tượng Page cho client
        return ApiResponse.<Page<AssetResponse>>builder()
                .success(true)
                .data(assetPage)
                .build();
    }

    /**
     * Endpoint lấy asset trong đã xóa
     */
    @GetMapping("/deleted")
    public ApiResponse<List<AssetResponse>> deletedAssets() {
        var assets = assetService.getDeletedAssets();
        return ApiResponse.<List<AssetResponse>>builder()
                .success(true)
                .data(assets)
                .build();
    }

    /**
     * Endpoint khôi phục asset đã xóa
     */
    @PostMapping("/{assetId}/restore")
    @PreAuthorize("@assetSecurity.isOwner(#assetId, authentication.name)")
    public ApiResponse<Void> restoreAsset(@PathVariable String assetId) {
        assetService.restoreAsset(assetId);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }
}
