"use client";

import { Asset } from "@/types/api";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Download,
  Trash2,
  ChevronLeft,
  ChevronRight,
  FileText,
  Calendar,
  HardDrive,
  Tag,
} from "lucide-react";
import { useEffect, useState } from "react";
import { getAssetPreviewUrl } from "@/lib/api/assets";
import {
  ImagePreview,
  VideoPreview,
  AudioPreview,
  PDFPreview,
  FallbackPreview,
} from "../previews";

interface AssetViewerProps {
  asset: Asset | null;
  assets?: Asset[];
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onDelete?: (asset: Asset) => void;
  onDownload?: (asset: Asset) => void;
  onNavigate?: (direction: "prev" | "next") => void;
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
    return "—";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "—";
  }
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
};

function AssetPreview({ asset }: { asset: Asset }) {
  const fileType = asset.fileType?.toLowerCase() ?? "";
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const getPreviewUrl = async () => {
      setIsLoading(true);
      const url = await getAssetPreviewUrl(asset.id);
      console.log("previewUrl", url);
      setPreviewUrl(url);
      setIsLoading(false);
    };
    getPreviewUrl();
  }, [asset.id]);

  const previewProps = { asset, previewUrl, isLoading };

  // Image preview
  if (fileType.startsWith("image/")) {
    return <ImagePreview {...previewProps} />;
  }

  // Video preview
  if (fileType.startsWith("video/")) {
    return <VideoPreview src={previewUrl ?? asset.thumbnailUrl} poster={asset.thumbnailUrl} />;
  }

  // Audio preview
  if (fileType.startsWith("audio/")) {
    return <AudioPreview {...previewProps} />;
  }

  // PDF preview
  if (fileType.includes("pdf")) {
    return <PDFPreview {...previewProps} />;
  }

  // Fallback for other file types
  return <FallbackPreview {...previewProps} />;
}

