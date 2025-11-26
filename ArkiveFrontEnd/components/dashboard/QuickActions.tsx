import { Card, CardContent } from "@/components/ui/card";
import { Upload, FolderPlus, Share2, Download } from "lucide-react";

const actions = [
  {
    label: "Tải file lên",
    icon: Upload,
  },
  {
    label: "Tạo thư mục mới",
    icon: FolderPlus,
  },
  {
    label: "Chia sẻ tài sản",
    icon: Share2,
  },
  {
    label: "Tải xuống nhiều",
    icon: Download,
  },
];

export function QuickActions() {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold text-foreground">Phím tắt hành động</h2>
      <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
        {actions.map((action) => {
          const Icon = action.icon;
          return (
            <Card
              key={action.label}
              className="cursor-pointer transition-colors hover:bg-accent"
            >
              <CardContent className="flex flex-col items-center justify-center gap-3 p-6">
                <div className="rounded-full bg-primary/10 p-4">
                  <Icon className="h-6 w-6 text-primary" />
                </div>
                <p className="text-sm font-semibold">{action.label}</p>
              </CardContent>
            </Card>
          );
        })}
      </div>
    </div>
  );
}

