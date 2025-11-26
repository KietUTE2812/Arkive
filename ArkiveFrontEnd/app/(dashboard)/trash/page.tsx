"use client";

import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Trash2, RotateCcw, X } from "lucide-react";
import { useEffect, useState } from "react";
import { getDeletedAssets, hardDeleteAsset, restoreDeletedAsset } from "@/lib/api/assets";
import { Asset } from "@/types/api";
import Loading from "@/app/loading";
import Image from "next/image";
import { SimpleModal } from "@/components/ui/modal";

export default function TrashPage() {

  const [deletedFiles, setDeletedFiles] = useState<Asset[]>([]);
  const [selectedFileId, setSelectedFileId] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [openModal, setOpenModal] = useState<boolean>(false);
  const [action, setAction] = useState<"restore" | "delete" | "deleteAll">("deleteAll");

  const formatAction = (action: "restore" | "delete" | "deleteAll") => {
    switch (action) {
      case "restore":
        return {
          onYes: () => restoreFile(selectedFileId!),
          title: "Khôi phục file",
          description: "Bạn có chắc chắn muốn khôi phục file này không?",
          yesText: "Khôi phục",
          noText: "Hủy",
        };
      case "delete":
        return {
          onYes: () => deleteFile(selectedFileId!),
          title: "Xóa file",
          description: "Bạn có chắc chắn muốn xóa file này không?",
          yesText: "Xóa",
          noText: "Hủy",
        };
      case "deleteAll":
        return {
          onYes: () => deleteAllFiles(),
          title: "Xóa tất cả files",
          description: "Bạn có chắc chắn muốn xóa tất cả files trong thùng rác không? Hành động này không thể hoàn tác.",
          yesText: "Xóa tất cả",
          noText: "Hủy",
        };
      default:
        return {
          onYes: () => {},
          title: "",
          description: "",
          yesText: "",
          noText: "",
        };
    }
  };

  useEffect(() => {
    const fetchDeletedFiles = async () => {
      setLoading(true);
      const response = await getDeletedAssets();
      setDeletedFiles(response);
      setLoading(false);
    };

    fetchDeletedFiles();
  }, []);

  const restoreFile = async (fileId: string) => {
    try {
      await restoreDeletedAsset(fileId);
      setDeletedFiles((prevFiles) => prevFiles.filter((file) => file.id !== fileId));
    } catch (error) {
      console.error("Failed to restore file:", error);
    }
  };

  const deleteFile = async (fileId: string) => {
    try {
      await hardDeleteAsset(fileId);
      setDeletedFiles((prevFiles) => prevFiles.filter((file) => file.id !== fileId));
    } catch (error) {
      console.error("Failed to delete file:", error);
    }
  };

  const deleteAllFiles = async () => {
    try {
      await Promise.all(deletedFiles.map((file) => hardDeleteAsset(file.id)));
      setDeletedFiles([]);
    } catch (error) {
      console.error("Failed to delete all files:", error);
    }
  };

  return (
    <>
      <SimpleModal
        open={openModal}
        onOpenChange={setOpenModal}
        footer={(
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => {
              setOpenModal(false);
            }}>Hủy</Button>
            <Button variant="destructive" onClick={() => {
              formatAction(action).onYes();
              setOpenModal(false);
            }}>{formatAction(action).yesText}</Button>
          </div>
        )}
        title={formatAction(action).title}
      >
        <p className="mb-4">{formatAction(action).description}</p>
        
      </SimpleModal>
      <div className="container mx-auto space-y-6 p-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-foreground">Thùng rác</h1>
            <p className="mt-2 text-sm text-muted-foreground">
              Files sẽ tự động xóa vĩnh viễn sau 30 ngày
            </p>
          </div>
          <Button variant="destructive" onClick={() => {
            setOpenModal(true);
            setAction("deleteAll");
          }} disabled={deletedFiles.length === 0}>
            <Trash2 className="mr-2 h-4 w-4" />
            Xóa tất cả
          </Button>
        </div>

        
        {loading && (
          <div className="flex flex-col items-center justify-center py-12">
            <Loading />
          </div>
        )}

        <div className="grid grid-cols-2 gap-4 md:grid  -cols-3 lg:grid-cols-4 xl:grid-cols-6">
          {deletedFiles.map((file) => (
            <Card key={file.id} className="overflow-hidden">
              <div className="relative aspect-video w-full bg-muted">
                <div className="flex h-full items-center justify-center text-muted-foreground">
                  <Image
                    src={file.thumbnailUrl || "/placeholder.png"}
                    alt={file.filename}
                    fill
                    style={{ objectFit: "cover" }}
                  />
                </div>
              </div>
              <CardContent className="p-3">
                <p className="truncate text-sm font-semibold">{file.filename}</p>
                <p className="text-xs text-muted-foreground">{file.updatedAt}</p>
                <div className="mt-2 flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1 text-xs"
                    onClick={() => {
                      setSelectedFileId(file.id);
                      setAction("restore");
                      setOpenModal(true);
                    }}
                  >
                    <RotateCcw className="mr-1 h-3 w-3" />
                    Khôi phục
                  </Button>
                  <Button
                    variant="destructive"
                    size="sm"
                    className="flex-1 text-xs"
                    onClick={() => {
                      setSelectedFileId(file.id);
                      setAction("delete");
                      setOpenModal(true);
                    }}
                  >
                    <X className="mr-1 h-3 w-3" />
                    Xóa
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {deletedFiles.length === 0 && !loading && (
          <div className="flex flex-col items-center justify-center py-12">
            <Trash2 className="mb-4 h-12 w-12 text-muted-foreground" />
            <p className="text-muted-foreground">Thùng rác trống</p>
          </div>
        )}
      </div>
    </>
  );
}

