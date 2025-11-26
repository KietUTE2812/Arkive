"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormField } from "@/components/ui/form";
import {
  Mail,
  Lock,
  User,
  Eye,
  EyeOff,
  CheckCircle2,
  ArrowLeft,
  RefreshCw,
} from "lucide-react";
import { useAuthStore } from "@/store/auth";

type Step = "register" | "verify";

export default function RegisterPage() {
  const router = useRouter();
  const { register, verifyEmail, resendVerificationCode, isLoading, error, clearError } = useAuthStore();
  const [step, setStep] = useState<Step>("register");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [codeError, setCodeError] = useState("");
  const [resendCooldown, setResendCooldown] = useState(0);
  const [formData, setFormData] = useState({
    name: "",
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [errors, setErrors] = useState<{
    name?: string;
    username?: string;
    email?: string;
    password?: string;
    confirmPassword?: string;
  }>({});

  // Resend cooldown timer
  useEffect(() => {
    if (resendCooldown > 0) {
      const timer = setTimeout(() => {
        setResendCooldown(resendCooldown - 1);
      }, 1000);
      return () => clearTimeout(timer);
    }
  }, [resendCooldown]);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    clearError();
    setErrors({});

    // Validation
    const newErrors: {
      name?: string;
      username?: string;
      email?: string;
      password?: string;
      confirmPassword?: string;
    } = {};

    if (!formData.name) {
      newErrors.name = "Họ và tên là bắt buộc";
    } else if (formData.name.length < 2) {
      newErrors.name = "Họ và tên phải có ít nhất 2 ký tự";
    }

    if (!formData.username) {
      newErrors.username = "Tên đăng nhập là bắt buộc";
    } else if (formData.username.length < 3) {
      newErrors.username = "Tên đăng nhập phải có ít nhất 3 ký tự";
    } else if (!/^[a-zA-Z0-9_]+$/.test(formData.username)) {
      newErrors.username = "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới";
    }

    if (!formData.email) {
      newErrors.email = "Email là bắt buộc";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Email không hợp lệ";
    }

    if (!formData.password) {
      newErrors.password = "Mật khẩu là bắt buộc";
    } else if (formData.password.length < 6) {
      newErrors.password = "Mật khẩu phải có ít nhất 6 ký tự";
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Xác nhận mật khẩu là bắt buộc";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Mật khẩu không khớp";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      await register({
        fullName: formData.name,
        username: formData.username,
        email: formData.email,
        password: formData.password,
      });

      // Chuyển sang bước verify
      setRegisteredEmail(formData.email);
      setStep("verify");
      setResendCooldown(60); // 60 giây cooldown
    } catch (err) {
      // Error is handled by Zustand store
      if (error) {
        setErrors({ email: error });
      }
    }
  };

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    clearError();
    setCodeError("");

    if (!verificationCode) {
      setCodeError("Mã xác thực là bắt buộc");
      return;
    }

    if (verificationCode.length < 6) {
      setCodeError("Mã xác thực phải có 6 ký tự");
      return;
    }

    try {
      await verifyEmail(verificationCode);

      // Redirect to login after successful verification
      router.push("/login?verified=true");
    } catch (err) {
      // Error is handled by Zustand store
      if (error) {
        setCodeError(error);
      }
    }
  };

  const handleResendCode = async () => {
    if (resendCooldown > 0) return;

    clearError();
    try {
      await resendVerificationCode(registeredEmail);
      setResendCooldown(60); // Reset cooldown
      setCodeError("");
    } catch (err) {
      if (error) {
        setCodeError(error);
      }
    }
  };

  // Register Form
  if (step === "register") {
    return (
      <>
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-foreground">Tạo tài khoản mới</h2>
          <p className="mt-2 text-sm text-muted-foreground">Đăng ký để bắt đầu sử dụng Arkive</p>
        </div>
        <form onSubmit={handleRegister} className="space-y-6">
          <FormField label="Họ và tên" error={errors.name}>
            <div className="relative">
              <User className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="text"
                placeholder="Nguyễn Văn A"
                className="pl-10"
                value={formData.name}
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
                disabled={isLoading}
              />
            </div>
          </FormField>

          <FormField label="Tên đăng nhập" error={errors.username}>
            <div className="relative">
              <User className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="text"
                placeholder="username"
                className="pl-10"
                value={formData.username}
                onChange={(e) =>
                  setFormData({ ...formData, username: e.target.value })
                }
                disabled={isLoading}
              />
            </div>
          </FormField>

          <FormField label="Email" error={errors.email}>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="email"
                placeholder="name@example.com"
                className="pl-10"
                value={formData.email}
                onChange={(e) =>
                  setFormData({ ...formData, email: e.target.value })
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
                placeholder="Tối thiểu 6 ký tự"
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

          <FormField label="Xác nhận mật khẩu" error={errors.confirmPassword}>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-muted-foreground" />
              <Input
                type={showConfirmPassword ? "text" : "password"}
                placeholder="Nhập lại mật khẩu"
                className="pl-10 pr-10"
                value={formData.confirmPassword}
                onChange={(e) =>
                  setFormData({ ...formData, confirmPassword: e.target.value })
                }
                disabled={isLoading}
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                {showConfirmPassword ? (
                  <EyeOff className="h-5 w-5" />
                ) : (
                  <Eye className="h-5 w-5" />
                )}
              </button>
            </div>
          </FormField>

          <div className="flex items-start">
            <div className="flex items-center h-5">
              <input
                id="terms"
                name="terms"
                type="checkbox"
                className="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
                required
              />
            </div>
            <div className="ml-3 text-sm">
              <label htmlFor="terms" className="text-muted-foreground">
                Tôi đồng ý với{" "}
                <Link
                  href="/terms"
                  className="font-medium text-primary hover:text-primary/80"
                >
                  Điều khoản sử dụng
                </Link>{" "}
                và{" "}
                <Link
                  href="/privacy"
                  className="font-medium text-primary hover:text-primary/80"
                >
                  Chính sách bảo mật
                </Link>
              </label>
            </div>
          </div>

          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? "Đang tạo tài khoản..." : "Đăng ký"}
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

          <div className="text-center text-sm">
            <span className="text-muted-foreground">Đã có tài khoản? </span>
            <Link
              href="/login"
              className="font-medium text-primary hover:text-primary/80"
            >
              Đăng nhập ngay
            </Link>
          </div>
        </form>
      </>
    );
  }

  // Verification Form
  return (
    <>
      <div className="mb-8">
        <h2 className="text-3xl font-bold text-foreground">Xác thực email</h2>
        <p className="mt-2 text-sm text-muted-foreground">Chúng tôi đã gửi mã xác thực đến {registeredEmail}</p>
      </div>
      <form onSubmit={handleVerify} className="space-y-6">
        <div className="flex flex-col items-center justify-center space-y-4 py-4">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
            <Mail className="h-8 w-8 text-primary" />
          </div>
          <p className="text-center text-sm text-muted-foreground">
            Vui lòng kiểm tra hộp thư của bạn và nhập mã xác thực 6 ký tự
          </p>
        </div>

        <FormField label="Mã xác thực" error={codeError}>
          <Input
            type="text"
            placeholder="Nhập mã xác thực"
            className="text-center text-2xl tracking-widest font-mono"
            maxLength={6}
            value={verificationCode}
            onChange={(e) => {
              const value = e.target.value.replace(/\D/g, ""); // Chỉ cho phép số
              setVerificationCode(value);
              setCodeError("");
            }}
            disabled={isLoading}
          />
        </FormField>

        <Button type="submit" className="w-full" disabled={isLoading || verificationCode.length !== 6}>
          {isLoading ? "Đang xác thực..." : "Xác thực"}
        </Button>

        {error && (
          <div className="rounded-lg border border-destructive bg-destructive/10 p-3 text-sm text-destructive">
            {error}
          </div>
        )}

        <div className="space-y-3">
          <p className="text-center text-sm text-muted-foreground">
            Không nhận được mã?
          </p>
          <Button
            type="button"
            variant="outline"
            className="w-full"
            onClick={handleResendCode}
            disabled={resendCooldown > 0 || isLoading}
          >
            <RefreshCw className={`mr-2 h-4 w-4 ${resendCooldown > 0 ? "animate-spin" : ""}`} />
            {resendCooldown > 0
              ? `Gửi lại sau ${resendCooldown}s`
              : "Gửi lại mã xác thực"}
          </Button>
        </div>

        <div className="text-center text-sm">
          <button
            type="button"
            onClick={() => {
              setStep("register");
              setVerificationCode("");
              setCodeError("");
              clearError();
            }}
            className="inline-flex items-center font-medium text-primary hover:text-primary/80"
          >
            <ArrowLeft className="mr-1 h-4 w-4" />
            Quay lại đăng ký
          </button>
        </div>
      </form>
    </>
  );
}
