import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import {
  FileText,
  Image as ImageIcon,
  Video,
  Music,
  Archive,
  File as FileIcon,
  type LucideIcon,
} from "lucide-react";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

/**
 * Format bytes to human readable string
 * @param bytes - Number of bytes
 * @returns Formatted string (e.g., "1.5 MB")
 */
export function formatBytes(bytes?: number): string {
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
}

/**
 * Format date string to Vietnamese format
 * @param value - ISO date string
 * @returns Formatted date string (e.g., "14/11/2025") or undefined
 */
export function formatDate(value?: string): string | undefined {
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
}

/**
 * Get asset type icon and label based on file type
 * @param fileType - MIME type of the file
 * @returns Object with icon component and label
 */
export function getAssetTypeInfo(fileType?: string): {
  icon: LucideIcon;
  label: string;
} {
  const type = fileType?.toLowerCase() ?? "";

  if (type.startsWith("image/")) {
    return { icon: ImageIcon, label: "Hình ảnh" };
  }
  if (type.startsWith("video/")) {
    return { icon: Video, label: "Video" };
  }
  if (type.startsWith("audio/")) {
    return { icon: Music, label: "Âm thanh" };
  }
  if (type.includes("pdf")) {
    return { icon: FileText, label: "Tài liệu" };
  }
  if (type.includes("zip") || type.includes("rar")) {
    return { icon: Archive, label: "Lưu trữ" };
  }

  return { icon: FileIcon, label: "Tập tin" };
}

