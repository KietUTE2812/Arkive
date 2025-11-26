package com.example.arkivebackend.controller;

import com.example.arkivebackend.dto.ApiResponse;
import com.example.arkivebackend.dto.request.AccessSharedLinkRequest;
import com.example.arkivebackend.dto.request.ShareCollectionRequest;
import com.example.arkivebackend.dto.response.SharedCollectionDetailResponse;
import com.example.arkivebackend.dto.response.SharedLinkResponse;
import com.example.arkivebackend.service.SharedLinkService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shared")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SharedLinkController {

    SharedLinkService sharedLinkService;

    /**
     * Tạo shared link cho một collection
     * URL: POST /api/v1/shared
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SharedLinkResponse> createSharedLink(@RequestBody @Valid ShareCollectionRequest request) {
        SharedLinkResponse response = sharedLinkService.createSharedLink(request);
        return ApiResponse.<SharedLinkResponse>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Lấy thông tin shared link của một collection (dành cho owner)
     * URL: GET /api/v1/shared/collection/{collectionId}
     */
    @GetMapping("/collection/{collectionId}")
    public ApiResponse<SharedLinkResponse> getSharedLinkByCollectionId(@PathVariable String collectionId) {
        SharedLinkResponse response = sharedLinkService.getSharedLinkByCollectionId(collectionId);
        return ApiResponse.<SharedLinkResponse>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Truy cập shared link (public - không cần authentication)
     * URL: POST /api/v1/shared/access
     */
    @PostMapping("/access")
    public ApiResponse<SharedCollectionDetailResponse> accessSharedLink(@RequestBody @Valid AccessSharedLinkRequest request) {
        SharedCollectionDetailResponse response = sharedLinkService.accessSharedLink(request);
        return ApiResponse.<SharedCollectionDetailResponse>builder()
                .success(true)
                .data(response)
                .build();
    }

    /**
     * Xóa shared link
     * URL: DELETE /api/v1/shared/collection/{collectionId}
     */
    @DeleteMapping("/collection/{collectionId}")
    public ApiResponse<Void> deleteSharedLink(@PathVariable String collectionId) {
        sharedLinkService.deleteSharedLink(collectionId);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    /**
     * Cập nhật mật khẩu của shared link
     * URL: PATCH /api/v1/shared/collection/{collectionId}/password
     */
    @PatchMapping("/collection/{collectionId}/password")
    public ApiResponse<SharedLinkResponse> updateSharedLinkPassword(
            @PathVariable String collectionId,
            @RequestParam(required = false) String newPassword) {
        SharedLinkResponse response = sharedLinkService.updateSharedLinkPassword(collectionId, newPassword);
        return ApiResponse.<SharedLinkResponse>builder()
                .success(true)
                .data(response)
                .build();
    }
}

