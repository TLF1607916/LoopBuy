import React, { useState, useEffect } from 'react';
import {
  UserQueryParams,
  UserStatus,
  UserStatusLabels,
  UserStatusColors,
  Gender,
  GenderLabels
} from '../types/user-management';
import './UserFilters.css';

interface UserFiltersProps {
  filters: UserQueryParams;
  onFiltersChange: (filters: UserQueryParams) => void;
  onReset: () => void;
}

const UserFilters: React.FC<UserFiltersProps> = ({
  filters,
  onFiltersChange,
  onReset
}) => {
  const [localFilters, setLocalFilters] = useState<UserQueryParams>(filters);

  // 同步外部filters到本地状态
  useEffect(() => {
    setLocalFilters(filters);
  }, [filters]);

  // 处理筛选条件变化
  const handleFilterChange = (key: keyof UserQueryParams, value: any) => {
    const newFilters = { ...localFilters, [key]: value };
    setLocalFilters(newFilters);
    onFiltersChange(newFilters);
  };

  // 处理搜索关键词变化（防抖）
  const handleKeywordChange = (keyword: string) => {
    setLocalFilters(prev => ({ ...prev, keyword }));
    // 简单的防抖实现
    setTimeout(() => {
      onFiltersChange({ ...localFilters, keyword });
    }, 500);
  };

  // 重置筛选条件
  const handleReset = () => {
    const resetFilters: UserQueryParams = {
      keyword: '',
      status: undefined,
      gender: undefined,
      pageNum: 1,
      pageSize: 20,
      sortBy: 'create_time',
      sortDirection: 'DESC'
    };
    setLocalFilters(resetFilters);
    onReset();
  };

  // 获取状态选项
  const statusOptions = [
    { label: '全部状态', value: undefined },
    ...Object.entries(UserStatusLabels).map(([value, label]) => ({
      label,
      value: parseInt(value) as UserStatus,
      color: UserStatusColors[parseInt(value) as UserStatus]
    }))
  ];

  // 获取性别选项
  const genderOptions = [
    { label: '全部性别', value: undefined },
    ...Object.entries(GenderLabels).map(([value, label]) => ({
      label,
      value: parseInt(value) as Gender
    }))
  ];

  return (
    <div className="user-filters">
      <div className="filters-row">
        {/* 搜索框 */}
        <div className="filter-item">
          <label>搜索用户</label>
          <input
            type="text"
            placeholder="输入用户名、邮箱或手机号"
            value={localFilters.keyword || ''}
            onChange={(e) => handleKeywordChange(e.target.value)}
            className="search-input"
          />
        </div>

        {/* 状态筛选 */}
        <div className="filter-item">
          <label>用户状态</label>
          <select
            value={localFilters.status ?? ''}
            onChange={(e) => handleFilterChange('status', 
              e.target.value === '' ? undefined : parseInt(e.target.value)
            )}
            className="filter-select"
          >
            {statusOptions.map(option => (
              <option key={option.value ?? 'all'} value={option.value ?? ''}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        {/* 性别筛选 */}
        <div className="filter-item">
          <label>性别</label>
          <select
            value={localFilters.gender ?? ''}
            onChange={(e) => handleFilterChange('gender', 
              e.target.value === '' ? undefined : parseInt(e.target.value)
            )}
            className="filter-select"
          >
            {genderOptions.map(option => (
              <option key={option.value ?? 'all'} value={option.value ?? ''}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        {/* 操作按钮 */}
        <div className="filter-actions">
          <button 
            onClick={handleReset}
            className="reset-btn"
          >
            重置筛选
          </button>
        </div>
      </div>

      {/* 当前筛选条件显示 */}
      <div className="active-filters">
        {localFilters.keyword && (
          <div className="filter-tag">
            关键词: {localFilters.keyword}
            <button onClick={() => handleFilterChange('keyword', '')}>×</button>
          </div>
        )}
        {localFilters.status !== undefined && (
          <div className="filter-tag">
            状态: {UserStatusLabels[localFilters.status]}
            <button onClick={() => handleFilterChange('status', undefined)}>×</button>
          </div>
        )}
        {localFilters.gender !== undefined && (
          <div className="filter-tag">
            性别: {GenderLabels[localFilters.gender]}
            <button onClick={() => handleFilterChange('gender', undefined)}>×</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserFilters;
