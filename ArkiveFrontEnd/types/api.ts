/**
 * Types và interfaces cho API responses và errors
 */

export interface ApiError {
    code: string;
    message: string;
    status: number;
}

export interface LoginResponse {
    success: boolean;
    data: {
        token: string;
        authenticated: boolean;
    };
}

export interface RegisterResponse {
    success: boolean;
    data: {
        id: string;
        fullName: string;
        username: string;
        email: string;
        isVerified: boolean;
    };
}

export interface VerifyEmailRequest {
    verificationCode: string;
}

export interface VerifyEmailResponse {
    success: boolean;
    data: {
        verified: boolean;
    };
}

export interface ResendVerificationRequest {
    email: string;
}

export interface ResendVerificationResponse {
    success: boolean;
    message?: string;
}

// ========== Collections / Assets ==========

export interface Asset {
    id: string;
    assetId?: string;
    filename: string;
    fileType: string;
    fileSize: number;
    storageKey: string;
    createdAt: string;
    updatedAt: string;
    thumbnailUrl: string;
    tags?: string[];
    collectionId: string;
}

export interface GetAssetsParams {
    collectionId?: string;
    page?: number;
    size?: number;
    keyword?: string;
    sortBy?: string;
    sortOrder?: string;
}

export interface PresignedUrlResponse {
    url: string;
    storageKey: string;
}

export interface AssetUploadRequestPayload {
    fileName: string;
    contentType: string;
}

export interface AssetUploadCompletePayload {
    collectionId?: string;
    storageKey: string;
    fileName: string;
    contentType: string;
    fileSize: number;
    tags?: string[];
    thumbnailUrl?: string;
}

export interface Collection {
    id: string;
    name: string;
    description?: string;
    assetCount?: number;
    createdAt?: string;
    updatedAt?: string;
}

export interface GetCollectionResponse {
    success: boolean;
    data: Collection;
}

export interface GetAssetsResponse {
    success: boolean;
    data: {
        content: Asset[];
        totalPages: number;
        totalElements: number;
        size: number;
        number: number;
        first: boolean;
        last: boolean;
        empty: boolean;
    };
}

export interface CreateAssetRequest {
    name: string;
    description?: string;
    url?: string;
}

export interface CreateAssetResponse {
    success: boolean;
    data: Asset;
}

