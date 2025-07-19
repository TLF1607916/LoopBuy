import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import DashboardLayout, { DashboardGrid, DashboardCard, DashboardRow, DashboardCol } from '../components/dashboard/DashboardLayout';
import StatCard from '../components/dashboard/StatCard';
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

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleLogout = () => {
    if (window.confirm('确定要退出登录吗？')) {
      logout();
    }
  };

  const handleRefresh = () => {
    fetchDashboardData();
  };

  // 渲染头部操作按钮
  const renderActions = () => (
    <>
      <button onClick={handleRefresh} className="refresh-button" disabled={loading}>
        {loading ? '刷新中...' : '刷新数据'}
      </button>
      <div className="user-info">
        <span>欢迎，{admin?.realName || admin?.username}</span>
        <span className="role-badge">{admin?.roleDescription}</span>
        <button onClick={handleLogout} className="logout-button">
          退出登录
        </button>
      </div>
    </>
  );

  if (error) {
    return (
      <DashboardLayout title="数据仪表盘" subtitle="实时监控平台运营数据" actions={renderActions()}>
        <div className="error-container">
          <h3>数据加载失败</h3>
          <p>{error}</p>
          <button onClick={handleRefresh} className="retry-button">
            重试
          </button>
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout
      title="数据仪表盘"
      subtitle="实时监控平台运营数据"
      actions={renderActions()}
    >
      {/* 核心统计指标 */}
      <DashboardGrid columns={4}>
        <StatCard
          title="总用户数"
          value={dashboardData?.stats.totalUsers || 0}
          change={dashboardData?.stats.todayNewUsers}
          changeLabel="今日新增"
          icon="👥"
          color="primary"
          loading={loading}
          formatter={(val) => `${val}人`}
        />
        <StatCard
          title="总商品数"
          value={dashboardData?.stats.totalProducts || 0}
          change={dashboardData?.stats.todayNewProducts}
          changeLabel="今日新增"
          icon="📦"
          color="success"
          loading={loading}
          formatter={(val) => `${val}件`}
        />
        <StatCard
          title="总交易数"
          value={dashboardData?.stats.totalTransactions || 0}
          icon="💰"
          color="warning"
          loading={loading}
          formatter={(val) => `${val}笔`}
        />
        <StatCard
          title="今日交易额"
          value={dashboardData?.stats.todayTransactionAmount || 0}
          icon="💵"
          color="danger"
          loading={loading}
          formatter={(val) => `¥${Number(val).toLocaleString()}`}
        />
      </DashboardGrid>

      {/* 趋势图表 */}
      <DashboardRow>
        <DashboardCol span={12}>
          <DashboardCard title="用户注册趋势" loading={loading}>
            {dashboardData && (
              <LineChart
                data={dashboardData.userTrend.registrationTrend}
                config={{ height: 300 }}
                yAxisLabel="注册人数"
                smooth={true}
                area={true}
              />
            )}
          </DashboardCard>
        </DashboardCol>
        <DashboardCol span={12}>
          <DashboardCard title="商品发布趋势" loading={loading}>
            {dashboardData && (
              <LineChart
                data={dashboardData.productTrend.publishTrend}
                config={{ height: 300, colors: ['#91cc75'] }}
                yAxisLabel="发布数量"
                smooth={true}
                area={true}
              />
            )}
          </DashboardCard>
        </DashboardCol>
      </DashboardRow>

      {/* 分布图表 */}
      <DashboardRow>
        <DashboardCol span={12}>
          <DashboardCard title="商品分类分布" loading={loading}>
            {dashboardData && (
              <PieChart
                data={dashboardData.categoryDistribution}
                config={{ height: 350 }}
                showPercentage={true}
              />
            )}
          </DashboardCard>
        </DashboardCol>
        <DashboardCol span={12}>
          <DashboardCard title="用户活跃度分布" loading={loading}>
            {dashboardData && (
              <BarChart
                data={[
                  { name: '非常活跃', value: dashboardData.userActivityDistribution.veryActive },
                  { name: '活跃', value: dashboardData.userActivityDistribution.active },
                  { name: '不活跃', value: dashboardData.userActivityDistribution.inactive }
                ]}
                config={{ height: 350, colors: ['#5470c6', '#91cc75', '#fac858'] }}
                yAxisLabel="用户数量"
              />
            )}
          </DashboardCard>
        </DashboardCol>
      </DashboardRow>

      {/* 热门商品表格 */}
      <DashboardCard title="热门商品排行" loading={loading}>
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
                    <td className="product-title">{product.title}</td>
                    <td>
                      <span className="category-tag">{product.category}</span>
                    </td>
                    <td className="price">¥{product.price.toLocaleString()}</td>
                    <td>{product.viewCount}</td>
                    <td>{product.favoriteCount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </DashboardCard>
    </DashboardLayout>
  );
};

export default DashboardPage;
