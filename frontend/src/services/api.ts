import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { AdminLoginRequest, AdminLoginResponse } from '../types/auth';

// 创建axios实例
const api: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('admin_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    // 处理401未授权错误
    if (error.response?.status === 401) {
      // 清除本地存储的token
      localStorage.removeItem('admin_token');
      localStorage.removeItem('admin_info');
      // 重定向到登录页面
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 管理员登录API
export const adminLogin = async (loginData: AdminLoginRequest): Promise<AdminLoginResponse> => {
  try {
    const response = await api.post<AdminLoginResponse>('/admin/login', loginData);
    return response.data;
  } catch (error: any) {
    // 处理网络错误或服务器错误
    if (error.response?.data) {
      return error.response.data;
    } else {
      return {
        success: false,
        error: {
          code: 'NETWORK_ERROR',
          message: '网络连接失败',
          userTip: '请检查网络连接后重试'
        }
      };
    }
  }
};

export default api;
