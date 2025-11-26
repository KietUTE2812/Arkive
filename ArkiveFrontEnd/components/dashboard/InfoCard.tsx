import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import { FileText, HardDrive, Share2, Tag } from "lucide-react";

// Map tên icon sang component
const iconMap = {
  FileText,
  HardDrive,
  Share2,
  Tag,
} as const;

type IconName = keyof typeof iconMap;

interface InfoCardProps {
  title: string;
  value: string;
  iconName: IconName; // Đổi từ icon sang iconName
  iconColor?: string;
  chart?: React.ReactNode;
}

export function InfoCard({
  title,
  value,
  iconName,
  iconColor = "text-primary",
  chart,
}: InfoCardProps) {
  const Icon = iconMap[iconName];
  
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        <Icon className={cn("h-8 w-8", iconColor)} />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{value}</div>
        {chart && <div className="mt-4 h-[50px]">{chart}</div>}
      </CardContent>
    </Card>
  );
}

