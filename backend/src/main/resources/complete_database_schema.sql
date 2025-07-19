-- ====================================================================
-- LoopBuy 完整数据库模式文件 (统一版本)
-- ====================================================================
--
-- 使用说明：
-- 1. 创建并初始化数据库：
--    mysql -u root -p123456 -e "DROP DATABASE IF EXISTS shiwu; CREATE DATABASE shiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
--    mysql -u root -p123456 shiwu < src/main/resources/complete_database_schema.sql
--
-- 2. 测试账户：
--    普通用户: alice/123456, bob/123456, charlie/123456, diana/123456, eve/123456
--    管理员: admin/admin123 (超级管理员), moderator/admin123 (普通管理员)
--
-- 3. 包含的表：
--    - system_user: 用户表
--    - user_follow: 用户关注表
--    - category: 商品分类表
--    - product: 商品表
--    - product_image: 商品图片表
--    - shopping_cart: 购物车表
--    - trade_order: 订单表
--    - payment: 支付表
--    - administrator: 管理员表
--    - audit_log: 审计日志表
--    - conversation: 会话表
--    - message: 消息表
--    - notification: 通知表
--    - notification_template: 通知模板表
--
-- 4. 功能支持：
--    - Task4_1_1_1: 消息/会话功能
--    - Task4_2_1_2: 商品审核通过粉丝通知
--    - Task4_2_1_3: 获取关注动态信息流
--    - Task5_3_1_3: 管理员审计日志
--
-- ====================================================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 使用数据库
USE shiwu;

-- ====================================================================
-- 1. 用户相关表
-- ====================================================================

