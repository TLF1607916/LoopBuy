// 审计日志类型定义

import { BaseApiResponse, PaginationParams } from '../../../shared/types/common';

// 审计日志基本信息
export interface AuditLog {
  id: number;
  adminId: number;
  adminUsername?: string;
  action: string;
  actionDescription?: string;
  targetType?: string;
  targetTypeDescription?: string;
  targetId?: number;
  details?: string;
  ipAddress?: string;
  userAgent?: string;
  result: number; // 0-失败, 1-成功
  resultText?: string;
  createTime: string;
  createTimeText?: string;
}

// 审计日志查询参数
export interface AuditLogQueryParams {
  adminId?: number;
  action?: string;
  targetType?: string;
  targetId?: number;
  ipAddress?: string;
  result?: number;
  startTime?: string;
  endTime?: string;
  keyword?: string;
  page?: number;
  pageSize?: number;
  sortBy?: string;
  sortOrder?: 'ASC' | 'DESC';
}

// 审计日志查询响应
export interface AuditLogQueryResponse extends BaseApiResponse {
  data?: {
    auditLogs: AuditLog[];
    pagination: {
      page: number;
      pageSize: number;
      total: number;
      totalPages: number;
    };
  };
}

// 审计日志详情响应
export interface AuditLogDetailResponse extends BaseApiResponse {
  data?: AuditLog;
}

// 审计日志统计信息
export interface AuditLogStats {
  totalLogs: number;
  successLogs: number;
  failureLogs: number;
  todayLogs: number;
  weekLogs: number;
  monthLogs: number;
  topActions: Array<{
    action: string;
    actionDescription: string;
    count: number;
  }>;
  topAdmins: Array<{
    adminId: number;
    adminUsername: string;
    count: number;
  }>;
}

// 审计日志统计响应
export interface AuditLogStatsResponse extends BaseApiResponse {
  data?: AuditLogStats;
}

// 审计日志趋势数据
export interface AuditLogTrendData {
  date: string;
  totalCount: number;
  successCount: number;
  failureCount: number;
}

// 审计日志趋势响应
export interface AuditLogTrendResponse extends BaseApiResponse {
  data?: AuditLogTrendData[];
}

// 操作类型选项
export interface ActionOption {
  code: string;
  description: string;
}

// 目标类型选项
export interface TargetTypeOption {
  code: string;
  description: string;
}

// 操作类型响应
export interface ActionsResponse extends BaseApiResponse {
  data?: ActionOption[];
}

// 目标类型响应
export interface TargetTypesResponse extends BaseApiResponse {
  data?: TargetTypeOption[];
}

// 审计日志导出参数
export interface AuditLogExportParams extends AuditLogQueryParams {
  format?: 'csv' | 'excel';
}

// 审计日志导出响应
export interface AuditLogExportResponse extends BaseApiResponse {
  data?: {
    downloadUrl: string;
    filename: string;
    fileSize: number;
  };
}

// 审计日志筛选选项
export interface AuditLogFilterOptions {
  actions: ActionOption[];
  targetTypes: TargetTypeOption[];
  results: Array<{
    label: string;
    value: number;
  }>;
}

// 审计日志页面状态
export interface AuditLogPageState {
  auditLogs: AuditLog[];
  loading: boolean;
  filters: AuditLogQueryParams;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  stats?: AuditLogStats;
  trendData?: AuditLogTrendData[];
  detailModalVisible: boolean;
  selectedLog?: AuditLog;
}

// 表格列配置
export interface AuditLogTableColumn {
  key: string;
  title: string;
  dataIndex: string;
  width?: number;
  sortable?: boolean;
  render?: (value: any, record: AuditLog) => React.ReactNode;
}

// 审计日志操作结果枚举
export enum AuditLogResult {
  FAILURE = 0,
  SUCCESS = 1
}

// 审计日志操作结果标签映射
export const AuditLogResultLabels: Record<AuditLogResult, string> = {
  [AuditLogResult.FAILURE]: '失败',
  [AuditLogResult.SUCCESS]: '成功'
};

// 审计日志操作结果颜色映射
export const AuditLogResultColors: Record<AuditLogResult, string> = {
  [AuditLogResult.FAILURE]: '#f5222d',
  [AuditLogResult.SUCCESS]: '#52c41a'
};

// 常用操作类型分组
export const ActionGroups = {
  ADMIN: {
    label: '管理员操作',
    actions: ['ADMIN_LOGIN', 'ADMIN_LOGOUT', 'ADMIN_CREATE', 'ADMIN_UPDATE', 'ADMIN_DELETE']
  },
  USER: {
    label: '用户管理',
    actions: ['USER_BAN', 'USER_UNBAN', 'USER_MUTE', 'USER_UNMUTE', 'USER_DELETE']
  },
  PRODUCT: {
    label: '商品管理',
    actions: ['PRODUCT_APPROVE', 'PRODUCT_REJECT', 'PRODUCT_TAKEDOWN', 'PRODUCT_DELETE']
  },
  SYSTEM: {
    label: '系统操作',
    actions: ['SYSTEM_CONFIG_UPDATE', 'SYSTEM_MAINTENANCE', 'DATA_EXPORT', 'DATA_IMPORT']
  },
  AUDIT: {
    label: '审计操作',
    actions: ['AUDIT_LOG_VIEW', 'AUDIT_LOG_EXPORT']
  }
};

// 常用目标类型
export const TargetTypes = {
  ADMIN: 'ADMIN',
  USER: 'USER',
  PRODUCT: 'PRODUCT',
  ORDER: 'ORDER',
  SYSTEM: 'SYSTEM',
  AUDIT_LOG: 'AUDIT_LOG'
};

// 时间范围预设选项
export const TimeRangePresets = [
  { label: '今天', value: 'today' },
  { label: '昨天', value: 'yesterday' },
  { label: '最近7天', value: 'last7days' },
  { label: '最近30天', value: 'last30days' },
  { label: '本月', value: 'thisMonth' },
  { label: '上月', value: 'lastMonth' }
];

// 分页大小选项
export const PageSizeOptions = [10, 20, 50, 100];

// 排序字段选项
export const SortFieldOptions = [
  { label: '操作时间', value: 'create_time' },
  { label: '管理员ID', value: 'admin_id' },
  { label: '操作类型', value: 'action' },
  { label: '操作结果', value: 'result' }
];

// 导出格式选项
export const ExportFormatOptions = [
  { label: 'CSV格式', value: 'csv' },
  { label: 'Excel格式', value: 'excel' }
];
