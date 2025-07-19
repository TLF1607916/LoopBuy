// 仪表盘数据类型定义

// 核心统计指标
export interface DashboardStats {
  totalUsers: number;
  todayNewUsers: number;
  totalProducts: number;
  todayNewProducts: number;
  totalTransactions: number;
  todayTransactionAmount: number;
  activeUsers: number;
  platformGrowthRate: number;
}

// 趋势数据点
export interface TrendDataPoint {
  date: string;
  value: number;
}

// 用户统计趋势
export interface UserTrend {
  registrationTrend: TrendDataPoint[];
  activeTrend: TrendDataPoint[];
}

// 商品统计趋势
export interface ProductTrend {
  publishTrend: TrendDataPoint[];
  soldTrend: TrendDataPoint[];
}

// 交易统计趋势
export interface TransactionTrend {
  volumeTrend: TrendDataPoint[];
  amountTrend: TrendDataPoint[];
}

// 分类分布数据
export interface CategoryDistribution {
  name: string;
  value: number;
  percentage: number;
}

// 商品状态分布
export interface ProductStatusDistribution {
  onSale: number;
  sold: number;
  draft: number;
  offline: number;
}

// 用户活跃度分布
export interface UserActivityDistribution {
  veryActive: number;    // 非常活跃（近7天有活动）
  active: number;        // 活跃（近30天有活动）
  inactive: number;      // 不活跃（30天以上无活动）
}

// 热门商品数据
export interface PopularProduct {
  id: number;
  title: string;
  category: string;
  price: number;
  viewCount: number;
  favoriteCount: number;
}

// 仪表盘完整数据
export interface DashboardData {
  stats: DashboardStats;
  userTrend: UserTrend;
  productTrend: ProductTrend;
  transactionTrend: TransactionTrend;
  categoryDistribution: CategoryDistribution[];
  productStatusDistribution: ProductStatusDistribution;
  userActivityDistribution: UserActivityDistribution;
  popularProducts: PopularProduct[];
  lastUpdated: string;
}

// API响应类型
export interface DashboardResponse {
  success: boolean;
  data?: DashboardData;
  error?: {
    code: string;
    message: string;
  };
}

// 图表配置类型
export interface ChartConfig {
  title?: string;
  height?: number;
  showLegend?: boolean;
  showGrid?: boolean;
  colors?: string[];
}

// ECharts选项类型（简化版）
export interface EChartsOption {
  title?: any;
  tooltip?: any;
  legend?: any;
  grid?: any;
  xAxis?: any;
  yAxis?: any;
  series?: any[];
  color?: string[];
}
