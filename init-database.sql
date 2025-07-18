-- LoopBuy 数据库初始化脚本
-- 适用于 MySQL 8.0+

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS shiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE shiwu;

-- 删除已存在的表（重新创建）
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS system_user;

-- 创建用户表
CREATE TABLE system_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    birthday DATE COMMENT '生日',
    location VARCHAR(100) COMMENT '所在地',
    bio TEXT COMMENT '个人简介',
    follower_count INT DEFAULT 0 COMMENT '粉丝数量',
    following_count INT DEFAULT 0 COMMENT '关注数量',
    product_count INT DEFAULT 0 COMMENT '商品数量',
    average_rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '平均评分',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建用户关注表
CREATE TABLE user_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关注记录ID',
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    followed_id BIGINT NOT NULL COMMENT '被关注者ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_follower_followed (follower_id, followed_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_followed_id (followed_id),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_created_at (created_at),
    
    FOREIGN KEY (follower_id) REFERENCES system_user(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES system_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- 插入测试用户数据
INSERT INTO system_user (username, password, email, nickname, follower_count, following_count, average_rating, status) VALUES
('test1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'test1@example.com', '测试用户1', 1, 2, 4.5, 1),
('test2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'test2@example.com', '测试用户2', 2, 1, 4.2, 1),
('test3', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'test3@example.com', '测试用户3', 0, 1, 4.8, 1),
('test4', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'test4@example.com', '测试用户4', 1, 0, 4.0, 1),
('test5', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'test5@example.com', '测试用户5', 0, 0, 3.9, 0);

-- 插入测试关注关系数据
INSERT INTO user_follow (follower_id, followed_id, is_deleted) VALUES
(1, 2, 0),  -- 用户1关注用户2
(1, 3, 0),  -- 用户1关注用户3
(2, 1, 0),  -- 用户2关注用户1
(2, 4, 0),  -- 用户2关注用户4
(4, 2, 0);  -- 用户4关注用户2

-- 更新用户的关注数和粉丝数（确保数据一致性）
UPDATE system_user SET 
    follower_count = (
        SELECT COUNT(*) FROM user_follow 
        WHERE followed_id = system_user.id AND is_deleted = 0
    ),
    following_count = (
        SELECT COUNT(*) FROM user_follow 
        WHERE follower_id = system_user.id AND is_deleted = 0
    );

-- 显示创建结果
SELECT '数据库初始化完成！' AS message;
SELECT '用户表记录数：' AS info, COUNT(*) AS count FROM system_user;
SELECT '关注关系记录数：' AS info, COUNT(*) AS count FROM user_follow WHERE is_deleted = 0;

-- 显示测试数据
SELECT 
    u.id,
    u.username,
    u.nickname,
    u.follower_count,
    u.following_count,
    u.status
FROM system_user u
ORDER BY u.id;

SELECT 
    f.id,
    f.follower_id,
    u1.username AS follower_username,
    f.followed_id,
    u2.username AS followed_username,
    f.created_at
FROM user_follow f
JOIN system_user u1 ON f.follower_id = u1.id
JOIN system_user u2 ON f.followed_id = u2.id
WHERE f.is_deleted = 0
ORDER BY f.created_at;
