"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormField } from "@/components/ui/form";
import { Mail, Lock, Eye, EyeOff, CheckCircle2 } from "lucide-react";
import { useAuthStore } from "@/store/auth";
import { GoogleLogin, CredentialResponse } from "@react-oauth/google";
import { loginWithGoogle } from "@/lib/api/auth";

export default function LoginPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login, isLoading, error, clearError, getUserInfo } = useAuthStore();
  const [showVerifiedMessage, setShowVerifiedMessage] = useState(false);

  useEffect(() => {
    if (searchParams.get("verified") === "true") {
      setShowVerifiedMessage(true);
      // Clear the query param
      router.replace("/login", { scroll: false });
      // Hide message after 5 seconds
      setTimeout(() => setShowVerifiedMessage(false), 5000);
    }
  }, [searchParams, router]);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const [errors, setErrors] = useState<{
    username?: string;
    password?: string;
  }>({});

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    clearError();
    setErrors({});

    // Validation
    const newErrors: { username?: string; password?: string } = {};
    if (!formData.username) {
      newErrors.username = "Tên đăng nhập là bắt buộc";
    } else if (formData.username.length < 3) {
      newErrors.username = "Tên đăng nhập phải có ít nhất 3 ký tự";
    }
    if (!formData.password) {
      newErrors.password = "Mật khẩu là bắt buộc";
    } else if (formData.password.length < 6) {
      newErrors.password = "Mật khẩu phải có ít nhất 6 ký tự";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      await login({
        username: formData.username,
        password: formData.password,
      });
      await getUserInfo();
      // Redirect to dashboard on success
      router.push("/dashboard");
    } catch (err) {
      // Error is handled by Zustand store
      if (error) {
        setErrors({ username: error });
      }
    }
  };

  const handleGoogleLogin = async (response: CredentialResponse) => {
    const googleIdToken = response.credential;
    if (!googleIdToken) {
      setErrors({ username: 'Không nhận được thông tin đăng nhập từ Google.' });
      return;
    }

    setErrors({});

    try {
      // Gửi ID Token lên backend
      await loginWithGoogle(googleIdToken);
      await getUserInfo();
      router.push('/dashboard');
    } catch (err) {
      setErrors({ username: err as string });
    }
  };

  return (
    <>
      <div className="mb-8">
        <h2 className="text-3xl font-bold text-foreground">Đăng nhập</h2>
        <p className="mt-2 text-sm text-muted-foreground">Đăng nhập vào tài khoản của bạn</p>
      </div>
      {showVerifiedMessage && (
        <div className="mb-6 rounded-lg border border-primary bg-primary/10 p-4 text-sm text-primary">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="h-5 w-5" />
            <span>Email đã được xác thực thành công! Bạn có thể đăng nhập ngay.</span>
          </div>
        </div>
      )}
      <form onSubmit={handleSubmit} className="space-y-6">
        <FormField label="Username" error={errors.username}>
          <div className="relative">
            <Mail className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Tên đăng nhập"
              className="pl-10"
              value={formData.username}
              onChange={(e) =>
                setFormData({ ...formData, username: e.target.value })
              }
              disabled={isLoading}
            />
          </div>
        </FormField>

        <FormField label="Mật khẩu" error={errors.password}>
          <div className="relative">
            <Lock className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
            <Input
              type={showPassword ? "text" : "password"}
              placeholder="Nhập mật khẩu"
              className="pl-10 pr-10"
              value={formData.password}
              onChange={(e) =>
                setFormData({ ...formData, password: e.target.value })
              }
              disabled={isLoading}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
            >
              {showPassword ? (
                <EyeOff className="h-5 w-5" />
              ) : (
                <Eye className="h-5 w-5" />
              )}
            </button>
          </div>
        </FormField>

        <div className="flex items-center justify-between">
          <div className="flex items-center">
            <input
              id="remember-me"
              name="remember-me"
              type="checkbox"
              className="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
            />
            <label
              htmlFor="remember-me"
              className="ml-2 block text-sm text-muted-foreground"
            >
              Ghi nhớ đăng nhập
            </label>
          </div>

          <div className="text-sm">
            <Link
              href="/forgot-password"
              className="font-medium text-primary hover:text-primary/80"
            >
              Quên mật khẩu?
            </Link>
          </div>
        </div>

        <Button type="submit" className="w-full" disabled={isLoading}>
          {isLoading ? "Đang đăng nhập..." : "Đăng nhập"}
        </Button>

        {error && (
          <div className="rounded-lg border border-destructive bg-destructive/10 p-3 text-sm text-destructive">
            {error}
          </div>
        )}

        <div className="relative">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-border" />
          </div>
          <div className="relative flex justify-center text-sm">
            <span className="bg-background px-2 text-muted-foreground">
              Hoặc
            </span>
          </div>
        </div>

        <GoogleLogin
          onSuccess={handleGoogleLogin}
          onError={() => {
            setErrors({ username: 'Đăng nhập Google thất bại.' });
          }}
        />

        <div className="text-center text-sm">
          <span className="text-muted-foreground">Chưa có tài khoản? </span>
          <Link
            href="/register"
            className="font-medium text-primary hover:text-primary/80"
          >
            Đăng ký ngay
          </Link>
        </div>
      </form>
    </>
  );
}

