"use client";

import { useEffect, useMemo, useState } from "react";
import {
  Modal,
  ModalContent,
  ModalDescription,
  ModalFooter,
  ModalHeader,
  ModalTitle,
} from "@/components/ui/modal";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { FormField } from "@/components/ui/form";
import { cn } from "@/lib/utils";
import { Asset } from "@/types/api";
import { uploadAsset } from "@/lib/api/assets";
import { toast } from "react-toastify";
import { File as FileIcon, Loader2, Upload, X } from "lucide-react";
import { generateThumbnail } from "@/utils/generateThumbnail";
import Image from "next/image";

const formatBytes = (bytes?: number) => {
  if (!bytes || bytes <= 0) {
    return "0 B";
  }
  const units = ["B", "KB", "MB", "GB", "TB"] as const;
  const index = Math.min(
    Math.floor(Math.log(bytes) / Math.log(1024)),
    units.length - 1
  );
  const value = bytes / Math.pow(1024, index);
  return `${value.toFixed(value >= 10 || index === 0 ? 0 : 1)} ${units[index]}`;
};

interface AddAssetModalProps {
  open: boolean;
  collectionId: string;
  onOpenChange: (open: boolean) => void;
  onAssetCreated?: (asset: Asset) => void;
}

export function AddAssetModal({
  open,
  collectionId,
  onOpenChange,
  onAssetCreated,
}: AddAssetModalProps) {
  const [file, setFile] = useState<File | null>(null);
  const [displayName, setDisplayName] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [progress, setProgress] = useState(0);
  const [tags, setTags] = useState<string[]>([]);
  const [tagInput, setTagInput] = useState("");
  const [thumbnail, setThumbnail] = useState<Blob | null>(null);
  useEffect(() => {
    if (!open) {
      setFile(null);
      setDisplayName("");
      setTags([]);
      setTagInput("");
      setProgress(0);
      setIsSubmitting(false);
      setThumbnail(null);
    }
  }, [open]);

  const fileExtension = useMemo(() => {
    if (!file) {
      return "";
    }
    const parts = file.name.split(".");
    return parts.length > 1 ? parts.pop() ?? "" : "";
  }, [file]);

  const effectiveFilename = useMemo(() => {
    if (!file) {
      return "";
    }
    if (!displayName.trim()) {
      return file.name;
    }
    if (!fileExtension) {
      return displayName.trim();
    }
    return `${displayName.trim()}.${fileExtension}`;
  }, [displayName, file, fileExtension]);

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const uploadedFile = event.target.files?.[0];
    if (!uploadedFile) {
      return;
    }
    setFile(uploadedFile);
    setThumbnail(await generateThumbnail(uploadedFile));
    const nameWithoutExtension = uploadedFile.name.replace(/\.[^/.]+$/, "");
    setDisplayName(nameWithoutExtension);
  };

  const handleSubmit = async () => {
    if (!collectionId) {
      toast.error("Không xác định được bộ sưu tập");
      return;
    }

    if (!file) {
      toast.error("Vui lòng chọn một file để tải lên");
      return;
    }

    try {
      setIsSubmitting(true);
      setProgress(0);

      const fileToUpload =
        effectiveFilename && effectiveFilename !== file.name
          ? new File([file], effectiveFilename, { type: file.type })
          : file;
      const asset = await uploadAsset(
        fileToUpload,
        thumbnail ?? new Blob([], { type: "image/jpeg" }),
        collectionId,
        tags,
        (value) => setProgress(value)
      );

      toast.success("Tải asset thành công");
      onAssetCreated?.(asset);
      onOpenChange(false);
    } catch (error: unknown) {
      console.error("Upload asset failed", error);
      toast.error("Không thể tải asset. Vui lòng thử lại.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleOpenChange = (value: boolean) => {
    if (!value) {
      setTimeout(() => {
        setFile(null);
        setDisplayName("");
        setTags([]);
        setTagInput("");
        setProgress(0);
        setIsSubmitting(false);
        setThumbnail(null);
      }, 200);
    }
    onOpenChange(value);
  };

  const addTag = () => {
    const value = tagInput.trim();
    if (!value) return;
    if (tags.includes(value)) {
      setTagInput("");
      return;
    }
    setTags((prev) => [...prev, value]);
    setTagInput("");
  };

  const removeTag = (value: string) => {
    setTags((prev) => prev.filter((t) => t !== value));
  };

  const handleTagKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      event.preventDefault();
      if (!isSubmitting) addTag();
    }
    if (event.key === ",") {
      // allow comma as a separator as well
      event.preventDefault();
      if (!isSubmitting) addTag();
    }
    if (event.key === "Backspace" && !tagInput && tags.length > 0) {
      // convenience: backspace with empty input removes last tag
      removeTag(tags[tags.length - 1]);
    }
  };

  return (
    <Modal open={open} onOpenChange={handleOpenChange}>
      <ModalContent size="lg">
        <ModalHeader>
          <ModalTitle>Thêm asset mới</ModalTitle>
          <ModalDescription>
            Chọn file của bạn, sau đó chúng tôi sẽ tải lên và gắn vào bộ sưu tập.
          </ModalDescription>
        </ModalHeader>
        <div className="space-y-6">
          <FormField label="File">
            <label
              className={cn(
                "flex cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed bg-muted/40 p-8 text-center transition-colors",
                file
                  ? "border-primary/60 bg-primary/5"
                  : "border-muted-foreground/25 hover:border-primary/60"
              )}
            >
              <Input
                type="file"
                accept="*/*"
                className="hidden"
                onChange={handleFileChange}
                disabled={isSubmitting}
              />
              <Upload className="mb-4 h-10 w-10 text-muted-foreground" />
              <p className="text-sm font-medium text-foreground">
                Kéo thả hoặc bấm để chọn file
              </p>
              <p className="text-xs text-muted-foreground">
                Hỗ trợ mọi định dạng, dung lượng tối đa phụ thuộc cấu hình hệ thống
              </p>
            </label>
          </FormField>
          {file && thumbnail && (
            <div className="rounded-lg border border-border/60 bg-muted/30 p-4">
              <div className="flex items-center gap-3">
                <div className="flex h-20 w-20 items-center justify-center rounded-md bg-background text-muted-foreground overflow-hidden">
                  <Image src={URL.createObjectURL(thumbnail)} alt="Thumbnail" width={100} height={100} />
                </div>
                <div className="space-y-1 text-left">
                  <p className="text-sm font-medium text-foreground">
                    {effectiveFilename}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {file.type || "Không xác định"} • {formatBytes(file.size)}
                  </p>
                </div>
              </div>
            </div>
          )}

          <FormField label="Tên hiển thị">
            <Input
              placeholder="Ví dụ: Banner chiến dịch tháng 11"
              value={displayName}
              onChange={(event) => setDisplayName(event.target.value)}
              disabled={isSubmitting || !file}
            />
          </FormField>

          <FormField label="Thẻ (tags)">
            <div className="space-y-2">
              <div className="flex flex-wrap gap-2">
                {tags.map((tag) => (
                  <Button
                    key={tag}
                    type="button"
                    variant="secondary"
                    size="sm"
                    className="h-6 gap-1 rounded-full px-2 py-0 text-xs"
                    onClick={() => removeTag(tag)}
                    disabled={isSubmitting}
                    aria-label={`Xoá thẻ ${tag}`}
                  >
                    {tag}
                    <X className="h-3 w-3" />
                  </Button>
                ))}
              </div>
              <Input
                placeholder="Nhập thẻ và nhấn Enter"
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyDown={handleTagKeyDown}
                disabled={isSubmitting}
              />
            </div>
          </FormField>

          {isSubmitting && (
            <div className="rounded-lg border border-border/60 bg-muted/30 p-4">
              <div className="flex items-center gap-2 text-sm font-medium text-foreground">
                <Loader2 className="h-4 w-4 animate-spin text-primary" />
                Đang tải lên asset...
              </div>
              <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-muted">
                <div
                  className="h-full rounded-full bg-primary transition-all"
                  style={{ width: `${Math.min(progress, 100)}%` }}
                />
              </div>
              <p className="mt-2 text-xs text-muted-foreground">
                {progress >= 100
                  ? "Hoàn tất!"
                  : `Đang xử lý... ${progress}%`}
              </p>
            </div>
          )}
        </div>
        <ModalFooter>
          <Button
            type="button"
            variant="outline"
            onClick={() => handleOpenChange(false)}
            disabled={isSubmitting}
          >
            Huỷ
          </Button>
          <Button
            type="button"
            onClick={handleSubmit}
            disabled={!file || isSubmitting}
            className="gap-2"
          >
            {isSubmitting && <Loader2 className="h-4 w-4 animate-spin" />}
            Tải asset
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
}
