import { Card, CardContent } from "@/components/ui/card";

interface FileItem {
  id: string;
  name: string;
  type: string;
  size: string;
  thumbnail?: string;
}

interface RecentFilesProps {
  files: FileItem[];
}

export function RecentFiles({ files }: RecentFilesProps) {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold text-foreground">Files gần đây</h2>
      <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6">
        {files.map((file) => (
          <Card key={file.id} className="overflow-hidden">
            <div className="relative aspect-video w-full bg-muted">
              {file.thumbnail ? (
                <img
                  src={file.thumbnail}
                  alt={file.name}
                  className="h-full w-full object-cover"
                />
              ) : (
                <div className="flex h-full items-center justify-center text-muted-foreground">
                  {file.type === "Ảnh" && "🖼️"}
                  {file.type === "Video" && "🎥"}
                  {file.type === "Tài liệu" && "📄"}
                  {file.type === "Âm thanh" && "🎵"}
                </div>
              )}
            </div>
            <CardContent className="p-3">
              <p className="truncate text-sm font-semibold text-foreground">
                {file.name}
              </p>
              <p className="text-xs text-muted-foreground">
                {file.type} · {file.size}
              </p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}

