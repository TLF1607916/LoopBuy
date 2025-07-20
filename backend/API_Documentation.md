# Shiwu校园二手交易平台 API文档

## 概述

本文档详细描述了Shiwu校园二手交易平台的所有API接口。平台采用RESTful API设计，支持JSON格式的数据交换。

### 基础信息

- **基础URL**: `http://localhost:8080`
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: JWT Token (部分接口需要)

### 通用响应格式

所有API接口都遵循统一的响应格式：

```json
{
  "success": true,
  "data": {},
  "error": {
    "code": "错误代码",
    "message": "错误信息",
    "userTip": "用户提示"
  },
  "message": "响应消息"
}
```

### 错误代码说明

- `200` - 成功
- `400` - 请求参数错误
- `401` - 未授权/未登录
- `403` - 权限不足
- `404` - 资源不存在
- `500` - 服务器内部错误

---

## 1. 用户模块 (UserController)

### 1.1 用户注册

**接口**: `POST /api/user/register`

**描述**: 用户注册新账号

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱",
  "phone": "手机号",
  "realName": "真实姓名",
  "studentId": "学号"
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "userId": 1001,
    "username": "testuser",
    "email": "test@example.com"
  },
  "message": "注册成功"
}
```

### 1.2 用户登录

**接口**: `POST /api/user/login`

**描述**: 用户登录获取访问令牌

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "token": "jwt_token_string",
    "userId": 1001,
    "username": "testuser",
    "userRole": "USER"
  },
  "message": "登录成功"
}
```

### 1.3 获取用户资料

**接口**: `GET /api/user/{userId}`

**描述**: 获取指定用户的公开资料信息

**路径参数**:
- `userId` - 用户ID

**响应示例**:
```json
{
  "success": true,
  "data": {
    "userId": 1001,
    "username": "testuser",
    "avatar": "头像URL",
    "bio": "个人简介",
    "followersCount": 10,
    "followingCount": 5,
    "productsCount": 3,
    "isFollowing": false
  }
}
```

### 1.4 关注用户

**接口**: `POST /api/user/{userId}/follow`

**描述**: 关注或取消关注指定用户

**路径参数**:
- `userId` - 要关注的用户ID

**请求头**:
- `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "isFollowing": true,
    "followersCount": 11
  },
  "message": "关注成功"
}
```

### 1.5 获取关注状态

**接口**: `GET /api/user/{userId}/follow`

**描述**: 获取当前用户对指定用户的关注状态

**路径参数**:
- `userId` - 用户ID

**请求头**:
- `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "isFollowing": true,
    "followersCount": 11
  }
}
```

### 1.6 获取关注动态

**接口**: `GET /api/user/follow/feed`

**描述**: 获取关注用户的商品动态

**请求头**:
- `Authorization: Bearer {token}`

**查询参数**:
- `page` - 页码 (默认: 1)
- `size` - 每页数量 (默认: 10)

**响应示例**:
```json
{
  "success": true,
  "data": {
    "products": [
      {
        "productId": 1001,
        "title": "商品标题",
        "price": 99.99,
        "images": ["图片URL"],
        "seller": {
          "userId": 2001,
          "username": "seller1",
          "avatar": "头像URL"
        },
        "publishTime": "2023-12-20T10:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalItems": 50,
      "hasNext": true
    }
  }
}
```

---

## 2. 商品模块 (ProductController)

### 2.1 获取商品列表

**接口**: `GET /api/products/`

**描述**: 获取商品列表，支持搜索和筛选

**查询参数**:
- `keyword` - 搜索关键词
- `category` - 商品分类
- `minPrice` - 最低价格
- `maxPrice` - 最高价格
- `condition` - 商品状态 (NEW/LIKE_NEW/GOOD/FAIR)
- `sort` - 排序方式 (LATEST/PRICE_ASC/PRICE_DESC/POPULAR)
- `page` - 页码 (默认: 1)
- `size` - 每页数量 (默认: 10)

**响应示例**:
```json
{
  "success": true,
  "data": {
    "products": [
      {
        "productId": 1001,
        "title": "iPhone 13",
        "description": "九成新iPhone 13",
        "price": 4999.00,
        "originalPrice": 5999.00,
        "condition": "LIKE_NEW",
        "category": "数码产品",
        "images": ["image1.jpg", "image2.jpg"],
        "seller": {
          "userId": 2001,
          "username": "seller1",
          "avatar": "avatar.jpg"
        },
        "publishTime": "2023-12-20T10:00:00",
        "viewCount": 100,
        "likeCount": 15
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 10,
      "totalItems": 100,
      "hasNext": true
    }
  }
}
```

