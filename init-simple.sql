-- 简化版数据库初始化脚本
CREATE DATABASE IF NOT EXISTS shiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shiwu;

-- 删除已存在的表
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS system_user;

-- 创建用户表（简化版）
CREATE TABLE system_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    nickname VARCHAR(50),
    follower_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建关注表
CREATE TABLE user_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    is_deleted TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_followed (follower_id, followed_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试用户（简化数据）
INSERT INTO system_user (username, password, email, nickname, status) VALUES
('test1', 'password123', 'test1@example.com', '用户1', 1),
('test2', 'password123', 'test2@example.com', '用户2', 1),
('test3', 'password123', 'test3@example.com', '用户3', 1),
('test4', 'password123', 'test4@example.com', '用户4', 1),
('test5', 'password123', 'test5@example.com', '用户5', 1);

-- 插入关注关系
INSERT INTO user_follow (follower_id, followed_id, is_deleted) VALUES
(1, 2, 0),
(1, 3, 0),
(2, 1, 0),
(2, 4, 0),
(4, 2, 0);

-- 更新统计数据
UPDATE system_user SET 
    follower_count = (SELECT COUNT(*) FROM user_follow WHERE followed_id = system_user.id AND is_deleted = 0),
    following_count = (SELECT COUNT(*) FROM user_follow WHERE follower_id = system_user.id AND is_deleted = 0);

-- 显示结果
SELECT 'Database initialized successfully!' AS message;
SELECT COUNT(*) AS user_count FROM system_user;
SELECT COUNT(*) AS follow_count FROM user_follow;
