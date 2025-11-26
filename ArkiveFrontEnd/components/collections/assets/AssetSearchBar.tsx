"use client";

import { useState, useEffect, useRef, useCallback } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Search, X, ArrowUpDown, ArrowDownAZ, ArrowDownZA, ArrowUpAZ } from "lucide-react";

interface AssetSearchBarProps {
  onSearch: (params: {
    keyword?: string;
    sortBy?: string;
    sortOrder?: string;
  }) => void;
  initialKeyword?: string;
  initialSortBy?: string;
  initialSortOrder?: string;
}

export function AssetSearchBar({ 
  onSearch,
  initialKeyword = "",
  initialSortBy = "createdAt",
  initialSortOrder = "asc",
}: AssetSearchBarProps) {
  const [keyword, setKeyword] = useState(initialKeyword);
  const [sortBy, setSortBy] = useState(initialSortBy);
  const [sortOrder, setSortOrder] = useState(initialSortOrder);

  // Debounce search keyword
  useEffect(() => {
    const timer = setTimeout(() => {
      onSearch({
        keyword: keyword.trim() || undefined,
        sortBy,
        sortOrder,
      });
    }, 1000); // 500ms debounce

    return () => clearTimeout(timer);
  }, [keyword, sortBy, sortOrder]); // Bỏ onSearch khỏi dependency

  const handleClearFilters = useCallback(() => {
    setKeyword("");
    setSortBy("createdAt");
    setSortOrder("asc");
  }, []);

  const hasActiveFilters = keyword || sortBy !== "createdAt" || sortOrder !== "asc";

  const toggleSortOrder = useCallback(() => {
    setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
  }, []);

  return (
    <div className="flex flex-col sm:flex-row gap-3 items-start sm:items-center">
      {/* Search Input */}
      <div className="relative flex-1 w-full sm:w-auto">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          placeholder="Tìm kiếm theo tên file..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className="pl-9 pr-9"
        />
        {keyword && (
          <button
            onClick={() => setKeyword("")}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {/* Sort By */}
      <Select value={sortBy} onValueChange={(value) => setSortBy(value)}>
        <SelectTrigger className="w-full sm:w-[180px]">
          <SelectValue placeholder="Sắp xếp theo" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="createdAt">Ngày tạo</SelectItem>
          <SelectItem value="updatedAt">Ngày cập nhật</SelectItem>
          <SelectItem value="filename">Tên file</SelectItem>
          <SelectItem value="fileSize">Kích thước</SelectItem>
          <SelectItem value="fileType">Loại file</SelectItem>
        </SelectContent>
      </Select>

      {/* Sort Order Toggle */}
      <Button
        variant="outline"
        size="icon"
        onClick={toggleSortOrder}
        title={sortOrder === "asc" ? "Tăng dần" : "Giảm dần"}
      >
        {sortOrder === "asc" ? (
          <ArrowDownAZ className="h-4 w-4" />
        ) : (
          <ArrowUpAZ className="h-4 w-4" />
        )}
        <span className="sr-only">
          {sortOrder === "asc" ? "Tăng dần" : "Giảm dần"}
        </span>
      </Button>

      {/* Clear Filters */}
      {hasActiveFilters && (
        <Button
          variant="ghost"
          size="sm"
          onClick={handleClearFilters}
          className="gap-2"
        >
          <X className="h-4 w-4" />
          Xóa bộ lọc
        </Button>
      )}
    </div>
  );
}