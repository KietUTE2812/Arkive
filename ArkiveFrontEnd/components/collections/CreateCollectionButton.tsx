"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import CollectionForm from "@/components/ui/collectionForm";
import { createCollection } from "@/lib/api/assets";
import { toast } from "react-toastify";
import { useRouter } from "next/navigation";

export function CreateCollectionButton() {
  const [isOpen, setIsOpen] = useState(false);
  const router = useRouter();

  const handleSubmit = async (data: any) => {
    try {
      await createCollection(data.name, data.description);
      toast.success("Collection created successfully");
      setIsOpen(false);
      router.refresh(); // Refresh Server Component data
    } catch (error) {
      console.error("Error creating collection:", error);
      toast.error("Error creating collection");
      setIsOpen(false);
    }
  };

  return (
    <>
      <Button onClick={() => setIsOpen(true)}>
        <span className="mr-2">+</span>
        Thêm mới
      </Button>
      {isOpen && (
        <CollectionForm 
          open={isOpen} 
          setOpen={setIsOpen} 
          onSubmit={handleSubmit} 
        />
      )}
    </>
  );
}