export function AssetViewer({
  asset,
  assets = [],
  open,
  onOpenChange,
  onDelete,
  onDownload,
  onNavigate,
}: AssetViewerProps) {
  if (!asset) return null;

  const currentIndex = assets.findIndex((a) => a.id === asset.id);
  const hasPrevious = currentIndex > 0;
  const hasNext = currentIndex < assets.length - 1 && currentIndex !== -1;

  const handlePrevious = () => {
    if (hasPrevious && onNavigate) {
      onNavigate("prev");
    }
  };

  const handleNext = () => {
    if (hasNext && onNavigate) {
      onNavigate("next");
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-6xl h-[90vh] flex flex-col p-0 gap-0">
        {/* Header */}
        <DialogHeader className="px-10 py-4 border-b border-border space-y-0">
          <div className="flex items-start justify-between gap-4">
            <div className="flex-1 min-w-0 space-y-1">
              <DialogTitle className="text-xl font-semibold line-clamp-1">
                {asset.filename}
              </DialogTitle>
              <DialogDescription className="text-sm">
                {asset.fileType || "Không xác định"} • {formatBytes(asset.fileSize)}
              </DialogDescription>
            </div>
            <div className="flex items-center gap-2">
              {onDownload && (
                <Button
                  size="sm"
                  variant="outline"
                  className="gap-2"
                  onClick={() => onDownload(asset)}
                >
                  <Download className="h-4 w-4" />
                  Tải xuống
                </Button>
              )}
              {onDelete && (
                <Button
                  size="sm"
                  variant="outline"
                  className="gap-2 text-destructive hover:bg-destructive hover:text-destructive-foreground"
                  onClick={() => {
                    onDelete(asset);
                    onOpenChange(false);
                  }}
                >
                  <Trash2 className="h-4 w-4" />
                  Xóa
                </Button>
              )}
            </div>
          </div>
        </DialogHeader>

        {/* Content */}
        <div className="flex flex-1 min-h-0">
          {/* Preview Area */}
          <div className="flex-1 relative overflow-auto">
            <div className="p-6">
              <AssetPreview asset={asset} />
            </div>

            {/* Navigation Buttons */}
            {assets.length > 1 && (
              <>
                {hasPrevious && (
                  <Button
                    size="icon"
                    variant="secondary"
                    className="absolute left-4 top-1/2 -translate-y-1/2 h-10 w-10 rounded-full shadow-lg"
                    onClick={handlePrevious}
                  >
                    <ChevronLeft className="h-5 w-5" />
                  </Button>
                )}
                {hasNext && (
                  <Button
                    size="icon"
                    variant="secondary"
                    className="absolute right-4 top-1/2 -translate-y-1/2 h-10 w-10 rounded-full shadow-lg"
                    onClick={handleNext}
                  >
                    <ChevronRight className="h-5 w-5" />
                  </Button>
                )}
              </>
            )}
          </div>

          {/* Sidebar - Metadata */}
          <div className="w-80 border-l border-border bg-muted/20">
            <ScrollArea className="h-full">
              <div className="p-6 space-y-6">
                {/* Basic Info */}
                <div className="space-y-3">
                  <h3 className="text-sm font-semibold text-foreground">
                    Thông tin cơ bản
                  </h3>
                  <div className="space-y-2.5 text-sm">
                    <div className="flex items-start gap-2">
                      <FileText className="h-4 w-4 text-muted-foreground mt-0.5 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="text-xs text-muted-foreground">Tên file</p>
                        <p className="font-medium text-foreground break-words">
                          {asset.filename}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-start gap-2">
                      <HardDrive className="h-4 w-4 text-muted-foreground mt-0.5 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="text-xs text-muted-foreground">Dung lượng</p>
                        <p className="font-medium text-foreground">
                          {formatBytes(asset.fileSize)}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-start gap-2">
                      <FileText className="h-4 w-4 text-muted-foreground mt-0.5 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="text-xs text-muted-foreground">Loại file</p>
                        <p className="font-medium text-foreground break-all">
                          {asset.fileType || "—"}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <Separator />

                {/* Dates */}
                <div className="space-y-3">
                  <h3 className="text-sm font-semibold text-foreground">
                    Thời gian
                  </h3>
                  <div className="space-y-2.5 text-sm">
                    <div className="flex items-start gap-2">
                      <Calendar className="h-4 w-4 text-muted-foreground mt-0.5 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="text-xs text-muted-foreground">Ngày tạo</p>
                        <p className="font-medium text-foreground">
                          {formatDate(asset.createdAt)}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-start gap-2">
                      <Calendar className="h-4 w-4 text-muted-foreground mt-0.5 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="text-xs text-muted-foreground">Cập nhật</p>
                        <p className="font-medium text-foreground">
                          {formatDate(asset.updatedAt)}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Tags */}
                {asset.tags && asset.tags.length > 0 && (
                  <>
                    <Separator />
                    <div className="space-y-3">
                      <h3 className="text-sm font-semibold text-foreground flex items-center gap-2">
                        <Tag className="h-4 w-4" />
                        Tags
                      </h3>
                      <div className="flex flex-wrap gap-2">
                        {asset.tags.map((tag) => (
                          <Badge
                            key={tag}
                            variant="secondary"
                            className="rounded-full"
                          >
                            {tag}
                          </Badge>
                        ))}
                      </div>
                    </div>
                  </>
                )}

                <Separator />

                {/* IDs */}
                <div className="space-y-3">
                  <h3 className="text-sm font-semibold text-foreground">
                    Định danh
                  </h3>
                  <div className="space-y-2.5 text-xs">
                    <div className="rounded-md bg-muted/50 p-2.5">
                      <p className="text-muted-foreground mb-1">Asset ID</p>
                      <p className="font-mono font-medium text-foreground break-all">
                        {asset.id}
                      </p>
                    </div>
                    {asset.assetId && (
                      <div className="rounded-md bg-muted/50 p-2.5">
                        <p className="text-muted-foreground mb-1">Asset Ref</p>
                        <p className="font-mono font-medium text-foreground break-all">
                          {asset.assetId}
                        </p>
                      </div>
                    )}
                    {asset.collectionId && (
                      <div className="rounded-md bg-muted/50 p-2.5">
                        <p className="text-muted-foreground mb-1">Collection ID</p>
                        <p className="font-mono font-medium text-foreground break-all">
                          {asset.collectionId}
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </ScrollArea>
          </div>
        </div>

        {/* Footer - Navigation info */}
        {assets.length > 1 && currentIndex !== -1 && (
          <div className="px-6 py-3 border-t border-border bg-muted/20">
            <p className="text-xs text-center text-muted-foreground">
              {currentIndex + 1} / {assets.length} assets
            </p>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}

