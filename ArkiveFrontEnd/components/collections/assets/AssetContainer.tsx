import { ReactNode } from "react";
import { Asset } from "@/types/api";
import { formatBytes } from "@/lib/utils";
import { Sparkles } from "lucide-react";
import { Card, CardDescription, CardTitle } from "@/components/ui/card";
import { AssetCard } from "./AssetCard";

interface AssetContainerProps {
  assets: Asset[];
  paginationSlot?: ReactNode;
  // Actions vẫn giữ lại vì nó gắn liền với từng item trong list
  onDownload: (asset: Asset) => void;
  onDelete: (asset: Asset) => void;
  onClick: (asset: Asset) => void;
}

const AssetContainer = ({
  assets,
  paginationSlot, // Nhận pagination đã render từ cha
  onDownload,
  onDelete,
  onClick,
}: AssetContainerProps) => {
  const assetCount = assets.length;
  // Logic tính toán vẫn giữ nguyên
  const totalSize = assets.reduce((sum, asset) => sum + (asset.fileSize || 0), 0);

  return (
    <section className="space-y-4">
      <div className="flex flex-col sm:flex-row justify-between gap-4">
        <div className="space-y-1">
          <h2 className="text-xl font-semibold">Assets ({assetCount})</h2>
           <p className="text-sm text-muted-foreground">
             Tổng dung lượng: {formatBytes(totalSize)}
           </p>
        </div>
      </div>

      {assets.length === 0 ? (
        <Card className="...">
          {/* Empty State Logic */}
           <CardTitle>
              Không tìm thấy...
           </CardTitle>
        </Card>
      ) : (
        <>
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {assets.map((asset, index) => (
              <AssetCard
                key={asset.id}
                asset={asset}
                index={index}
                onDownload={() => onDownload(asset)}
                onDelete={() => onDelete(asset)}
                onClick={() => onClick(asset)}
              />
            ))}
          </div>

          {/* Render Slot: Pagination */}
          <div className="flex justify-center pt-6">
            {paginationSlot}
          </div>
        </>
      )}
    </section>
  );
};

export default AssetContainer;