-- 创建数据库
CREATE DATABASE IF NOT EXISTS shiwu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shiwu;

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
('电子产品', 0),
('图书教材', 0),
('服饰鞋包', 0),
('运动户外', 0),
('日用百货', 0),
('手机', 1),
('电脑', 1),
('专业书籍', 2),
('文学小说', 2),
('运动鞋', 3);

-- 插入测试商品数据
INSERT INTO product (seller_id, category_id, title, description, price, status) VALUES
(1, 6, 'iPhone 12 Pro Max', '九成新，256GB，海蓝色', 5999.00, 1),
(1, 7, 'MacBook Pro 2020', '全新未拆封，M1芯片，13寸', 8999.00, 1),
(2, 10, '耐克运动鞋', '全新，尺码42，黑色', 399.00, 1),
(3, 8, '数据结构与算法教材', '几乎全新，有少量笔记', 35.00, 1);

-- 插入测试商品图片数据
INSERT INTO product_image (product_id, image_url, is_main) VALUES
(1, 'https://example.com/iphone1.jpg', 1),
(1, 'https://example.com/iphone2.jpg', 0),
(2, 'https://example.com/macbook1.jpg', 1),
(3, 'https://example.com/nike1.jpg', 1),
(4, 'https://example.com/book1.jpg', 1);
