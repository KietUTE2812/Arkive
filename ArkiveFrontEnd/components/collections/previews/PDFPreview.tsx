"use client";

import { Asset } from "@/types/api";
import { Loader2 } from "lucide-react";

interface PDFPreviewProps {
  asset: Asset;
  previewUrl: string | null;
  isLoading: boolean;
}

export function PDFPreview({ asset, previewUrl, isLoading }: PDFPreviewProps) {
  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <Loader2 className="h-4 w-4 animate-spin" />
      </div>
    );
  }

  return (
    <div className="w-full h-[70vh] rounded-lg overflow-hidden border border-border">
      <iframe
        src={previewUrl ?? asset.thumbnailUrl}
        className="w-full h-full"
        title={asset.filename}
      />
    </div>
  );
}

