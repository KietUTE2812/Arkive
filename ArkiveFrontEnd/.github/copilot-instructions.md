### Về Dự Án Này
Đây là dự án Arkive, một ứng dụng Next.js (App Router) với TypeScript, TailwindCSS, và shadcn/ui. Backend là Java/Spring Boot.

Mục tiêu chính của chúng ta là **hiệu suất tối đa** và **trải nghiệm người dùng không-flicker**, bằng cách tuân thủ nghiêm ngặt kiến trúc "Server-First".

---

### Luôn trả lời bằng tiếng Việt.

### 📜 QUY TẮC VÀNG: Server-First & Kiến Trúc Ốc Đảo

Đây là quy tắc quan trọng nhất.

1.  **Mặc Định Là Server Component (RSC):** **TUYỆT ĐỐI KHÔNG** thêm `"use client"` vào một component trừ khi nó **BẮT BUỘC** phải có tính tương tác (ví dụ: `useState`, `useEffect`, `useRouter`, `onClick`).
2.  **Đẩy `"use client"` Xuống "Lá":** Nếu một trang (`page.tsx`) cần một nút bấm tương tác, **ĐỪNG** biến cả trang thành Client Component.
    * **CÁCH LÀM SAI ❌:**
        ```tsx
        // app/my-page/page.tsx
        "use client"; // SAI!
        import { useState } from "react";
        export default function MyPage() {
          const [count, setCount] = useState(0);
          return (
            <div>
              <StaticHeader /> {/* Bị biến thành Client Component một cách lãng phí */}
              <button onClick={() => setCount(c => c + 1)}>Click</button>
            </div>
          );
        }
        ```
    * **CÁCH LÀM ĐÚNG ✅ (Kiến trúc Ốc đảo):**
        ```tsx
        // app/my-page/components/click-button.tsx
        "use client"; // ĐÚNG! "Ốc đảo" tương tác
        import { useState } from "react";
        import { Button } from "@/components/ui/button";
        export function ClickButton() {
          const [count, setCount] = useState(0);
          return <Button onClick={() => setCount(c => c + 1)}>Click ({count})</Button>;
        }

        // app/my-page/page.tsx
        // KHÔNG CÓ "use client". Đây là RSC.
        import { ClickButton } from "./components/click-button";
        export default function MyPage() {
          return (
            <div>
              <StaticHeader /> {/* Vẫn là RSC, render ở server */}
              <ClickButton /> {/* Chỉ hòn đảo này là Client Component */}
            </div>
          );
        }
        ```

---

### 🚀 Data Fetching & Caching

1.  **Dùng `async/await` trong RSC:** Luôn fetch dữ liệu trực tiếp trong Server Components (`page.tsx`, `layout.tsx`) bằng `async/await`.
2.  **Dùng `loading.tsx` và `error.tsx`:** Thay vì tự quản lý state `isLoading`, `isError` trong `useEffect` (cách làm của Client Component), hãy sử dụng các file `loading.tsx` và `error.tsx` của Next.js. Next.js sẽ tự động hiển thị chúng trong khi `page.tsx` đang `await` data.

---

### 💧 Xử Lý Lỗi Hydration

Lỗi Hydration xảy ra khi HTML render ở Server không khớp với HTML render lần đầu ở Client.

1.  **Nguyên nhân:** Dùng các API chỉ có ở Client như `window`, `localStorage`, `navigator` ở cấp độ gốc của component.
2.  **Giải pháp:** Nếu BẮT BUỘC phải dùng, hãy "gate" (chặn) nó bằng `useEffect` và `useState` để đảm bảo nó chỉ chạy *sau khi* hydrate xong.
    ```tsx
    // Cách làm ĐÚNG để tránh hydration mismatch
    'use client';
    import { useState, useEffect } from 'react';

    function MyComponent() {
      const [isMounted, setIsMounted] = useState(false);

      useEffect(() => {
        setIsMounted(true); // Chỉ chạy ở client, sau khi hydrate
      }, []);

      if (!isMounted) {
        return null; // Hoặc một Skeleton.
                     // Cả Server và Client (lần đầu) đều render `null`.
      }

      // Bây giờ mới an toàn để dùng API client
      return <div>Window width: {window.innerWidth}</div>;
    }
    ```

---

### 🎨 Component & Styling

1.  **Tách Biệt Utils:** Các hàm helper thuần túy (như `formatBytes`, `formatDate`) phải được đặt trong `lib/utils.ts` và import vào, không được viết trực tiếp trong file component.
2.  **`shadcn/ui`:** Ưu tiên sử dụng các component từ `shadcn/ui` (Button, Card, Input, Dialog, ...) để đảm bảo tính nhất quán.
3.  **`lucide-react`:** Dùng `lucide-react` cho tất cả các icon.
4.  **`TailwindCSS`:** Chỉ dùng utility classes. Không viết CSS thuần hoặc CSS-in-JS.

---

### ⚡ Mutations & Forms (Server Actions)

1.  **Ưu tiên Server Actions:** Khi submit form (tạo, sửa, xóa dữ liệu), hãy ưu tiên dùng Server Actions.
2.  **Refresh Data:**
    * Nếu dùng Server Action, gọi `revalidatePath('/path-to-refresh')` ở cuối action để làm mới dữ liệu.
    * Nếu dùng API route (trong `apiClient`), hãy gọi `router.refresh()` ở client sau khi mutation thành công để buộc Server Component fetch lại dữ liệu mới nhất.