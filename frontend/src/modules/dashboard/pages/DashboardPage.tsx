import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/contexts/AuthContext';
import DashboardLayout, { DashboardGrid, DashboardCard, DashboardRow, DashboardCol } from '../components/layout/DashboardLayout';
import StatCard from '../components/cards/StatCard';
import LineChart from '../components/charts/LineChart';
import PieChart from '../components/charts/PieChart';
import BarChart from '../components/charts/BarChart';
import dashboardApi from '../services/dashboardApi';
import { DashboardData } from '../types/dashboard';
import './DashboardPage.css';

const DashboardPage: React.FC = () => {
  const { admin, logout } = useAuth();
  const [dashboardData, setDashboardData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 获取仪表盘数据
  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);

    try {
      // 暂时使用模拟数据，实际项目中会调用真实API
      const mockData = dashboardApi.generateMockData();
      setDashboardData(mockData);
    } catch (err: any) {
      setError(err.message || '获取数据失败');
    } finally {
      setLoading(false);
    }
  };

  // 组件挂载时获取数据
  useEffect(() => {
    fetchDashboardData();
  }, []);

  // 刷新数据
  const handleRefresh = () => {
    fetchDashboardData();
  };

  // 登出处理
  const handleLogout = () => {
    logout();
  };

  // 格式化货币
  const formatCurrency = (value: number | string): string => {
    const num = typeof value === 'string' ? parseFloat(value) : value;
    return `¥${num.toLocaleString('zh-CN', { minimumFractionDigits: 2 })}`;
  };

  if (error) {
    return (
      <DashboardLayout
        title="数据仪表盘"
        subtitle="Shiwu校园二手交易平台管理系统"
        actions={
          <div className="dashboard-header-actions">
            <button onClick={handleRefresh} className="refresh-btn">
              重新加载
            </button>
            <span className="admin-info">
              欢迎，{admin?.realName || admin?.username}
            </span>
            <button onClick={handleLogout} className="logout-btn">
              退出登录
            </button>
          </div>
        }
      >
        <div className="error-container">
          <div className="error-message">
            <h3>数据加载失败</h3>
            <p>{error}</p>
            <button onClick={handleRefresh} className="retry-btn">
              重试
            </button>
          </div>
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout
      title="数据仪表盘"
      subtitle="Shiwu校园二手交易平台管理系统"
      actions={
        <div className="dashboard-header-actions">
          <button onClick={handleRefresh} className="refresh-btn" disabled={loading}>
            {loading ? '加载中...' : '刷新数据'}
          </button>
          <span className="admin-info">
            欢迎，{admin?.realName || admin?.username}
          </span>
          <button onClick={handleLogout} className="logout-btn">
            退出登录
          </button>
        </div>
      }
    >
      {/* 统计卡片区域 */}
      <DashboardGrid columns={4}>
        <StatCard
          title="总用户数"
          value={dashboardData?.stats.totalUsers || 0}
          change={dashboardData?.stats.todayNewUsers}
          changeLabel="今日新增"
          icon="👥"
          color="primary"
          loading={loading}
        />
        <StatCard
          title="总商品数"
          value={dashboardData?.stats.totalProducts || 0}
          change={dashboardData?.stats.todayNewProducts}
          changeLabel="今日新增"
          icon="📦"
          color="success"
          loading={loading}
        />
        <StatCard
          title="总交易数"
          value={dashboardData?.stats.totalTransactions || 0}
          change={12}
          changeLabel="较昨日"
          icon="💰"
          color="warning"
          loading={loading}
        />
        <StatCard
          title="今日交易额"
          value={dashboardData?.stats.todayTransactionAmount || 0}
          change={8.5}
          changeLabel="较昨日 +8.5%"
          icon="💵"
          color="info"
          loading={loading}
          formatter={formatCurrency}
        />
      </DashboardGrid>

      {/* 图表区域 */}
      <DashboardRow gutter={24}>
        <DashboardCol span={12}>
          <DashboardCard title="用户注册趋势" loading={loading}>
            {dashboardData && (
              <LineChart
                data={dashboardData.userTrend.registrationTrend}
                yAxisLabel="注册人数"
                smooth={true}
                area={true}
                loading={loading}
              />
            )}
          </DashboardCard>
        </DashboardCol>
        
        <DashboardCol span={12}>
          <DashboardCard title="商品分类分布" loading={loading}>
            {dashboardData && (
              <PieChart
                data={dashboardData.categoryDistribution}
                loading={loading}
                showPercentage={true}
              />
            )}
          </DashboardCard>
        </DashboardCol>
      </DashboardRow>

      <DashboardRow gutter={24}>
        <DashboardCol span={12}>
          <DashboardCard title="用户活跃度分布" loading={loading}>
            {dashboardData && (
              <BarChart
                data={[
                  { name: '非常活跃', value: dashboardData.userActivityDistribution.veryActive },
                  { name: '活跃', value: dashboardData.userActivityDistribution.active },
                  { name: '不活跃', value: dashboardData.userActivityDistribution.inactive }
                ]}
                yAxisLabel="用户数量"
                loading={loading}
              />
            )}
          </DashboardCard>
        </DashboardCol>
        
        <DashboardCol span={12}>
          <DashboardCard title="热门商品排行榜" loading={loading}>
            {dashboardData && (
              <div className="popular-products-table">
                <table>
                  <thead>
                    <tr>
                      <th>排名</th>
                      <th>商品名称</th>
                      <th>分类</th>
                      <th>价格</th>
                      <th>浏览量</th>
                      <th>收藏量</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dashboardData.popularProducts.map((product, index) => (
                      <tr key={product.id}>
                        <td>
                          <span className={`rank rank-${index + 1}`}>
                            {index + 1}
                          </span>
                        </td>
                        <td className="product-name">{product.title}</td>
                        <td>
                          <span className="category-tag">{product.category}</span>
                        </td>
                        <td className="price">¥{product.price}</td>
                        <td>{product.viewCount}</td>
                        <td>{product.favoriteCount}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </DashboardCard>
        </DashboardCol>
      </DashboardRow>

      {/* 数据更新时间 */}
      {dashboardData && (
        <div className="dashboard-footer">
          <p>数据更新时间: {new Date(dashboardData.lastUpdated).toLocaleString('zh-CN')}</p>
        </div>
      )}
    </DashboardLayout>
  );
};

export default DashboardPage;
