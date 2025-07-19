// API常量定义

// API基础配置
export const API_CONFIG = {
  BASE_URL: '/api',
  TIMEOUT: 10000,
  RETRY_TIMES: 3,
  RETRY_DELAY: 1000,
} as const;

// API端点
export const API_ENDPOINTS = {
  // 认证相关
  AUTH: {
    LOGIN: '/admin/login',
    LOGOUT: '/admin/logout',
    REFRESH_TOKEN: '/admin/refresh-token',
    VALIDATE_TOKEN: '/admin/validate-token',
  },
  
  // 仪表盘相关
  DASHBOARD: {
    STATS: '/admin/dashboard/stats',
    USER_STATS: '/admin/dashboard/user-stats',
    PRODUCT_STATS: '/admin/dashboard/product-stats',
    TRANSACTION_STATS: '/admin/dashboard/transaction-stats',
  },
  
  // 用户管理
  USER: {
    LIST: '/admin/users',
    DETAIL: '/admin/users/:id',
    CREATE: '/admin/users',
    UPDATE: '/admin/users/:id',
    DELETE: '/admin/users/:id',
    BATCH_DELETE: '/admin/users/batch-delete',
  },
  
  // 商品管理
  PRODUCT: {
    LIST: '/admin/products',
    DETAIL: '/admin/products/:id',
    CREATE: '/admin/products',
    UPDATE: '/admin/products/:id',
    DELETE: '/admin/products/:id',
    BATCH_DELETE: '/admin/products/batch-delete',
    AUDIT: '/admin/products/:id/audit',
  },
  
  // 交易管理
  TRANSACTION: {
    LIST: '/admin/transactions',
    DETAIL: '/admin/transactions/:id',
    STATS: '/admin/transactions/stats',
  },
  
  // 文件上传
  UPLOAD: {
    IMAGE: '/admin/upload/image',
    FILE: '/admin/upload/file',
  },
} as const;

// HTTP状态码
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
} as const;

// 错误码
export const ERROR_CODES = {
  NETWORK_ERROR: 'NETWORK_ERROR',
  TIMEOUT_ERROR: 'TIMEOUT_ERROR',
  AUTH_ERROR: 'AUTH_ERROR',
  PERMISSION_ERROR: 'PERMISSION_ERROR',
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  SERVER_ERROR: 'SERVER_ERROR',
  UNKNOWN_ERROR: 'UNKNOWN_ERROR',
} as const;

// 请求头
export const REQUEST_HEADERS = {
  CONTENT_TYPE: 'Content-Type',
  AUTHORIZATION: 'Authorization',
  ACCEPT: 'Accept',
  USER_AGENT: 'User-Agent',
} as const;

// 内容类型
export const CONTENT_TYPES = {
  JSON: 'application/json',
  FORM_DATA: 'multipart/form-data',
  FORM_URLENCODED: 'application/x-www-form-urlencoded',
  TEXT: 'text/plain',
} as const;
