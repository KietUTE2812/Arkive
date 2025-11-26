package com.example.arkivebackend.security;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.example.arkivebackend.repository.AssetRepository;

import org.springframework.stereotype.Component;

@Component("assetSecurity") // Đặt tên bean để gọi trong SpEL
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AssetSecurity {
    AssetRepository assetRepository;

    /**
     * Kiểm tra xem user có phải owner của collection không.
     */
    public boolean isCollectionOwner(String collectionId, String username) {
        return assetRepository.existsByCollectionIdAndCollectionOwnerUsername(collectionId, username);
    }

    /**
     * Kiểm tra xem user có phải owner của asset không.
     */
    public boolean isOwner(String assetId, String username) {
        return assetRepository.existsByIdAndCollectionOwnerUsername(assetId, username);
    }
}
