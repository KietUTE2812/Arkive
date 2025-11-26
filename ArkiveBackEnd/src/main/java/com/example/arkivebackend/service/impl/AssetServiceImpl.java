package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.AssetUpdateRequest;
import com.example.arkivebackend.dto.request.AssetUploadCompleteRequest;
import com.example.arkivebackend.dto.request.AssetUploadRequest;
import com.example.arkivebackend.dto.response.AssetResponse;
import com.example.arkivebackend.dto.response.PresignedUrlResponse;
import com.example.arkivebackend.entity.Asset;
import com.example.arkivebackend.entity.Collection;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.enums.AssetSortField;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.repository.AssetRepository;
import com.example.arkivebackend.repository.CollectionRepository;
import com.example.arkivebackend.repository.UserRepository;
import com.example.arkivebackend.service.AssetService;
import com.example.arkivebackend.service.StorageService;
import com.example.arkivebackend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    StorageService storageService;
    AssetRepository assetRepository;
    CollectionRepository collectionRepository;
    UserRepository userRepository;

    @Override
    public PresignedUrlResponse requestUploadUrl(AssetUploadRequest request) {
        return storageService.generatePresignedUploadUrl(
                request.getFileName(),
                request.getContentType()
        );
    }

    @Override
    public AssetResponse completeUpload(AssetUploadCompleteRequest request) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Kiểm tra Collection
        Collection collection = collectionRepository.findById(request.getCollectionId())
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        // 3. Kiểm tra quyền sở hữu Collection
        if (!collection.getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Lưu Asset vào CSDL
        Asset asset = Asset.builder()
                .filename(request.getFileName())
                .storageKey(request.getStorageKey())
                .fileType(request.getContentType())
                .fileSize(request.getFileSize())
                .thumbnailUrl(request.getThumbnailUrl())
                .tags(request.getTags())
                .collection(collection)
                .build();
        Asset savedAsset = assetRepository.save(asset);

        userRepository.save(user);

        // 5. Trả về DTO
        return AssetResponse.builder()
                .id(savedAsset.getId())
                .filename(savedAsset.getFilename())
                .fileType(savedAsset.getFileType())
                .fileSize(savedAsset.getFileSize())
                .collectionId(savedAsset.getCollection().getId())
                .thumbnailUrl(savedAsset.getThumbnailUrl())
                .tags(savedAsset.getTags())
                .createdAt(savedAsset.getCreatedAt())
                .updatedAt(savedAsset.getUpdatedAt())
                .build();
    }

    @Override
    public PresignedUrlResponse requestThumbnailUploadUrl(AssetUploadRequest request) {
        return storageService.generatePresignedThumbnailUrl(
                request.getFileName(),
                request.getContentType()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponse> getAssetsByCollectionId(String collectionId) {
        // 1. LẤY USER HIỆN TẠI
        String userId = SecurityUtil.getCurrentUserId();

        // 2. TÌM COLLECTION VÀ KIỂM TRA QUYỀN SỞ HỮU
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 3. LẤY DANH SÁCH ASSET TỪ COLLECTION
        List<Asset> assets = collection.getAssets().stream()
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .toList();

        // 4. CHUYỂN ĐỔI SANG DTO
        return assets.stream()
                .map(asset -> AssetResponse.builder()
                        .id(asset.getId())
                        .filename(asset.getFilename())
                        .fileType(asset.getFileType())
                        .fileSize(asset.getFileSize())
                        .collectionId(asset.getCollection().getId())
                        .thumbnailUrl(asset.getThumbnailUrl())
                        .tags(asset.getTags())
                        .createdAt(asset.getCreatedAt())
                        .updatedAt(asset.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void softDeleteAsset(String assetId) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm Asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        // 3. Kiểm tra quyền sở hữu
        if (!asset.getCollection().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 5. Update isDeleted Asset khỏi CSDL
        asset.setIsDeleted(true);
        assetRepository.save(asset);
    }

    @Override
    public void hardDeleteAsset(String assetId) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm Asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        // 3. Kiểm tra quyền sở hữu
        if (!asset.getCollection().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Xóa Asset khỏi Storage
        storageService.deleteAsset(asset.getId());

        // 5. Xoa Asset khỏi CSDL
        assetRepository.deleteById(assetId);
    }

    @Override
    public Page<AssetResponse> getAssetsPaginated(String collectionId, String keyword, String sortBy, String sortOrder, int page, int size) {
        // 1. Tạo đối tượng Sort (ví dụ: sắp xếp theo tên, A-Z)
        // sortBy = "filename", "createdAt", "fileSize", ...
        // sortOrder = "ASC" or "DESC"
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        AssetSortField sortFieldEnum = AssetSortField.fromString(sortBy);
        log.info(sortBy);
        Sort sort = Sort.by(direction, sortFieldEnum.getFieldName());

        log.info("Sorting by field: {}, order: {}", sortFieldEnum.getFieldName(), direction);

        // 2. Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Gọi repository
        // Bạn không cần @Transactional ở đây vì bạn không truy cập collection.getAssets()
        var asset = assetRepository.searchAssets(collectionId, keyword, pageable);
        // 4. Chuyển đổi sang DTO
        return asset.map(a -> AssetResponse.builder()
                .id(a.getId())
                .filename(a.getFilename())
                .fileType(a.getFileType())
                .fileSize(a.getFileSize())
                .collectionId(a.getCollection().getId())
                .thumbnailUrl(a.getThumbnailUrl())
                .tags(a.getTags())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getAssetById(String assetId) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm Asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        // 3. Kiểm tra quyền sở hữu
        if (!asset.getCollection().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Trả về DTO
        return AssetResponse.builder()
                .id(asset.getId())
                .filename(asset.getFilename())
                .fileType(asset.getFileType())
                .fileSize(asset.getFileSize())
                .collectionId(asset.getCollection().getId())
                .thumbnailUrl(asset.getThumbnailUrl())
                .tags(asset.getTags())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public AssetResponse updateAsset(String assetId, AssetUpdateRequest request) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm Asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        // 3. Kiểm tra quyền sở hữu
        if (!asset.getCollection().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Cập nhật thông tin
        if (request.getFilename() != null) {
            asset.setFilename(request.getFilename());
        }
        if (request.getTags() != null) {
            asset.setTags(request.getTags());
        }
        if (request.getThumbnailUrl() != null) {
            asset.setThumbnailUrl(request.getThumbnailUrl());
        }

        Asset updatedAsset = assetRepository.save(asset);

        // 5. Trả về DTO
        return AssetResponse.builder()
                .id(updatedAsset.getId())
                .filename(updatedAsset.getFilename())
                .fileType(updatedAsset.getFileType())
                .fileSize(updatedAsset.getFileSize())
                .collectionId(updatedAsset.getCollection().getId())
                .thumbnailUrl(updatedAsset.getThumbnailUrl())
                .tags(updatedAsset.getTags())
                .createdAt(updatedAsset.getCreatedAt())
                .updatedAt(updatedAsset.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PresignedUrlResponse getDownloadUrl(String assetId) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm Asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        // 3. Kiểm tra quyền sở hữu
        if (!asset.getCollection().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Tạo presigned download URL
        return storageService.generatePresignedDownloadUrl(asset.getStorageKey(), asset.getFilename());
    }

    @Override
    public PresignedUrlResponse getPreviewUrl(String assetId) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm Asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        // 3. Kiểm tra quyền sở hữu
        if (!asset.getCollection().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Tạo presigned download URL
        return storageService.generatePresignedPreviewUrl(asset.getStorageKey());
    }

    @Override
    public List<AssetResponse> getDeletedAssets() {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // 2. Lấy danh sách assets đã xóa của user
        List<Asset> deletedAssets = assetRepository.findAll().stream()
                .filter(asset -> asset.getIsDeleted() != null && asset.getIsDeleted())
                .filter(asset -> asset.getCollection().getOwner().getId().equals(user.getId()))
                .toList();
        // 3. Chuyển đổi sang DTO
        return deletedAssets.stream()
                .map(asset -> AssetResponse.builder()
                        .id(asset.getId())
                        .filename(asset.getFilename())
                        .fileType(asset.getFileType())
                        .fileSize(asset.getFileSize())
                        .collectionId(asset.getCollection().getId())
                        .thumbnailUrl(asset.getThumbnailUrl())
                        .tags(asset.getTags())
                        .createdAt(asset.getCreatedAt())
                        .updatedAt(asset.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void restoreAsset(String assetId) {
        // 1. Lấy User hiện tại
        String userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Tìm Asset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        if (asset.getIsDeleted() == null || !asset.getIsDeleted()) {
            throw new AppException(ErrorCode.ASSET_ALREADY_DELETED);
        }

        // 3. Kiểm tra quyền sở hữu
        if (!asset.getCollection().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Cập nhật isDeleted về false
        asset.setIsDeleted(false);
        assetRepository.save(asset);
    }
}
