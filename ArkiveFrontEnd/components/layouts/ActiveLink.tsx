"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";
import {
  LayoutDashboard,
  FolderOpen,
  Plus,
  Trash2,
  Settings,
  HelpCircle,
  Search,
} from "lucide-react";

// Map tên icon sang component
const iconMap = {
  LayoutDashboard,
  FolderOpen,
  Search,
  Trash2,
  Settings,
  HelpCircle,
} as const;

type IconName = keyof typeof iconMap;

interface ActiveLinkProps {
  href: string;
  label: string;
  iconName: IconName; // Nhận tên icon thay vì component
}

export function ActiveLink({ href, label, iconName }: ActiveLinkProps) {
  const pathname = usePathname();
  const isActive = pathname === href;
  
  // Lấy icon component từ map
  const Icon = iconMap[iconName];

  return (
    <Link
      href={href}
      className={cn(
        "flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium transition-colors",
        isActive
          ? "bg-primary/20 text-white"
          : "text-white/80 hover:bg-primary/20 hover:text-white"
      )}
    >
      <Icon className="h-5 w-5" />
      <span>{label}</span>
    </Link>
  );
}
