import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

// 1. Danh sách các route public (không cần auth)
const publicRoutes = ['/landing', '/login', '/register', '/forgot-password', '/'];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const authToken = request.cookies.get('refreshToken');

  // 2. Kiểm tra xem route hiện tại có phải là public không
  const isPublicRoute = publicRoutes.some((route) => pathname === route || pathname.startsWith(route + '/'));

  // 3. Logic cho người CHƯA đăng nhập
  if (!authToken) {
    if (!isPublicRoute) {
      // Nếu chưa đăng nhập và cố vào route private,
      // redirect về /
      const loginUrl = new URL('/', request.url);
      return NextResponse.redirect(loginUrl);
    }
    // Nếu ở trang public rồi thì cho qua
    return NextResponse.next();
  }

  // // 4. Logic cho người ĐÃ đăng nhập
  // if (authToken) {
  //   if (isPublicRoute) {
  //     // Nếu đã đăng nhập và cố vào /landing,
  //     // đá họ về trang chủ
  //     const homeUrl = new URL('/dashboard', request.url);
  //     return NextResponse.redirect(homeUrl);
  //   }
  //   // Nếu ở trang private thì cho qua
  //   return NextResponse.next();
  // }

  // Trường hợp dự phòng
  return NextResponse.next();
}

export const config = {
  matcher: [
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
};