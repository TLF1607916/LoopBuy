# LoopBuy 校园二手交易平台 API 文档

## 项目概述

LoopBuy是一个专为校园设计的二手交易平台，采用前后端分离架构，提供用户管理、商品交易、消息通讯等核心功能。

### 技术栈
- **后端**: Java 8 + Servlet + MySQL
- **前端**: HTML5 + CSS3 + JavaScript
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **测试框架**: JUnit 5

### 项目结构
```
backend/
├── src/main/java/com/shiwu/
│   ├── admin/          # 管理员模块
│   ├── user/           # 用户模块  
│   ├── product/        # 商品模块
│   ├── message/        # 消息模块
│   ├── cart/           # 购物车模块
│   └── common/         # 公共模块
├── src/main/resources/ # 配置文件
├── src/test/          # 测试代码
└── docs/              # API文档
```

## 通用规范

### 请求格式
- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **请求方法**: GET, POST, PUT, DELETE

### 响应格式
所有API响应都遵循统一的JSON格式：

```json
{
    "success": true,
    "message": "操作成功",
    "data": {},
    "timestamp": "2024-01-15T10:30:00"
}
```

### 状态码说明
- **200**: 请求成功
- **400**: 请求参数错误
- **401**: 未授权访问
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

### 认证机制
使用JWT Token进行身份认证：
- **Header**: `Authorization: Bearer <token>`
- **Token有效期**: 24小时
- **刷新机制**: 自动续期

## 用户模块 API

### 用户注册
**POST** `/api/user/register`

**请求参数:**
```json
{
    "username": "string",      // 用户名，3-20字符
    "password": "string",      // 密码，6-20字符
    "email": "string",         // 邮箱地址
    "phone": "string",         // 手机号码
    "realName": "string",      // 真实姓名
    "studentId": "string",     // 学号
    "campus": "string",        // 校区
    "dormitory": "string"      // 宿舍信息
}
```

**响应示例:**
```json
{
    "success": true,
    "message": "注册成功",
    "data": {
        "userId": 12345,
        "username": "testuser",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
}
```

### 用户登录
**POST** `/api/user/login`

**请求参数:**
```json
{
    "username": "string",      // 用户名或邮箱
    "password": "string"       // 密码
}
```

**响应示例:**
```json
{
    "success": true,
    "message": "登录成功",
    "data": {
        "userId": 12345,
        "username": "testuser",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "userInfo": {
            "email": "test@example.com",
            "phone": "13800138000",
            "campus": "东校区",
            "status": 0
        }
    }
}
```

### 获取用户信息
**GET** `/api/user/profile/{userId}`

**路径参数:**
- `userId`: 用户ID

**响应示例:**
```json
{
    "success": true,
    "message": "获取成功",
    "data": {
        "userId": 12345,
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "realName": "张三",
        "campus": "东校区",
        "dormitory": "A栋101",
        "avatar": "http://example.com/avatar.jpg",
        "registerTime": "2024-01-15T10:30:00",
        "lastLoginTime": "2024-01-15T15:20:00",
        "status": 0
    }
}
```

### 更新用户信息
**PUT** `/api/user/profile`

**请求头:**
- `Authorization: Bearer <token>`

**请求参数:**
```json
{
    "email": "string",         // 邮箱地址
    "phone": "string",         // 手机号码
    "realName": "string",      // 真实姓名
    "campus": "string",        // 校区
    "dormitory": "string",     // 宿舍信息
    "avatar": "string"         // 头像URL
}
```

## 商品模块 API

### 发布商品
**POST** `/api/product/publish`

**请求头:**
- `Authorization: Bearer <token>`

**请求参数:**
```json
{
    "title": "string",         // 商品标题
    "description": "string",   // 商品描述
    "price": "decimal",        // 商品价格
    "category": "string",      // 商品分类
    "condition": "string",     // 商品成色
    "images": ["string"],      // 商品图片URL数组
    "tags": ["string"],        // 商品标签
    "location": "string",      // 交易地点
    "contactInfo": "string"    // 联系方式
}
```

### 获取商品列表
**GET** `/api/product/list`

**查询参数:**
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `category`: 商品分类
- `keyword`: 搜索关键词
- `minPrice`: 最低价格
- `maxPrice`: 最高价格
- `sortBy`: 排序字段 (price, time)
- `sortOrder`: 排序方向 (asc, desc)

**响应示例:**
```json
{
    "success": true,
    "message": "获取成功",
    "data": {
        "products": [
            {
                "productId": 1001,
                "title": "iPhone 12 Pro Max",
                "price": 5999.00,
                "mainImageUrl": "http://example.com/image1.jpg",
                "category": "数码产品",
                "condition": "九成新",
                "location": "东校区",
                "publishTime": "2024-01-15T10:30:00",
                "status": 1,
                "sellerId": 12345,
                "sellerName": "张三"
            }
        ],
        "pagination": {
            "currentPage": 1,
            "totalPages": 10,
            "totalItems": 95,
            "pageSize": 10
        }
    }
}
```

### 获取商品详情
**GET** `/api/product/detail/{productId}`

**路径参数:**
- `productId`: 商品ID

