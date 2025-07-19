# 模块使用示例

## 🔐 Auth模块使用示例

### 1. 在主应用中使用认证功能

```typescript
// App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { 
  AuthProvider, 
  LoginPage, 
  ProtectedRoute 
} from './modules/auth';
import { DashboardPage } from './modules/dashboard';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}
```

### 2. 在组件中使用认证状态

```typescript
// 任意组件中
import React from 'react';
import { useAuth } from './modules/auth';

const UserProfile: React.FC = () => {
  const { admin, logout, isLoading } = useAuth();

  if (isLoading) {
    return <div>加载中...</div>;
  }

  return (
    <div>
      <h2>欢迎，{admin?.realName}</h2>
      <p>角色：{admin?.roleDescription}</p>
      <button onClick={logout}>退出登录</button>
    </div>
  );
};
```

### 3. 自定义API调用

```typescript
// 使用Auth API服务
import { authApi } from './modules/auth';

const handleCustomLogin = async (username: string, password: string) => {
  const result = await authApi.adminLogin({ username, password });
  if (result.success) {
    console.log('登录成功', result.data);
  } else {
    console.error('登录失败', result.error);
  }
};
```

## 📊 Dashboard模块使用示例

### 1. 使用完整的仪表盘页面

```typescript
// 直接使用完整页面
import { DashboardPage } from './modules/dashboard';

const AdminPanel: React.FC = () => {
  return <DashboardPage />;
};
```

### 2. 使用单独的图表组件

```typescript
// 使用单独的图表组件
import React from 'react';
import { 
  LineChart, 
  PieChart, 
  BarChart, 
  StatCard 
} from './modules/dashboard';

const CustomDashboard: React.FC = () => {
  const trendData = [
    { date: '2024-01-01', value: 100 },
    { date: '2024-01-02', value: 120 },
    { date: '2024-01-03', value: 150 },
  ];

  const categoryData = [
    { name: '电子产品', value: 856, percentage: 35.2 },
    { name: '图书教材', value: 642, percentage: 26.4 },
  ];

  return (
    <div>
      <StatCard
        title="总用户数"
        value={1248}
        change={23}
        changeLabel="今日新增"
        icon="👥"
        color="primary"
      />
      
      <LineChart
        data={trendData}
        title="用户增长趋势"
        yAxisLabel="用户数"
        smooth={true}
        area={true}
      />
      
      <PieChart
        data={categoryData}
        title="商品分类分布"
        showPercentage={true}
      />
    </div>
  );
};
```

### 3. 使用布局组件

```typescript
// 使用仪表盘布局
import React from 'react';
import { 
  DashboardLayout, 
  DashboardGrid, 
  DashboardCard 
} from './modules/dashboard';

const MyDashboard: React.FC = () => {
  return (
    <DashboardLayout
      title="我的仪表盘"
      subtitle="自定义数据展示"
      actions={
        <button onClick={() => console.log('刷新')}>
          刷新数据
        </button>
      }
    >
      <DashboardGrid columns={3}>
        <DashboardCard title="卡片1">
          <p>内容1</p>
        </DashboardCard>
        <DashboardCard title="卡片2">
          <p>内容2</p>
        </DashboardCard>
        <DashboardCard title="卡片3">
          <p>内容3</p>
        </DashboardCard>
      </DashboardGrid>
    </DashboardLayout>
  );
};
```

## 🛠️ Shared模块使用示例

### 1. 使用格式化工具

```typescript
// 使用格式化工具
import { 
  formatCurrency, 
  formatDate, 
  formatNumber,
  formatPercentage 
} from './shared';

const ProductCard: React.FC = ({ product }) => {
  return (
    <div>
      <h3>{product.name}</h3>
      <p>价格: {formatCurrency(product.price)}</p>
      <p>浏览量: {formatNumber(product.views)}</p>
      <p>增长率: {formatPercentage(product.growthRate)}</p>
      <p>发布时间: {formatDate(product.createdAt, 'YYYY-MM-DD HH:mm')}</p>
    </div>
  );
};
```

### 2. 使用API常量

```typescript
// 使用API常量
import { API_ENDPOINTS, HTTP_STATUS } from './shared';
import { baseApi } from './shared';

const fetchUserList = async () => {
  try {
    const response = await baseApi.get(API_ENDPOINTS.USER.LIST);
    if (response.status === HTTP_STATUS.OK) {
      return response.data;
    }
  } catch (error) {
    console.error('获取用户列表失败', error);
  }
};
```

### 3. 使用通用类型

```typescript
// 使用通用类型
import { 
  BaseApiResponse, 
  PaginationParams, 
  QueryParams 
} from './shared';

interface UserListResponse extends BaseApiResponse<User[]> {
  pagination: PaginationParams;
}

const searchUsers = async (params: QueryParams): Promise<UserListResponse> => {
  // API调用逻辑
};
```

## 🎯 最佳实践

### 1. 模块导入建议

```typescript
// ✅ 推荐：从模块根目录导入
import { LoginPage, useAuth } from './modules/auth';
import { DashboardPage, LineChart } from './modules/dashboard';

// ❌ 不推荐：深层路径导入
import LoginPage from './modules/auth/pages/LoginPage';
import LineChart from './modules/dashboard/components/charts/LineChart';
```

### 2. 类型定义使用

```typescript
// ✅ 推荐：使用模块提供的类型
import { AdminVO, DashboardData } from './modules/auth';
import { TrendDataPoint } from './modules/dashboard';

// 扩展模块类型
interface ExtendedAdminVO extends AdminVO {
  customField: string;
}
```

### 3. 组件组合使用

```typescript
// ✅ 推荐：组合使用模块组件
import { AuthProvider, ProtectedRoute } from './modules/auth';
import { DashboardLayout, StatCard } from './modules/dashboard';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <ProtectedRoute>
        <DashboardLayout>
          <StatCard title="示例" value={100} />
        </DashboardLayout>
      </ProtectedRoute>
    </AuthProvider>
  );
};
```

---

这些示例展示了如何在实际项目中使用重构后的模块化架构。每个模块都提供了清晰的接口和完整的功能封装。
