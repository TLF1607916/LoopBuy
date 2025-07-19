# Task5_3_1_3: 审计日志查询API使用文档

## 概述

Task5_3_1_3实现了完整的审计日志查询API，支持筛选和搜索功能。该API提供了强大的查询能力，满足NFR-SEC-03安全需求。

## API端点

### 基础URL
```
/api/admin/audit-logs
```

## 主要功能

### 1. 查询审计日志列表
**GET** `/api/admin/audit-logs`

#### 支持的查询参数：

| 参数名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| adminId | Long | 管理员ID | `1` |
| action | String | 操作类型 | `USER_BAN` |
| targetType | String | 目标类型 | `USER` |
| targetId | Long | 目标ID | `123` |
| ipAddress | String | IP地址 | `127.0.0.1` |
| result | Integer | 操作结果(0-失败,1-成功) | `1` |
| startTime | String | 开始时间 | `2024-01-01 00:00:00` |
| endTime | String | 结束时间 | `2024-12-31 23:59:59` |
| keyword | String | 关键词搜索 | `测试` |
| page | Integer | 页码(默认1) | `1` |
| pageSize | Integer | 每页大小(默认20) | `20` |
| sortBy | String | 排序字段(默认create_time) | `create_time` |
| sortOrder | String | 排序方向(默认DESC) | `DESC` |

#### 示例请求：
```bash
# 基本查询
GET /api/admin/audit-logs?page=1&pageSize=20

# 筛选特定管理员的操作
GET /api/admin/audit-logs?adminId=1&page=1&pageSize=10

# 查询用户封禁操作
GET /api/admin/audit-logs?action=USER_BAN&targetType=USER

# 关键词搜索
GET /api/admin/audit-logs?keyword=测试&page=1&pageSize=15

# 时间范围查询
GET /api/admin/audit-logs?startTime=2024-01-01 00:00:00&endTime=2024-01-31 23:59:59

# 复合条件查询
GET /api/admin/audit-logs?adminId=1&action=USER_BAN&result=1&keyword=违规&page=1&pageSize=10
```

#### 响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "adminId": 1,
        "adminUsername": "admin",
        "action": "USER_BAN",
        "actionDescription": "封禁用户",
        "targetType": "USER",
        "targetTypeDescription": "用户",
        "targetId": 123,
        "details": "封禁用户：违规行为",
        "ipAddress": "127.0.0.1",
        "userAgent": "Mozilla/5.0",
        "result": 1,
        "createTime": "2024-01-15T10:30:00"
      }
    ],
    "totalCount": 100,
    "page": 1,
    "pageSize": 20,
    "totalPages": 5
  }
}
```

### 2. 获取审计日志详情
**GET** `/api/admin/audit-logs/{id}`

#### 示例请求：
```bash
GET /api/admin/audit-logs/123
```

#### 响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 123,
    "adminId": 1,
    "adminUsername": "admin",
    "action": "USER_BAN",
    "actionDescription": "封禁用户",
    "targetType": "USER",
    "targetTypeDescription": "用户",
    "targetId": 456,
    "details": "封禁用户：发布违规内容",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
    "result": 1,
    "createTime": "2024-01-15T10:30:00"
  }
}
```

### 3. 获取可用操作类型
**GET** `/api/admin/audit-logs/actions`

#### 响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "code": "USER_BAN",
      "description": "封禁用户"
    },
    {
      "code": "PRODUCT_APPROVE",
      "description": "审核商品"
    }
  ]
}
```

### 4. 获取可用目标类型
**GET** `/api/admin/audit-logs/target-types`

#### 响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "code": "USER",
      "description": "用户"
    },
    {
      "code": "PRODUCT",
      "description": "商品"
    }
  ]
}
```

### 5. 获取操作统计数据
**GET** `/api/admin/audit-logs/stats?days=7`

#### 响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "totalOperations": 1000,
    "successOperations": 950,
    "failedOperations": 50,
    "successRate": 0.95
  }
}
```

### 6. 获取活动趋势数据
**GET** `/api/admin/audit-logs/trend?days=30`

#### 响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "date": "2024-01-01",
      "count": 25
    },
    {
      "date": "2024-01-02",
      "count": 30
    }
  ]
}
```

### 7. 导出审计日志
**POST** `/api/admin/audit-logs/export`

#### 请求体：
```json
{
  "adminId": 1,
  "action": "USER_BAN",
  "startTime": "2024-01-01 00:00:00",
  "endTime": "2024-01-31 23:59:59"
}
```

#### 响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "adminUsername": "admin",
      "action": "USER_BAN",
      "actionDescription": "封禁用户",
      "targetType": "USER",
      "details": "封禁用户：违规行为",
      "createTime": "2024-01-15T10:30:00"
    }
  ]
}
```

## 权限要求

所有API端点都需要：
1. 有效的JWT令牌（在Authorization头中）
2. 管理员权限（ADMIN角色）

## 错误响应

```json
{
  "success": false,
  "code": "401",
  "message": "未授权访问"
}
```

```json
{
  "success": false,
  "code": "403",
  "message": "权限不足"
}
```

```json
{
  "success": false,
  "code": "404",
  "message": "审计日志不存在"
}
```

## 测试验证

Task5_3_1_3的功能已通过以下测试验证：

1. ✅ **复合筛选条件查询测试** - 验证多个筛选条件同时使用
2. ✅ **关键词搜索功能测试** - 验证在详情字段中搜索关键词
3. ✅ **时间范围筛选测试** - 验证按时间范围查询日志
4. ✅ **分页功能测试** - 验证分页查询的正确性
5. ✅ **API完整性测试** - 验证所有API端点的功能

所有测试均通过，确保API功能的稳定性和可靠性。
