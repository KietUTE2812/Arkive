import { User } from "@/store/auth";
import apiClient from "./client";
import { LoginResponse, RegisterResponse, ApiError } from "@/types/api";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  username: string;
  email: string;
  password: string;
}

export interface VerifyEmailRequest {
  verificationCode: string;
}

export interface ResendVerificationRequest {
  email: string;
}

/**
 * Đăng nhập
 */
export async function login(
  credentials: LoginRequest
): Promise<LoginResponse> {
  try {
    const response = await apiClient.post<LoginResponse>(
      "/auth/login",
      credentials
    );
    return response.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Đăng ký
 */
export async function register(
  data: RegisterRequest
): Promise<RegisterResponse> {
  try {
    const response = await apiClient.post<RegisterResponse>(
      "/auth/register",
      data
    );
    return response.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Đăng xuất
 */
export async function logout(): Promise<void> {
  try {
    await apiClient.post("/auth/logout");
  } catch (error) {
    // Ignore logout errors
    console.error("Logout error:", error);
  }
}

/**
 * Refresh token
 */
export async function refreshToken(): Promise<LoginResponse> {
  try {
    console.log("Refresh token");
    const response = await apiClient.post<LoginResponse>("/auth/refresh");
    return response.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Verify email với verification code
 */
export async function verifyEmail(
  verificationCode: string
): Promise<{ success: boolean; data: { verified: boolean } }> {
  try {
    const response = await apiClient.post<{
      success: boolean;
      data: { verified: boolean };
    }>("/auth/verify-email", { verificationCode });
    return response.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Gửi lại verification code
 */
export async function resendVerificationCode(
  email: string
): Promise<{ success: boolean; message?: string }> {
  try {
    const response = await apiClient.post<{
      success: boolean;
      message?: string;
    }>("/auth/resend-verification-code", { email });
    return response.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Đăng nhập với Google
 * Backend sẽ nhận idToken (ID token từ Google) và verify với Google
 */
export async function loginWithGoogle(
  idToken: string
): Promise<LoginResponse> {
  try {
    const response = await apiClient.post<LoginResponse>("/auth/google", {
      idToken, // Gửi ID token từ Google
    });
    return response.data;
  } catch (error) {
    throw error as ApiError;
  }
}

/**
 * Get user info
 * Lấy thông tin user từ backend
 */
export async function getUserInfo(): Promise<User> {
  try {
    const response = await apiClient.get<User>("/users/me");
    return response.data;
  } catch (error) {
    throw error as ApiError;
  }
}

