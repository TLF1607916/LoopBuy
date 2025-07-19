import api from '../../../shared/services/baseApi';
import { AdminLoginRequest, AdminLoginResponse } from '../types/auth';

// Auth API服务类
class AuthApiService {
  
  // 管理员登录API
  async adminLogin(loginData: AdminLoginRequest): Promise<AdminLoginResponse> {
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
  }

  // 管理员登出API
  async adminLogout(): Promise<void> {
    try {
      await api.post('/admin/logout');
    } catch (error) {
      console.error('登出请求失败:', error);
    } finally {
      // 无论请求是否成功，都清除本地存储
      localStorage.removeItem('admin_token');
      localStorage.removeItem('admin_info');
    }
  }

  // 验证token有效性
  async validateToken(): Promise<boolean> {
    try {
      const response = await api.get('/admin/validate-token');
      return response.data.success;
    } catch (error) {
      return false;
    }
  }

  // 刷新token
  async refreshToken(): Promise<string | null> {
    try {
      const response = await api.post('/admin/refresh-token');
      if (response.data.success) {
        const newToken = response.data.data.token;
        localStorage.setItem('admin_token', newToken);
        return newToken;
      }
      return null;
    } catch (error) {
      return null;
    }
  }
}

// 导出单例实例
export const authApi = new AuthApiService();
export default authApi;
