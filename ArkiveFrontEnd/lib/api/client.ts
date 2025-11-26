import axios, {
  AxiosError,
  AxiosInstance,
  InternalAxiosRequestConfig,
} from "axios";
import { ApiError } from "@/types/api";
import { refreshToken } from "./auth";

// Base URL - có thể lấy từ environment variable
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

// Tạo axios instance
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Rất quan trọng: để gửi cookie (chứa refresh token)
});

// Request interceptor - thêm token vào header (Giữ nguyên)
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token =
      typeof window !== "undefined"
        ? localStorage.getItem("arkive_auth_token")
        : null;

    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// --- LOGIC XỬ LÝ REFRESH TOKEN ---

// Flag để chỉ cho biết có đang refresh token hay không
let isRefreshing = false;

// Hàng chờ (queue) chứa các request bị lỗi 401 trong khi đang refresh
// Mỗi item là một hàm (resolve, reject) của Promise
let failedQueue: Array<{
  resolve: (value: any) => void;
  reject: (reason: any) => void;
}> = [];

/**
 * Xử lý các request trong hàng chờ
 * @param error - Lỗi (nếu refresh thất bại)
 * @param token - Token mới (nếu refresh thành công)
 */
const processQueue = (error: ApiError | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      // Refresh thất bại, reject tất cả request đang chờ
      prom.reject(error);
    } else {
      // Refresh thành công, resolve request (để nó được retry)
      // (interceptor sẽ retry tự động khi resolve Promise)
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

/**
 * Hàm thực hiện logout
 */
const logoutUser = () => {
  if (typeof window !== "undefined") {
    localStorage.removeItem("arkive_auth_token");
    localStorage.removeItem("arkive_user");
    // Redirect về trang login, bạn có thể thay bằng router của Next.js
    window.location.href = "/login";
  }
};

// Response interceptor - XỬ LÝ LỖI VÀ REFRESH TOKEN
apiClient.interceptors.response.use(
  (response) => {
    // Request thành công, trả về response
    return response;
  },
  async (error: AxiosError) => {
    // Định dạng lỗi (giữ nguyên logic của bạn)
    const apiError: ApiError = {
      code: error.code || "UNKNOWN_ERROR",
      message:
        (error.response?.data as any)?.message ||
        error.message ||
        "Có lỗi xảy ra",
      status: error.response?.status || 500,
    };

    // Lấy config của request gốc đã thất bại
    const originalRequest = error.config as InternalAxiosRequestConfig;

    // CHỈ XỬ LÝ LỖI 401
    // và đảm bảo đây không phải là request refresh token (tránh lặp vô hạn)
    if (apiError.status === 401 && originalRequest.url !== "/auth/refresh") {

      // Nếu đang trong quá trình refresh, thêm request này vào hàng chờ
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            // Khi có token mới, cập nhật header và retry
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            return apiClient(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      // Đây là request 401 đầu tiên, bắt đầu quá trình refresh
      isRefreshing = true;

      try {
        // GỌI API REFRESH TOKEN
        // Giả sử refresh token được lưu trong httpOnly cookie
        // và API của bạn là '/auth/refresh' trả về { accessToken: "..." }
        const response = await refreshToken();

        const newAccessToken = response.data.token;

        // Lưu token mới vào localStorage
        if (typeof window !== "undefined") {
          localStorage.setItem("arkive_auth_token", newAccessToken);
        }

        // Cập nhật header của request *gốc*
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        }

        // Xử lý hàng chờ (thông báo refresh thành công)
        processQueue(null, newAccessToken);

        // Retry request gốc với token mới
        return apiClient(originalRequest);

      } catch (refreshError: any) {
        // REFRESH TOKEN THẤT BẠI (ví dụ: refresh token cũng hết hạn)
        console.error("Refresh token failed:", refreshError);
        const refreshApiError: ApiError = {
          code: refreshError.code || "REFRESH_FAILED",
          message:
            (refreshError.response?.data as any)?.message ||
            "Phiên làm việc đã hết hạn",
          status: refreshError.response?.status || 500,
        };

        // Xử lý hàng chờ (thông báo refresh thất bại)
        processQueue(refreshApiError, null);

        // Đăng xuất người dùng
        logoutUser();

        // Reject lỗi
        return Promise.reject(refreshApiError);

      } finally {
        // Hoàn tất quá trình refresh
        isRefreshing = false;
      }
    }

    // Nếu không phải lỗi 401, reject lỗi như bình thường
    return Promise.reject(apiError);
  }
);

export default apiClient;