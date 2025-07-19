-- ====================================================================
-- LoopBuy 数据库模式文件 (统一版本)
-- ====================================================================
--
-- 使用说明：
-- 1. 创建并初始化数据库：
--    mysql -u root -p -e "CREATE DATABASE shiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; USE shiwu; SOURCE src/main/resources/schema.sql;"
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
--    - administrator: 管理员表 (新增)
--    - audit_log: 审计日志表 (新增)
--
-- ====================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS shiwu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shiwu;
-- USE shiwu; -- 注释掉，让调用者决定使用哪个数据库

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



-- 插入测试用户数据（密码为123456的BCrypt哈希值）
-- BCrypt哈希会随机生成盐值，因此每次生成的哈希值都不同
INSERT INTO system_user (username, password, email, phone, status, nickname) VALUES
('alice', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'alice@example.com', '13800138000', 0, 'Alice Smith'),
('bob', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'bob@example.com', '13800138001', 0, 'Bob Johnson'),
('charlie', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'charlie@example.com', '13800138002', 0, 'Charlie Brown'),
('diana', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'diana@example.com', '13800138003', 0, 'Diana Wilson'),
('eve', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'eve@example.com', '13800138004', 1, 'Eve Davis');

-- 插入关注关系测试数据
INSERT INTO user_follow (follower_id, followed_id) VALUES
(1, 2),  -- alice follows bob
(1, 3),  -- alice follows charlie
(2, 1),  -- bob follows alice
(2, 4),  -- bob follows diana
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
(1, 6, 'iPhone 12 Pro Max', 'Like new, 256GB, Pacific Blue', 5999.00, 1),
(1, 7, 'MacBook Pro 2020', 'Brand new, M1 chip, 13 inch', 8999.00, 1),
(2, 10, 'Nike Sports Shoes', 'Brand new, Size 42, Black', 399.00, 1),
(3, 8, 'Data Structure Textbook', 'Almost new, with some notes', 35.00, 1);

-- 插入测试商品图片数据
INSERT INTO product_image (product_id, image_url, is_main) VALUES
(1, 'https://example.com/iphone1.jpg', 1),
(1, 'https://example.com/iphone2.jpg', 0),
(2, 'https://example.com/macbook1.jpg', 1),
(3, 'https://example.com/nike1.jpg', 1),
(4, 'https://example.com/book1.jpg', 1);

-- ====================================================================
-- 管理员系统相关表（新增）
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
-- 管理员测试数据（新增）
-- ====================================================================

-- 插入管理员测试数据
-- Password: admin123, BCrypt encrypted
INSERT INTO administrator (username, password, email, real_name, role, status, login_count) VALUES
('admin', '$2a$10$zET/DZxiY3ZIElkyQth62u6rmqttBv62/bK0C1.vqw41zH.F9bfA6', 'admin@shiwu.com', 'System Administrator', 'SUPER_ADMIN', 1, 0),
('moderator', '$2a$10$zET/DZxiY3ZIElkyQth62u6rmqttBv62/bK0C1.vqw41zH.F9bfA6', 'moderator@shiwu.com', 'Content Moderator', 'ADMIN', 1, 0);