### 2.2 获取商品详情

**接口**: `GET /api/products/{productId}`

**描述**: 获取指定商品的详细信息

**路径参数**:
- `productId` - 商品ID

**响应示例**:
```json
{
  "success": true,
  "data": {
    "productId": 1001,
    "title": "iPhone 13",
    "description": "九成新iPhone 13，功能完好",
    "price": 4999.00,
    "originalPrice": 5999.00,
    "condition": "LIKE_NEW",
    "category": "数码产品",
    "images": ["image1.jpg", "image2.jpg"],
    "seller": {
      "userId": 2001,
      "username": "seller1",
      "avatar": "avatar.jpg",
      "rating": 4.8
    },
    "publishTime": "2023-12-20T10:00:00",
    "viewCount": 100,
    "likeCount": 15,
    "status": "AVAILABLE",
    "location": "北京市海淀区",
    "tags": ["苹果", "手机", "数码"]
  }
}
```

### 2.3 发布商品

**接口**: `POST /api/products/`

**描述**: 发布新商品

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "title": "商品标题",
  "description": "商品描述",
  "price": 99.99,
  "originalPrice": 199.99,
  "condition": "LIKE_NEW",
  "categoryId": 1,
  "images": ["image1.jpg", "image2.jpg"],
  "location": "北京市海淀区",
  "tags": ["标签1", "标签2"],
  "action": "PUBLISH"
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "productId": 1001
  },
  "message": "商品发布成功"
}
```

### 2.4 保存草稿

**接口**: `POST /api/products/draft`

**描述**: 保存商品草稿

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "title": "商品标题",
  "description": "商品描述",
  "price": 99.99,
  "action": "SAVE_DRAFT"
}
```

### 2.5 上传商品图片

**接口**: `POST /api/products/images`

**描述**: 上传商品图片

**请求头**:
- `Authorization: Bearer {token}`
- `Content-Type: multipart/form-data`

**请求参数**:
- `image` - 图片文件

**响应示例**:
```json
{
  "success": true,
  "data": {
    "imageUrl": "uploads/products/image_123456.jpg"
  }
}
```

### 2.6 获取我的商品

**接口**: `GET /api/products/my`

**描述**: 获取当前用户发布的商品列表

**请求头**:
- `Authorization: Bearer {token}`

**查询参数**:
- `status` - 商品状态筛选
- `page` - 页码
- `size` - 每页数量

### 2.7 编辑商品

**接口**: `POST /api/products/{productId}`

**描述**: 编辑已发布的商品

**路径参数**:
- `productId` - 商品ID

**请求头**:
- `Authorization: Bearer {token}`

---

## 3. 商品分类模块 (CategoryController)

### 3.1 获取所有分类

**接口**: `GET /api/categories/`

