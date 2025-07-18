# 用户关注功能API文档

## API概述

**功能**: 用户关注/取关功能  
**描述**: 实现用户之间的关注关系管理，包括关注用户、取关用户、查询关注状态等功能

## API列表

### 1. 关注用户

**URL**: `POST /api/user/{userId}/follow`  
**描述**: 当前登录用户关注指定用户

#### 请求参数

##### 路径参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 目标用户ID |

##### 请求头
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| Authorization | String | 是 | JWT令牌 |

#### 响应格式

##### 成功响应 (200 OK)
```json
{
  "success": true,
  "data": {
    "isFollowing": true,
    "followerCount": 15
  }
}
```

##### 失败响应

###### 目标用户不存在 (404 Not Found)
```json
{
  "success": false,
  "error": {
    "code": "A0120",
    "message": "目标用户不存在"
  }
}
```

###### 目标用户已被封禁 (403 Forbidden)
```json
{
  "success": false,
  "error": {
    "code": "A0121",
    "message": "目标用户已被封禁"
  }
}
```

###### 不能关注自己 (400 Bad Request)
```json
{
  "success": false,
  "error": {
    "code": "A0130",
    "message": "不能关注自己"
  }
}
```

###### 已经关注了该用户 (400 Bad Request)
```json
{
  "success": false,
  "error": {
    "code": "A0131",
    "message": "已经关注了该用户"
  }
}
```

###### 用户ID格式错误 (400 Bad Request)
```json
{
  "success": false,
  "error": {
    "code": "A0201",
    "message": "用户ID格式错误"
  }
}
```

###### 未登录 (401 Unauthorized)
```json
{
  "success": false,
  "error": {
    "code": "A0300",
    "message": "请先登录"
  }
}
```

### 2. 取关用户

**URL**: `DELETE /api/user/{userId}/follow`  
**描述**: 当前登录用户取关指定用户

#### 请求参数

##### 路径参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 目标用户ID |

##### 请求头
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| Authorization | String | 是 | JWT令牌 |

#### 响应格式

##### 成功响应 (200 OK)
```json
{
  "success": true,
  "data": {
    "isFollowing": false,
    "followerCount": 14
  }
}
```

##### 失败响应

###### 未关注该用户 (400 Bad Request)
```json
{
  "success": false,
  "error": {
    "code": "A0132",
    "message": "未关注该用户"
  }
}
```

其他错误响应与关注用户API相同。

### 3. 获取关注状态

**URL**: `GET /api/user/{userId}/follow`  
**描述**: 获取指定用户的关注状态信息

#### 请求参数

##### 路径参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 目标用户ID |

##### 请求头
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| Authorization | String | 否 | JWT令牌（用于判断是否关注该用户） |

#### 响应格式

##### 成功响应 (200 OK)
```json
{
  "success": true,
  "data": {
    "userId": 2,
    "username": "test2",
    "nickname": "测试用户2",
    "isFollowing": true,
    "followerCount": 15,
    "followingCount": 8
  }
}
```

##### 失败响应

###### 用户不存在 (404 Not Found)
```json
{
  "success": false,
  "error": {
    "code": "A0120",
    "message": "用户不存在"
  }
}
```

## 错误码说明

| 错误码 | 错误类型 | 描述 |
|--------|----------|------|
| A0120 | 目标用户不存在 | 请求的目标用户ID在系统中不存在 |
| A0121 | 目标用户已被封禁 | 目标用户账户已被管理员封禁 |
| A0130 | 不能关注自己 | 用户不能关注自己 |
| A0131 | 已经关注了该用户 | 重复关注同一用户 |
| A0132 | 未关注该用户 | 尝试取关未关注的用户 |
| A0201 | 用户ID格式错误 | 用户ID必须为正整数 |
| A0202 | 参数错误 | 请求参数格式或内容错误 |
| A0300 | 未登录 | 需要登录才能执行该操作 |
| B0001 | 系统执行错误 | 服务器内部错误 |
| B0002 | 数据访问异常 | 数据库访问出现问题 |

## 业务规则

1. **关注权限**: 只有登录用户才能执行关注/取关操作
2. **自关注限制**: 用户不能关注自己
3. **重复操作**: 重复关注或取关会返回相应错误
4. **用户状态**: 被封禁的用户不能被关注
5. **关注状态查询**: 任何用户（包括未登录用户）都可以查看关注状态
6. **数据一致性**: 关注/取关操作会实时更新粉丝数量
7. **软删除**: 取关操作使用软删除，保留历史记录

## 实现说明

### 数据库设计
- 使用 `user_follow` 表存储关注关系
- 支持软删除机制（`is_deleted` 字段）
- 建立适当的索引优化查询性能
- 外键约束确保数据完整性

### 安全考虑
- JWT令牌验证确保用户身份
- 参数验证防止恶意输入
- 状态检查防止非法操作
- 错误信息统一处理

### 性能优化
- 数据库索引优化查询
- 批量操作支持（预留）
- 缓存机制（预留）

## 测试用例

参见以下测试文件：
- `UserFollowDaoTest.java` - DAO层测试
- `UserFollowServiceTest.java` - 服务层测试
- `UserFollowControllerTest.java` - 控制器层测试
- `UserFollowIntegrationTest.java` - 集成测试

测试覆盖：
- 正常流程测试
- 边界条件测试
- 错误情况测试
- 并发安全测试
- 数据一致性测试

## 使用示例

### JavaScript/Ajax示例

```javascript
// 关注用户
function followUser(userId) {
    fetch(`/api/user/${userId}/follow`, {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + getJwtToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            console.log('关注成功，粉丝数量:', data.data.followerCount);
            updateFollowButton(true, data.data.followerCount);
        } else {
            console.error('关注失败:', data.error.message);
            showError(data.error.message);
        }
    })
    .catch(error => {
        console.error('请求失败:', error);
    });
}

// 取关用户
function unfollowUser(userId) {
    fetch(`/api/user/${userId}/follow`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + getJwtToken()
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            console.log('取关成功，粉丝数量:', data.data.followerCount);
            updateFollowButton(false, data.data.followerCount);
        } else {
            console.error('取关失败:', data.error.message);
            showError(data.error.message);
        }
    })
    .catch(error => {
        console.error('请求失败:', error);
    });
}

// 获取关注状态
function getFollowStatus(userId) {
    fetch(`/api/user/${userId}/follow`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getJwtToken()
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            updateFollowButton(data.data.isFollowing, data.data.followerCount);
            updateUserStats(data.data);
        } else {
            console.error('获取关注状态失败:', data.error.message);
        }
    })
    .catch(error => {
        console.error('请求失败:', error);
    });
}
```
