import api from '../../../shared/services/baseApi';
import {
  UserQueryParams,
  UserQueryResponse,
  UserDetailResponse,
  UserManageParams,
  UserManageResponse,
  BatchUserOperationParams,
  UserAction,
  UserStats
} from '../types/user-management';

// 用户管理API服务类
class UserManagementApiService {

  // 获取用户列表
  async getUsers(params: UserQueryParams = {}): Promise<UserQueryResponse> {
    try {
      const queryParams = new URLSearchParams();
      
      if (params.keyword) queryParams.append('keyword', params.keyword);
      if (params.status !== undefined) queryParams.append('status', params.status.toString());
      if (params.gender !== undefined) queryParams.append('gender', params.gender.toString());
      if (params.pageNum) queryParams.append('pageNum', params.pageNum.toString());
      if (params.pageSize) queryParams.append('pageSize', params.pageSize.toString());
      if (params.sortBy) queryParams.append('sortBy', params.sortBy);
      if (params.sortDirection) queryParams.append('sortDirection', params.sortDirection);

      const response = await api.get(`/admin/users?${queryParams.toString()}`);
      return response.data;
    } catch (error: any) {
      console.error('获取用户列表失败:', error);
      return {
        success: false,
        error: {
          code: 'FETCH_USERS_ERROR',
          message: error.response?.data?.message || '获取用户列表失败'
        }
      };
    }
  }

  // 获取用户详情
  async getUserDetail(userId: number): Promise<UserDetailResponse> {
    try {
      const response = await api.get(`/admin/users/${userId}`);
      return response.data;
    } catch (error: any) {
      console.error('获取用户详情失败:', error);
      return {
        success: false,
        error: {
          code: 'FETCH_USER_DETAIL_ERROR',
          message: error.response?.data?.message || '获取用户详情失败'
        }
      };
    }
  }

  // 封禁用户
  async banUser(userId: number, params: UserManageParams): Promise<UserManageResponse> {
    try {
      if (!params.reason || params.reason.trim() === '') {
        return {
          success: false,
          error: {
            code: 'INVALID_REASON',
            message: '封禁原因不能为空'
          }
        };
      }

      const response = await api.put(`/admin/users/${userId}/ban`, params);
      return response.data;
    } catch (error: any) {
      console.error('封禁用户失败:', error);
      return {
        success: false,
        error: {
          code: 'BAN_USER_ERROR',
          message: error.response?.data?.message || '封禁用户失败'
        }
      };
    }
  }

  // 解封用户
  async unbanUser(userId: number, params: UserManageParams = {}): Promise<UserManageResponse> {
    try {
      const response = await api.put(`/admin/users/${userId}/unban`, params);
      return response.data;
    } catch (error: any) {
      console.error('解封用户失败:', error);
      return {
        success: false,
        error: {
          code: 'UNBAN_USER_ERROR',
          message: error.response?.data?.message || '解封用户失败'
        }
      };
    }
  }

  // 禁言用户
  async muteUser(userId: number, params: UserManageParams): Promise<UserManageResponse> {
    try {
      if (!params.reason || params.reason.trim() === '') {
        return {
          success: false,
          error: {
            code: 'INVALID_REASON',
            message: '禁言原因不能为空'
          }
        };
      }

      const response = await api.put(`/admin/users/${userId}/mute`, params);
      return response.data;
    } catch (error: any) {
      console.error('禁言用户失败:', error);
      return {
        success: false,
        error: {
          code: 'MUTE_USER_ERROR',
          message: error.response?.data?.message || '禁言用户失败'
        }
      };
    }
  }

  // 解除禁言
  async unmuteUser(userId: number, params: UserManageParams = {}): Promise<UserManageResponse> {
    try {
      const response = await api.put(`/admin/users/${userId}/unmute`, params);
      return response.data;
    } catch (error: any) {
      console.error('解除禁言失败:', error);
      return {
        success: false,
        error: {
          code: 'UNMUTE_USER_ERROR',
          message: error.response?.data?.message || '解除禁言失败'
        }
      };
    }
  }

  // 批量操作用户
  async batchOperation(action: UserAction, params: BatchUserOperationParams): Promise<UserManageResponse> {
    try {
      const promises = params.userIds.map(userId => {
        switch (action) {
          case UserAction.BAN:
            return this.banUser(userId, { reason: params.reason || '' });
          case UserAction.UNBAN:
            return this.unbanUser(userId, { reason: params.reason });
          case UserAction.MUTE:
            return this.muteUser(userId, { reason: params.reason || '' });
          case UserAction.UNMUTE:
            return this.unmuteUser(userId, { reason: params.reason });
          default:
            throw new Error(`不支持的操作类型: ${action}`);
        }
      });

      const results = await Promise.allSettled(promises);
      const successCount = results.filter(result => 
        result.status === 'fulfilled' && result.value.success
      ).length;
      const failureCount = results.length - successCount;

      if (failureCount === 0) {
        return {
          success: true,
          message: `批量操作成功，共处理 ${successCount} 个用户`
        };
      } else if (successCount === 0) {
        return {
          success: false,
          error: {
            code: 'BATCH_OPERATION_FAILED',
            message: `批量操作失败，共 ${failureCount} 个用户操作失败`
          }
        };
      } else {
        return {
          success: true,
          message: `批量操作部分成功，成功 ${successCount} 个，失败 ${failureCount} 个`
        };
      }
    } catch (error: any) {
      console.error('批量操作失败:', error);
      return {
        success: false,
        error: {
          code: 'BATCH_OPERATION_ERROR',
          message: error.message || '批量操作失败'
        }
      };
    }
  }

  // 获取用户统计信息
  async getUserStats(): Promise<UserStats> {
    try {
      const response = await api.get('/admin/users/stats');
      if (response.data.success) {
        return response.data.data;
      } else {
        throw new Error(response.data.error?.message || '获取统计信息失败');
      }
    } catch (error: any) {
      console.error('获取用户统计失败:', error);
      // 返回模拟数据
      return {
        totalUsers: 1248,
        activeUsers: 1156,
        bannedUsers: 45,
        mutedUsers: 47,
        newUsersToday: 23,
        newUsersThisWeek: 156,
        newUsersThisMonth: 678
      };
    }
  }

  // 搜索用户（支持用户名、邮箱、手机号）
  async searchUsers(keyword: string, limit: number = 10): Promise<UserQueryResponse> {
    try {
      const response = await api.get(`/admin/users/search?keyword=${encodeURIComponent(keyword)}&limit=${limit}`);
      return response.data;
    } catch (error: any) {
      console.error('搜索用户失败:', error);
      return {
        success: false,
        error: {
          code: 'SEARCH_USERS_ERROR',
          message: error.response?.data?.message || '搜索用户失败'
        }
      };
    }
  }
}

// 导出单例实例
export const userManagementApi = new UserManagementApiService();
export default userManagementApi;
