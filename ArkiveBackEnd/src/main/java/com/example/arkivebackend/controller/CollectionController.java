package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.request.CollectionRequest;
import com.example.arkivebackend.dto.response.AssetResponse;
import com.example.arkivebackend.dto.response.CollectionResponse;
import com.example.arkivebackend.service.AssetService;
import com.example.arkivebackend.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/collections")
public class CollectionController {
    CollectionService collectionService;
    AssetService assetService;

    /*
     * Tạo mới một collection
     * URL: Post/collections
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CollectionResponse> create(@RequestBody @Valid CollectionRequest request) {
        var collection = collectionService.create(request);
        return ApiResponse.<CollectionResponse>builder()
                .success(true)
                .data(collection)
                .build();
    }

    /*
     * Lấy tất cả collection theo ownerId
     * URL: GET/collections
     */
    @GetMapping("")
    public ApiResponse<List<CollectionResponse>> getAllByOwnerId() {
        var collections = collectionService.findAllByOwerId();
        return ApiResponse.<List<CollectionResponse>>builder()
                .success(true)
                .data(collections)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CollectionResponse> getById(@PathVariable String id) {
        var collection = collectionService.getById(id);
        return ApiResponse.<CollectionResponse>builder()
                .success(true)
                .data(collection)
                .build();
    }

    /*
     * Xoá một collection theo id
     * URL: DELETE/collections/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        collectionService.delete(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    /*
     * Cập nhật một collection theo id
     * URL: PUT/collections/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<CollectionResponse> update(@PathVariable String id, @RequestBody @Valid CollectionRequest request) {
        var collection = collectionService.update(id, request);
        return ApiResponse.<CollectionResponse>builder()
                .success(true)
                .data(collection)
                .build();
    }

    @GetMapping("/{collectionId}/assets")
    public ApiResponse<List<AssetResponse>> getAssetsByCollection(@PathVariable String collectionId) {
        List<AssetResponse> assets = assetService.getAssetsByCollectionId(collectionId);
        return ApiResponse.<List<AssetResponse>>builder()
                .success(true)
                .data(assets)
                .build();
    }
}
