import type { Metadata } from "next";
import { AuthLayout } from "@/components/layouts/AuthLayout";
import { AuthProvider } from "@/components/providers/AuthProvider";

export const metadata: Metadata = {
    title: "Arkive - Quản lý Tài sản Kỹ thuật số",
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
        <>
            <AuthProvider>
                <AuthLayout>
                    {children}
                </AuthLayout>
            </AuthProvider>
        </>
    );
}


