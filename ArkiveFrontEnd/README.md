# Arkive - Quản lý Tài sản Kỹ thuật số

Dự án front-end cho hệ thống quản lý tài sản kỹ thuật số (Digital Asset Management - DAM).

## Tech Stack

- **Next.js 14** (App Router)
- **TypeScript**
- **Tailwind CSS**
- **shadcn/ui** components
- **Lucide React** icons
- **Axios** - HTTP client
- **Zustand** - State management
- **Google Identity Services** - Google OAuth integration

## Cài đặt

```bash
# Cài đặt dependencies
npm install

# Tạo file .env.local từ .env.local.example
cp .env.local.example .env.local

# Chỉnh sửa .env.local với API URL và Google Client ID
# NEXT_PUBLIC_API_URL=http://localhost:8080/api
# NEXT_PUBLIC_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com

# Chạy development server
npm run dev

# Build production
npm run build

# Start production server
npm start
```

## Cấu trúc dự án

```
arkive/
├── app/                    # Next.js App Router
│   ├── layout.tsx          # Root layout
│   ├── page.tsx            # Dashboard page
│   ├── login/              # Authentication pages
│   ├── register/
│   ├── (landing)/          # Landing page
│   └── ...
├── components/
│   ├── ui/                 # shadcn/ui components
│   ├── layouts/            # Layout components
│   ├── auth/                # Auth components (GoogleLoginButton)
│   ├── providers/           # Context providers
│   └── dashboard/           # Dashboard components
├── lib/
│   ├── api/                 # API clients và services
│   │   ├── client.ts       # Axios instance
│   │   ├── auth.ts         # Auth API
│   │   └── assets.ts       # Assets API
│   └── utils.ts             # Utility functions
├── store/                   # Zustand stores
│   ├── auth.ts              # Auth store
│   └── assets.ts            # Assets store
├── types/                    # TypeScript types
│   └── api.ts               # API types
└── tailwind.config.ts        # Tailwind configuration
```

## API Configuration

API được cấu hình trong `lib/api/client.ts`. Base URL được lấy từ environment variable:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

### Axios Interceptors

- **Request Interceptor**: Tự động thêm Bearer token vào header
- **Response Interceptor**: Xử lý errors và tự động redirect đến login nếu 401

## State Management (Zustand)

### Auth Store (`store/auth.ts`)

```typescript
const { 
  login, 
  register, 
  loginWithGoogle,
  verifyEmail,
  logout, 
  user, 
  isAuthenticated, 
  isLoading, 
  error 
} = useAuthStore();
```

### Assets Store (`store/assets.ts`)

```typescript
const { 
  assets, 
  collections, 
  fetchAssets, 
  uploadAsset, 
  createCollection 
} = useAssetsStore();
```

## API Endpoints

### Authentication
- `POST /auth/login` - Đăng nhập (với username)
- `POST /auth/register` - Đăng ký
- `POST /auth/google` - Đăng nhập với Google
- `POST /auth/verify-email` - Xác thực email
- `POST /auth/resend-verification-code` - Gửi lại mã xác thực
- `POST /auth/logout` - Đăng xuất
- `POST /auth/refresh` - Refresh token

### Assets
- `GET /assets` - Lấy danh sách assets
- `GET /assets/:id` - Lấy asset theo ID
- `POST /assets` - Tạo asset mới
- `POST /assets/upload` - Upload file
- `DELETE /assets/:id` - Xóa asset

### Collections
- `GET /collections` - Lấy danh sách collections
- `GET /collections/:id` - Lấy collection theo ID
- `POST /collections` - Tạo collection mới
- `DELETE /collections/:id` - Xóa collection

## Google OAuth Setup

1. Tạo Google OAuth Client ID tại [Google Cloud Console](https://console.cloud.google.com/)
2. Thêm `NEXT_PUBLIC_GOOGLE_CLIENT_ID` vào `.env.local`
3. Cấu hình Authorized JavaScript origins:
   - Development: `http://localhost:3000`
   - Production: `https://yourdomain.com`
4. Google Identity Services sẽ tự động load script khi cần

## Design

Design được lấy từ Figma: [Arkive Design](https://www.figma.com/design/9oS3Ss4zzhxzdEdvOkauf9/Arkive)

## Routes

```
/                    - Dashboard (yêu cầu login)
/(landing)           - Landing page (public)
/login               - Đăng nhập (với Google OAuth)
/register            - Đăng ký (với email verification)
/forgot-password     - Quên mật khẩu
/assets              - Tất cả tài sản
/upload              - Upload files
/settings            - Cài đặt
/trash               - Thùng rác
```