**描述**: 获取所有商品分类列表

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "categoryId": 1,
      "name": "数码产品",
      "description": "手机、电脑、相机等数码设备",
      "icon": "digital.png",
      "productCount": 150
    },
    {
      "categoryId": 2,
      "name": "图书教材",
      "description": "教科书、参考书、小说等",
      "icon": "books.png",
      "productCount": 200
    }
  ]
}
```

---

## 4. 消息模块 (MessageController)

### 4.1 获取会话列表

**接口**: `GET /api/messages/conversations`

**描述**: 获取当前用户的所有会话列表

**请求头**:
- `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "conversationId": "conv_123",
      "otherUser": {
        "userId": 2001,
        "username": "user2",
        "avatar": "avatar.jpg"
      },
      "lastMessage": {
        "content": "最后一条消息内容",
        "sendTime": "2023-12-20T15:30:00",
        "isRead": false
      },
      "unreadCount": 3
    }
  ]
}
```

### 4.2 获取消息历史

**接口**: `GET /api/messages/history/{conversationId}`

**描述**: 获取指定会话的消息历史

**路径参数**:
- `conversationId` - 会话ID

**请求头**:
- `Authorization: Bearer {token}`

**查询参数**:
- `page` - 页码
- `size` - 每页数量

**响应示例**:
```json
{
  "success": true,
  "data": {
    "messages": [
      {
        "messageId": 1001,
        "senderId": 2001,
        "receiverId": 3001,
        "content": "消息内容",
        "messageType": "TEXT",
        "sendTime": "2023-12-20T15:30:00",
        "isRead": true
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "hasNext": true
    }
  }
}
```

### 4.3 发送消息

**接口**: `POST /api/messages/send`

**描述**: 发送消息给指定用户

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "receiverId": 2001,
  "content": "消息内容",
  "messageType": "TEXT"
}
```

### 4.4 获取未读消息数量

**接口**: `GET /api/messages/unread-count`

**描述**: 获取当前用户的未读消息总数

**请求头**:
- `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "unreadCount": 5
  }
}
```

### 4.5 标记消息已读

**接口**: `POST /api/messages/mark-read`

**描述**: 标记指定会话的消息为已读

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "conversationId": "conv_123"
}
```

### 4.6 获取会话详情

**接口**: `GET /api/messages/conversation/{conversationId}`

**描述**: 获取指定会话的详细信息

**路径参数**:
- `conversationId` - 会话ID

**请求头**:
- `Authorization: Bearer {token}`

### 4.7 获取新消息

**接口**: `GET /api/messages/new`

**描述**: 获取新收到的消息

**请求头**:
- `Authorization: Bearer {token}`

---

## 5. 订单模块 (OrderController)

### 5.1 创建订单

**接口**: `POST /api/orders/`

**描述**: 创建新订单

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "productId": 1001,
  "quantity": 1,
  "shippingAddress": {
    "receiverName": "收货人姓名",
    "phone": "手机号",
    "address": "详细地址"
  },
  "remark": "备注信息"
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "orderId": 2001,
    "orderNumber": "ORD20231220001",
    "totalAmount": 99.99,
    "status": "PENDING_PAYMENT"
  }
}
```

### 5.2 获取订单列表

**接口**: `GET /api/orders/`

**描述**: 获取订单列表

**请求头**:
- `Authorization: Bearer {token}`

**查询参数**:
- `type` - 订单类型 (buyer/seller)
- `status` - 订单状态筛选
- `page` - 页码
- `size` - 每页数量

**响应示例**:
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "orderId": 2001,
        "orderNumber": "ORD20231220001",
        "product": {
          "productId": 1001,
          "title": "商品标题",
          "image": "image.jpg",
          "price": 99.99
        },
        "buyer": {
          "userId": 3001,
          "username": "buyer1"
        },
        "seller": {
          "userId": 2001,
          "username": "seller1"
        },
        "quantity": 1,
        "totalAmount": 99.99,
        "status": "PENDING_PAYMENT",
        "createTime": "2023-12-20T10:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 3,
      "totalItems": 25
    }
  }
}
```

### 5.3 获取买家订单

**接口**: `GET /api/orders/buyer`

**描述**: 获取当前用户作为买家的订单列表

**请求头**:
- `Authorization: Bearer {token}`

### 5.4 获取卖家订单

**接口**: `GET /api/orders/seller`

**描述**: 获取当前用户作为卖家的订单列表

**请求头**:
- `Authorization: Bearer {token}`

### 5.5 获取订单详情

**接口**: `GET /api/orders/{orderId}`

**描述**: 获取指定订单的详细信息

**路径参数**:
- `orderId` - 订单ID

**请求头**:
- `Authorization: Bearer {token}`

### 5.6 更新订单状态

**接口**: `POST /api/orders/{orderId}/status`

**描述**: 更新订单状态

**路径参数**:
- `orderId` - 订单ID

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "status": "PAID",
  "remark": "状态更新备注"
}
```

### 5.7 卖家发货

**接口**: `POST /api/orders/{orderId}/ship`

**描述**: 卖家发货

**路径参数**:
- `orderId` - 订单ID

**请求参数**:
```json
{
  "trackingNumber": "快递单号",
  "shippingCompany": "快递公司"
}
```

### 5.8 买家确认收货

**接口**: `POST /api/orders/{orderId}/confirm`

**描述**: 买家确认收货

**路径参数**:
- `orderId` - 订单ID

### 5.9 申请退货

**接口**: `POST /api/orders/{orderId}/return`

**描述**: 买家申请退货

**路径参数**:
- `orderId` - 订单ID

