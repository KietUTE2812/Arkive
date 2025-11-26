package com.example.arkivebackend.service.impl;

import com.example.arkivebackend.dto.response.PresignedUrlResponse;
import com.example.arkivebackend.service.StorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageServiceImpl implements StorageService {

    S3Client s3Client;
    S3Presigner s3Presigner;
    @Value("${app.s3.bucket-name}")
    @NonFinal
    String bucketName;

    @Value("${app.s3.thumbnail-bucket-name}")
    @NonFinal
    String thumbnailBucketName;

    @Override
    public PresignedUrlResponse generatePresignedUploadUrl(String fileName, String contentType) {
        String storageKey = UUID.randomUUID().toString() + "-" + fileName;

        log.info("Generating PresignedUrlResponse for fileName {}, {}", fileName, contentType);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // URL hết hạn sau 10 phút
                .putObjectRequest(objectRequest)
                .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return new PresignedUrlResponse(url, storageKey);
    }

    @Override
    public PresignedUrlResponse generatePresignedThumbnailUrl(String fileName, String contentType) {
        String storageKey = UUID.randomUUID().toString() + "-" + fileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(thumbnailBucketName)
                .key(storageKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // URL hết hạn sau 10 phút
                .putObjectRequest(objectRequest)
                .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return new PresignedUrlResponse(url, storageKey);
    }

    @Override
    public void deleteAsset(String storageKey) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    @Override
    public String generatePresignedGetUrl(String storageKey) {
        // 1. Tạo request để lấy object
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .build();

        // 2. Tạo yêu cầu Pre-sign cho GET
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // URL có hiệu lực 10 phút
                .getObjectRequest(getRequest)
                .build();

        // 3. Lấy URL đã ký và trả về
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public PresignedUrlResponse generatePresignedDownloadUrl(String storageKey, String fileName) {
        log.info("Generating presigned download URL for storageKey: {}, fileName: {}", storageKey, fileName);

        // 1. Tạo request để lấy object với response-content-disposition để download
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                .build();

        // 2. Tạo yêu cầu Pre-sign cho GET
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30)) // URL có hiệu lực 30 phút
                .getObjectRequest(getRequest)
                .build();

        // 3. Lấy URL đã ký và trả về
        String url = s3Presigner.presignGetObject(presignRequest).url().toString();

        return new PresignedUrlResponse(url, storageKey);
    }

    @Override
    public PresignedUrlResponse generatePresignedPreviewUrl(String storageKey) {
        log.info("Generating presigned preview URL for storageKey: {}", storageKey);

        // 1. Tạo request để lấy object với response-content-disposition để preview
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .responseContentDisposition("inline")
                .build();

        // 2. Tạo yêu cầu Pre-sign cho GET
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30)) // URL có hiệu lực 30 phút
                .getObjectRequest(getRequest)
                .build();

        // 3. Lấy URL đã ký và trả về
        String url = s3Presigner.presignGetObject(presignRequest).url().toString();

        return new PresignedUrlResponse(url, storageKey);
    }
}
