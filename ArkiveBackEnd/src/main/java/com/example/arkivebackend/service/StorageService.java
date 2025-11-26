package com.example.arkivebackend.service;

import com.example.arkivebackend.dto.response.PresignedUrlResponse;

public interface StorageService {

    // Tạo URL có chữ ký trước để tải lên tệp tin
    PresignedUrlResponse generatePresignedUploadUrl(String fileName, String contentType);

    // Tạo URL tải thumbnail có chữ ký trước
    PresignedUrlResponse generatePresignedThumbnailUrl(String fileName,String contentType);

    // Xóa tệp tin khỏi kho lưu trữ dựa trên khoá lưu trữ
    void deleteAsset(String storageKey);

    // Tạo URL có chữ ký trước để tải xuống tệp tin
    String generatePresignedGetUrl(String storageKey);

    // Tạo PresignedUrlResponse để download asset với filename
    PresignedUrlResponse generatePresignedDownloadUrl(String storageKey, String fileName);

    // Tạo PresignedUrlResponse để preview asset
    PresignedUrlResponse generatePresignedPreviewUrl(String storageKey);

}
