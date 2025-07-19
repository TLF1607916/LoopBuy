# LoopBuy 数据库设计文档

## 数据库概述

LoopBuy校园二手交易平台采用MySQL 8.0作为主数据库，设计遵循第三范式，支持高并发访问和数据一致性。

### 数据库配置
- **数据库名**: `shiwu`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`
- **引擎**: InnoDB
- **时区**: Asia/Shanghai

## 表结构设计

### 1. 用户表 (system_user)

用户基础信息表，存储平台所有用户的基本信息。

```sql
CREATE TABLE system_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    student_id VARCHAR(20) COMMENT '学号',
    campus VARCHAR(50) COMMENT '校区',
    dormitory VARCHAR(100) COMMENT '宿舍信息',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 0 COMMENT '状态：0-正常，1-禁言，2-封禁',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT '用户表';
```

**字段说明:**
- `id`: 主键，用户唯一标识
- `username`: 用户名，3-20字符，唯一
- `password_hash`: 使用BCrypt加密的密码哈希
- `status`: 用户状态，支持正常、禁言、封禁三种状态
- `campus`: 校区信息，便于同校区交易
- `dormitory`: 宿舍信息，便于线下交易

### 2. 管理员表 (admin_user)

管理员账户表，存储平台管理员信息。

```sql
CREATE TABLE admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色：SUPER_ADMIN, ADMIN',
    status TINYINT DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) COMMENT '管理员表';
```

### 3. 商品表 (product)

商品信息表，存储用户发布的二手商品信息。

```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    category VARCHAR(50) COMMENT '商品分类',
    condition_desc VARCHAR(100) COMMENT '商品成色描述',
    main_image_url VARCHAR(255) COMMENT '主图片URL',
    image_urls JSON COMMENT '图片URL数组',
    tags JSON COMMENT '商品标签数组',
    location VARCHAR(100) COMMENT '交易地点',
    contact_info VARCHAR(200) COMMENT '联系方式',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿，1-在售，2-已售，3-下架',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    favorite_count INT DEFAULT 0 COMMENT '收藏次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_seller_id (seller_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_price (price),
    INDEX idx_create_time (create_time),
    FULLTEXT idx_title_desc (title, description),
    
    FOREIGN KEY (seller_id) REFERENCES system_user(id)
) COMMENT '商品表';
```

**字段说明:**
- `image_urls`: JSON格式存储多张图片URL
- `tags`: JSON格式存储商品标签，便于搜索
- `status`: 商品状态，支持草稿、在售、已售、下架
- `view_count`: 浏览次数，用于热度排序
- `favorite_count`: 收藏次数，用于推荐算法

### 4. 会话表 (conversation)

用户间会话表，管理用户之间的对话会话。

```sql
CREATE TABLE conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    conversation_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话唯一标识',
    participant1_id BIGINT NOT NULL COMMENT '参与者1 ID',
    participant2_id BIGINT NOT NULL COMMENT '参与者2 ID',
    product_id BIGINT COMMENT '关联商品ID',
    last_message TEXT COMMENT '最后一条消息',
    last_message_time DATETIME COMMENT '最后消息时间',
    unread_count1 INT DEFAULT 0 COMMENT '参与者1未读数量',
    unread_count2 INT DEFAULT 0 COMMENT '参与者2未读数量',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '会话状态：ACTIVE, ARCHIVED, BLOCKED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_participant1 (participant1_id),
    INDEX idx_participant2 (participant2_id),
    INDEX idx_product_id (product_id),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_status (status),
    
    FOREIGN KEY (participant1_id) REFERENCES system_user(id),
    FOREIGN KEY (participant2_id) REFERENCES system_user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) COMMENT '会话表';
```

**字段说明:**
- `conversation_id`: 会话唯一标识，格式：{user1_id}_{user2_id}_{product_id}
- `participant1_id/participant2_id`: 会话参与者，按ID大小排序
- `unread_count1/unread_count2`: 分别记录两个参与者的未读消息数
- `status`: 会话状态，支持活跃、归档、屏蔽

### 5. 消息表 (message)

消息内容表，存储用户间的具体消息内容。

```sql
CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    conversation_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    product_id BIGINT COMMENT '关联商品ID',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) DEFAULT 'TEXT' COMMENT '消息类型：TEXT, IMAGE, SYSTEM',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_product_id (product_id),
    INDEX idx_create_time (create_time),
    INDEX idx_is_read (is_read),
    
    FOREIGN KEY (sender_id) REFERENCES system_user(id),
    FOREIGN KEY (receiver_id) REFERENCES system_user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) COMMENT '消息表';
