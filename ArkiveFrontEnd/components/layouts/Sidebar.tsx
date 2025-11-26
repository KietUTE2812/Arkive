// Server Component - không có "use client"
import Image from "next/image";
import Link from "next/link";
import { HelpCircle } from "lucide-react";
import { ActiveLink } from "./ActiveLink";
import { LogoutButton } from "./LogoutButton";

const menuItems = [
  {
    label: "Tổng quan",
    href: "/dashboard",
    iconName: "LayoutDashboard" as const,
  },
  {
    label: "Tất cả bộ sưu tập",
    href: "/collections",
    iconName: "FolderOpen" as const,
  },
  {
    label: "Tìm kiếm nâng cao",
    href: "/advanced-search",
    iconName: "Search" as const,
  },
  {
    label: "Thùng rác",
    href: "/trash",
    iconName: "Trash2" as const,
  },
  {
    label: "Cài đặt",
    href: "/settings",
    iconName: "Settings" as const,
  },
];

export function Sidebar() {
  return (
    <aside className="fixed left-0 top-0 h-screen w-[250px] bg-secondary-foreground text-white">
      <div className="flex h-full flex-col">
        {/* Logo */}
        <div className="flex h-20 items-center justify-center border-b border-white/10 px-5">
          <Image src="/logo.png" alt="Arkive" width={50} height={50} className="mr-2" />
          <h1 className="text-2xl font-bold italic text-white">Arkive</h1>
        </div>

        {/* Navigation */}
        <nav className="flex-1 space-y-1 px-5 py-6">
          {menuItems.map((item) => (
            <ActiveLink
              key={item.href}
              href={item.href}
              label={item.label}
              iconName={item.iconName}
            />
          ))}
        </nav>

        {/* Footer */}
        <div className="border-t border-white/10 p-5 space-y-3">
          <Link
            href="/help"
            className="flex items-center gap-2 text-sm text-white/60 underline-offset-4 hover:text-white hover:underline"
          >
            <HelpCircle className="h-4 w-4" />
            <span>Trợ giúp</span>
          </Link>
          <LogoutButton />
        </div>
      </div>
    </aside>
  );
}
