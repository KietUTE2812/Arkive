"use client";

import { useAuthStore } from "@/store/auth";

export function UserGreeting() {
  const { user } = useAuthStore();
  
  return (
    <h1 className="text-4xl font-bold text-foreground">
      Chào mừng trở lại, {user?.fullName || "Arkive's User"}!
    </h1>
  );
}
