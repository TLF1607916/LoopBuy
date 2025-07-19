// 管理员登录请求类型
export interface AdminLoginRequest {
  username: string;
  password: string;
}

// 管理员信息类型
export interface AdminVO {
  id: number;
  username: string;
  email: string;
  realName: string;
  role: string;
  roleDescription: string;
  status: number;
  lastLoginTime: string | null;
  loginCount: number;
  createTime: string;
  token: string;
}

// 登录错误类型
export interface AdminLoginError {
  code: string;
  message: string;
  userTip: string;
}

// API响应基础类型
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: AdminLoginError;
}

// 登录响应类型
export type AdminLoginResponse = ApiResponse<AdminVO>;

// 认证上下文类型
export interface AuthContextType {
  admin: AdminVO | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  isLoading: boolean;
  error: string | null;
}
