# LoopBuy Backend

LoopBuy项目的后端服务，基于Java Servlet技术栈开发的二手交易平台后端API。

## 项目概述

LoopBuy是一个现代化的二手交易平台，提供用户注册登录、商品发布交易、管理员后台等完整功能。

## 技术栈

- **Java 8+** - 核心开发语言
- **Maven** - 项目构建和依赖管理
- **Servlet API** - Web服务框架
- **MySQL** - 数据库
- **JWT** - 身份认证
- **JUnit 5** - 单元测试
- **Mockito** - 测试Mock框架

## 项目结构

```
backend/
├── src/
│   ├── main/java/com/shiwu/
│   │   ├── admin/          # 管理员模块
│   │   │   ├── controller/ # 管理员控制器
│   │   │   ├── service/    # 管理员服务层
│   │   │   ├── dao/        # 管理员数据访问层
│   │   │   ├── model/      # 管理员数据模型
│   │   │   ├── dto/        # 数据传输对象
│   │   │   ├── vo/         # 视图对象
│   │   │   └── enums/      # 枚举类
│   │   ├── user/           # 用户模块
│   │   │   ├── controller/ # 用户控制器
│   │   │   ├── service/    # 用户服务层
│   │   │   ├── dao/        # 用户数据访问层
│   │   │   └── model/      # 用户数据模型
│   │   ├── common/         # 公共模块
│   │   │   └── util/       # 工具类
│   │   └── test/           # 测试工具
│   └── test/java/          # 测试代码
├── docs/                   # 项目文档
├── pom.xml                 # Maven配置
└── README.md              # 项目说明
```

## 核心功能模块

### 1. 用户模块 (User Module)
- 用户注册/登录
- 用户信息管理
- 用户关注系统
- JWT身份认证

### 2. 管理员模块 (Admin Module)
- 管理员登录/权限验证
- 用户管理（封禁、禁言等）
- 商品管理（审核、下架等）
- 系统统计数据
- 审计日志系统

### 3. 审计日志系统 (Audit Log System)
- **Task5_3_1_3**: 完整的审计日志查询API
- 支持筛选和搜索功能
- 操作记录和追踪
- 统计和趋势分析
- 数据导出功能

### 4. 公共模块 (Common Module)
- JWT工具类
- 密码加密工具
- 数据库连接工具
- JSON处理工具

## 快速开始

### 环境要求
- Java 8 或更高版本
- Maven 3.6+
- MySQL 5.7+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/Doggod727/LoopBuy.git
cd LoopBuy/backend
```

2. **配置数据库**
- 创建MySQL数据库
- 执行数据库初始化脚本
- 配置数据库连接参数

3. **编译项目**
```bash
mvn clean compile
```

4. **运行测试**
```bash
mvn test
```

5. **打包部署**
```bash
mvn clean package
```

## API文档

### 用户API
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/profile` - 获取用户信息
- `POST /api/user/follow` - 关注用户
- `DELETE /api/user/follow` - 取消关注

### 管理员API
- `POST /api/admin/login` - 管理员登录
- `POST /api/admin/verify` - 二次验证
- `GET /api/admin/dashboard` - 获取统计数据
- `POST /api/admin/users/ban` - 封禁用户
- `POST /api/admin/products/approve` - 审核商品

### 审计日志API (Task5_3_1_3)
- `GET /api/admin/audit-logs` - 查询审计日志（支持筛选和搜索）
- `GET /api/admin/audit-logs/{id}` - 获取日志详情
- `GET /api/admin/audit-logs/actions` - 获取操作类型
- `GET /api/admin/audit-logs/stats` - 获取统计数据
- `POST /api/admin/audit-logs/export` - 导出日志

详细API文档请参考 `docs/` 目录。

## 测试

项目包含完整的单元测试和集成测试：

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest="AuditLogServiceTest"

# 运行Task5_3_1_3专门测试
mvn test -Dtest="AuditLogServiceTest#testTask5_3_1_3*"
```

### 测试覆盖率
- **用户模块**: 100%覆盖
- **管理员模块**: 100%覆盖
- **审计日志模块**: 100%覆盖
- **公共工具类**: 100%覆盖

## 安全特性

1. **身份认证**: JWT令牌验证
2. **权限控制**: 基于角色的访问控制
3. **密码安全**: BCrypt加密存储
4. **审计日志**: 完整的操作记录
5. **输入验证**: 参数校验和SQL注入防护

## 性能优化

1. **数据库优化**: 索引优化和查询优化
2. **分页查询**: 避免大数据量查询
3. **缓存机制**: 统计数据缓存
4. **连接池**: 数据库连接池管理

## 开发规范

1. **代码规范**: 遵循Java编码规范
2. **注释规范**: 完整的JavaDoc注释
3. **测试规范**: 每个功能都有对应测试
4. **日志规范**: 统一的日志格式和级别

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码变更
4. 编写测试用例
5. 提交Pull Request

## 许可证

本项目采用MIT许可证，详情请参考LICENSE文件。

## 联系方式

如有问题或建议，请通过以下方式联系：
- GitHub Issues: https://github.com/Doggod727/LoopBuy/issues
- 项目维护者: Doggod727

---

**注意**: 这是LoopBuy项目的后端部分，前端代码请参考项目根目录的其他文件夹。
