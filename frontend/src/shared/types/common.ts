// 共享类型定义

// 基础API响应类型
export interface BaseApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    userTip?: string;
  };
  timestamp?: string;
}

// 分页参数
export interface PaginationParams {
  page: number;
  pageSize: number;
  total?: number;
}

// 分页响应
export interface PaginatedResponse<T> extends BaseApiResponse<T[]> {
  pagination: {
    page: number;
    pageSize: number;
    total: number;
    totalPages: number;
  };
}

// 排序参数
export interface SortParams {
  field: string;
  order: 'asc' | 'desc';
}

// 筛选参数
export interface FilterParams {
  [key: string]: any;
}

// 查询参数
export interface QueryParams extends PaginationParams {
  sort?: SortParams;
  filters?: FilterParams;
  search?: string;
}

// 操作结果
export interface OperationResult {
  success: boolean;
  message?: string;
  data?: any;
}

// 文件上传响应
export interface FileUploadResponse extends BaseApiResponse {
  data?: {
    url: string;
    filename: string;
    size: number;
    type: string;
  };
}

// 选项类型
export interface Option {
  label: string;
  value: string | number;
  disabled?: boolean;
}

// 菜单项类型
export interface MenuItem {
  key: string;
  label: string;
  icon?: string;
  path?: string;
  children?: MenuItem[];
  disabled?: boolean;
}

// 面包屑项类型
export interface BreadcrumbItem {
  title: string;
  path?: string;
}
