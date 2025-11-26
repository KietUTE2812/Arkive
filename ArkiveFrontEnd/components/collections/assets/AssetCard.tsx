import { Asset } from "@/types/api";
import {
  Card,
  CardContent,
  CardFooter,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import {
  File as FileIcon,
  FileText,
  Image as ImageIcon,
  Music,
  Video,
  Archive,
  HardDrive,
  Calendar,
  Download,
  ExternalLink,
  Trash,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { useRouter } from "next/navigation";

const assetAccentVariants: string[] = [
  "bg-gradient-to-br from-primary/20 to-primary/5 text-primary",
  "bg-gradient-to-br from-secondary/20 to-secondary/5 text-secondary",
  "bg-gradient-to-br from-accent/20 to-accent/5 text-accent",
  "bg-gradient-to-br from-blue-500/20 to-blue-500/5 text-blue-600",
  "bg-gradient-to-br from-purple-500/20 to-purple-500/5 text-purple-600",
  "bg-gradient-to-br from-pink-500/20 to-pink-500/5 text-pink-600",
];

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

const getAssetPresentation = (asset: Asset): {
  icon: LucideIcon;
  label: string;
  color: string;
} => {
  const type = asset.fileType?.toLowerCase() ?? "";

  if (type.startsWith("image/")) {
    return { icon: ImageIcon, label: "Hình ảnh", color: "text-green-600" };
  }
  if (type.startsWith("video/")) {
    return { icon: Video, label: "Video", color: "text-red-600" };
  }
  if (type.startsWith("audio/")) {
    return { icon: Music, label: "Âm thanh", color: "text-purple-600" };
  }
  if (type.includes("pdf")) {
    return { icon: FileText, label: "Tài liệu", color: "text-orange-600" };
  }
  if (type.includes("zip") || type.includes("rar")) {
    return { icon: Archive, label: "Lưu trữ", color: "text-yellow-600" };
  }

  return { icon: FileIcon, label: "Tập tin", color: "text-gray-600" };
};

interface AssetCardProps {
  asset: Asset;
  index: number;
  onDownload?: () => void;
  onDelete?: () => void;
  onClick?: () => void;
}

export function AssetCard({ asset, index, onClick, onDownload, onDelete }: AssetCardProps) {
  const { icon: Icon, label, color } = getAssetPresentation(asset);
  const accentClass = assetAccentVariants[index % assetAccentVariants.length];
  const size = formatBytes(asset.fileSize);
  const updated = formatDate(asset.updatedAt);
  const created = formatDate(asset.createdAt);
  const dateLabel = updated || created || "—";

  const router = useRouter();

  return (
    <Card
      className="group relative flex h-full flex-col overflow-hidden border-border/60 transition-all duration-300 hover:-translate-y-1 hover:border-primary/60 hover:shadow-xl cursor-pointer"
      onClick={onClick}
    >
      {/* Thumbnail Area - Large and Prominent */}
      <div className="relative aspect-video w-full overflow-hidden bg-muted/40">
        {asset.thumbnailUrl ? (
          <>
            <img
              src={asset.thumbnailUrl}
              alt={asset.filename}
              className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-110"
            />
            {/* Overlay on hover */}
            <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/0 to-black/0 opacity-0 transition-opacity duration-300 group-hover:opacity-100" />

            {/* Quick Action Buttons - Show on hover */}
            <div className="absolute right-3 top-3 flex gap-2 opacity-0 transition-opacity duration-300 group-hover:opacity-100">
              <Button
                size="icon"
                variant="secondary"
                className="h-8 w-8 rounded-full bg-white/90 backdrop-blur-sm hover:bg-primary hover:text-primary-foreground"
                onClick={(e) => {
                  e.stopPropagation();
                  onDownload && onDownload();
                }}
              >
                <Download className="h-4 w-4" />
              </Button>
              <Button
                size="icon"
                variant="secondary"
                className="h-8 w-8 rounded-full bg-white/90 backdrop-blur-sm hover:bg-red-500 hover:text-white"
                onClick={(e) => {
                  e.stopPropagation();
                  onDelete && onDelete();
                }}
              >
                <Trash className="h-4 w-4" />
              </Button>
            </div>
          </>
        ) : (
          <div className={cn("flex h-full w-full items-center justify-center", accentClass)}>
            <Icon className="h-16 w-16 opacity-60" />
          </div>
        )}

        {/* File Type Badge */}
        <div className="absolute bottom-3 left-3">
          <div className="inline-flex items-center gap-1.5 rounded-full bg-white/95 px-3 py-1 text-xs font-medium shadow-lg backdrop-blur-sm">
            <Icon className={cn("h-3.5 w-3.5", color)} />
            <span className={color}>{label}</span>
          </div>
        </div>
      </div>

      {/* Content Area */}
      <CardContent className="flex flex-1 flex-col gap-3 p-4">
        {/* File Name */}
        <div className="space-y-1">
          <CardTitle className="line-clamp-2 text-base font-semibold leading-snug">
            {asset.filename}
          </CardTitle>
          <CardDescription className="text-xs">
            {asset.fileType || "Không xác định"}
          </CardDescription>
        </div>

        {/* Metadata Grid */}
        <div className="grid grid-cols-2 gap-2 text-xs">
          <div className="flex items-center gap-1.5 rounded-md bg-muted/50 px-2.5 py-1.5">
            <HardDrive className="h-3.5 w-3.5 text-muted-foreground" />
            <span className="font-medium text-foreground">{size}</span>
          </div>
          <div className="flex items-center gap-1.5 rounded-md bg-muted/50 px-2.5 py-1.5">
            <Calendar className="h-3.5 w-3.5 text-muted-foreground" />
            <span className="font-medium text-foreground">{dateLabel}</span>
          </div>
        </div>

        {/* Tags */}
        {asset.tags && asset.tags.length > 0 && (
          <div className="flex flex-wrap gap-1.5">
            {asset.tags.slice(0, 3).map((tag) => (
              <span
                key={tag}
                className="inline-flex items-center rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary"
              >
                {tag}
              </span>
            ))}
            {asset.tags.length > 3 && (
              <span className="inline-flex items-center rounded-full bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
                +{asset.tags.length - 3}
              </span>
            )}
          </div>
        )}
      </CardContent>

      {/* Footer */}
      <CardFooter className="border-t border-border/60 bg-muted/20 px-4 py-3">
        <div className="flex w-full items-center justify-between gap-2">
          <span className="truncate font-mono text-xs text-muted-foreground">
            ID: {asset.id.slice(0, 8)}
          </span>
          {asset.assetId && (
            <span className="truncate font-mono text-xs text-muted-foreground">
              #{asset.assetId.slice(0, 8)}
            </span>
          )}
        </div>
      </CardFooter>
    </Card>
  );
}