-- 创建用户表
CREATE TABLE IF NOT EXISTS system_user (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码哈希',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '用户状态：0-正常，1-已封禁，2-已禁言',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    nickname VARCHAR(50) COMMENT '昵称',
    gender TINYINT COMMENT '性别：0-未设置，1-男，2-女',
    bio VARCHAR(500) COMMENT '个人简介',
    follower_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '粉丝数量',
    average_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '平均评分（作为卖家）',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建用户关注表
CREATE TABLE IF NOT EXISTS user_follow (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    follower_id BIGINT UNSIGNED NOT NULL COMMENT '关注者用户ID',
    followed_id BIGINT UNSIGNED NOT NULL COMMENT '被关注者用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_follower_followed (follower_id, followed_id),
    KEY idx_follower_id (follower_id),
    KEY idx_followed_id (followed_id),
    CONSTRAINT fk_follower_user FOREIGN KEY (follower_id) REFERENCES system_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_followed_user FOREIGN KEY (followed_id) REFERENCES system_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- ====================================================================
-- 2. 商品相关表
-- ====================================================================

-- 创建商品分类表
CREATE TABLE IF NOT EXISTS category (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID（0表示顶级分类）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 创建商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家ID',
    category_id INT UNSIGNED NOT NULL COMMENT '商品分类ID',
    title VARCHAR(100) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '商品状态：0-待审核，1-在售，2-已售出，3-已下架，4-草稿',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_seller_id (seller_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    FOREIGN KEY (seller_id) REFERENCES system_user(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 创建商品图片表
CREATE TABLE IF NOT EXISTS product_image (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    is_main TINYINT NOT NULL DEFAULT 0 COMMENT '是否主图：0-否，1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- ====================================================================
-- 3. 交易相关表
-- ====================================================================

-- 创建购物车表
CREATE TABLE IF NOT EXISTS shopping_cart (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    quantity INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '商品数量（本项目固定为1）',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标志（0：未删除，1：已删除）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id_product_id (user_id, product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (user_id) REFERENCES system_user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 创建订单表
CREATE TABLE IF NOT EXISTS trade_order (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    buyer_id BIGINT UNSIGNED NOT NULL COMMENT '买家用户ID',
    seller_id BIGINT UNSIGNED NOT NULL COMMENT '卖家用户ID',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    price_at_purchase DECIMAL(10,2) NOT NULL COMMENT '购买时的商品价格（快照）',
    product_title_snapshot VARCHAR(100) NOT NULL COMMENT '商品标题快照',
    product_description_snapshot TEXT COMMENT '商品描述快照',
    product_image_urls_snapshot TEXT COMMENT '商品图片URL列表快照（JSON格式）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-已发货，3-已完成，4-已取消，5-申请退货，6-已退货',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_buyer_id_status (buyer_id, status),
    INDEX idx_seller_id_status (seller_id, status),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),

    FOREIGN KEY (buyer_id) REFERENCES system_user(id),
    FOREIGN KEY (seller_id) REFERENCES system_user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建支付表
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '支付记录ID',
    payment_id VARCHAR(64) NOT NULL UNIQUE COMMENT '支付流水号（唯一标识）',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    order_ids TEXT NOT NULL COMMENT '订单ID列表（JSON格式）',
    payment_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method TINYINT NOT NULL COMMENT '支付方式：1-支付宝，2-微信支付，3-银行卡',
    payment_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败，3-支付取消，4-支付超时',
    third_party_transaction_id VARCHAR(128) COMMENT '第三方交易号',
    failure_reason VARCHAR(255) COMMENT '失败原因',
    payment_time DATETIME COMMENT '支付完成时间',
    expire_time DATETIME NOT NULL COMMENT '支付超时时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_payment_id (payment_id),
    INDEX idx_user_id_status (user_id, payment_status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_expire_time (expire_time),
    INDEX idx_create_time (create_time),

    FOREIGN KEY (user_id) REFERENCES system_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ====================================================================
-- 4. 管理员相关表
-- ====================================================================

-- 创建管理员表
CREATE TABLE IF NOT EXISTS administrator (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    email VARCHAR(100) COMMENT '邮箱',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色：ADMIN-普通管理员，SUPER_ADMIN-超级管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '登录次数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    admin_id BIGINT UNSIGNED NOT NULL COMMENT '管理员ID',
    action VARCHAR(100) NOT NULL COMMENT '操作类型',
    target_type VARCHAR(50) COMMENT '操作目标类型（USER、PRODUCT等）',
    target_id BIGINT UNSIGNED COMMENT '操作目标ID',
    details TEXT COMMENT '操作详情（JSON格式）',
    ip_address VARCHAR(45) COMMENT 'IP地址（支持IPv6）',
    user_agent VARCHAR(500) COMMENT '用户代理',
    result TINYINT NOT NULL DEFAULT 1 COMMENT '操作结果：0-失败，1-成功',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',

    INDEX idx_admin_id (admin_id),
    INDEX idx_action (action),
    INDEX idx_target_type (target_type),
    INDEX idx_target_id (target_id),
    INDEX idx_create_time (create_time),
    INDEX idx_result (result),

    FOREIGN KEY (admin_id) REFERENCES administrator(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- ====================================================================
-- 5. 消息/会话相关表 (Task4_1_1_1)
-- ====================================================================

-- 创建会话表
CREATE TABLE IF NOT EXISTS conversation (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    conversation_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话唯一标识符，格式：smaller_user_id_larger_user_id_product_id',
    participant1_id BIGINT UNSIGNED NOT NULL COMMENT '参与者1的用户ID（较小的用户ID）',
    participant2_id BIGINT UNSIGNED NOT NULL COMMENT '参与者2的用户ID（较大的用户ID）',
    product_id BIGINT UNSIGNED COMMENT '关联的商品ID（可选，用于商品咨询）',

    last_message TEXT COMMENT '最后一条消息内容',
    last_message_time DATETIME COMMENT '最后一条消息时间',
    unread_count1 INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '参与者1的未读消息数量',
    unread_count2 INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '参与者2的未读消息数量',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态：ACTIVE-活跃，ARCHIVED-已归档，BLOCKED-已屏蔽',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',

    INDEX idx_conversation_id (conversation_id),
    INDEX idx_participant1_id (participant1_id),
    INDEX idx_participant2_id (participant2_id),
    INDEX idx_product_id (product_id),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_status (status),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- 创建消息表
CREATE TABLE IF NOT EXISTS message (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID，关联conversation表',
    sender_id BIGINT UNSIGNED NOT NULL COMMENT '发送者用户ID',
    receiver_id BIGINT UNSIGNED NOT NULL COMMENT '接收者用户ID',
    product_id BIGINT UNSIGNED COMMENT '关联的商品ID（可选）',

    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型：TEXT-文本消息，IMAGE-图片消息，SYSTEM-系统消息',

    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',

    INDEX idx_conversation_id_create_time (conversation_id, create_time),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_product_id (product_id),
    INDEX idx_message_type (message_type),
    INDEX idx_is_read (is_read),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ====================================================================
-- 6. 通知相关表 (Task4_2_1_2, Task4_2_1_3)
-- ====================================================================

-- 创建通知表
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',

    recipient_id BIGINT UNSIGNED NOT NULL COMMENT '接收者用户ID',

    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content VARCHAR(500) NOT NULL COMMENT '通知内容',

    notification_type VARCHAR(50) NOT NULL COMMENT '通知类型：PRODUCT_APPROVED-商品审核通过, PRODUCT_PUBLISHED-新商品发布, ORDER_STATUS-订单状态变更, MESSAGE_RECEIVED-新消息, SYSTEM_NOTICE-系统公告',
    source_type VARCHAR(50) NOT NULL COMMENT '来源类型：PRODUCT-商品, ORDER-订单, MESSAGE-消息, SYSTEM-系统',
    source_id BIGINT UNSIGNED COMMENT '来源实体ID（如商品ID、订单ID等）',

    related_user_id BIGINT UNSIGNED COMMENT '相关用户ID（如发送者、卖家等）',
    related_user_name VARCHAR(50) COMMENT '相关用户名称（冗余字段，提高查询效率）',

    action_url VARCHAR(255) COMMENT '点击通知后的跳转链接',

    is_read TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    read_time DATETIME COMMENT '阅读时间',

    priority TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '优先级：1-普通，2-重要，3-紧急',

    expire_time DATETIME COMMENT '过期时间（可选）',

    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-正常，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_recipient_id (recipient_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_source_type_id (source_type, source_id),
    INDEX idx_related_user_id (related_user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time),
    INDEX idx_priority (priority),
    INDEX idx_expire_time (expire_time),

    INDEX idx_recipient_read_time (recipient_id, is_read, create_time DESC),
    INDEX idx_recipient_type (recipient_id, notification_type),
    INDEX idx_notification_unread_priority (recipient_id, is_read, priority DESC, create_time DESC),
    INDEX idx_notification_cleanup (is_deleted, expire_time, create_time),

    FOREIGN KEY (recipient_id) REFERENCES system_user(id) ON DELETE CASCADE,
    FOREIGN KEY (related_user_id) REFERENCES system_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 创建通知模板表
CREATE TABLE IF NOT EXISTS notification_template (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',

    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板代码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',

    title_template VARCHAR(200) NOT NULL COMMENT '标题模板（支持占位符）',
    content_template VARCHAR(1000) NOT NULL COMMENT '内容模板（支持占位符）',

    notification_type VARCHAR(50) NOT NULL COMMENT '通知类型',
    priority TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '默认优先级',
    expire_hours INT UNSIGNED COMMENT '过期小时数（null表示不过期）',

    is_active TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_template_code (template_code),
    INDEX idx_notification_type (notification_type),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- ====================================================================
-- 7. 创建视图简化查询
-- ====================================================================

-- 创建未读通知统计视图
CREATE OR REPLACE VIEW v_unread_notification_count AS
SELECT
    recipient_id,
    COUNT(*) as unread_count,
    COUNT(CASE WHEN priority >= 2 THEN 1 END) as important_count
FROM notification
WHERE is_read = 0
  AND is_deleted = 0
  AND (expire_time IS NULL OR expire_time > NOW())
GROUP BY recipient_id;

-- 创建最新通知视图
CREATE OR REPLACE VIEW v_latest_notifications AS
SELECT
    n.*,
    u.username as related_username,
    u.avatar_url as related_user_avatar
FROM notification n
LEFT JOIN system_user u ON n.related_user_id = u.id
WHERE n.is_deleted = 0
  AND (n.expire_time IS NULL OR n.expire_time > NOW())
ORDER BY n.create_time DESC;

-- ====================================================================
-- 8. 插入测试数据
-- ====================================================================

-- 插入测试用户数据（密码为123456的BCrypt哈希值）
INSERT INTO system_user (username, password, email, phone, status, nickname, follower_count) VALUES
('alice', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'alice@example.com', '13800138000', 0, 'Alice Smith', 2),
('bob', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'bob@example.com', '13800138001', 0, 'Bob Johnson', 3),
('charlie', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'charlie@example.com', '13800138002', 0, 'Charlie Brown', 1),
('diana', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'diana@example.com', '13800138003', 0, 'Diana Wilson', 1),
('eve', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'eve@example.com', '13800138004', 1, 'Eve Davis', 0);

-- 插入关注关系测试数据
INSERT INTO user_follow (follower_id, followed_id) VALUES
(1, 2),  -- alice follows bob
(1, 3),  -- alice follows charlie
(2, 1),  -- bob follows alice
(2, 4),  -- bob follows diana
(3, 2),  -- charlie follows bob
(4, 2);  -- diana follows bob

-- 插入测试分类数据
INSERT INTO category (name, parent_id) VALUES
('Electronics', 0),
('Books', 0),
('Fashion', 0),
('Sports', 0),
('Daily Goods', 0),
('Mobile Phones', 1),
('Computers', 1),
('Professional Books', 2),
('Literature', 2),
('Sports Shoes', 3);

-- 插入测试商品数据
INSERT INTO product (seller_id, category_id, title, description, price, status) VALUES
(2, 6, 'iPhone 13 Pro 二手', '95成新，256GB，太平洋蓝色，无磕碰', 5999.00, 1),
(2, 7, 'MacBook Air M2', '全新未拆封，M2芯片，13寸', 8999.00, 1),
(3, 10, 'Nike运动鞋', '全新，42码，黑色', 399.00, 1),
(3, 8, '数据结构教材', '九成新，有少量笔记', 35.00, 1),
(4, 6, 'Samsung Galaxy S23', '全新，128GB，幻影黑', 4999.00, 1),
(4, 3, '冬季外套', '品牌外套，L码，保暖', 299.00, 1);

-- 插入测试商品图片数据
INSERT INTO product_image (product_id, image_url, is_main) VALUES
(1, '/uploads/products/1_1.jpg', 1),
(1, '/uploads/products/1_2.jpg', 0),
(2, '/uploads/products/2_1.jpg', 1),
(3, '/uploads/products/3_1.jpg', 1),
(4, '/uploads/products/4_1.jpg', 1),
(5, '/uploads/products/5_1.jpg', 1),
(6, '/uploads/products/6_1.jpg', 1);

-- 插入管理员测试数据（密码为admin123的BCrypt哈希值）
INSERT INTO administrator (username, password, email, real_name, role, status, login_count) VALUES
('admin', '$2a$10$zET/DZxiY3ZIElkyQth62u6rmqttBv62/bK0C1.vqw41zH.F9bfA6', 'admin@shiwu.com', 'System Administrator', 'SUPER_ADMIN', 1, 0),
('moderator', '$2a$10$zET/DZxiY3ZIElkyQth62u6rmqttBv62/bK0C1.vqw41zH.F9bfA6', 'moderator@shiwu.com', 'Content Moderator', 'ADMIN', 1, 0);

-- 插入测试会话数据
INSERT INTO conversation (conversation_id, participant1_id, participant2_id, product_id, last_message, last_message_time, unread_count1, unread_count2, status) VALUES
('1_2_1', 1, 2, 1, 'iPhone还有货吗？', '2024-01-15 10:30:00', 0, 1, 'ACTIVE'),
('1_3_2', 1, 3, 2, 'MacBook配置怎么样？', '2024-01-15 11:00:00', 1, 0, 'ACTIVE'),
('2_4_3', 2, 4, 3, '这双鞋还有其他颜色吗？', '2024-01-15 14:20:00', 0, 2, 'ACTIVE');

-- 插入测试消息数据
INSERT INTO message (conversation_id, sender_id, receiver_id, product_id, content, message_type, is_read) VALUES
('1_2_1', 1, 2, 1, '你好，这个iPhone 13 Pro还有货吗？', 'TEXT', 1),
('1_2_1', 2, 1, 1, '有的，成色很好，几乎全新', 'TEXT', 1),
('1_2_1', 1, 2, 1, '可以面交吗？你在哪个校区？', 'TEXT', 1),
('1_2_1', 2, 1, 1, '可以，我在东校区，你呢？', 'TEXT', 0),

('1_3_2', 1, 3, 2, '这个MacBook Pro配置怎么样？', 'TEXT', 1),
('1_3_2', 3, 1, 2, 'M2芯片，13寸，256GB存储，全新未拆封', 'TEXT', 0),

('2_4_3', 2, 4, 3, '这双Nike运动鞋还有其他颜色吗？', 'TEXT', 1),
('2_4_3', 4, 2, 3, '只有这双黑色的了，42码', 'TEXT', 1),
('2_4_3', 2, 4, 3, '好的，价格可以商量吗？', 'TEXT', 0);

-- 插入通知模板数据
INSERT INTO notification_template (
    template_code,
    template_name,
    title_template,
    content_template,
    notification_type,
    priority,
    expire_hours
) VALUES
(
    'PRODUCT_APPROVED_FOLLOWER',
    '关注的卖家发布新商品',
    '您关注的 {sellerName} 发布了新商品',
    '您关注的卖家 {sellerName} 刚刚发布了新商品《{productTitle}》，快来看看吧！',
    'PRODUCT_APPROVED',
    1,
    168  -- 7天过期
),
(
    'PRODUCT_PUBLISHED_FOLLOWER',
    '关注的卖家发布新商品',
    '您关注的 {sellerName} 发布了新商品',
    '您关注的卖家 {sellerName} 刚刚发布了新商品《{productTitle}》，快来看看吧！',
    'PRODUCT_PUBLISHED',
    1,
    168  -- 7天过期
),
(
    'SYSTEM_NOTICE',
    '系统公告',
    '系统公告：{title}',
    '{content}',
    'SYSTEM_NOTICE',
    2,
    NULL  -- 不过期
),
(
    'MESSAGE_RECEIVED',
    '收到新消息',
    '您收到了来自 {senderName} 的新消息',
    '{senderName} 给您发送了一条消息：{messageContent}',
    'MESSAGE_RECEIVED',
    1,
    72  -- 3天过期
) ON DUPLICATE KEY UPDATE
    template_name = VALUES(template_name),
    title_template = VALUES(title_template),
    content_template = VALUES(content_template),
    update_time = CURRENT_TIMESTAMP;

-- 插入测试通知数据（用于Task4_2_1_3测试）
INSERT INTO notification (
    recipient_id,
    title,
    content,
    notification_type,
    source_type,
    source_id,
    related_user_id,
    related_user_name,
    action_url,
    is_read,
    priority,
    create_time
) VALUES
-- Alice关注Bob，Bob发布了商品，Alice收到通知
(1, '您关注的 Bob Johnson 发布了新商品', '您关注的卖家 Bob Johnson 刚刚发布了新商品《iPhone 13 Pro 二手》，快来看看吧！', 'PRODUCT_APPROVED', 'PRODUCT', 1, 2, 'Bob Johnson', '/product/1', 0, 1, '2024-01-15 10:00:00'),
(1, '您关注的 Bob Johnson 发布了新商品', '您关注的卖家 Bob Johnson 刚刚发布了新商品《MacBook Air M2》，快来看看吧！', 'PRODUCT_PUBLISHED', 'PRODUCT', 2, 2, 'Bob Johnson', '/product/2', 0, 1, '2024-01-15 11:30:00'),

-- Alice关注Charlie，Charlie发布了商品，Alice收到通知
(1, '您关注的 Charlie Brown 发布了新商品', '您关注的卖家 Charlie Brown 刚刚发布了新商品《Nike运动鞋》，快来看看吧！', 'PRODUCT_APPROVED', 'PRODUCT', 3, 3, 'Charlie Brown', '/product/3', 1, 1, '2024-01-15 09:15:00'),
(1, '您关注的 Charlie Brown 发布了新商品', '您关注的卖家 Charlie Brown 刚刚发布了新商品《数据结构教材》，快来看看吧！', 'PRODUCT_PUBLISHED', 'PRODUCT', 4, 3, 'Charlie Brown', '/product/4', 0, 1, '2024-01-15 12:45:00'),

-- Bob关注Alice和Diana，收到相关通知
(2, '您关注的 Diana Wilson 发布了新商品', '您关注的卖家 Diana Wilson 刚刚发布了新商品《Samsung Galaxy S23》，快来看看吧！', 'PRODUCT_APPROVED', 'PRODUCT', 5, 4, 'Diana Wilson', '/product/5', 0, 1, '2024-01-15 13:20:00'),
(2, '您关注的 Diana Wilson 发布了新商品', '您关注的卖家 Diana Wilson 刚刚发布了新商品《冬季外套》，快来看看吧！', 'PRODUCT_PUBLISHED', 'PRODUCT', 6, 4, 'Diana Wilson', '/product/6', 1, 1, '2024-01-15 14:10:00'),

-- Charlie关注Bob，收到Bob的商品通知
(3, '您关注的 Bob Johnson 发布了新商品', '您关注的卖家 Bob Johnson 刚刚发布了新商品《iPhone 13 Pro 二手》，快来看看吧！', 'PRODUCT_APPROVED', 'PRODUCT', 1, 2, 'Bob Johnson', '/product/1', 0, 1, '2024-01-15 10:00:00'),
(3, '您关注的 Bob Johnson 发布了新商品', '您关注的卖家 Bob Johnson 刚刚发布了新商品《MacBook Air M2》，快来看看吧！', 'PRODUCT_PUBLISHED', 'PRODUCT', 2, 2, 'Bob Johnson', '/product/2', 0, 1, '2024-01-15 11:30:00'),

-- Diana关注Bob，收到Bob的商品通知
(4, '您关注的 Bob Johnson 发布了新商品', '您关注的卖家 Bob Johnson 刚刚发布了新商品《iPhone 13 Pro 二手》，快来看看吧！', 'PRODUCT_APPROVED', 'PRODUCT', 1, 2, 'Bob Johnson', '/product/1', 1, 1, '2024-01-15 10:00:00'),
(4, '您关注的 Bob Johnson 发布了新商品', '您关注的卖家 Bob Johnson 刚刚发布了新商品《MacBook Air M2》，快来看看吧！', 'PRODUCT_PUBLISHED', 'PRODUCT', 2, 2, 'Bob Johnson', '/product/2', 0, 1, '2024-01-15 11:30:00');

-- ====================================================================
-- 9. 恢复外键检查和完成设置
-- ====================================================================

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 完成提示
SELECT 'LoopBuy complete database schema created successfully!' as status,
       'Users: alice, bob, charlie, diana, eve (password: 123456)' as test_users,
       'Admins: admin, moderator (password: admin123)' as test_admins,
       'All tables and test data have been created.' as note;
