"use client";

import { Asset } from "@/types/api";
import { FileText, Loader2 } from "lucide-react";

interface AudioPreviewProps {
  asset: Asset;
  previewUrl: string | null;
  isLoading: boolean;
}

export function AudioPreview({ asset, previewUrl, isLoading }: AudioPreviewProps) {
  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <Loader2 className="h-4 w-4 animate-spin" />
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center gap-4 p-8 bg-muted/30 rounded-lg">
      <div className="h-32 w-32 rounded-full bg-primary/10 flex items-center justify-center">
        <FileText className="h-16 w-16 text-primary" />
      </div>
      <audio controls className="w-full max-w-md">
        <source src={previewUrl ?? asset.thumbnailUrl} type={asset.fileType || undefined} />
        Trình duyệt của bạn không hỗ trợ phát audio.
      </audio>
    </div>
  );
}