**请求参数**:
```json
{
  "reason": "退货原因",
  "description": "详细说明",
  "images": ["证明图片URL"]
}
```

### 5.10 处理退货申请

**接口**: `POST /api/orders/{orderId}/process-return`

**描述**: 卖家处理退货申请

**路径参数**:
- `orderId` - 订单ID

**请求参数**:
```json
{
  "action": "APPROVE",
  "remark": "处理备注"
}
```

---

## 6. 购物车模块 (CartController)

### 6.1 获取购物车

**接口**: `GET /api/cart/`

**描述**: 获取当前用户的购物车内容

**请求头**:
- `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "cartItemId": 1001,
        "product": {
          "productId": 2001,
          "title": "商品标题",
          "price": 99.99,
          "image": "image.jpg",
          "status": "AVAILABLE"
        },
        "quantity": 1,
        "addTime": "2023-12-20T10:00:00"
      }
    ],
    "totalItems": 3,
    "totalAmount": 299.97
  }
}
```

### 6.2 添加到购物车

**接口**: `POST /api/cart/add`

**描述**: 添加商品到购物车

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "productId": 2001,
  "quantity": 1
}
```

### 6.3 更新购物车商品数量

**接口**: `POST /api/cart/update`

**描述**: 更新购物车中商品的数量

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "cartItemId": 1001,
  "quantity": 2
}
```

### 6.4 移除购物车商品

**接口**: `POST /api/cart/remove`

**描述**: 从购物车中移除商品

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "cartItemId": 1001
}
```

### 6.5 清空购物车

**接口**: `POST /api/cart/clear`

**描述**: 清空当前用户的购物车

**请求头**:
- `Authorization: Bearer {token}`

---

## 7. 评价模块 (ReviewController)

### 7.1 提交评价

**接口**: `POST /api/reviews/`

**描述**: 对已完成的订单进行评价

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "orderId": 2001,
  "rating": 5,
  "content": "评价内容",
  "images": ["评价图片URL"]
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "reviewId": 3001
  },
  "message": "评价提交成功"
}
```

### 7.2 获取商品评价列表

**接口**: `GET /api/reviews/product/{productId}`

**描述**: 获取指定商品的评价列表

**路径参数**:
- `productId` - 商品ID

**查询参数**:
- `page` - 页码
- `size` - 每页数量

**响应示例**:
```json
{
  "success": true,
  "data": {
    "reviews": [
      {
        "reviewId": 3001,
        "reviewer": {
          "userId": 4001,
          "username": "reviewer1",
          "avatar": "avatar.jpg"
        },
        "rating": 5,
        "content": "评价内容",
        "images": ["image1.jpg"],
        "createTime": "2023-12-20T16:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 2,
      "totalItems": 15
    },
    "statistics": {
      "averageRating": 4.5,
      "totalReviews": 15,
      "ratingDistribution": {
        "5": 8,
        "4": 5,
        "3": 2,
        "2": 0,
        "1": 0
      }
    }
  }
}
```

### 7.3 获取用户评价列表

**接口**: `GET /api/reviews/user/{userId}`

**描述**: 获取指定用户收到的评价列表

**路径参数**:
- `userId` - 用户ID

### 7.4 检查订单可评价状态

**接口**: `GET /api/reviews/check/{orderId}`

**描述**: 检查指定订单是否可以进行评价

**路径参数**:
- `orderId` - 订单ID

**请求头**:
- `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "canReview": true,
    "reason": "订单已完成，可以评价"
  }
}
```

---

## 8. 支付模块 (PaymentController)

### 8.1 创建支付

**接口**: `POST /api/payments/`

**描述**: 为订单创建支付

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "orderIds": [2001, 2002],
  "totalAmount": 199.98,
  "paymentMethod": 1
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "paymentId": "PAY_20231220_001",
    "orderIds": [2001, 2002],
    "paymentAmount": 199.98,
    "paymentMethod": 1,
    "paymentMethodText": "支付宝",
    "paymentUrl": "/payment/page?paymentId=PAY_20231220_001",
    "expireTime": "2023-12-20T16:15:00"
  }
}
```

### 8.2 处理支付

**接口**: `POST /api/payments/process`

**描述**: 处理支付请求

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "paymentId": "PAY_20231220_001",
  "paymentPassword": "123456"
}
```

### 8.3 查询支付状态

**接口**: `GET /api/payments/status`

