"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter, usePathname, useSearchParams } from "next/navigation";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Asset, Collection } from "@/types/api";
import { getAssets, getDownloadUrl, getCollectionById, softDeleteAsset } from "@/lib/api/assets";
import { ArrowLeft, Database, File, FileClock, IdCard, List } from "lucide-react";
import { AddAssetModal } from "@/components/collections/assets/AddAssetModal";
import { AssetViewer } from "@/components/collections/assets/AssetViewer";
import AssetContainer from "@/components/collections/assets/AssetContainer";
import { AssetSearchBar } from "@/components/collections/assets/AssetSearchBar";
import { AssetPagination } from "@/components/collections/assets/AssetPagination";
import { Modal, SimpleModal } from "@/components/ui/modal";
import Image from 'next/image';

interface CollectionDetailClientProps {
  collectionId: string;
  initialSearchParams: {
    keyword?: string;
    sortBy?: string;
    sortOrder?: string;
    page?: string;
  };
}

const formatBytes = (bytes?: number) => {
  if (!bytes || bytes <= 0) {
    return "0 B";
  }
  const units = ["B", "KB", "MB", "GB", "TB"];
  const index = Math.min(
    Math.floor(Math.log(bytes) / Math.log(1024)),
    units.length - 1
  );
  const value = bytes / Math.pow(1024, index);
  return `${value.toFixed(value >= 10 || index === 0 ? 0 : 1)} ${units[index]}`;
};

const formatDate = (value?: string) => {
  if (!value) {
    return undefined;
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return undefined;
  }
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(date);
};

