// 产品管理类型定义

import { BaseApiResponse, PaginationParams } from '../../../shared/types/common';

// 商品状态枚举
export enum ProductStatus {
  PENDING_REVIEW = 0,  // 待审核
  ONSALE = 1,          // 在售
  SOLD = 2,            // 已售出
  DELISTED = 3,        // 已下架
  DRAFT = 4            // 草稿
}

// 商品状态标签映射
export const ProductStatusLabels: Record<ProductStatus, string> = {
  [ProductStatus.PENDING_REVIEW]: '待审核',
  [ProductStatus.ONSALE]: '在售',
  [ProductStatus.SOLD]: '已售出',
  [ProductStatus.DELISTED]: '已下架',
  [ProductStatus.DRAFT]: '草稿'
};

// 商品状态颜色映射
export const ProductStatusColors: Record<ProductStatus, string> = {
  [ProductStatus.PENDING_REVIEW]: '#faad14',  // 橙色
  [ProductStatus.ONSALE]: '#52c41a',          // 绿色
  [ProductStatus.SOLD]: '#1890ff',            // 蓝色
  [ProductStatus.DELISTED]: '#f5222d',        // 红色
  [ProductStatus.DRAFT]: '#d9d9d9'            // 灰色
};

// 商品基本信息
export interface Product {
  id: number;
  title: string;
  description?: string;
  price: number;
  status: ProductStatus;
  sellerId: number;
  sellerName: string;
  categoryId: number;
  categoryName?: string;
  imageUrls?: string[];
  createTime: string;
  updateTime: string;
}

// 商品查询参数
export interface ProductQueryParams {
  keyword?: string;
  status?: ProductStatus;
  sellerId?: number;
  categoryId?: number;
  pageNum?: number;
  pageSize?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

// 商品查询响应
export interface ProductQueryResponse extends BaseApiResponse {
  data?: {
    products: Product[];
    pagination: {
      pageNum: number;
      pageSize: number;
      total: number;
      totalPages: number;
    };
  };
}

// 商品详情响应
export interface ProductDetailResponse extends BaseApiResponse {
  data?: Product;
}

// 商品管理操作参数
export interface ProductManageParams {
  reason?: string;
}

// 商品管理操作响应
export interface ProductManageResponse extends BaseApiResponse {
  message?: string;
}

// 批量操作参数
export interface BatchOperationParams {
  productIds: number[];
  reason?: string;
}

// 商品筛选选项
export interface ProductFilterOptions {
  statuses: Array<{
    label: string;
    value: ProductStatus;
    color: string;
  }>;
  categories: Array<{
    label: string;
    value: number;
  }>;
}

// 商品操作类型
export enum ProductAction {
  APPROVE = 'approve',
  REJECT = 'reject',
  DELIST = 'delist',
  DELETE = 'delete'
}

// 商品操作配置
export interface ProductActionConfig {
  action: ProductAction;
  label: string;
  color: string;
  icon: string;
  requiresReason: boolean;
  confirmMessage: string;
}

// 商品操作配置映射
export const ProductActionConfigs: Record<ProductAction, ProductActionConfig> = {
  [ProductAction.APPROVE]: {
    action: ProductAction.APPROVE,
    label: '审核通过',
    color: '#52c41a',
    icon: '✓',
    requiresReason: false,
    confirmMessage: '确认审核通过选中的商品吗？'
  },
  [ProductAction.REJECT]: {
    action: ProductAction.REJECT,
    label: '审核拒绝',
    color: '#f5222d',
    icon: '✗',
    requiresReason: true,
    confirmMessage: '确认拒绝选中的商品吗？请填写拒绝原因。'
  },
  [ProductAction.DELIST]: {
    action: ProductAction.DELIST,
    label: '下架商品',
    color: '#faad14',
    icon: '↓',
    requiresReason: false,
    confirmMessage: '确认下架选中的商品吗？'
  },
  [ProductAction.DELETE]: {
    action: ProductAction.DELETE,
    label: '删除商品',
    color: '#ff4d4f',
    icon: '🗑',
    requiresReason: false,
    confirmMessage: '确认删除选中的商品吗？此操作不可恢复！'
  }
};

// 表格列配置
export interface TableColumn {
  key: string;
  title: string;
  dataIndex: string;
  width?: number;
  sortable?: boolean;
  render?: (value: any, record: Product) => React.ReactNode;
}

// 商品管理页面状态
export interface ProductManagementState {
  products: Product[];
  loading: boolean;
  selectedProductIds: number[];
  filters: ProductQueryParams;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  auditModalVisible: boolean;
  currentAction?: ProductAction;
}
