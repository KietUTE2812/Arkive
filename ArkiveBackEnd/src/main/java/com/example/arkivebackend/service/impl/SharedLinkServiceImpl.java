package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.AccessSharedLinkRequest;
import com.example.arkivebackend.dto.request.ShareCollectionRequest;
import com.example.arkivebackend.dto.response.AssetResponse;
import com.example.arkivebackend.dto.response.SharedCollectionDetailResponse;
import com.example.arkivebackend.dto.response.SharedLinkResponse;
import com.example.arkivebackend.entity.Asset;
import com.example.arkivebackend.entity.Collection;
import com.example.arkivebackend.entity.SharedLink;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.repository.CollectionRepository;
import com.example.arkivebackend.repository.SharedLinkRepository;
import com.example.arkivebackend.service.SharedLinkService;
import com.example.arkivebackend.util.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SharedLinkServiceImpl implements SharedLinkService {

    SharedLinkRepository sharedLinkRepository;
    CollectionRepository collectionRepository;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SharedLinkResponse createSharedLink(ShareCollectionRequest request) {
        String userId = SecurityUtil.getCurrentUserId();

        // 1. Kiểm tra collection tồn tại và thuộc về user
        Collection collection = collectionRepository.findById(request.getCollectionId())
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 2. Kiểm tra shared link đã tồn tại chưa
        if (sharedLinkRepository.findByCollectionId(request.getCollectionId()).isPresent()) {
            throw new AppException(ErrorCode.SHARED_LINK_ALREADY_EXISTS);
        }

        // 3. Tạo publicId unique
        String publicId = generateUniquePublicId();

        // 4. Hash password nếu có
        String hashedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            hashedPassword = passwordEncoder.encode(request.getPassword());
        }

        // 5. Tạo và lưu SharedLink
        SharedLink sharedLink = SharedLink.builder()
                .publicId(publicId)
                .password(hashedPassword)
                .collection(collection)
                .build();

        SharedLink savedLink = sharedLinkRepository.save(sharedLink);

        log.info("Created shared link with publicId: {} for collection: {}", publicId, collection.getId());

        return buildSharedLinkResponse(savedLink);
    }

    @Override
    @Transactional(readOnly = true)
    public SharedLinkResponse getSharedLinkByCollectionId(String collectionId) {
        String userId = SecurityUtil.getCurrentUserId();

        // 1. Kiểm tra collection
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 2. Lấy shared link
        SharedLink sharedLink = sharedLinkRepository.findByCollectionId(collectionId)
                .orElseThrow(() -> new AppException(ErrorCode.SHARED_LINK_NOT_FOUND));

        return buildSharedLinkResponse(sharedLink);
    }

    @Override
    @Transactional(readOnly = true)
    public SharedCollectionDetailResponse accessSharedLink(AccessSharedLinkRequest request) {
        // 1. Tìm shared link theo publicId
        SharedLink sharedLink = sharedLinkRepository.findByPublicId(request.getPublicId())
                .orElseThrow(() -> new AppException(ErrorCode.SHARED_LINK_NOT_FOUND));

        // 2. Kiểm tra password nếu có
        if (sharedLink.getPassword() != null) {
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new AppException(ErrorCode.SHARED_LINK_PASSWORD_REQUIRED);
            }

            if (!passwordEncoder.matches(request.getPassword(), sharedLink.getPassword())) {
                throw new AppException(ErrorCode.SHARED_LINK_PASSWORD_INCORRECT);
            }
        }

        // 3. Lấy thông tin collection và assets
        Collection collection = sharedLink.getCollection();
        List<Asset> assets = collection.getAssets();

        // 4. Convert sang DTO
        List<AssetResponse> assetResponses = assets.stream()
                .map(asset -> AssetResponse.builder()
                        .id(asset.getId())
                        .filename(asset.getFilename())
                        .fileType(asset.getFileType())
                        .fileSize(asset.getFileSize())
                        .thumbnailUrl(asset.getThumbnailUrl())
                        .tags(asset.getTags())
                        .createdAt(asset.getCreatedAt())
                        .updatedAt(asset.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        log.info("Shared link accessed: publicId={}, collectionId={}", request.getPublicId(), collection.getId());

        return SharedCollectionDetailResponse.builder()
                .collectionName(collection.getName())
                .collectionDescription(collection.getDescription())
                .ownerName(collection.getOwner().getFullName())
                .assetCount(assets.size())
                .assets(assetResponses)
                .build();
    }

    @Override
    @Transactional
    public void deleteSharedLink(String collectionId) {
        String userId = SecurityUtil.getCurrentUserId();

        // 1. Kiểm tra collection
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 2. Tìm và xóa shared link
        SharedLink sharedLink = sharedLinkRepository.findByCollectionId(collectionId)
                .orElseThrow(() -> new AppException(ErrorCode.SHARED_LINK_NOT_FOUND));

        sharedLinkRepository.delete(sharedLink);

        log.info("Deleted shared link for collection: {}", collectionId);
    }

    @Override
    @Transactional
    public SharedLinkResponse updateSharedLinkPassword(String collectionId, String newPassword) {
        String userId = SecurityUtil.getCurrentUserId();

        // 1. Kiểm tra collection
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 2. Tìm shared link
        SharedLink sharedLink = sharedLinkRepository.findByCollectionId(collectionId)
                .orElseThrow(() -> new AppException(ErrorCode.SHARED_LINK_NOT_FOUND));

        // 3. Cập nhật password
        String hashedPassword = null;
        if (newPassword != null && !newPassword.isEmpty()) {
            hashedPassword = passwordEncoder.encode(newPassword);
        }
        sharedLink.setPassword(hashedPassword);

        SharedLink updatedLink = sharedLinkRepository.save(sharedLink);

        log.info("Updated password for shared link of collection: {}", collectionId);

        return buildSharedLinkResponse(updatedLink);
    }

    // ==================== Helper Methods ====================

    /**
     * Tạo publicId unique (8 ký tự)
     */
    private String generateUniquePublicId() {
        String publicId;
        do {
            publicId = UUID.randomUUID().toString().substring(0, 8);
        } while (sharedLinkRepository.existsByPublicId(publicId));
        return publicId;
    }

    /**
     * Build SharedLinkResponse từ SharedLink entity
     */
    private SharedLinkResponse buildSharedLinkResponse(SharedLink sharedLink) {
        Collection collection = sharedLink.getCollection();

        return SharedLinkResponse.builder()
                .id(sharedLink.getId())
                .publicId(sharedLink.getPublicId())
                .hasPassword(sharedLink.getPassword() != null)
                .collectionId(collection.getId())
                .collectionName(collection.getName())
                .collectionDescription(collection.getDescription())
                .assetCount(collection.getAssets().size())
                .createdAt(sharedLink.getCreatedAt())
                .updatedAt(sharedLink.getUpdatedAt())
                .shareUrl("/api/v1/shared/" + sharedLink.getPublicId())
                .build();
    }
}

