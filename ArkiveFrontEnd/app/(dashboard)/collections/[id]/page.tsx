'use client';
import { use } from "react";
import { Button } from "@/components/ui/button";
import { ArrowLeft } from "lucide-react";
import { useRouter } from "next/navigation";
import { CollectionDetailClient } from "./components/collection-detail-client";

interface CollectionDetailPageProps {
  params: Promise<{
    id: string;
  }>;
  searchParams: Promise<{
    keyword?: string;
    sortBy?: string;
    sortOrder?: string;
    page?: string;
  }>;
}

export default function CollectionDetailPage({
  params,
  searchParams,
}: CollectionDetailPageProps) {
  const { id } = use(params);
  const search = use(searchParams);
  const router = useRouter();

  return (
    <>
      <div className="container mx-auto space-y-6 p-8">
        <div className="flex items-center gap-3">
          <Button
            type="button"
            variant="ghost"
            size="sm"
            className="gap-2"
            onClick={() => router.push('/collections')}
          >
            <ArrowLeft className="h-4 w-4" />
            Quay lại
          </Button>
          <span className="text-sm text-muted-foreground">/ Bộ sưu tập</span>
        </div>
        <CollectionDetailClient collectionId={id} initialSearchParams={search} />
      </div>
    </>
  );
}

