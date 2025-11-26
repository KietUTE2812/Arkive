"use client";

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormField } from "@/components/ui/form";
import { User, Bell, Shield, CreditCard } from "lucide-react";

export default function SettingsPage() {
  return (
    <>
      <div className="container mx-auto space-y-6 p-8">
        <h1 className="text-3xl font-bold text-foreground">Cài đặt</h1>

        <div className="grid gap-6 md:grid-cols-2">
          {/* Profile Settings */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <User className="h-5 w-5" />
                <CardTitle>Thông tin cá nhân</CardTitle>
              </div>
              <CardDescription>
                Cập nhật thông tin tài khoản của bạn
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <FormField label="Họ và tên">
                <Input defaultValue="Nguyễn Văn A" />
              </FormField>
              <FormField label="Email">
                <Input type="email" defaultValue="user@example.com" />
              </FormField>
              <Button>Cập nhật</Button>
            </CardContent>
          </Card>

          {/* Notifications */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <Bell className="h-5 w-5" />
                <CardTitle>Thông báo</CardTitle>
              </div>
              <CardDescription>
                Quản lý cài đặt thông báo
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <label className="text-sm font-medium">
                  Email thông báo
                </label>
                <input type="checkbox" defaultChecked className="h-4 w-4 rounded" />
              </div>
              <div className="flex items-center justify-between">
                <label className="text-sm font-medium">
                  Thông báo push
                </label>
                <input type="checkbox" className="h-4 w-4 rounded" />
              </div>
              <Button variant="outline">Lưu cài đặt</Button>
            </CardContent>
          </Card>

          {/* Security */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <Shield className="h-5 w-5" />
                <CardTitle>Bảo mật</CardTitle>
              </div>
              <CardDescription>
                Quản lý mật khẩu và bảo mật
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <FormField label="Mật khẩu hiện tại">
                <Input type="password" />
              </FormField>
              <FormField label="Mật khẩu mới">
                <Input type="password" />
              </FormField>
              <FormField label="Xác nhận mật khẩu mới">
                <Input type="password" />
              </FormField>
              <Button>Đổi mật khẩu</Button>
            </CardContent>
          </Card>

          {/* Billing */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <CreditCard className="h-5 w-5" />
                <CardTitle>Gói dịch vụ</CardTitle>
              </div>
              <CardDescription>
                Quản lý gói đăng ký của bạn
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="rounded-lg border p-4">
                <p className="font-semibold">Gói Free</p>
                <p className="text-sm text-muted-foreground">
                  9.8 GB / 15 GB đã sử dụng
                </p>
                <div className="mt-2 h-2 w-full overflow-hidden rounded-full bg-muted">
                  <div
                    className="h-full bg-primary"
                    style={{ width: "65%" }}
                  />
                </div>
              </div>
              <Button variant="outline" className="w-full">
                Nâng cấp gói
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </>
  );
}

