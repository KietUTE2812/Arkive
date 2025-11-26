import { Collection } from "@/types/api";
import { CollectionCard } from "./CollectionCard";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Sparkles } from "lucide-react";
import { CreateCollectionButton } from "./CreateCollectionButton";

interface CollectionGridProps {
  collections: Collection[];
}

export function CollectionGrid({ collections }: CollectionGridProps) {
  if (collections.length === 0) {
    return (
      <Card className="col-span-full flex flex-col items-center justify-center gap-4 border-dashed border-border/60 bg-muted/30 py-12 text-center">
        <Sparkles className="h-10 w-10 text-muted-foreground" />
        <div className="space-y-2">
          <h2 className="text-xl font-semibold text-foreground">
            Chưa có bộ sưu tập nào
          </h2>
          <p className="text-sm text-muted-foreground">
            Bắt đầu bằng cách tạo bộ sưu tập để sắp xếp tài sản của bạn.
          </p>
        </div>
        <CreateCollectionButton />
      </Card>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      {collections.map((collection, index) => (
        <CollectionCard 
          key={collection.id} 
          collection={collection} 
          index={index}
        />
      ))}
    </div>
  );
}
