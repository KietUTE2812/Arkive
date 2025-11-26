"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Search, Filter, Grid, List } from "lucide-react";

export function SearchAndFilters() {
  const [searchQuery, setSearchQuery] = useState("");
  
  return (
    <div className="flex gap-4">
      <div className="relative flex-1">
        <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
        <Input
          type="search"
          placeholder="Tìm kiếm bộ sưu tập..."
          className="pl-10"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>
      <Button variant="outline">
        <Filter className="mr-2 h-4 w-4" />
        Lọc
      </Button>
      <div className="flex gap-2">
        <Button variant="outline" size="icon">
          <Grid className="h-4 w-4" />
        </Button>
        <Button variant="outline" size="icon">
          <List className="h-4 w-4" />
        </Button>
      </div>
    </div>
  );
}
