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
    school VARCHAR(100) COMMENT '学校',
    follower_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '粉丝数量',
    average_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '平均评分（作为卖家）',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户数据（密码为123456的BCrypt哈希值）
-- BCrypt哈希会随机生成盐值，因此每次生成的哈希值都不同
INSERT INTO system_user (username, password, email, phone, status, nickname, school) VALUES
('test', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'test@example.com', '13800138000', 0, '测试用户', '示例大学');