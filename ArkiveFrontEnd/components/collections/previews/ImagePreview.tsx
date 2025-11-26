"use client";

import { useState } from "react";
import { Asset } from "@/types/api";
import { Button } from "@/components/ui/button";
import { Maximize2, Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";

interface ImagePreviewProps {
  asset: Asset;
  previewUrl: string | null;
  isLoading: boolean;
}

export function ImagePreview({ asset, previewUrl, isLoading }: ImagePreviewProps) {
  const [isFullscreen, setIsFullscreen] = useState(false);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <Loader2 className="h-4 w-4 animate-spin" />
      </div>
    );
  }

  return (
    <div className="relative group">
      <img
        src={previewUrl ?? asset.thumbnailUrl}
        alt={asset.filename}
        className={cn(
          "w-full h-auto max-h-[70vh] object-contain rounded-lg bg-muted/50",
          isFullscreen && "max-h-screen"
        )}
      />
      <Button
        size="icon"
        variant="secondary"
        className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-opacity"
        onClick={() => setIsFullscreen(!isFullscreen)}
      >
        <Maximize2 className="h-4 w-4" />
      </Button>
    </div>
  );
}

