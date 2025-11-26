import { create } from "zustand";
import { Asset, Collection } from "@/types/api";
import {
    getAssets as fetchAssets,
    getAssetById,
    createAsset as createAssetApi,
    uploadAsset as uploadAssetApi,
    softDeleteAsset as softDeleteAssetApi,
    hardDeleteAsset as hardDeleteAssetApi,
    getCollections as fetchCollections,
    getCollectionById,
    createCollection as createCollectionApi,
    deleteCollection as deleteCollectionApi,
} from "@/lib/api/assets";
import { CreateAssetRequest, GetAssetsParams } from "@/types/api";

interface AssetsState {
    // State
    assets: Asset[];
    collections: Collection[];
    currentCollection: Collection | null;
    selectedAsset: Asset | null;
    isLoading: boolean;
    error: string | null;

    // Asset Actions
    fetchAssets: (collectionId?: string) => Promise<void>;
    fetchAssetById: (assetId: string) => Promise<void>;
    createAsset: (data: CreateAssetRequest) => Promise<void>;
    uploadAsset: (
        file: File,
        thumbnail: Blob,
        collectionId?: string,
        tags?: string[],
        onProgress?: (progress: number) => void
    ) => Promise<void>;
    softDeleteAsset: (assetId: string) => Promise<void>;
    hardDeleteAsset: (assetId: string) => Promise<void>;
    setSelectedAsset: (asset: Asset | null) => void;

    // Collection Actions
    fetchCollections: () => Promise<void>;
    fetchCollectionById: (collectionId: string) => Promise<void>;
    createCollection: (name: string, description?: string) => Promise<void>;
    deleteCollection: (collectionId: string) => Promise<void>;
    setCurrentCollection: (collection: Collection | null) => void;

    // Utility
    clearError: () => void;
    reset: () => void;
}

export const useAssetsStore = create<AssetsState>((set, get) => ({
    // Initial state
    assets: [],
    collections: [],
    currentCollection: null,
    selectedAsset: null,
    isLoading: false,
    error: null,

    // Fetch assets
    fetchAssets: async (collectionId?: string) => {
        set({ isLoading: true, error: null });
        try {
            const assets = await fetchAssets({ collectionId } as GetAssetsParams);
            set({ assets: assets.content, isLoading: false });
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi tải assets",
            });
        }
    },

    // Fetch asset by ID
    fetchAssetById: async (assetId: string) => {
        set({ isLoading: true, error: null });
        try {
            const asset = await getAssetById(assetId);
            set({ selectedAsset: asset, isLoading: false });
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi tải asset",
            });
        }
    },

    // Create asset
    createAsset: async (data: CreateAssetRequest) => {
        set({ isLoading: true, error: null });
        try {
            const newAsset = await createAssetApi(data);
            set((state) => ({
                assets: [newAsset, ...state.assets],
                isLoading: false,
            }));
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi tạo asset",
            });
            throw error;
        }
    },

    // Upload asset
    uploadAsset: async (file: File, thumbnail : Blob, collectionId?: string, tags?: string[], onProgress?: (progress: number) => void) => {
        set({ isLoading: true, error: null });
        try {
            const newAsset = await uploadAssetApi(file, thumbnail, collectionId, tags, onProgress);
            set((state) => ({
                assets: [newAsset, ...state.assets],
                isLoading: false,
            }));
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi upload asset",
            });
            throw error;
        }
    },

    // Delete asset
    softDeleteAsset: async (assetId: string) => {
        set({ isLoading: true, error: null });
        try {
            await softDeleteAssetApi(assetId);
            set((state) => ({
                assets: state.assets.filter((asset) => asset.id !== assetId),
                isLoading: false,
            }));
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi xóa asset",
            });
            throw error;
        }
    },

    hardDeleteAsset: async (assetId: string) => {
        set({ isLoading: true, error: null });
        try {
            await hardDeleteAssetApi(assetId);
            set((state) => ({
                assets: state.assets.filter((asset) => asset.id !== assetId),
                isLoading: false,
            }));
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi xóa vĩnh viễn asset",
            });
            throw error;
        }
    },

    // Set selected asset
    setSelectedAsset: (asset: Asset | null) => {
        set({ selectedAsset: asset });
    },

    // Fetch collections
    fetchCollections: async () => {
        set({ isLoading: true, error: null });
        try {
            const collections = await fetchCollections();
            set({ collections, isLoading: false });
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi tải collections",
            });
        }
    },

    // Fetch collection by ID
    fetchCollectionById: async (collectionId: string) => {
        set({ isLoading: true, error: null });
        try {
            const collection = await getCollectionById(collectionId);
            set({ currentCollection: collection, isLoading: false });
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi tải collection",
            });
        }
    },

    // Create collection
    createCollection: async (name: string, description?: string) => {
        set({ isLoading: true, error: null });
        try {
            const newCollection = await createCollectionApi(name, description);
            set((state) => ({
                collections: [newCollection, ...state.collections],
                isLoading: false,
            }));
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi tạo collection",
            });
            throw error;
        }
    },

    // Delete collection
    deleteCollection: async (collectionId: string) => {
        set({ isLoading: true, error: null });
        try {
            await deleteCollectionApi(collectionId);
            set((state) => ({
                collections: state.collections.filter((col) => col.id !== collectionId),
                isLoading: false,
            }));
        } catch (error: any) {
            set({
                isLoading: false,
                error: error.response?.data?.errors?.message || error.message || "Lỗi khi xóa collection",
            });
            throw error;
        }
    },

    // Set current collection
    setCurrentCollection: (collection: Collection | null) => {
        set({ currentCollection: collection });
    },

    // Clear error
    clearError: () => {
        set({ error: null });
    },

    // Reset state
    reset: () => {
        set({
            assets: [],
            collections: [],
            currentCollection: null,
            selectedAsset: null,
            isLoading: false,
            error: null,
        });
    },
}));