**描述**: 查询支付状态

**请求头**:
- `Authorization: Bearer {token}`

**查询参数**:
- `paymentId` - 支付ID

**响应示例**:
```json
{
  "success": true,
  "data": {
    "paymentId": "PAY_20231220_001",
    "paymentStatus": 2,
    "paymentStatusText": "支付成功",
    "paymentAmount": 199.98,
    "paymentTime": "2023-12-20T15:45:00"
  }
}
```

### 8.4 取消支付

**接口**: `POST /api/payments/cancel`

**描述**: 取消支付

**请求头**:
- `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "paymentId": "PAY_20231220_001"
}
```

### 8.5 获取用户支付记录

**接口**: `GET /api/payments/`

**描述**: 获取当前用户的支付记录

**请求头**:
- `Authorization: Bearer {token}`

### 8.6 根据订单获取支付信息

**接口**: `GET /api/payments/by-orders`

**描述**: 根据订单ID获取支付信息

**请求头**:
- `Authorization: Bearer {token}`

**查询参数**:
- `orderIds` - 订单ID列表 (JSON数组格式)

### 8.7 获取支付页面

**接口**: `GET /api/payments/page`

**描述**: 获取支付页面信息

**查询参数**:
- `paymentId` - 支付ID

**响应示例**:
```json
{
  "success": true,
  "data": {
    "paymentId": "PAY_20231220_001",
    "pageTitle": "支付页面",
    "message": "请输入支付密码完成支付",
    "defaultPassword": "123456",
    "note": "输入正确的支付密码后将立即完成支付，默认密码为123456"
  }
}
```

---

## 9. 支付超时管理模块 (PaymentTimeoutController)

### 9.1 获取超时检查状态

**接口**: `GET /api/payment-timeout/`

**描述**: 获取支付超时检查任务状态 (管理员权限)

**请求头**:
- `Authorization: Bearer {admin_token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "isRunning": true,
    "message": "支付超时检查任务状态",
    "expiredPaymentCount": 5
  }
}
```

### 9.2 获取过期支付记录数量

**接口**: `GET /api/payment-timeout/count`

**描述**: 获取过期支付记录数量 (管理员权限)

**请求头**:
- `Authorization: Bearer {admin_token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "message": "当前有5条过期支付记录",
    "expiredPaymentCount": 5
  }
}
```

### 9.3 手动触发超时检查

**接口**: `POST /api/payment-timeout/`

**描述**: 手动触发支付超时检查 (管理员权限)

**请求头**:
- `Authorization: Bearer {admin_token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "note": "系统会自动每分钟检查一次过期支付记录",
    "message": "超时检查任务正在后台运行",
    "expiredPaymentCount": 5
  }
}
```

### 9.4 手动处理指定过期支付

**接口**: `POST /api/payment-timeout/handle`

**描述**: 手动处理指定的过期支付 (管理员权限)

**请求头**:
- `Authorization: Bearer {admin_token}`

**请求参数**:
```json
{
  "paymentId": "PAY_20231220_001"
}
```

---

## 10. 管理员模块

### 10.1 管理员登录 (AdminController)

**接口**: `POST /api/admin/login`

**描述**: 管理员登录

