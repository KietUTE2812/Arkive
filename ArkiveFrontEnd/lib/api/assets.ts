import apiClient from "./client";
import {
  Asset,
  Collection,
  GetCollectionResponse,
  GetAssetsResponse,
  CreateAssetRequest,
  CreateAssetResponse,
  ApiError,
  AssetUploadRequestPayload,
  PresignedUrlResponse,
  AssetUploadCompletePayload,
  GetAssetsParams,
} from "@/types/api";

const DEFAULT_CONTENT_TYPE = "application/octet-stream";

async function uploadFileToPresignedUrl(
  url: string,
  file: File,
  onProgress?: (progress: number) => void
): Promise<void> {
  if (typeof XMLHttpRequest === "undefined") {
    throw new Error("XMLHttpRequest is not available in this environment");
  }

  await new Promise<void>((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open("PUT", url);
    xhr.setRequestHeader(
      "Content-Type",
      file.type && file.type.length > 0 ? file.type : DEFAULT_CONTENT_TYPE
    );

    if (xhr.upload && onProgress) {
      xhr.upload.onprogress = (event) => {
        if (!event.lengthComputable) {
          return;
        }
        const progress = Math.round((event.loaded / event.total) * 100);
        onProgress(progress);
      };
    }

    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        onProgress?.(100);
        resolve();
      } else {
        reject(
          new Error(
            `Upload failed with status ${xhr.status}: ${xhr.responseText}`
          )
        );
      }
    };

    xhr.onerror = () => {
      reject(new Error("Không thể tải file lên presigned URL"));
    };

    xhr.send(file);
  });
}

export async function requestAssetUploadUrl(
  payload: AssetUploadRequestPayload
): Promise<PresignedUrlResponse> {
  try {
    const response = await apiClient.post<{
      success: boolean;
      data: PresignedUrlResponse;
    }>("/assets/upload-request", payload);
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

export async function requestThumbnailUploadUrl(
  payload: AssetUploadRequestPayload
): Promise<PresignedUrlResponse> {
  try {
    const response = await apiClient.post<{
      success: boolean;
      data: PresignedUrlResponse;
    }>("/assets/upload-thumbnail-request", payload);
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

export async function completeAssetUpload(
  payload: AssetUploadCompletePayload
): Promise<Asset> {
  try {
    const response = await apiClient.post<{
      success: boolean;
      data: Asset;
    }>("/assets/upload-complete", payload);
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Lấy danh sách assets
 */
export async function getAssets(
  params: GetAssetsParams
): Promise<{ content: Asset[]; totalPages: number; totalElements: number; size: number; number: number; first: boolean; last: boolean; empty: boolean }> {
  try {
    const response = await apiClient.get<GetAssetsResponse>("/assets", {
      params: {
        ...params
      }
    });
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Lấy asset theo ID
 */
export async function getAssetById(assetId: string): Promise<Asset> {
  try {
    const response = await apiClient.get<{ success: boolean; data: Asset }>(
      `/assets/${assetId}`
    );
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

export async function getAssetPreviewUrl(assetId: string): Promise<string> {
  try {
    const response = await apiClient.get<{ success: boolean; data: PresignedUrlResponse }>(
      `/assets/${assetId}/preview`
    );
    return response.data.data.url;
  } catch (error) {
    throw error as ApiError;
  }
}

export async function getDownloadUrl(assetId: string): Promise<string> {
  try {
    const response = await apiClient.get<{ success: boolean; data: PresignedUrlResponse }>(
      `/assets/${assetId}/download`
    );
    return response.data.data.url;
  }
  catch (error) {
    throw error as ApiError;
  }
}
/**
 * Tạo asset mới
 */
export async function createAsset(
  data: CreateAssetRequest
): Promise<Asset> {
  try {
    const response = await apiClient.post<CreateAssetResponse>(
      "/assets",
      data
    );
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Upload file
 */
export async function uploadAsset(
  file: File,
  thumbnail: Blob,
  collectionId?: string,
  tags?: string[] | undefined,
  onProgress?: (progress: number) => void
): Promise<Asset> {
  try {
    // B1: Tạo thumbnail

    // B2: Tạo presigned URL cho thumbnail và upload
    const thumbnailPresigned = await requestThumbnailUploadUrl({
      fileName: file.name.replace(/\.[^.]+$/, ".jpg"),
      contentType: "image/jpeg"
    });
    await uploadFileToPresignedUrl(thumbnailPresigned.url, new File([thumbnail], file.name.replace(/\.[^.]+$/, ".jpg"), { type: "image/jpeg" }), (progress) => {
      onProgress?.(Math.min(progress, 30));
    });

    // B3: Tạo presigned URL cho file và upload
    const requestPayload: AssetUploadRequestPayload = {
      fileName: file.name,
      contentType: file.type && file.type.length > 0 ? file.type : DEFAULT_CONTENT_TYPE
    };
    const presigned = await requestAssetUploadUrl(requestPayload);
    await uploadFileToPresignedUrl(presigned.url, file, (progress) => {
      onProgress?.(Math.min(progress, 70));
    });

    // B4: Hoàn thành upload
    const asset = await completeAssetUpload({
      collectionId,
      storageKey: presigned.storageKey,
      fileName: file.name,
      contentType: requestPayload.contentType,
      fileSize: file.size,
      tags,
      thumbnailUrl: `${process.env.NEXT_PUBLIC_R2_PUBLIC_URL}/${thumbnailPresigned.storageKey}`,
    });

    onProgress?.(100);

    return asset;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Xóa asset
 */
export async function softDeleteAsset(assetId: string): Promise<void> {
  try {
    await apiClient.delete(`/assets/${assetId}`);
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Xóa asset
 */
export async function hardDeleteAsset(assetId: string): Promise<void> {
  try {
    await apiClient.delete(`/assets/${assetId}/hard`);
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Lấy asset đã xóa
 */
export async function getDeletedAssets() {
  try {
    const response = await apiClient.get<{
      success: boolean;
      data: Asset[];
    }>("/assets/deleted");
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/** Khôi phục asset đã xóa
 */
export async function restoreDeletedAsset(assetId: string): Promise<void> {
  try {
    await apiClient.post(`/assets/${assetId}/restore`);
  } catch (error) {
    throw error as ApiError;
  }
}

// ------------------------------Collections------------------------------------------
/**
 * Lấy danh sách collections
 */
export async function getCollections(): Promise<Collection[]> {
  try {
    const response = await apiClient.get<{
      success: boolean;
      data: Collection[];
    }>("/collections");
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Lấy collection theo ID
 */
export async function getCollectionById(
  collectionId: string
): Promise<Collection> {
  try {
    const response = await apiClient.get<GetCollectionResponse>(
      `/collections/${collectionId}`
    );
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Tạo collection mới
 */
export async function createCollection(
  name: string,
  description?: string
): Promise<Collection> {
  try {
    const response = await apiClient.post<GetCollectionResponse>(
      "/collections",
      { name, description }
    );
    return response.data.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Xóa collection
 */
export async function deleteCollection(collectionId: string): Promise<void> {
  try {
    await apiClient.delete(`/collections/${collectionId}`);
  } catch (error) {
    throw error as ApiError;
  }
}

