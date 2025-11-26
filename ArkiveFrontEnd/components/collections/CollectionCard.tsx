import { Collection } from "@/types/api";
import { 
  Card, 
  CardHeader, 
  CardContent, 
  CardFooter, 
  CardTitle, 
  CardDescription 
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { formatDate } from "@/lib/utils";
import { cn } from "@/lib/utils";
import Link from "next/link";
import { 
  ArrowRight, 
  Layers, 
  FolderKanban, 
  Image as ImageIcon, 
  Archive, 
  Sparkles, 
  Palette 
} from "lucide-react";
import type { LucideIcon } from "lucide-react";

const collectionIcons: LucideIcon[] = [
  FolderKanban,
  ImageIcon,
  Layers,
  Archive,
  Sparkles,
  Palette,
];

const accentVariants: string[] = [
  "bg-primary/15 text-primary",
  "bg-secondary/15 text-secondary",
  "bg-accent/15 text-accent",
];

interface CollectionCardProps {
  collection: Collection;
  index: number;
}

export function CollectionCard({ collection, index }: CollectionCardProps) {
  const Icon = collectionIcons[index % collectionIcons.length];
  const accentClass = accentVariants[index % accentVariants.length];
  const assetCount = collection.assetCount ?? 0;
  const createdLabel = formatDate(collection.createdAt);
  const updatedLabel = formatDate(collection.updatedAt);
  const statusLabel = updatedLabel
    ? `Cập nhật ${updatedLabel}`
    : createdLabel
      ? `Tạo ${createdLabel}`
      : "Mới tạo";

  return (
    <Card className="group flex h-full flex-col border-border/60 transition-transform duration-200 hover:-translate-y-1 hover:border-primary/60 hover:shadow-lg">
      <CardHeader className="space-y-4">
        <div className="flex items-start justify-between">
          <span
            className={cn(
              "flex h-12 w-12 items-center justify-center rounded-xl transition-all duration-200 group-hover:scale-105 group-hover:shadow-lg",
              accentClass
            )}
          >
            <Icon className="h-6 w-6" />
          </span>
          <span className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
            {statusLabel}
          </span>
        </div>
        <div className="space-y-1">
          <CardTitle className="line-clamp-1 text-xl">
            {collection.name}
          </CardTitle>
          <CardDescription className="line-clamp-2">
            {collection.description || "Chưa có mô tả cho bộ sưu tập này"}
          </CardDescription>
        </div>
      </CardHeader>
      <CardContent className="flex flex-1 flex-col gap-4 pt-0">
        <div className="flex items-center justify-between rounded-lg border border-border/60 bg-background/80 p-3">
          <div>
            <p className="text-xs text-muted-foreground uppercase tracking-wide">
              Tài sản
            </p>
            <p className="mt-1 text-lg font-semibold text-foreground">
              {assetCount}
            </p>
          </div>
          <Layers className="h-5 w-5 text-muted-foreground" />
        </div>
        <div className="grid grid-cols-2 gap-3 text-xs text-muted-foreground">
          <div className="rounded-md bg-muted/40 px-3 py-2">
            <p className="uppercase tracking-wide">Ngày tạo</p>
            <p className="mt-1 text-foreground">{createdLabel ?? "—"}</p>
          </div>
          <div className="rounded-md bg-muted/40 px-3 py-2">
            <p className="uppercase tracking-wide">Cập nhật</p>
            <p className="mt-1 text-foreground">{updatedLabel ?? "—"}</p>
          </div>
        </div>
      </CardContent>
      <CardFooter className="border-t border-border/60 pt-4">
        <Link href={`/collections/${collection.id}`} className="ml-auto">
          <Button variant="ghost" size="sm" className="gap-2">
            Mở bộ sưu tập
            <ArrowRight className="h-4 w-4" />
          </Button>
        </Link>
      </CardFooter>
    </Card>
  );
}