**请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "token": "admin_jwt_token",
    "adminId": 1,
    "username": "admin",
    "role": "ADMIN"
  },
  "message": "登录成功"
}
```

### 10.2 获取仪表盘数据 (AdminController)

**接口**: `GET /api/admin/dashboard`

**描述**: 获取管理员仪表盘统计数据

**请求头**:
- `Authorization: Bearer {admin_token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "userStats": {
      "totalUsers": 1000,
      "activeUsers": 800,
      "newUsersToday": 50
    },
    "productStats": {
      "totalProducts": 2000,
      "pendingApproval": 20,
      "soldToday": 15
    },
    "orderStats": {
      "totalOrders": 5000,
      "pendingOrders": 100,
      "completedToday": 80
    },
    "revenueStats": {
      "totalRevenue": 100000.00,
      "revenueToday": 2000.00
    }
  }
}
```

### 10.3 用户管理 (AdminUserController)

#### 10.3.1 获取用户列表

**接口**: `GET /api/admin/users/`

**描述**: 获取用户列表 (管理员权限)

**请求头**:
- `Authorization: Bearer {admin_token}`

**查询参数**:
- `keyword` - 搜索关键词
- `status` - 用户状态筛选
- `page` - 页码
- `size` - 每页数量

#### 10.3.2 获取用户详情

**接口**: `GET /api/admin/users/{userId}`

**描述**: 获取指定用户的详细信息

**路径参数**:
- `userId` - 用户ID

**请求头**:
- `Authorization: Bearer {admin_token}`

#### 10.3.3 封禁用户

**接口**: `POST /api/admin/users/{userId}/ban`

**描述**: 封禁指定用户

**路径参数**:
- `userId` - 用户ID

**请求头**:
- `Authorization: Bearer {admin_token}`

**请求参数**:
```json
{
  "reason": "封禁原因",
  "duration": 7
}
```

#### 10.3.4 解封用户

**接口**: `POST /api/admin/users/{userId}/unban`

**描述**: 解封指定用户

### 10.4 商品管理 (AdminProductController)

#### 10.4.1 获取商品列表

**接口**: `GET /api/admin/products/`

**描述**: 获取商品列表 (管理员权限)

**请求头**:
- `Authorization: Bearer {admin_token}`

#### 10.4.2 审核商品

**接口**: `POST /api/admin/products/{productId}/approve`

**描述**: 审核商品

**路径参数**:
- `productId` - 商品ID

**请求头**:
- `Authorization: Bearer {admin_token}`

**请求参数**:
```json
{
  "action": "APPROVE",
  "remark": "审核备注"
}
```

#### 10.4.3 下架商品

**接口**: `POST /api/admin/products/{productId}/remove`

**描述**: 下架商品

### 10.5 审计日志管理 (AuditLogController)

#### 10.5.1 查询审计日志

**接口**: `GET /api/admin/audit-logs/`

**描述**: 查询审计日志列表

**请求头**:
- `Authorization: Bearer {admin_token}`

**查询参数**:
- `action` - 操作类型筛选
- `targetType` - 目标类型筛选
- `userId` - 用户ID筛选
- `startTime` - 开始时间
- `endTime` - 结束时间
- `keyword` - 搜索关键词
- `page` - 页码
- `size` - 每页数量

**响应示例**:
```json
{
  "success": true,
  "data": {
    "logs": [
      {
        "logId": 1001,
        "userId": 2001,
        "username": "testuser",
        "action": "LOGIN",
        "targetType": "USER",
        "targetId": "2001",
        "details": "用户登录",
        "ipAddress": "192.168.1.100",
        "userAgent": "Mozilla/5.0...",
        "timestamp": "2023-12-20T10:00:00",
        "result": "SUCCESS"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 10,
      "totalItems": 100
    }
  }
}
```

#### 10.5.2 获取审计日志详情

**接口**: `GET /api/admin/audit-logs/{logId}`

**描述**: 获取指定审计日志的详细信息

**路径参数**:
- `logId` - 日志ID

**请求头**:
- `Authorization: Bearer {admin_token}`

#### 10.5.3 获取统计数据

**接口**: `GET /api/admin/audit-logs/stats`

**描述**: 获取审计日志统计数据

**请求头**:
- `Authorization: Bearer {admin_token}`

**响应示例**:
```json
{
  "success": true,
  "data": {
    "totalLogs": 10000,
    "todayLogs": 500,
    "actionStats": {
      "LOGIN": 3000,
      "LOGOUT": 2800,
      "CREATE_PRODUCT": 1500,
      "UPDATE_PRODUCT": 800
    },
    "userStats": {
      "activeUsers": 200,
      "totalUsers": 1000
    }
  }
}
```

#### 10.5.4 获取趋势数据

**接口**: `GET /api/admin/audit-logs/trend`

**描述**: 获取审计日志趋势数据

**请求头**:
- `Authorization: Bearer {admin_token}`

**查询参数**:
- `days` - 天数 (默认: 7)

#### 10.5.5 导出审计日志

**接口**: `POST /api/admin/audit-logs/export`

**描述**: 导出审计日志

**请求头**:
- `Authorization: Bearer {admin_token}`

**请求参数**:
```json
{
  "format": "CSV",
  "filters": {
    "startTime": "2023-12-01T00:00:00",
    "endTime": "2023-12-31T23:59:59",
    "action": "LOGIN"
  }
}
```

#### 10.5.6 获取可用操作类型

**接口**: `GET /api/admin/audit-logs/actions`

**描述**: 获取可用的操作类型列表

#### 10.5.7 获取可用目标类型

**接口**: `GET /api/admin/audit-logs/target-types`

**描述**: 获取可用的目标类型列表

---

## 附录

### A. 错误代码对照表

| 错误代码 | 描述 | HTTP状态码 |
|---------|------|-----------|
| A0001 | 用户端错误 | 400 |
| A0100 | 用户注册错误 | 400 |
| A0101 | 用户未同意隐私协议 | 400 |
| A0102 | 注册国家或地区受限 | 400 |
| A0110 | 用户名校验失败 | 400 |
| A0111 | 用户名已存在 | 400 |
| A0112 | 用户名包含敏感词 | 400 |
| A0120 | 用户密码校验失败 | 400 |
| A0121 | 密码长度不够 | 400 |
| A0122 | 密码强度不够 | 400 |
| A0200 | 用户登录异常 | 401 |
| A0201 | 用户账户不存在 | 401 |
| A0202 | 用户账户被冻结 | 401 |
| A0203 | 用户账户已作废 | 401 |
| A0210 | 用户密码错误 | 401 |
| A0300 | 访问权限异常 | 403 |
| A0301 | 访问未授权 | 403 |
| A0302 | 正在授权中 | 403 |
| A0400 | 用户请求参数错误 | 400 |
| A0401 | 包含非法恶意跳转链接 | 400 |
| A0402 | 无效的用户输入 | 400 |
| B0001 | 系统执行出错 | 500 |
| B0100 | 系统执行超时 | 500 |
| B0200 | 系统容灾功能被触发 | 500 |
| B0300 | 系统资源异常 | 500 |

### B. 商品状态说明

| 状态值 | 描述 |
|-------|------|
| DRAFT | 草稿 |
| PENDING_APPROVAL | 待审核 |
| AVAILABLE | 可购买 |
| SOLD | 已售出 |
| REMOVED | 已下架 |
| REJECTED | 审核拒绝 |

### C. 订单状态说明

| 状态值 | 描述 |
|-------|------|
| PENDING_PAYMENT | 待支付 |
| PAID | 已支付 |
| SHIPPED | 已发货 |
| DELIVERED | 已送达 |
| COMPLETED | 已完成 |
| CANCELLED | 已取消 |
| REFUNDING | 退款中 |
| REFUNDED | 已退款 |

### D. 支付方式说明

| 方式值 | 描述 |
|-------|------|
| 1 | 支付宝 |
| 2 | 微信支付 |
| 3 | 银行卡 |
| 4 | 余额支付 |

### E. 消息类型说明

| 类型值 | 描述 |
|-------|------|
| TEXT | 文本消息 |
| IMAGE | 图片消息 |
| SYSTEM | 系统消息 |

### F. 商品状况说明

| 状况值 | 描述 |
|-------|------|
| NEW | 全新 |
| LIKE_NEW | 几乎全新 |
| GOOD | 良好 |
| FAIR | 一般 |

### G. 审计日志操作类型

| 操作类型 | 描述 |
|---------|------|
| LOGIN | 用户登录 |
| LOGOUT | 用户登出 |
| REGISTER | 用户注册 |
| CREATE_PRODUCT | 创建商品 |
| UPDATE_PRODUCT | 更新商品 |
| DELETE_PRODUCT | 删除商品 |
| CREATE_ORDER | 创建订单 |
| UPDATE_ORDER | 更新订单 |
| CANCEL_ORDER | 取消订单 |
| CREATE_PAYMENT | 创建支付 |
| PROCESS_PAYMENT | 处理支付 |
| CANCEL_PAYMENT | 取消支付 |

### H. 审计日志目标类型

| 目标类型 | 描述 |
|---------|------|
| USER | 用户 |
| PRODUCT | 商品 |
| ORDER | 订单 |
| PAYMENT | 支付 |
| MESSAGE | 消息 |
| REVIEW | 评价 |
| CART | 购物车 |

---

**文档版本**: v1.0
**最后更新**: 2023-12-20
**维护者**: Shiwu开发团队

## 联系方式

如有API使用问题，请联系开发团队：
- 邮箱: dev@shiwu.com
- 技术支持: support@shiwu.com

## 更新日志

### v1.0 (2023-12-20)
- 初始版本发布
- 包含所有核心模块API
- 完整的错误代码和状态说明
