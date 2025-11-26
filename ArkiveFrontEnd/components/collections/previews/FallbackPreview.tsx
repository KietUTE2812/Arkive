"use client";

import { Asset } from "@/types/api";
import { Button } from "@/components/ui/button";
import { Download, Loader2, File as FileIcon } from "lucide-react";

interface FallbackPreviewProps {
  asset: Asset;
  previewUrl: string | null;
  isLoading: boolean;
}

export function FallbackPreview({ asset, previewUrl, isLoading }: FallbackPreviewProps) {
  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <Loader2 className="h-4 w-4 animate-spin" />
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center gap-4 p-12 bg-muted/30 rounded-lg">
      <div className="h-32 w-32 rounded-2xl bg-primary/10 flex items-center justify-center">
        <FileIcon className="h-16 w-16 text-primary" />
      </div>
      <div className="text-center space-y-2">
        <p className="text-lg font-semibold text-foreground">
          Không thể xem trước file này
        </p>
        <p className="text-sm text-muted-foreground">
          Loại file: {asset.fileType || "Không xác định"}
        </p>
      </div>
      <Button
        onClick={() => window.open(previewUrl ?? asset.thumbnailUrl, "_blank")}
        className="gap-2"
      >
        <Download className="h-4 w-4" />
        Tải xuống để xem
      </Button>
    </div>
  );
}