export function CollectionDetailClient({
  collectionId,
  initialSearchParams,
}: CollectionDetailClientProps) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const [collection, setCollection] = useState<Collection>({} as Collection);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isAddAssetModalOpen, setIsAddAssetModalOpen] = useState(false);
  const [selectedAsset, setSelectedAsset] = useState<Asset | null>(null);
  const [isViewerOpen, setIsViewerOpen] = useState(false);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [refreshKey, setRefreshKey] = useState(0);
  const [yesNoModalOpen, setYesNoModalOpen] = useState(false);
  const [assetToDelete, setAssetToDelete] = useState<Asset | null>(null);

  const pageSize = 8;
  const currentPage = parseInt(initialSearchParams.page || "0");
  const keyword = initialSearchParams.keyword;
  const sortBy = initialSearchParams.sortBy || "createdAt";
  const sortOrder = initialSearchParams.sortOrder || "asc";

  // Fetch collection details on mount
  useEffect(() => {
    const fetchCollection = async () => {
      try {
        const data = await getCollectionById(collectionId);
        setCollection(data);
      } catch (err) {
        console.error("Error fetching collection:", err);
      }
    }
    fetchCollection();
  }, [collectionId]);

  // Fetch assets khi URL params thay đổi
  useEffect(() => {
    const fetchAssets = async () => {
      setLoading(true);
      try {
        const data = await getAssets({
          collectionId,
          keyword,
          sortBy,
          sortOrder,
          page: currentPage,
          size: pageSize,
        });
        setAssets(data.content);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
        setError(null);
      } catch (err) {
        console.error("Error fetching assets:", err);
        setError("Không thể tải assets.");
      } finally {
        setLoading(false);
      }
    };

    fetchAssets();
  }, [collectionId, keyword, sortBy, sortOrder, currentPage, refreshKey]);

  const handleSearch = useCallback(
    (params: { keyword?: string; sortBy?: string; sortOrder?: string }) => {
      const url = new URLSearchParams();
      if (params.keyword) url.set("keyword", params.keyword);
      url.set("sortBy", params.sortBy || "createdAt");
      url.set("sortOrder", params.sortOrder || "desc");
      url.set("page", "0"); // Reset về trang 0

      router.push(`${pathname}?${url.toString()}`);
    },
    [collectionId, router, pathname]
  );

  const handlePageChange = useCallback(
    (page: number) => {
      const url = new URLSearchParams(searchParams.toString());
      url.set("page", page.toString());
      router.push(`${pathname}?${url.toString()}`);
      window.scrollTo({ top: 0, behavior: "smooth" });
    },
    [router, pathname, searchParams]
  );

  const handleAssetCreated = (asset: Asset) => {
    setAssets((previous) => [asset, ...previous]);
    setCollection((previous) => ({
      ...previous,
      assetCount: (previous.assetCount ?? 0) + 1,
    }));
    setTotalElements((prev) => prev + 1);
  };

  const handleAssetClick = (asset: Asset) => {
    setSelectedAsset(asset);
    setIsViewerOpen(true);
  };

  const handleDownload = async (asset: Asset) => {
    const downloadUrl = await getDownloadUrl(asset.id);
    window.open(downloadUrl, "_blank");
  };

  const handleDelete = async(asset: Asset) => {
    setAssetToDelete(asset);
    setYesNoModalOpen(true);
  };

  const confirmDelete = async () => {
    if (!assetToDelete) return;
    const asset = assetToDelete;
    setAssetToDelete(null);
    setYesNoModalOpen(false);
    try {
      await softDeleteAsset(asset.id);
      setRefreshKey((prev) => prev + 1);
    } catch (err) {
      console.error("Error deleting asset:", err);
      setError("Lỗi khi xóa asset.");
    }
    
  };

  const handleNavigate = (direction: "prev" | "next") => {
    if (!selectedAsset) return;

    const currentIndex = assets.findIndex((a) => a.id === selectedAsset.id);
    if (currentIndex === -1) return;

    let newIndex = currentIndex;
    if (direction === "prev" && currentIndex > 0) {
      newIndex = currentIndex - 1;
    } else if (direction === "next" && currentIndex < assets.length - 1) {
      newIndex = currentIndex + 1;
    }

    if (newIndex !== currentIndex) {
      setSelectedAsset(assets[newIndex]);
    }
  };

  const totalSize = assets.reduce((sum, asset) => sum + (asset.fileSize ?? 0), 0);
  const createdLabel = formatDate(collection?.createdAt);
  const updatedLabel = formatDate(collection?.updatedAt);

  return (
    <>
      <div className="container mx-auto space-y-6 p-8">
        {error ? (
          <Card className="border-destructive/50 bg-destructive/10">
            <CardHeader>
              <CardTitle className="text-destructive">
                Đã có lỗi xảy ra
              </CardTitle>
              <CardDescription>{error}</CardDescription>
            </CardHeader>
            <CardFooter>
              <Button type="button" onClick={() => router.refresh()}>
                Thử lại
              </Button>
            </CardFooter>
          </Card>
        ) : (
          <div className="space-y-6">
            <Card className="border-border/60">
              <CardHeader className="space-y-4">
                <div className="flex flex-wrap items-center justify-between gap-4">
                  <div className="space-y-2">
                    <div className="inline-flex items-center gap-2 rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
                      Bộ sưu tập
                    </div>
                    <CardTitle className="text-3xl font-semibold text-foreground">
                      {collection.name}
                    </CardTitle>
                    <CardDescription className="text-base">
                      {collection.description ||
                        "Chưa có mô tả cho bộ sưu tập này."}
                    </CardDescription>
                    <div className="flex flex-wrap gap-3 text-sm text-muted-foreground">
                      <span>Ngày tạo: {createdLabel ?? "—"}</span>
                      <span>•</span>
                      <span>Lần cập nhật cuối: {updatedLabel ?? "—"}</span>
                    </div>
                  </div>
                  <div className="flex flex-col items-end gap-3">
                    <Button
                      type="button"
                      onClick={() => setIsAddAssetModalOpen(true)}
                    >
                      Thêm tài sản
                    </Button>
                  </div>
                </div>
                <div className="grid grid-cols-1 gap-4 md:grid-cols-3 lg:grid-cols-4">
                  <div className="rounded-xl border border-border/60 bg-muted/40 p-4">
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                      Tổng tài sản
                    </p>
                    <p className="mt-2 text-xl font-semibold text-foreground flex items-center gap-2">
                      <File className="h-5 w-5 text-muted-foreground" />
                      {collection.assetCount ?? 0}
                    </p>
                  </div>
                  <div className="rounded-xl border border-border/60 bg-muted/40 p-4">
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                      Tổng dung lượng
                    </p>
                    <div className="mt-2 flex items-center gap-2 text-xl font-semibold text-foreground">
                      <Database className="h-5 w-5 text-muted-foreground" />
                      {formatBytes(totalSize)}
                    </div>
                  </div>
                  <div className="rounded-xl border border-border/60 bg-muted/40 p-4">
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                      Assets đã đồng bộ
                    </p>
                    <p className="mt-2 text-xl font-semibold text-foreground flex items-center gap-2">
                      <FileClock className="h-5 w-5 text-muted-foreground" />
                      {collection.assetCount ?? 0}
                    </p>
                  </div>
                  <div className="rounded-xl border border-border/60 bg-muted/40 p-4">
                    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                      ID bộ sưu tập
                    </p>
                    <p className="mt-2 truncate font-mono text-sm text-foreground flex items-center gap-2">
                      <IdCard />
                      {"..." + (collection.id || "").split("-").slice(-1)[0]}
                    </p>
                  </div>
                </div>
              </CardHeader>
            </Card>

            <AssetSearchBar
              onSearch={handleSearch}
              initialKeyword={keyword}
              initialSortBy={sortBy}
              initialSortOrder={sortOrder}
            />

            {loading ? (
              // Skeleton cho Grid Assets
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-4">
                {Array.from({ length: 8 }).map((_, i) => (
                  <div
                    key={i}
                    className="h-64 w-full animate-pulse rounded-xl bg-muted"
                  />
                ))}
              </div>
            ) : (
              // Hiển thị danh sách thật
              <AssetContainer
                assets={assets}
                paginationSlot={
                  <AssetPagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                  />
                }
                onDownload={handleDownload}
                onDelete={handleDelete}
                onClick={handleAssetClick}
              />
            )}
          </div>
        )}
      </div>

      <AddAssetModal
        open={isAddAssetModalOpen}
        collectionId={collectionId}
        onOpenChange={setIsAddAssetModalOpen}
        onAssetCreated={handleAssetCreated}
      />

      <AssetViewer
        asset={selectedAsset}
        assets={assets}
        open={isViewerOpen}
        onOpenChange={setIsViewerOpen}
        onDownload={handleDownload}
        onDelete={handleDelete}
        onNavigate={handleNavigate}
      />

      <SimpleModal
        open={yesNoModalOpen}
        onOpenChange={setYesNoModalOpen}
        title="Xác nhận xóa"
        description="Bạn có chắc chắn muốn xóa tài sản này không? Xóa vào thùng rác và có thể khôi phục sau."
        size="sm"
        footer={
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setYesNoModalOpen(false)}>
              Không
            </Button>
            <Button variant="destructive" onClick={confirmDelete}>
              Có
            </Button>
          </div>
        }
      >
        {/* Nội dung modal nếu cần */}
        <CardContent>
          {assetToDelete && (
            <div className="mt-4 p-4 rounded-lg bg-red-50 border border-red-200">
              <div className="mb-4 flex justify-center max-h-20 overflow-hidden">
                <Image
                  src={assetToDelete.thumbnailUrl || "/placeholder.png"}
                  alt={assetToDelete.filename}
                  width={100}
                  height={100}
                  className="rounded-md object-cover"
                />
              </div>  
              <p className="text-sm text-red-700">
                <strong>{assetToDelete.filename}</strong>
              </p>
              <p className="text-xs text-red-600 mt-1">
                Kích thước: {formatBytes(assetToDelete.fileSize)} • Loại: {assetToDelete.fileType}
              </p>
            </div>
          )}
        </CardContent>
      </SimpleModal>
    </>
  );
}
