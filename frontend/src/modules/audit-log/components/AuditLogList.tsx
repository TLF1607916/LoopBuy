import React, { useState } from 'react';
import {
  AuditLog,
  AuditLogResult,
  AuditLogResultLabels,
  AuditLogResultColors
} from '../types/audit-log';
import { formatDate, formatRelativeTime } from '../../../shared/utils/format';
import './AuditLogList.css';

interface AuditLogListProps {
  auditLogs: AuditLog[];
  loading: boolean;
  onSort: (sortBy: string, sortOrder: 'ASC' | 'DESC') => void;
  onViewDetail: (log: AuditLog) => void;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  onPageChange: (page: number, pageSize: number) => void;
}

const AuditLogList: React.FC<AuditLogListProps> = ({
  auditLogs,
  loading,
  onSort,
  onViewDetail,
  pagination,
  onPageChange
}) => {
  const [sortBy, setSortBy] = useState<string>('create_time');
  const [sortOrder, setSortOrder] = useState<'ASC' | 'DESC'>('DESC');

  // 处理排序
  const handleSort = (field: string) => {
    let newOrder: 'ASC' | 'DESC' = 'DESC';
    if (sortBy === field && sortOrder === 'DESC') {
      newOrder = 'ASC';
    }
    setSortBy(field);
    setSortOrder(newOrder);
    onSort(field, newOrder);
  };

  // 获取结果标签
  const getResultTag = (result: number) => {
    const label = AuditLogResultLabels[result as AuditLogResult];
    const color = AuditLogResultColors[result as AuditLogResult];
    return (
      <span 
        className="result-tag" 
        style={{ backgroundColor: color, color: 'white' }}
      >
        {label}
      </span>
    );
  };

  // 渲染操作详情
  const renderDetails = (details?: string) => {
    if (!details) return '-';
    
    // 如果详情太长，截断显示
    if (details.length > 50) {
      return (
        <span className="details-text" title={details}>
          {details.substring(0, 50)}...
        </span>
      );
    }
    
    return <span className="details-text">{details}</span>;
  };

  // 渲染IP地址
  const renderIpAddress = (ipAddress?: string) => {
    if (!ipAddress) return '-';
    return <span className="ip-address">{ipAddress}</span>;
  };

  // 渲染用户代理
  const renderUserAgent = (userAgent?: string) => {
    if (!userAgent) return '-';
    
    // 简化用户代理显示
    let simplified = userAgent;
    if (userAgent.includes('Chrome')) {
      simplified = 'Chrome';
    } else if (userAgent.includes('Firefox')) {
      simplified = 'Firefox';
    } else if (userAgent.includes('Safari')) {
      simplified = 'Safari';
    } else if (userAgent.includes('Edge')) {
      simplified = 'Edge';
    }
    
    return (
      <span className="user-agent" title={userAgent}>
        {simplified}
      </span>
    );
  };

  // 渲染分页
  const renderPagination = () => {
    const { current, pageSize, total } = pagination;
    const totalPages = Math.ceil(total / pageSize);
    
    if (totalPages <= 1) return null;

    const pages = [];
    const startPage = Math.max(1, current - 2);
    const endPage = Math.min(totalPages, current + 2);

    // 上一页
    pages.push(
      <button
        key="prev"
        className="pagination-btn"
        disabled={current === 1}
        onClick={() => onPageChange(current - 1, pageSize)}
      >
        上一页
      </button>
    );

    // 页码
    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          className={`pagination-btn ${i === current ? 'active' : ''}`}
          onClick={() => onPageChange(i, pageSize)}
        >
          {i}
        </button>
      );
    }

    // 下一页
    pages.push(
      <button
        key="next"
        className="pagination-btn"
        disabled={current === totalPages}
        onClick={() => onPageChange(current + 1, pageSize)}
      >
        下一页
      </button>
    );

    return (
      <div className="pagination">
        <div className="pagination-info">
          共 {total} 条记录，第 {current} / {totalPages} 页
        </div>
        <div className="pagination-controls">
          {pages}
        </div>
      </div>
    );
  };

  return (
    <div className="audit-log-list">
      {/* 表格 */}
      <div className="table-container">
        <table className="audit-log-table">
          <thead>
            <tr>
              <th>日志ID</th>
              <th>管理员</th>
              <th>操作类型</th>
              <th>目标类型</th>
              <th>目标ID</th>
              <th>操作详情</th>
              <th>IP地址</th>
              <th>浏览器</th>
              <th>结果</th>
              <th 
                className="sortable"
                onClick={() => handleSort('create_time')}
              >
                操作时间
                {sortBy === 'create_time' && (
                  <span className="sort-indicator">
                    {sortOrder === 'ASC' ? '↑' : '↓'}
                  </span>
                )}
              </th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={11} className="loading-cell">
                  <div className="loading-spinner"></div>
                  <span>加载中...</span>
                </td>
              </tr>
            ) : auditLogs.length === 0 ? (
              <tr>
                <td colSpan={11} className="empty-cell">
                  暂无审计日志数据
                </td>
              </tr>
            ) : (
              auditLogs.map(log => (
                <tr key={log.id}>
                  <td className="log-id">#{log.id}</td>
                  <td className="admin-info">
                    <div className="admin-details">
                      <div className="admin-username">
                        {log.adminUsername || `ID:${log.adminId}`}
                      </div>
                      <div className="admin-id">ID: {log.adminId}</div>
                    </div>
                  </td>
                  <td className="action-info">
                    <div className="action-code">{log.action}</div>
                    {log.actionDescription && (
                      <div className="action-description">{log.actionDescription}</div>
                    )}
                  </td>
                  <td className="target-info">
                    {log.targetType ? (
                      <div>
                        <div className="target-type">{log.targetType}</div>
                        {log.targetTypeDescription && (
                          <div className="target-description">{log.targetTypeDescription}</div>
                        )}
                      </div>
                    ) : (
                      '-'
                    )}
                  </td>
                  <td className="target-id">
                    {log.targetId || '-'}
                  </td>
                  <td className="details-cell">
                    {renderDetails(log.details)}
                  </td>
                  <td className="ip-cell">
                    {renderIpAddress(log.ipAddress)}
                  </td>
                  <td className="user-agent-cell">
                    {renderUserAgent(log.userAgent)}
                  </td>
                  <td className="result-cell">
                    {getResultTag(log.result)}
                  </td>
                  <td className="time-cell">
                    <div className="time-primary">
                      {formatRelativeTime(log.createTime)}
                    </div>
                    <div className="time-secondary">
                      {formatDate(log.createTime, 'MM-DD HH:mm:ss')}
                    </div>
                  </td>
                  <td className="action-cell">
                    <button
                      className="detail-btn"
                      onClick={() => onViewDetail(log)}
                    >
                      查看详情
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* 分页 */}
      {renderPagination()}
    </div>
  );
};

export default AuditLogList;