```

**字段说明:**
- `message_type`: 消息类型，支持文本、图片、系统消息
- `is_read`: 已读状态，用于未读消息提醒
- `product_id`: 关联商品，便于商品咨询场景

### 6. 购物车表 (cart_item)

购物车表，存储用户的购物车商品。

```sql
CREATE TABLE cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车项ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    
    FOREIGN KEY (user_id) REFERENCES system_user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) COMMENT '购物车表';
```

### 7. 用户关注表 (user_follow)

用户关注关系表，存储用户间的关注关系。

```sql
CREATE TABLE user_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注ID',
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    followed_id BIGINT NOT NULL COMMENT '被关注者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    UNIQUE KEY uk_follower_followed (follower_id, followed_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_followed_id (followed_id),
    
    FOREIGN KEY (follower_id) REFERENCES system_user(id),
    FOREIGN KEY (followed_id) REFERENCES system_user(id)
) COMMENT '用户关注表';
```

### 8. 审计日志表 (audit_log)

管理员操作审计日志表，记录所有管理员操作。

```sql
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    admin_id BIGINT NOT NULL COMMENT '管理员ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    target_type VARCHAR(50) COMMENT '目标类型',
    target_id BIGINT COMMENT '目标ID',
    details TEXT COMMENT '操作详情',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    success BOOLEAN DEFAULT TRUE COMMENT '操作是否成功',
    error_message TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_admin_id (admin_id),
    INDEX idx_action (action),
    INDEX idx_target_type (target_type),
    INDEX idx_target_id (target_id),
    INDEX idx_create_time (create_time),
    INDEX idx_success (success),
    
    FOREIGN KEY (admin_id) REFERENCES admin_user(id)
) COMMENT '审计日志表';
```

## 索引设计

### 主要索引策略
1. **主键索引**: 所有表都有自增主键
2. **唯一索引**: 用户名、邮箱等唯一字段
3. **复合索引**: 多字段查询场景
4. **外键索引**: 关联查询优化
5. **全文索引**: 商品标题和描述搜索

### 性能优化索引
- `idx_product_seller_status`: 商品表按卖家和状态查询
- `idx_message_conversation_time`: 消息表按会话和时间排序
- `idx_user_status_create`: 用户表按状态和创建时间

## 数据约束

### 外键约束
- 商品表关联用户表（卖家）
- 消息表关联用户表（发送者、接收者）
- 会话表关联用户表和商品表
- 购物车表关联用户表和商品表

### 检查约束
- 用户状态：0-2范围
- 商品状态：0-3范围
- 价格：大于0
- 数量：大于0

### 业务约束
- 用户不能关注自己
- 会话参与者不能相同
- 消息发送者和接收者不能相同

## 数据初始化

### 管理员账户
```sql
INSERT INTO admin_user (username, password_hash, real_name, role) VALUES
('admin', '$2a$10$...', '系统管理员', 'SUPER_ADMIN');
```

### 基础数据
- 商品分类数据
- 校区信息数据
- 系统配置数据

## 备份策略

### 备份方案
1. **全量备份**: 每日凌晨2点
2. **增量备份**: 每4小时一次
3. **日志备份**: 实时备份binlog
4. **异地备份**: 每周同步到异地

### 恢复策略
1. **时间点恢复**: 基于binlog
2. **表级恢复**: 单表数据恢复
3. **灾难恢复**: 异地数据中心切换

## 监控指标

### 性能监控
- 查询响应时间
- 慢查询统计
- 连接数监控
- 锁等待监控

### 容量监控
- 表空间使用率
- 索引大小监控
- 数据增长趋势
- 磁盘空间预警

## 版本管理

### 数据库版本
- 当前版本：v1.0.0
- 升级脚本：migration/
- 回滚脚本：rollback/

### 变更记录
- v1.0.0: 初始版本，包含用户、商品、消息模块
- 后续版本将支持订单、支付、评价等功能
