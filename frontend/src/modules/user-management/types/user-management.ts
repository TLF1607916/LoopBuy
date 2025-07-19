// 用户管理类型定义

import { BaseApiResponse, PaginationParams } from '../../../shared/types/common';

// 用户状态枚举
export enum UserStatus {
  ACTIVE = 0,    // 正常
  BANNED = 1,    // 已封禁
  MUTED = 2      // 已禁言
}

// 用户状态标签映射
export const UserStatusLabels: Record<UserStatus, string> = {
  [UserStatus.ACTIVE]: '正常',
  [UserStatus.BANNED]: '已封禁',
  [UserStatus.MUTED]: '已禁言'
};

// 用户状态颜色映射
export const UserStatusColors: Record<UserStatus, string> = {
  [UserStatus.ACTIVE]: '#52c41a',    // 绿色
  [UserStatus.BANNED]: '#f5222d',    // 红色
  [UserStatus.MUTED]: '#faad14'      // 橙色
};

// 性别枚举
export enum Gender {
  UNSET = 0,     // 未设置
  MALE = 1,      // 男
  FEMALE = 2     // 女
}

// 性别标签映射
export const GenderLabels: Record<Gender, string> = {
  [Gender.UNSET]: '未设置',
  [Gender.MALE]: '男',
  [Gender.FEMALE]: '女'
};

// 用户基本信息
export interface User {
  id: number;
  username: string;
  email?: string;
  phone?: string;
  status: UserStatus;
  avatarUrl?: string;
  nickname?: string;
  gender?: Gender;
  bio?: string;
  followerCount: number;
  averageRating: number;
  lastLoginTime?: string;
  createTime: string;
  updateTime: string;
}

// 用户查询参数
export interface UserQueryParams {
  keyword?: string;
  status?: UserStatus;
  gender?: Gender;
  pageNum?: number;
  pageSize?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

// 用户查询响应
export interface UserQueryResponse extends BaseApiResponse {
  data?: {
    users: User[];
    pagination: {
      pageNum: number;
      pageSize: number;
      total: number;
      totalPages: number;
    };
  };
}

// 用户详情响应
export interface UserDetailResponse extends BaseApiResponse {
  data?: User;
}

// 用户管理操作参数
export interface UserManageParams {
  reason?: string;
}

// 用户管理操作响应
export interface UserManageResponse extends BaseApiResponse {
  message?: string;
}

// 批量操作参数
export interface BatchUserOperationParams {
  userIds: number[];
  reason?: string;
}

// 用户筛选选项
export interface UserFilterOptions {
  statuses: Array<{
    label: string;
    value: UserStatus;
    color: string;
  }>;
  genders: Array<{
    label: string;
    value: Gender;
  }>;
}

// 用户操作类型
export enum UserAction {
  BAN = 'ban',
  UNBAN = 'unban',
  MUTE = 'mute',
  UNMUTE = 'unmute'
}

// 用户操作配置
export interface UserActionConfig {
  action: UserAction;
  label: string;
  color: string;
  icon: string;
  requiresReason: boolean;
  confirmMessage: string;
}

// 用户操作配置映射
export const UserActionConfigs: Record<UserAction, UserActionConfig> = {
  [UserAction.BAN]: {
    action: UserAction.BAN,
    label: '封禁用户',
    color: '#f5222d',
    icon: '🚫',
    requiresReason: true,
    confirmMessage: '确认封禁选中的用户吗？请填写封禁原因。'
  },
  [UserAction.UNBAN]: {
    action: UserAction.UNBAN,
    label: '解封用户',
    color: '#52c41a',
    icon: '✅',
    requiresReason: false,
    confirmMessage: '确认解封选中的用户吗？'
  },
  [UserAction.MUTE]: {
    action: UserAction.MUTE,
    label: '禁言用户',
    color: '#faad14',
    icon: '🔇',
    requiresReason: true,
    confirmMessage: '确认禁言选中的用户吗？请填写禁言原因。'
  },
  [UserAction.UNMUTE]: {
    action: UserAction.UNMUTE,
    label: '解除禁言',
    color: '#1890ff',
    icon: '🔊',
    requiresReason: false,
    confirmMessage: '确认解除禁言选中的用户吗？'
  }
};

// 表格列配置
export interface TableColumn {
  key: string;
  title: string;
  dataIndex: string;
  width?: number;
  sortable?: boolean;
  render?: (value: any, record: User) => React.ReactNode;
}

// 用户管理页面状态
export interface UserManagementState {
  users: User[];
  loading: boolean;
  selectedUserIds: number[];
  filters: UserQueryParams;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  actionModalVisible: boolean;
  currentAction?: UserAction;
}

// 用户统计信息
export interface UserStats {
  totalUsers: number;
  activeUsers: number;
  bannedUsers: number;
  mutedUsers: number;
  newUsersToday: number;
  newUsersThisWeek: number;
  newUsersThisMonth: number;
}
