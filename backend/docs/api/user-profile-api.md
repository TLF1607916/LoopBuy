# 用户公开信息API文档

## API概述

**功能**: 获取用户公开信息（UC-02: View User Profile）  
**URL**: `GET /api/user/{userId}`  
**描述**: 获取指定用户的公开信息，包括昵称、头像、评分、在售商品列表等

## 请求参数

### 路径参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

### 请求头
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| Authorization | String | 否 | JWT令牌（用于判断是否关注该用户） |

## 响应格式

### 成功响应 (200 OK)
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "username": "test_user",
      "nickname": "测试用户",
      "avatarUrl": "http://example.com/avatar.jpg"
    },
    "followerCount": 10,
    "averageRating": 4.5,
    "onSaleProducts": [
      {
        "productId": 1,
        "title": "二手教科书",
        "mainImageUrl": "http://example.com/product1.jpg",
        "price": 50.00
      }
    ],
    "isFollowing": false,
    "registrationDate": "2024-01-01T10:00:00"
  }
}
```

### 失败响应

#### 用户不存在 (404 Not Found)
```json
{
  "success": false,
  "error": {
    "code": "A0120",
    "message": "用户不存在或已被封禁"
  }
}
```

#### 参数错误 (400 Bad Request)
```json
{
  "success": false,
  "error": {
    "code": "A0201",
    "message": "用户ID格式错误"
  }
}
```

#### 系统错误 (500 Internal Server Error)
```json
{
  "success": false,
  "error": {
    "code": "B0001",
    "message": "系统执行错误"
  }
}
```

## 数据模型

### UserProfileVO
| 字段名 | 类型 | 描述 |
|--------|------|------|
| user | UserVO | 用户基本信息 |
| followerCount | Integer | 粉丝数量 |
| averageRating | BigDecimal | 平均评分（作为卖家） |
| onSaleProducts | List<ProductCardVO> | 在售商品列表 |
| isFollowing | Boolean | 当前用户是否关注了该用户 |
| registrationDate | LocalDateTime | 注册时间 |

### UserVO
| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| nickname | String | 昵称 |
| avatarUrl | String | 头像URL |

### ProductCardVO
| 字段名 | 类型 | 描述 |
|--------|------|------|
| productId | Long | 商品ID |
| title | String | 商品标题 |
| mainImageUrl | String | 主图URL |
| price | BigDecimal | 价格 |

## 业务规则

1. **访问权限**: 任何用户（包括未登录用户）都可以查看用户公开信息
2. **隐私保护**: 不返回敏感信息（如邮箱、手机号、密码等）
3. **状态过滤**: 被封禁的用户不显示公开信息
4. **关注状态**: 只有登录用户才能看到是否关注了该用户
5. **商品列表**: 只显示状态为"在售"的商品

## 实现说明

### 模块解耦
- 严格遵循模块解耦原则
- UserService只处理用户相关逻辑
- 商品信息通过ProductService获取（待实现）
- 关注关系通过FollowService处理（待实现）

### 安全考虑
- 参数验证：检查用户ID格式
- 状态检查：过滤被封禁用户
- 错误处理：统一错误码和错误信息

### 性能优化
- 使用专门的查询方法只获取必要字段
- 支持分页查询商品列表（待实现）
- 预留缓存接口（待实现）

## 测试用例

参见 `UserServiceImplTest.java` 中的测试用例，包括：
- 正常情况测试
- 边界条件测试（null参数）
- 错误情况测试（用户不存在）
