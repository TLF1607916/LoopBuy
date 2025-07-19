import React from 'react';
import {
  AuditLogStats as StatsType,
  AuditLogTrendData
} from '../types/audit-log';
import './AuditLogStats.css';

interface AuditLogStatsProps {
  stats?: StatsType;
  trendData?: AuditLogTrendData[];
  loading?: boolean;
}

const AuditLogStats: React.FC<AuditLogStatsProps> = ({
  stats,
  trendData,
  loading = false
}) => {

  // 渲染统计卡片
  const renderStatsCards = () => {
    if (!stats) return null;

    const statsData = [
      { 
        label: '总日志数', 
        value: stats.totalLogs, 
        color: '#1890ff',
        icon: '📊'
      },
      { 
        label: '成功操作', 
        value: stats.successLogs, 
        color: '#52c41a',
        icon: '✅',
        percentage: stats.totalLogs > 0 ? ((stats.successLogs / stats.totalLogs) * 100).toFixed(1) : '0'
      },
      { 
        label: '失败操作', 
        value: stats.failureLogs, 
        color: '#f5222d',
        icon: '❌',
        percentage: stats.totalLogs > 0 ? ((stats.failureLogs / stats.totalLogs) * 100).toFixed(1) : '0'
      },
      { 
        label: '今日操作', 
        value: stats.todayLogs, 
        color: '#722ed1',
        icon: '📅'
      },
      { 
        label: '本周操作', 
        value: stats.weekLogs, 
        color: '#13c2c2',
        icon: '📈'
      },
      { 
        label: '本月操作', 
        value: stats.monthLogs, 
        color: '#faad14',
        icon: '📆'
      }
    ];

    return (
      <div className="stats-cards">
        {statsData.map(stat => (
          <div key={stat.label} className="stat-card">
            <div className="stat-icon">{stat.icon}</div>
            <div className="stat-content">
              <div className="stat-value" style={{ color: stat.color }}>
                {stat.value.toLocaleString()}
              </div>
              <div className="stat-label">{stat.label}</div>
              {stat.percentage && (
                <div className="stat-percentage" style={{ color: stat.color }}>
                  {stat.percentage}%
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    );
  };

  // 渲染热门操作
  const renderTopActions = () => {
    if (!stats?.topActions || stats.topActions.length === 0) return null;

    return (
      <div className="top-actions">
        <h4 className="section-title">热门操作</h4>
        <div className="action-list">
          {stats.topActions.map((action, index) => (
            <div key={action.action} className="action-item">
              <div className="action-rank">#{index + 1}</div>
              <div className="action-info">
                <div className="action-name">{action.actionDescription}</div>
                <div className="action-code">{action.action}</div>
              </div>
              <div className="action-count">{action.count}</div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  // 渲染活跃管理员
  const renderTopAdmins = () => {
    if (!stats?.topAdmins || stats.topAdmins.length === 0) return null;

    return (
      <div className="top-admins">
        <h4 className="section-title">活跃管理员</h4>
        <div className="admin-list">
          {stats.topAdmins.map((admin, index) => (
            <div key={admin.adminId} className="admin-item">
              <div className="admin-rank">#{index + 1}</div>
              <div className="admin-info">
                <div className="admin-name">{admin.adminUsername}</div>
                <div className="admin-id">ID: {admin.adminId}</div>
              </div>
              <div className="admin-count">{admin.count}</div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  // 渲染趋势图表（简化版本）
  const renderTrendChart = () => {
    if (!trendData || trendData.length === 0) return null;

    const maxCount = Math.max(...trendData.map(d => d.totalCount));
    
    return (
      <div className="trend-chart">
        <h4 className="section-title">操作趋势</h4>
        <div className="chart-container">
          <div className="chart-bars">
            {trendData.map((data, index) => {
              const height = maxCount > 0 ? (data.totalCount / maxCount) * 100 : 0;
              const successHeight = data.totalCount > 0 ? (data.successCount / data.totalCount) * height : 0;
              const failureHeight = height - successHeight;
              
              return (
                <div key={data.date} className="chart-bar-container">
                  <div className="chart-bar" style={{ height: '100px' }}>
                    <div 
                      className="bar-success" 
                      style={{ 
                        height: `${successHeight}%`,
                        backgroundColor: '#52c41a'
                      }}
                      title={`成功: ${data.successCount}`}
                    ></div>
                    <div 
                      className="bar-failure" 
                      style={{ 
                        height: `${failureHeight}%`,
                        backgroundColor: '#f5222d'
                      }}
                      title={`失败: ${data.failureCount}`}
                    ></div>
                  </div>
                  <div className="chart-label">
                    {new Date(data.date).toLocaleDateString('zh-CN', { 
                      month: 'short', 
                      day: 'numeric' 
                    })}
                  </div>
                  <div className="chart-value">{data.totalCount}</div>
                </div>
              );
            })}
          </div>
          <div className="chart-legend">
            <div className="legend-item">
              <div className="legend-color" style={{ backgroundColor: '#52c41a' }}></div>
              <span>成功</span>
            </div>
            <div className="legend-item">
              <div className="legend-color" style={{ backgroundColor: '#f5222d' }}></div>
              <span>失败</span>
            </div>
          </div>
        </div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="audit-log-stats loading">
        <div className="loading-spinner"></div>
        <span>加载统计数据中...</span>
      </div>
    );
  }

  return (
    <div className="audit-log-stats">
      {/* 统计卡片 */}
      {renderStatsCards()}

      {/* 详细统计 */}
      <div className="detailed-stats">
        {/* 热门操作 */}
        {renderTopActions()}

        {/* 活跃管理员 */}
        {renderTopAdmins()}

        {/* 趋势图表 */}
        {renderTrendChart()}
      </div>
    </div>
  );
};

export default AuditLogStats;
