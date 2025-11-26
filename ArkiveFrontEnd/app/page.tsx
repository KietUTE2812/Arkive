"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  ArrowRight,
  Check,
  FileText,
  Share2,
  Shield,
  Zap,
  Users,
  FolderOpen,
} from "lucide-react";
import Image from "next/image";
import { useRouter } from "next/navigation";

export default function LandingPage() {
  const router = useRouter();
  return (
    <div className="flex min-h-screen flex-col">
      {/* Header */}
      <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-2"
            onClick={() => router.push('/')}
          >
            <Image src="/logo.png" alt="Arkive" width={50} height={50} />
            <h1 className="text-2xl font-bold italic text-primary">Arkive</h1>
          </div>
          <nav className="hidden items-center gap-6 md:flex">
            <Link
              href="#features"
              className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              Tính năng
            </Link>
            <Link
              href="#pricing"
              className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              Bảng giá
            </Link>
            <Link
              href="#about"
              className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              Về chúng tôi
            </Link>
          </nav>
          <div className="flex items-center gap-4">
            <Button variant="ghost" asChild>
              <Link href="/login">Đăng nhập</Link>
            </Button>
            <Button asChild>
              <Link href="/register">Bắt đầu miễn phí</Link>
            </Button>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="flex flex-col items-center justify-center px-4 py-20 text-center bg-secondary-foreground/80">
        <div className="mb-6 inline-flex items-center rounded-full border bg-muted px-4 py-1 text-sm">
          <Zap className="mr-2 h-4 w-4 text-primary" />
          <span>Quản lý tài sản kỹ thuật số thông minh</span>
        </div>
        <h1 className="mb-6 text-5xl font-bold tracking-tight sm:text-6xl lg:text-7xl text-white">
          Quản lý tài sản thương hiệu
          <br />
          <span className="text-primary">một cách dễ dàng</span>
        </h1>
        <p className="mb-8 max-w-2xl text-xl text-muted">
          Arkive giúp các đội nhóm nhỏ và cá nhân tổ chức, lưu trữ và chia sẻ
          tài sản thương hiệu một cách hiệu quả.
        </p>
        <div className="flex flex-col gap-4 sm:flex-row">
          <Button size="lg" asChild>
            <Link href="/register" className="text-black w-full">
              Bắt đầu miễn phí
            </Link>
          </Button>
          <Button size="lg" variant="outline" asChild>
            <Link href="#demo">Xem demo</Link>
          </Button>
        </div>
        <p className="mt-4 text-sm text-muted">
          Không cần thẻ tín dụng • Dùng thử miễn phí 14 ngày
        </p>
      </section>

      {/* Features Section */}
      <section id="features" className="border-t bg-muted/50 py-20">
        <div className="container mx-auto px-4">
          <div className="mb-12 text-center">
            <h2 className="mb-4 text-3xl font-bold">Tại sao chọn Arkive?</h2>
            <p className="text-muted-foreground">
              Giải pháp quản lý tài sản kỹ thuật số toàn diện cho doanh nghiệp
            </p>
          </div>
          <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
            <div className="rounded-lg border bg-card p-6">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <FolderOpen className="h-6 w-6 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-semibold">
                Quản lý tập trung
              </h3>
              <p className="text-muted-foreground">
                Tổ chức tất cả tài sản của bạn ở một nơi, dễ dàng tìm kiếm và
                quản lý.
              </p>
            </div>
            <div className="rounded-lg border bg-card p-6">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Share2 className="h-6 w-6 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-semibold">Chia sẻ dễ dàng</h3>
              <p className="text-muted-foreground">
                Chia sẻ files với team hoặc khách hàng chỉ với một link, không
                cần đăng nhập.
              </p>
            </div>
            <div className="rounded-lg border bg-card p-6">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Shield className="h-6 w-6 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-semibold">Bảo mật cao</h3>
              <p className="text-muted-foreground">
                Dữ liệu được mã hóa và lưu trữ an toàn, đảm bảo quyền riêng tư
                của bạn.
              </p>
            </div>
            <div className="rounded-lg border bg-card p-6">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Zap className="h-6 w-6 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-semibold">Tốc độ nhanh</h3>
              <p className="text-muted-foreground">
                Upload và tải xuống files với tốc độ cao, hỗ trợ nhiều định
                dạng.
              </p>
            </div>
            <div className="rounded-lg border bg-card p-6">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Users className="h-6 w-6 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-semibold">Cộng tác nhóm</h3>
              <p className="text-muted-foreground">
                Làm việc cùng team, phân quyền truy cập và quản lý dự án hiệu
                quả.
              </p>
            </div>
            <div className="rounded-lg border bg-card p-6">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <FileText className="h-6 w-6 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-semibold">Nhiều định dạng</h3>
              <p className="text-muted-foreground">
                Hỗ trợ đầy đủ các định dạng: hình ảnh, video, tài liệu, âm
                thanh và hơn thế nữa.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section id="pricing" className="border-t py-20">
        <div className="container mx-auto px-4">
          <div className="mb-12 text-center">
            <h2 className="mb-4 text-3xl font-bold">Bảng giá</h2>
            <p className="text-muted-foreground">
              Chọn gói phù hợp với nhu cầu của bạn
            </p>
          </div>
          <div className="grid gap-8 md:grid-cols-3">
            {/* Free Plan */}
            <div className="rounded-lg border bg-card p-8">
              <h3 className="mb-2 text-2xl font-bold">Free</h3>
              <div className="mb-4">
                <span className="text-4xl font-bold">0₫</span>
                <span className="text-muted-foreground">/tháng</span>
              </div>
              <ul className="mb-6 space-y-3">
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>15 GB dung lượng</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Upload không giới hạn</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Chia sẻ cơ bản</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Hỗ trợ email</span>
                </li>
              </ul>
              <Button variant="outline" className="w-full" asChild>
                <Link href="/register">Bắt đầu miễn phí</Link>
              </Button>
            </div>

            {/* Pro Plan */}
            <div className="rounded-lg border-2 border-primary bg-card p-8">
              <div className="mb-2 flex items-center justify-between">
                <h3 className="text-2xl font-bold">Pro</h3>
                <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary">
                  Phổ biến
                </span>
              </div>
              <div className="mb-4">
                <span className="text-4xl font-bold">299,000₫</span>
                <span className="text-muted-foreground">/tháng</span>
              </div>
              <ul className="mb-6 space-y-3">
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>100 GB dung lượng</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Upload không giới hạn</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Chia sẻ nâng cao</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Hỗ trợ ưu tiên</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>API access</span>
                </li>
              </ul>
              <Button className="w-full" asChild>
                <Link href="/register">Nâng cấp ngay</Link>
              </Button>
            </div>

            {/* Enterprise Plan */}
            <div className="rounded-lg border bg-card p-8">
              <h3 className="mb-2 text-2xl font-bold">Enterprise</h3>
              <div className="mb-4">
                <span className="text-4xl font-bold">Liên hệ</span>
              </div>
              <ul className="mb-6 space-y-3">
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Dung lượng không giới hạn</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Tất cả tính năng Pro</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Quản lý team nâng cao</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Hỗ trợ 24/7</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="h-5 w-5 text-primary" />
                  <span>Tùy chỉnh theo nhu cầu</span>
                </li>
              </ul>
              <Button variant="outline" className="w-full" asChild>
                <Link href="/contact">Liên hệ bán hàng</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="border-t bg-primary py-20">
        <div className="container mx-auto px-4 text-center">
          <h2 className="mb-4 text-3xl font-bold text-white">
            Sẵn sàng bắt đầu?
          </h2>
          <p className="mb-8 text-lg text-white/90">
            Tham gia cùng hàng nghìn người dùng đang tin tưởng Arkive
          </p>
          <Button size="lg" variant="secondary" asChild>
            <Link href="/register">
              Tạo tài khoản miễn phí
              <ArrowRight className="ml-2 h-4 w-4" />
            </Link>
          </Button>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t bg-muted/50 py-12">
        <div className="container mx-auto px-4">
          <div className="grid gap-8 md:grid-cols-4">
            <div>
              <h3 className="mb-4 text-xl font-bold italic text-primary">
                Arkive
              </h3>
              <p className="text-sm text-muted-foreground">
                Quản lý tài sản kỹ thuật số của bạn một cách hiệu quả và an
                toàn.
              </p>
            </div>
            <div>
              <h4 className="mb-4 font-semibold">Sản phẩm</h4>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>
                  <Link href="#features" className="hover:text-foreground">
                    Tính năng
                  </Link>
                </li>
                <li>
                  <Link href="#pricing" className="hover:text-foreground">
                    Bảng giá
                  </Link>
                </li>
                <li>
                  <Link href="/login" className="hover:text-foreground">
                    Đăng nhập
                  </Link>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="mb-4 font-semibold">Công ty</h4>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>
                  <Link href="#about" className="hover:text-foreground">
                    Về chúng tôi
                  </Link>
                </li>
                <li>
                  <Link href="/contact" className="hover:text-foreground">
                    Liên hệ
                  </Link>
                </li>
                <li>
                  <Link href="/blog" className="hover:text-foreground">
                    Blog
                  </Link>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="mb-4 font-semibold">Pháp lý</h4>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>
                  <Link href="/privacy" className="hover:text-foreground">
                    Chính sách bảo mật
                  </Link>
                </li>
                <li>
                  <Link href="/terms" className="hover:text-foreground">
                    Điều khoản sử dụng
                  </Link>
                </li>
              </ul>
            </div>
          </div>
          <div className="mt-8 border-t pt-8 text-center text-sm text-muted-foreground">
            <p>
              © {new Date().getFullYear()} Arkive. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}

