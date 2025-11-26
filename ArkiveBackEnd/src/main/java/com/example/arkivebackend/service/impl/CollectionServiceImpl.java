package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.request.CollectionRequest;
import com.example.arkivebackend.dto.response.CollectionResponse;
import com.example.arkivebackend.entity.Collection;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.enums.ErrorCode;
import com.example.arkivebackend.exception.AppException;
import com.example.arkivebackend.repository.CollectionRepository;
import com.example.arkivebackend.service.CollectionService;
import com.example.arkivebackend.util.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CollectionServiceImpl implements CollectionService {

    CollectionRepository collectionRepository;

    @Override
    public List<CollectionResponse> findAllByOwerId() {
        String userId = SecurityUtil.getCurrentUserId();

        List<Collection> collections = collectionRepository.findAllByOwnerId(userId);

        return collections.stream().map(collection -> CollectionResponse.builder()
                        .id(collection.getId())
                        .name(collection.getName())
                        .description(collection.getDescription())
                        .ownerId(collection.getOwner().getId())
                        .createdAt(collection.getCreatedAt())
                        .updatedAt(collection.getUpdatedAt())
                        .assetCount(collection.getAssets().size())
                        .build())
                .toList();
    }

    @Override
    public CollectionResponse create(CollectionRequest request) {
        String userId = SecurityUtil.getCurrentUserId();
        var exist = collectionRepository.findByNameAndOwnerId(request.getName(), userId);
        User user = User.builder().id(userId).build();
        if (exist.isPresent()) {
            throw new AppException(ErrorCode.COLLECTION_EXISTED);
        }
        // Create and save the collection
        Collection collection = Collection.builder()
                .name(request.getName())
                .owner(user)
                .description(request.getDescription())
                .build();
        Collection savedCollection = collectionRepository.save(collection);
        return CollectionResponse.builder()
                .id(savedCollection.getId())
                .name(savedCollection.getName())
                .description(savedCollection.getDescription())
                .ownerId(savedCollection.getOwner().getId())
                .updatedAt(collection.getUpdatedAt())
                .createdAt(collection.getCreatedAt())
                .build();
    }

    @PostAuthorize("returnObject.ownerId.equals(authentication.principal.claims['userId'])")
    @Override
    public CollectionResponse getById(String s) {
        Collection collection = collectionRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .description(collection.getDescription())
                .ownerId(collection.getOwner().getId())
                .assetCount(collection.getAssets().size())
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }

    @PostAuthorize("returnObject.ownerId.equals(authentication.principal.claims['userId'])")
    @Override
    public CollectionResponse update(String s, CollectionRequest request) {
        Collection collection = collectionRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));

        collection.setName(request.getName());
        collection.setDescription(request.getDescription());

        Collection updatedCollection = collectionRepository.save(collection);
        return CollectionResponse.builder()
                .id(updatedCollection.getId())
                .name(updatedCollection.getName())
                .description(updatedCollection.getDescription())
                .ownerId(updatedCollection.getOwner().getId())
                .build();
    }

    @PreAuthorize("hasRole('USER') and @collectionServiceImpl.getById(#s).ownerId == authentication.principal.id")
    @Override
    public void delete(String s) {
        Collection collection = collectionRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND));
        collectionRepository.delete(collection);
    }
}
