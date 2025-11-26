import { create } from "zustand";
import { persist } from "zustand/middleware";
import {
    login,
    register,
    logout as apiLogout,
    verifyEmail,
    resendVerificationCode,
    loginWithGoogle as apiLoginWithGoogle,
    LoginRequest,
    RegisterRequest,
    getUserInfo,
} from "@/lib/api/auth";
import { LoginResponse, RegisterResponse, ApiError } from "@/types/api";

export interface User {
    id: string;
    fullName: string;
    username: string;
    email: string;
}

interface AuthState {
    // State
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    error: string | null;

    // Actions
    login: (credentials: LoginRequest) => Promise<void>;
    register: (data: RegisterRequest) => Promise<void>;
    verifyEmail: (verificationCode: string) => Promise<void>;
    resendVerificationCode: (email: string) => Promise<void>;
    loginWithGoogle: (idToken: string) => Promise<void>;
    logout: () => Promise<void>;
    getUserInfo: () => Promise<void>;
    setUser: (user: User) => void;
    setToken: (token: string) => void;
    clearError: () => void;
    reset: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            // Initial state
            user: null,
            token: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,

            // Login action
            login: async (credentials: LoginRequest) => {
                set({ isLoading: true, error: null });
                try {
                    const response: LoginResponse = await login(credentials);

                    if (response.success && response.data.token) {
                        set({
                            token: response.data.token,
                            isAuthenticated: true,
                            isLoading: false,
                            error: null,
                        });

                        // Lưu token vào localStorage để axios interceptor sử dụng
                        if (typeof window !== "undefined") {
                            localStorage.setItem("arkive_auth_token", response.data.token);
                        }
                    } else {
                        throw new Error("Đăng nhập thất bại");
                    }
                } catch (error) {
                    const apiError = error as ApiError;
                    set({
                        isLoading: false,
                        error: apiError.message || "Đăng nhập thất bại",
                        isAuthenticated: false,
                        token: null,
                    });
                    throw error;
                }
            },

            // Register action
            register: async (data: RegisterRequest) => {
                set({ isLoading: true, error: null });
                try {
                    const response: RegisterResponse = await register(data);

                    if (response.success) {
                        set({
                            user: {
                                id: response.data.id,
                                fullName: response.data.fullName,
                                username: response.data.username,
                                email: response.data.email,
                            },
                            isLoading: false,
                            error: null,
                        });
                    } else {
                        throw new Error("Đăng ký thất bại");
                    }
                } catch (error) {
                    const apiError = error as ApiError;
                    set({
                        isLoading: false,
                        error: apiError.message || "Đăng ký thất bại",
                    });
                    throw error;
                }
            },

            // Verify email action
            verifyEmail: async (verificationCode: string) => {
                set({ isLoading: true, error: null });
                try {
                    const response = await verifyEmail(verificationCode);

                    if (response.success && response.data.verified) {
                        set((state) => ({
                            user: state.user
                                ? { ...state.user }
                                : null,
                            isLoading: false,
                            error: null,
                        }));
                    } else {
                        throw new Error("Xác thực thất bại");
                    }
                } catch (error) {
                    const apiError = error as ApiError;
                    set({
                        isLoading: false,
                        error: apiError.message || "Xác thực thất bại",
                    });
                    throw error;
                }
            },

            // Resend verification code
            resendVerificationCode: async (email: string) => {
                set({ isLoading: true, error: null });
                try {
                    await resendVerificationCode(email);
                    set({ isLoading: false, error: null });
                } catch (error) {
                    const apiError = error as ApiError;
                    set({
                        isLoading: false,
                        error: apiError.message || "Gửi lại mã thất bại",
                    });
                    throw error;
                }
            },

            // Login with Google
            loginWithGoogle: async (idToken: string) => {
                set({ isLoading: true, error: null });
                try {
                    const response: LoginResponse = await apiLoginWithGoogle(idToken);

                    if (response.success && response.data.token) {
                        set({
                            token: response.data.token,
                            isAuthenticated: true,
                            isLoading: false,
                            error: null,
                        });

                        // Lưu token vào localStorage để axios interceptor sử dụng
                        if (typeof window !== "undefined") {
                            localStorage.setItem("arkive_auth_token", response.data.token);
                        }
                    } else {
                        throw new Error("Đăng nhập với Google thất bại");
                    }
                } catch (error) {
                    const apiError = error as ApiError;
                    set({
                        isLoading: false,
                        error: apiError.message || "Đăng nhập với Google thất bại",
                        isAuthenticated: false,
                        token: null,
                    });
                    throw error;
                }
            },

            // Logout action
            logout: async () => {
                try {
                    await apiLogout();
                } catch (error) {
                    console.error("Logout error:", error);
                } finally {
                    set({
                        user: null,
                        token: null,
                        isAuthenticated: false,
                        error: null,
                    });

                    // Xóa token khỏi localStorage
                    if (typeof window !== "undefined") {
                        localStorage.removeItem("arkive_auth_token");
                    }
                }
            },

            // Get user info
            getUserInfo: async () => {
                set({ isLoading: true, error: null });
                try {
                    const response: any = await getUserInfo();
                    const user: User = response.data;
                    set({ user });
                    set({ isLoading: false, error: null });
                }
                catch (error) {
                    const apiError = error as ApiError;
                    set({
                        isLoading: false,
                        error: apiError.message || "Lấy thông tin user thất bại",
                    });
                    throw error;
                }
            },

            // Set user
            setUser: (user: User) => {
                set({ user });
            },

            // Set token
            setToken: (token: string) => {
                set({ token, isAuthenticated: true });
                if (typeof window !== "undefined") {
                    localStorage.setItem("arkive_auth_token", token);
                }
            },

            // Clear error
            clearError: () => {
                set({ error: null });
            },

            // Reset state
            reset: () => {
                set({
                    user: null,
                    token: null,
                    isAuthenticated: false,
                    isLoading: false,
                    error: null,
                });
                if (typeof window !== "undefined") {
                    localStorage.removeItem("arkive_auth_token");
                }
            },
        }),
        {
            name: "arkive-auth-storage",
            partialize: (state) => ({
                user: state.user,
                token: state.token,
                isAuthenticated: state.isAuthenticated,
            }),
        }
    )
);

