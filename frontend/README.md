# Shiwu管理后台前端

这是Shiwu校园二手交易平台的管理员后台登录页面，使用React + TypeScript + Vite构建。

## 功能特性

### 核心功能
- 管理员登录功能
- 数据仪表盘（使用ECharts图表库）
- JWT Token认证
- 响应式设计
- 错误处理和用户反馈
- 路由保护
- 现代化UI设计

### 仪表盘功能
- 实时统计数据展示（用户、商品、交易等）
- 趋势分析图表（用户注册、商品发布、交易量）
- 分布统计图表（商品分类、用户活跃度）
- 热门商品排行榜
- 数据自动刷新和手动刷新

## 技术栈

- React 18
- TypeScript
- Vite
- React Router DOM
- Axios
- ECharts 5.4.2 + echarts-for-react
- CSS3

## 项目结构

```
src/
├── components/          # 可复用组件
│   ├── charts/         # 图表组件
│   │   ├── BaseChart.tsx
│   │   ├── LineChart.tsx
│   │   ├── PieChart.tsx
│   │   ├── BarChart.tsx
│   │   └── charts.css
│   ├── dashboard/      # 仪表盘组件
│   │   ├── DashboardLayout.tsx
│   │   ├── DashboardLayout.css
│   │   ├── StatCard.tsx
│   │   └── StatCard.css
│   └── ProtectedRoute.tsx
├── contexts/           # React上下文
│   └── AuthContext.tsx
├── pages/              # 页面组件
│   ├── LoginPage.tsx
│   ├── LoginPage.css
│   ├── DashboardPage.tsx
│   └── DashboardPage.css
├── services/           # API服务
│   ├── api.ts
│   └── dashboardApi.ts
├── types/              # TypeScript类型定义
│   ├── auth.ts
│   └── dashboard.ts
├── App.tsx
├── App.css
├── main.tsx
└── index.css
```

## 安装和运行

1. 安装依赖：
```bash
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

3. 构建生产版本：
```bash
npm run build
```

## API接口

前端调用后端管理员登录API：
- URL: `POST /api/admin/login`
- 请求体: `{ "username": "string", "password": "string" }`
- 响应: 包含管理员信息和JWT token

## 测试账号

请使用后端数据库中配置的管理员账号进行测试。

## 注意事项

1. 确保后端服务运行在 `http://localhost:8080`
2. 前端开发服务器运行在 `http://localhost:3000`
3. 已配置代理，前端API请求会自动转发到后端
4. JWT Token存储在localStorage中
5. 支持自动登录状态保持
