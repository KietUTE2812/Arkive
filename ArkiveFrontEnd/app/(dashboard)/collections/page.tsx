'use client';
import { getCollections } from "@/lib/api/assets";
import { CollectionGrid } from "@/components/collections/CollectionGrid";
import { SearchAndFilters } from "@/components/collections/SearchAndFilters";
import { CreateCollectionButton } from "@/components/collections/CreateCollectionButton";
import { Collection } from "@/types/api";
import { useState, useEffect } from "react";
import AppLoader from "@/components/ui/loader";

export default function CollectionsPage() {
  const [collections, setCollections] = useState<Collection[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    async function fetchCollections() {
      try {
        const data = await getCollections();
        setCollections(data);
        setIsLoading(false);
      } catch (error) {
        console.error("Error fetching collections:", error);
        setIsLoading(false);
      }
    }
    fetchCollections();
  }, []);

  return (
    <>
      <div className="container mx-auto space-y-6 p-8">
        {/* Header */}
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold text-foreground">
            Tất cả bộ sưu tập
          </h1>
          <CreateCollectionButton />
        </div>

        {/* Search and Filters */}
        <SearchAndFilters />

        {/* Collections Grid HOẶC Loader */}
        {isLoading ? (
          <div className="flex w-full items-center justify-center pt-20">
            <AppLoader />
          </div>
        ) : (
          <CollectionGrid collections={collections} />
        )}
      </div>
      {/* ...và chuyển vào bên trong div.container */}
    </>
  );
}