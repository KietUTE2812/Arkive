import { Sidebar } from "@/components/layouts/Sidebar";
import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Dashboard - Arkive",
    description: "Quản lý và tổ chức tài sản thương hiệu của bạn",
    icons: {
        icon: "/logo.png",
    },
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <div className="flex min-h-screen">
            <Sidebar />
            <main className="ml-[250px] flex-1 bg-background">
                {children}
            </main>
        </div>
    );
}


