import api from '../../../shared/services/baseApi';
import { DashboardResponse, DashboardData } from '../types/dashboard';

// 仪表盘API服务类
class DashboardApiService {
  
  // 获取仪表盘统计数据
  async getDashboardData(): Promise<DashboardResponse> {
    try {
      const response = await api.get<DashboardResponse>('/admin/dashboard/stats');
      return response.data;
    } catch (error: any) {
      console.error('获取仪表盘数据失败:', error);
      return {
        success: false,
        error: {
          code: 'DASHBOARD_ERROR',
          message: error.response?.data?.message || '获取仪表盘数据失败'
        }
      };
    }
  }

  // 获取用户统计数据
  async getUserStats(days: number = 30): Promise<any> {
    try {
      const response = await api.get(`/admin/dashboard/user-stats?days=${days}`);
      return response.data;
    } catch (error: any) {
      console.error('获取用户统计失败:', error);
      return { success: false, error: error.response?.data };
    }
  }

  // 获取商品统计数据
  async getProductStats(days: number = 30): Promise<any> {
    try {
      const response = await api.get(`/admin/dashboard/product-stats?days=${days}`);
      return response.data;
    } catch (error: any) {
      console.error('获取商品统计失败:', error);
      return { success: false, error: error.response?.data };
    }
  }

  // 获取交易统计数据
  async getTransactionStats(days: number = 30): Promise<any> {
    try {
      const response = await api.get(`/admin/dashboard/transaction-stats?days=${days}`);
      return response.data;
    } catch (error: any) {
      console.error('获取交易统计失败:', error);
      return { success: false, error: error.response?.data };
    }
  }

  // 模拟数据生成器（用于开发测试）
  generateMockData(): DashboardData {
    const today = new Date();
    const dates = Array.from({ length: 30 }, (_, i) => {
      const date = new Date(today);
      date.setDate(date.getDate() - (29 - i));
      return date.toISOString().split('T')[0];
    });

    return {
      stats: {
        totalUsers: 1248,
        todayNewUsers: 23,
        totalProducts: 3567,
        todayNewProducts: 45,
        totalTransactions: 892,
        todayTransactionAmount: 12580.50,
        activeUsers: 456,
        platformGrowthRate: 15.8
      },
      userTrend: {
        registrationTrend: dates.map(date => ({
          date,
          value: Math.floor(Math.random() * 50) + 10
        })),
        activeTrend: dates.map(date => ({
          date,
          value: Math.floor(Math.random() * 200) + 100
        }))
      },
      productTrend: {
        publishTrend: dates.map(date => ({
          date,
          value: Math.floor(Math.random() * 80) + 20
        })),
        soldTrend: dates.map(date => ({
          date,
          value: Math.floor(Math.random() * 40) + 10
        }))
      },
      transactionTrend: {
        volumeTrend: dates.map(date => ({
          date,
          value: Math.floor(Math.random() * 60) + 15
        })),
        amountTrend: dates.map(date => ({
          date,
          value: Math.floor(Math.random() * 5000) + 1000
        }))
      },
      categoryDistribution: [
        { name: '电子产品', value: 856, percentage: 35.2 },
        { name: '图书教材', value: 642, percentage: 26.4 },
        { name: '生活用品', value: 423, percentage: 17.4 },
        { name: '服装配饰', value: 312, percentage: 12.8 },
        { name: '运动器材', value: 198, percentage: 8.2 }
      ],
      productStatusDistribution: {
        onSale: 2145,
        sold: 892,
        draft: 234,
        offline: 296
      },
      userActivityDistribution: {
        veryActive: 156,
        active: 423,
        inactive: 669
      },
      popularProducts: [
        { id: 1, title: 'iPhone 13 Pro', category: '电子产品', price: 6999, viewCount: 234, favoriteCount: 45 },
        { id: 2, title: '高等数学教材', category: '图书教材', price: 45, viewCount: 189, favoriteCount: 32 },
        { id: 3, title: 'Nike运动鞋', category: '服装配饰', price: 299, viewCount: 156, favoriteCount: 28 },
        { id: 4, title: '台式电脑主机', category: '电子产品', price: 3200, viewCount: 145, favoriteCount: 25 },
        { id: 5, title: '宿舍小冰箱', category: '生活用品', price: 280, viewCount: 134, favoriteCount: 22 }
      ],
      lastUpdated: new Date().toISOString()
    };
  }
}

// 导出单例实例
export const dashboardApi = new DashboardApiService();
export default dashboardApi;