**响应示例:**
```json
{
    "success": true,
    "message": "获取成功",
    "data": {
        "productId": 1001,
        "title": "iPhone 12 Pro Max",
        "description": "个人自用iPhone 12 Pro Max，256GB，深空灰色...",
        "price": 5999.00,
        "category": "数码产品",
        "condition": "九成新",
        "images": [
            "http://example.com/image1.jpg",
            "http://example.com/image2.jpg"
        ],
        "tags": ["苹果", "手机", "256GB"],
        "location": "东校区",
        "contactInfo": "微信：abc123",
        "publishTime": "2024-01-15T10:30:00",
        "updateTime": "2024-01-15T12:00:00",
        "status": 1,
        "viewCount": 156,
        "favoriteCount": 23,
        "seller": {
            "userId": 12345,
            "username": "张三",
            "avatar": "http://example.com/avatar.jpg",
            "campus": "东校区",
            "rating": 4.8
        }
    }
}
```

## 消息模块 API

### 发送消息
**POST** `/api/message/send`

**请求头:**
- `Authorization: Bearer <token>`

**请求参数:**
```json
{
    "receiverId": 12346,       // 接收者ID
    "productId": 1001,         // 商品ID（可选）
    "content": "string",       // 消息内容
    "messageType": "TEXT"      // 消息类型：TEXT, IMAGE, SYSTEM
}
```

### 获取会话列表
**GET** `/api/message/conversations`

**请求头:**
- `Authorization: Bearer <token>`

**查询参数:**
- `page`: 页码，默认1
- `size`: 每页数量，默认20

**响应示例:**
```json
{
    "success": true,
    "message": "获取成功",
    "data": {
        "conversations": [
            {
                "conversationId": "1_2_1001",
                "participant": {
                    "userId": 12346,
                    "username": "李四",
                    "avatar": "http://example.com/avatar2.jpg"
                },
                "product": {
                    "productId": 1001,
                    "title": "iPhone 12 Pro Max",
                    "mainImageUrl": "http://example.com/image1.jpg"
                },
                "lastMessage": "这个手机还在吗？",
                "lastMessageTime": "2024-01-15T15:30:00",
                "unreadCount": 2,
                "status": "ACTIVE"
            }
        ],
        "pagination": {
            "currentPage": 1,
            "totalPages": 3,
            "totalItems": 25,
            "pageSize": 20
        }
    }
}
```

### 获取消息历史
**GET** `/api/message/history/{conversationId}`

**请求头:**
- `Authorization: Bearer <token>`

**路径参数:**
- `conversationId`: 会话ID

**查询参数:**
- `page`: 页码，默认1
- `size`: 每页数量，默认50

**响应示例:**
```json
{
    "success": true,
    "message": "获取成功",
    "data": {
        "messages": [
            {
                "messageId": 10001,
                "senderId": 12345,
                "receiverId": 12346,
                "content": "你好，这个iPhone还在吗？",
                "messageType": "TEXT",
                "isRead": true,
                "sendTime": "2024-01-15T14:30:00"
            },
            {
                "messageId": 10002,
                "senderId": 12346,
                "receiverId": 12345,
                "content": "在的，成色很好",
                "messageType": "TEXT",
                "isRead": false,
                "sendTime": "2024-01-15T14:35:00"
            }
        ],
        "pagination": {
            "currentPage": 1,
            "totalPages": 2,
            "totalItems": 15,
            "pageSize": 50
        }
    }
}
```

### 标记消息已读
**PUT** `/api/message/read/{conversationId}`

**请求头:**
- `Authorization: Bearer <token>`

**路径参数:**
- `conversationId`: 会话ID

## 管理员模块 API

### 管理员登录
**POST** `/api/admin/login`

**请求参数:**
```json
{
    "username": "string",      // 管理员用户名
    "password": "string"       // 密码
}
```

### 获取用户列表
**GET** `/api/admin/users`

**请求头:**
- `Authorization: Bearer <admin_token>`

**查询参数:**
- `page`: 页码，默认1
- `size`: 每页数量，默认20
- `status`: 用户状态筛选
- `keyword`: 搜索关键词

### 用户管理操作
**PUT** `/api/admin/users/{userId}/status`

**请求头:**
- `Authorization: Bearer <admin_token>`

**路径参数:**
- `userId`: 用户ID

**请求参数:**
```json
{
    "status": 2,               // 新状态：0-正常，1-禁言，2-封禁
    "reason": "string"         // 操作原因
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户名已存在 |
| 1002 | 邮箱已被注册 |
| 1003 | 用户名或密码错误 |
| 1004 | 用户不存在 |
| 1005 | 用户已被封禁 |
| 2001 | 商品不存在 |
| 2002 | 商品已下架 |
| 2003 | 无权限操作该商品 |
| 3001 | 会话不存在 |
| 3002 | 消息发送失败 |
| 4001 | 管理员权限不足 |
| 5001 | Token无效或已过期 |
| 5002 | 参数验证失败 |

## 开发指南

### 本地开发环境搭建
1. 安装JDK 8+
2. 安装MySQL 8.0
3. 导入数据库脚本
4. 配置数据库连接
5. 运行项目

### 测试
```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -Dtest="com.shiwu.user.**"
```

### 部署
项目支持Docker部署和传统部署方式，详见部署文档。
