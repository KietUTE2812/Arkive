
import { InfoCard } from "@/components/dashboard/InfoCard";
import { RecentFiles } from "@/components/dashboard/RecentFiles";
import { QuickActions } from "@/components/dashboard/QuickActions";
import { SearchBar } from "@/components/dashboard/SearchBar";
import { UserGreeting } from "@/components/dashboard/UserGreeting";

// Mock data - Trong production, fetch từ API
const stats = [
  {
    title: "Tổng số Files",
    value: "1,245",
    iconName: "FileText" as const,
    iconColor: "text-primary",
  },
  {
    title: "Dung lượng đã sử dụng",
    value: "9.8 GB / 15 GB",
    iconName: "HardDrive" as const,
    iconColor: "text-primary",
  },
  {
    title: "Files được chia sẻ",
    value: "128",
    iconName: "Share2" as const,
    iconColor: "text-primary",
  },
  {
    title: "Thẻ phổ biến nhất",
    value: "Thiết kế",
    iconName: "Tag" as const,
    iconColor: "text-primary",
  },
];

const recentFiles = [
  {
    id: "1",
    name: "sunset_mountains.jpg",
    type: "Ảnh",
    size: "2.4 MB",
  },
  {
    id: "2",
    name: "city_timelapse.mp4",
    type: "Video",
    size: "128 MB",
  },
  {
    id: "3",
    name: "Project_Report_Q3.pdf",
    type: "Tài liệu",
    size: "5.1 MB",
  },
  {
    id: "4",
    name: "forest_meditation.mp3",
    type: "Âm thanh",
    size: "8.7 MB",
  },
  {
    id: "5",
    name: "product_design_v2.psd",
    type: "Ảnh",
    size: "15.3 MB",
  },
  {
    id: "6",
    name: "team_meeting.jpg",
    type: "Ảnh",
    size: "3.1 MB",
  },
];

// Trong production, có thể fetch data từ API
// async function getStats() {
//   const res = await fetch('/api/stats');
//   return res.json();
// }

export default function DashboardPage() {
  // Middleware đã xử lý authentication check
  // Không cần useEffect hay useState cho auth nữa

  return (
    <div className="container mx-auto space-y-8 p-8">
      {/* Header */}
      <div className="space-y-4">
        <UserGreeting />
        <SearchBar />
      </div>

      {/* Stats Cards */}
      <div>
        <h2 className="mb-4 text-2xl font-bold text-foreground">
          Thống kê tổng quan
        </h2>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          {stats.map((stat) => (
            <InfoCard
              key={stat.title}
              title={stat.title}
              value={stat.value}
              iconName={stat.iconName}
              iconColor={stat.iconColor}
            />
          ))}
        </div>
      </div>

      {/* Recent Files */}
      <RecentFiles files={recentFiles} />

      {/* Quick Actions */}
      <QuickActions />

      {/* Footer */}
      <footer suppressHydrationWarning className="border-t pt-6 text-center text-sm text-muted-foreground">
        <p>
          © {new Date().getFullYear()} Arkive. Phiên bản 1.0.{" "}
          <a href="#" className="text-primary hover:underline">
            Chính sách bảo mật
          </a>{" "}
          |{" "}
          <a href="#" className="text-primary hover:underline">
            Điều khoản sử dụng
          </a>
        </p>
      </footer>
    </div>
  );
}

