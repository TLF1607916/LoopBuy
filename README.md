# Shiwu校园二手交易平台

## 项目简介
Shiwu是一个C2C校园二手交易平台，采用Java Servlet作为后端，React+TypeScript作为前端的Web应用。

## 技术栈
- 后端：Java Servlet API
- 前端：React + TypeScript
- 数据库：MySQL
- 日志：SLF4J
- 数据库访问：JDBC
- 测试：Mockito

## 项目结构
```
src/main/java/com/shiwu/
├── common/           # 公共模块
│   ├── result/       # 统一响应结果
│   └── util/         # 工具类
├── user/             # 用户模块
│   ├── controller/   # 控制器
│   ├── service/      # 服务层
│   ├── dao/          # 数据访问层
│   └── model/        # 数据模型
└── ...               # 其他模块
```

## 已实现功能
- 用户登录：验证用户名是否存在及密码哈希值是否匹配

## 如何运行
1. 配置MySQL数据库，执行`src/main/resources/schema.sql`创建数据库和表
2. 修改`src/main/resources/db.properties`中的数据库连接信息
3. 使用Maven构建项目：`mvn clean package`
4. 将生成的WAR包部署到Servlet容器（如Tomcat）中

## 测试账号
- 用户名：test
- 密码：123456