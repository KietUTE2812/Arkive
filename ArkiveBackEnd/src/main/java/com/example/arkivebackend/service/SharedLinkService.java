package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.request.AccessSharedLinkRequest;
import com.example.arkivebackend.dto.request.ShareCollectionRequest;
import com.example.arkivebackend.dto.response.SharedCollectionDetailResponse;
import com.example.arkivebackend.dto.response.SharedLinkResponse;

public interface SharedLinkService {

    /**
     * Tạo shared link cho một collection
     */
    SharedLinkResponse createSharedLink(ShareCollectionRequest request);

    /**
     * Lấy thông tin shared link của một collection (dành cho owner)
     */
    SharedLinkResponse getSharedLinkByCollectionId(String collectionId);

    /**
     * Truy cập shared link (public - không cần authentication)
     */
    SharedCollectionDetailResponse accessSharedLink(AccessSharedLinkRequest request);

    /**
     * Xóa shared link
     */
    void deleteSharedLink(String collectionId);

    /**
     * Cập nhật mật khẩu của shared link
     */
    SharedLinkResponse updateSharedLinkPassword(String collectionId, String newPassword);
}

