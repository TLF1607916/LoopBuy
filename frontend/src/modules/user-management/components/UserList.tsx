import React, { useState } from 'react';
import {
  User,
  UserStatus,
  UserStatusLabels,
  UserStatusColors,
  UserAction,
  UserActionConfigs,
  Gender,
  GenderLabels
} from '../types/user-management';
import { formatDate, formatRelativeTime } from '../../../shared/utils/format';
import './UserList.css';

interface UserListProps {
  users: User[];
  loading: boolean;
  selectedUserIds: number[];
  onSelectionChange: (selectedIds: number[]) => void;
  onUserAction: (action: UserAction, userIds: number[]) => void;
  onSort: (sortBy: string, sortDirection: 'ASC' | 'DESC') => void;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  onPageChange: (page: number, pageSize: number) => void;
}

const UserList: React.FC<UserListProps> = ({
  users,
  loading,
  selectedUserIds,
  onSelectionChange,
  onUserAction,
  onSort,
  pagination,
  onPageChange
}) => {
  const [sortBy, setSortBy] = useState<string>('create_time');
  const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('DESC');

  // 处理全选/取消全选
  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      const allIds = users.map(user => user.id);
      onSelectionChange(allIds);
    } else {
      onSelectionChange([]);
    }
  };

  // 处理单个选择
  const handleSelectUser = (userId: number, checked: boolean) => {
    if (checked) {
      onSelectionChange([...selectedUserIds, userId]);
    } else {
      onSelectionChange(selectedUserIds.filter(id => id !== userId));
    }
  };

  // 处理排序
  const handleSort = (field: string) => {
    let newDirection: 'ASC' | 'DESC' = 'DESC';
    if (sortBy === field && sortDirection === 'DESC') {
      newDirection = 'ASC';
    }
    setSortBy(field);
    setSortDirection(newDirection);
    onSort(field, newDirection);
  };

  // 获取状态标签
  const getStatusTag = (status: UserStatus) => {
    const label = UserStatusLabels[status];
    const color = UserStatusColors[status];
    return (
      <span 
        className="status-tag" 
        style={{ backgroundColor: color, color: 'white' }}
      >
        {label}
      </span>
    );
  };

  // 获取可用操作
  const getAvailableActions = (user: User): UserAction[] => {
    const actions: UserAction[] = [];
    
    switch (user.status) {
      case UserStatus.ACTIVE:
        actions.push(UserAction.BAN, UserAction.MUTE);
        break;
      case UserStatus.BANNED:
        actions.push(UserAction.UNBAN);
        break;
      case UserStatus.MUTED:
        actions.push(UserAction.UNMUTE, UserAction.BAN);
        break;
    }
    
    return actions;
  };

  // 渲染操作按钮
  const renderActions = (user: User) => {
    const availableActions = getAvailableActions(user);
    
    return (
      <div className="user-actions">
        {availableActions.map(action => {
          const config = UserActionConfigs[action];
          return (
            <button
              key={action}
              className={`action-btn action-btn-${action}`}
              style={{ color: config.color, borderColor: config.color }}
              onClick={() => onUserAction(action, [user.id])}
              title={config.label}
            >
              <span className="action-icon">{config.icon}</span>
              {config.label}
            </button>
          );
        })}
      </div>
    );
  };

  // 渲染用户头像
  const renderAvatar = (user: User) => {
    if (user.avatarUrl) {
      return (
        <img 
          src={user.avatarUrl} 
          alt={user.username}
          className="user-avatar"
          onError={(e) => {
            (e.target as HTMLImageElement).src = '/default-avatar.png';
          }}
        />
      );
    } else {
      return (
        <div className="user-avatar-placeholder">
          {user.username.charAt(0).toUpperCase()}
        </div>
      );
    }
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

  const isAllSelected = users.length > 0 && selectedUserIds.length === users.length;
  const isIndeterminate = selectedUserIds.length > 0 && selectedUserIds.length < users.length;

  return (
    <div className="user-list">
      {/* 表格头部 */}
      <div className="table-header">
        <div className="selection-info">
          {selectedUserIds.length > 0 && (
            <span>已选择 {selectedUserIds.length} 个用户</span>
          )}
        </div>
      </div>

      {/* 表格 */}
      <div className="table-container">
        <table className="user-table">
          <thead>
            <tr>
              <th className="checkbox-column">
                <input
                  type="checkbox"
                  checked={isAllSelected}
                  ref={input => {
                    if (input) input.indeterminate = isIndeterminate;
                  }}
                  onChange={(e) => handleSelectAll(e.target.checked)}
                />
              </th>
              <th>用户信息</th>
              <th>联系方式</th>
              <th>状态</th>
              <th>性别</th>
              <th>粉丝数</th>
              <th>评分</th>
              <th 
                className="sortable"
                onClick={() => handleSort('last_login_time')}
              >
                最后登录
                {sortBy === 'last_login_time' && (
                  <span className="sort-indicator">
                    {sortDirection === 'ASC' ? '↑' : '↓'}
                  </span>
                )}
              </th>
              <th 
                className="sortable"
                onClick={() => handleSort('create_time')}
              >
                注册时间
                {sortBy === 'create_time' && (
                  <span className="sort-indicator">
                    {sortDirection === 'ASC' ? '↑' : '↓'}
                  </span>
                )}
              </th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={10} className="loading-cell">
                  <div className="loading-spinner"></div>
                  <span>加载中...</span>
                </td>
              </tr>
            ) : users.length === 0 ? (
              <tr>
                <td colSpan={10} className="empty-cell">
                  暂无用户数据
                </td>
              </tr>
            ) : (
              users.map(user => (
                <tr key={user.id}>
                  <td>
                    <input
                      type="checkbox"
                      checked={selectedUserIds.includes(user.id)}
                      onChange={(e) => handleSelectUser(user.id, e.target.checked)}
                    />
                  </td>
                  <td className="user-info">
                    <div className="user-profile">
                      {renderAvatar(user)}
                      <div className="user-details">
                        <div className="user-name">
                          {user.nickname || user.username}
                        </div>
                        <div className="user-id">ID: {user.id}</div>
                        <div className="username">@{user.username}</div>
                      </div>
                    </div>
                  </td>
                  <td className="contact-info">
                    {user.email && (
                      <div className="contact-item">
                        <span className="contact-label">邮箱:</span>
                        <span className="contact-value">{user.email}</span>
                      </div>
                    )}
                    {user.phone && (
                      <div className="contact-item">
                        <span className="contact-label">手机:</span>
                        <span className="contact-value">{user.phone}</span>
                      </div>
                    )}
                  </td>
                  <td>
                    {getStatusTag(user.status)}
                  </td>
                  <td>
                    {user.gender !== undefined ? GenderLabels[user.gender] : '-'}
                  </td>
                  <td className="number-cell">
                    {user.followerCount}
                  </td>
                  <td className="rating-cell">
                    <div className="rating">
                      <span className="rating-value">{user.averageRating.toFixed(1)}</span>
                      <span className="rating-stars">⭐</span>
                    </div>
                  </td>
                  <td className="time-cell">
                    {user.lastLoginTime ? (
                      <div>
                        <div className="time-primary">
                          {formatRelativeTime(user.lastLoginTime)}
                        </div>
                        <div className="time-secondary">
                          {formatDate(user.lastLoginTime, 'MM-DD HH:mm')}
                        </div>
                      </div>
                    ) : (
                      <span className="never-login">从未登录</span>
                    )}
                  </td>
                  <td className="time-cell">
                    {formatDate(user.createTime, 'YYYY-MM-DD')}
                  </td>
                  <td>
                    {renderActions(user)}
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

export default UserList;
