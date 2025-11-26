"use client";

import { useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormField } from "@/components/ui/form";
import { Mail, ArrowLeft, CheckCircle2 } from "lucide-react";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");

    if (!email) {
      setError("Email là bắt buộc");
      setIsLoading(false);
      return;
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      setError("Email không hợp lệ");
      setIsLoading(false);
      return;
    }

    // TODO: Call API để gửi email reset password
    try {
      await new Promise((resolve) => setTimeout(resolve, 1500));
      setIsSuccess(true);
    } catch (error) {
      setError("Có lỗi xảy ra. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <>
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-foreground">Email đã được gửi</h2>
          <p className="mt-2 text-sm text-muted-foreground">Vui lòng kiểm tra hộp thư của bạn</p>
        </div>
        <div className="flex flex-col items-center justify-center space-y-4 py-8">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
            <CheckCircle2 className="h-10 w-10 text-primary" />
          </div>
          <p className="text-center text-muted-foreground">
            Chúng tôi đã gửi link đặt lại mật khẩu đến email{" "}
            <span className="font-semibold text-foreground">{email}</span>
          </p>
          <p className="text-center text-sm text-muted-foreground">
            Vui lòng kiểm tra hộp thư và làm theo hướng dẫn.
          </p>
          <div className="mt-6 w-full space-y-3">
            <Link href="/login" className="block w-full">
              <Button
                variant="outline"
                className="w-full"
              >
                <ArrowLeft className="mr-2 h-4 w-4" />
                Quay lại đăng nhập
              </Button>
            </Link>
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      <div className="mb-8">
        <h2 className="text-3xl font-bold text-foreground">Quên mật khẩu</h2>
        <p className="mt-2 text-sm text-muted-foreground">Nhập email của bạn để nhận link đặt lại mật khẩu</p>
      </div>
      <form onSubmit={handleSubmit} className="space-y-6">
        <FormField label="Email" error={error}>
          <div className="relative">
            <Mail className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
            <Input
              type="email"
              placeholder="name@example.com"
              className="pl-10"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={isLoading}
            />
          </div>
        </FormField>

        <Button type="submit" className="w-full" disabled={isLoading}>
          {isLoading ? "Đang gửi..." : "Gửi link đặt lại mật khẩu"}
        </Button>

        <div className="text-center text-sm">
          <Link
            href="/login"
            className="inline-flex items-center font-medium text-primary hover:text-primary/80"
          >
            <ArrowLeft className="mr-1 h-4 w-4" />
            Quay lại đăng nhập
          </Link>
        </div>
      </form>
    </>
  );
}

