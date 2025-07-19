import React, { useState, useEffect } from 'react';
import {
  AuditLogQueryParams,
  AuditLogResult,
  AuditLogResultLabels,
  TimeRangePresets,
  ActionOption,
  TargetTypeOption
} from '../types/audit-log';
import './AuditLogFilters.css';

interface AuditLogFiltersProps {
  filters: AuditLogQueryParams;
  onFiltersChange: (filters: AuditLogQueryParams) => void;
  onReset: () => void;
  actions: ActionOption[];
  targetTypes: TargetTypeOption[];
}

const AuditLogFilters: React.FC<AuditLogFiltersProps> = ({
  filters,
  onFiltersChange,
  onReset,
  actions,
  targetTypes
}) => {
  const [localFilters, setLocalFilters] = useState<AuditLogQueryParams>(filters);

  // 同步外部filters到本地状态
  useEffect(() => {
    setLocalFilters(filters);
  }, [filters]);

  // 处理筛选条件变化
  const handleFilterChange = (key: keyof AuditLogQueryParams, value: any) => {
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

  // 处理时间范围预设选择
  const handleTimeRangePreset = (preset: string) => {
    const now = new Date();
    let startTime: string;
    let endTime: string = now.toISOString().split('T')[0] + ' 23:59:59';

    switch (preset) {
      case 'today':
        startTime = now.toISOString().split('T')[0] + ' 00:00:00';
        break;
      case 'yesterday':
        const yesterday = new Date(now);
        yesterday.setDate(yesterday.getDate() - 1);
        startTime = yesterday.toISOString().split('T')[0] + ' 00:00:00';
        endTime = yesterday.toISOString().split('T')[0] + ' 23:59:59';
        break;
      case 'last7days':
        const last7days = new Date(now);
        last7days.setDate(last7days.getDate() - 7);
        startTime = last7days.toISOString().split('T')[0] + ' 00:00:00';
        break;
      case 'last30days':
        const last30days = new Date(now);
        last30days.setDate(last30days.getDate() - 30);
        startTime = last30days.toISOString().split('T')[0] + ' 00:00:00';
        break;
      case 'thisMonth':
        startTime = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0] + ' 00:00:00';
        break;
      case 'lastMonth':
        const lastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1);
        const lastMonthEnd = new Date(now.getFullYear(), now.getMonth(), 0);
        startTime = lastMonth.toISOString().split('T')[0] + ' 00:00:00';
        endTime = lastMonthEnd.toISOString().split('T')[0] + ' 23:59:59';
        break;
      default:
        return;
    }

    const newFilters = { ...localFilters, startTime, endTime };
    setLocalFilters(newFilters);
    onFiltersChange(newFilters);
  };

  // 重置筛选条件
  const handleReset = () => {
    const resetFilters: AuditLogQueryParams = {
      page: 1,
      pageSize: 20,
      sortBy: 'create_time',
      sortOrder: 'DESC'
    };
    setLocalFilters(resetFilters);
    onReset();
  };

  // 获取结果选项
  const resultOptions = [
    { label: '全部结果', value: undefined },
    { label: AuditLogResultLabels[AuditLogResult.SUCCESS], value: AuditLogResult.SUCCESS },
    { label: AuditLogResultLabels[AuditLogResult.FAILURE], value: AuditLogResult.FAILURE }
  ];

  return (
    <div className="audit-log-filters">
      <div className="filters-row">
        {/* 搜索框 */}
        <div className="filter-item">
          <label>关键词搜索</label>
          <input
            type="text"
            placeholder="搜索操作详情、IP地址等"
            value={localFilters.keyword || ''}
            onChange={(e) => handleKeywordChange(e.target.value)}
            className="search-input"
          />
        </div>

        {/* 操作类型筛选 */}
        <div className="filter-item">
          <label>操作类型</label>
          <select
            value={localFilters.action || ''}
            onChange={(e) => handleFilterChange('action', e.target.value || undefined)}
            className="filter-select"
          >
            <option value="">全部操作</option>
            {actions.map(action => (
              <option key={action.code} value={action.code}>
                {action.description}
              </option>
            ))}
          </select>
        </div>

        {/* 目标类型筛选 */}
        <div className="filter-item">
          <label>目标类型</label>
          <select
            value={localFilters.targetType || ''}
            onChange={(e) => handleFilterChange('targetType', e.target.value || undefined)}
            className="filter-select"
          >
            <option value="">全部类型</option>
            {targetTypes.map(type => (
              <option key={type.code} value={type.code}>
                {type.description}
              </option>
            ))}
          </select>
        </div>

        {/* 操作结果筛选 */}
        <div className="filter-item">
          <label>操作结果</label>
          <select
            value={localFilters.result ?? ''}
            onChange={(e) => handleFilterChange('result', 
              e.target.value === '' ? undefined : parseInt(e.target.value)
            )}
            className="filter-select"
          >
            {resultOptions.map(option => (
              <option key={option.value ?? 'all'} value={option.value ?? ''}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="filters-row">
        {/* 管理员ID筛选 */}
        <div className="filter-item">
          <label>管理员ID</label>
          <input
            type="number"
            placeholder="输入管理员ID"
            value={localFilters.adminId || ''}
            onChange={(e) => handleFilterChange('adminId', 
              e.target.value ? parseInt(e.target.value) : undefined
            )}
            className="filter-input"
          />
        </div>

        {/* IP地址筛选 */}
        <div className="filter-item">
          <label>IP地址</label>
          <input
            type="text"
            placeholder="输入IP地址"
            value={localFilters.ipAddress || ''}
            onChange={(e) => handleFilterChange('ipAddress', e.target.value || undefined)}
            className="filter-input"
          />
        </div>

        {/* 目标ID筛选 */}
        <div className="filter-item">
          <label>目标ID</label>
          <input
            type="number"
            placeholder="输入目标ID"
            value={localFilters.targetId || ''}
            onChange={(e) => handleFilterChange('targetId', 
              e.target.value ? parseInt(e.target.value) : undefined
            )}
            className="filter-input"
          />
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

      {/* 时间范围筛选 */}
      <div className="time-range-section">
        <div className="time-range-presets">
          <label>时间范围：</label>
          <div className="preset-buttons">
            {TimeRangePresets.map(preset => (
              <button
                key={preset.value}
                type="button"
                className="preset-btn"
                onClick={() => handleTimeRangePreset(preset.value)}
              >
                {preset.label}
              </button>
            ))}
          </div>
        </div>
        
        <div className="custom-time-range">
          <div className="time-input-group">
            <label>开始时间：</label>
            <input
              type="datetime-local"
              value={localFilters.startTime ? localFilters.startTime.replace(' ', 'T') : ''}
              onChange={(e) => handleFilterChange('startTime', 
                e.target.value ? e.target.value.replace('T', ' ') : undefined
              )}
              className="time-input"
            />
          </div>
          <div className="time-input-group">
            <label>结束时间：</label>
            <input
              type="datetime-local"
              value={localFilters.endTime ? localFilters.endTime.replace(' ', 'T') : ''}
              onChange={(e) => handleFilterChange('endTime', 
                e.target.value ? e.target.value.replace('T', ' ') : undefined
              )}
              className="time-input"
            />
          </div>
        </div>
      </div>

      {/* 当前筛选条件显示 */}
      <div className="active-filters">
        {localFilters.keyword && (
          <div className="filter-tag">
            关键词: {localFilters.keyword}
            <button onClick={() => handleFilterChange('keyword', undefined)}>×</button>
          </div>
        )}
        {localFilters.action && (
          <div className="filter-tag">
            操作: {actions.find(a => a.code === localFilters.action)?.description || localFilters.action}
            <button onClick={() => handleFilterChange('action', undefined)}>×</button>
          </div>
        )}
        {localFilters.targetType && (
          <div className="filter-tag">
            目标: {targetTypes.find(t => t.code === localFilters.targetType)?.description || localFilters.targetType}
            <button onClick={() => handleFilterChange('targetType', undefined)}>×</button>
          </div>
        )}
        {localFilters.result !== undefined && (
          <div className="filter-tag">
            结果: {AuditLogResultLabels[localFilters.result as AuditLogResult]}
            <button onClick={() => handleFilterChange('result', undefined)}>×</button>
          </div>
        )}
        {localFilters.adminId && (
          <div className="filter-tag">
            管理员: {localFilters.adminId}
            <button onClick={() => handleFilterChange('adminId', undefined)}>×</button>
          </div>
        )}
        {localFilters.startTime && (
          <div className="filter-tag">
            开始: {localFilters.startTime}
            <button onClick={() => handleFilterChange('startTime', undefined)}>×</button>
          </div>
        )}
        {localFilters.endTime && (
          <div className="filter-tag">
            结束: {localFilters.endTime}
            <button onClick={() => handleFilterChange('endTime', undefined)}>×</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuditLogFilters;
