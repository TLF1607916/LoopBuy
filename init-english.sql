-- LoopBuy Database Initialization Script (English Version)
-- For MySQL 8.0+

CREATE DATABASE IF NOT EXISTS shiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shiwu;

-- Drop existing tables
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS system_user;

-- Create user table
CREATE TABLE system_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'User ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    password VARCHAR(255) NOT NULL COMMENT 'Password (encrypted)',
    email VARCHAR(100) UNIQUE COMMENT 'Email',
    nickname VARCHAR(50) COMMENT 'Nickname',
    avatar_url VARCHAR(255) COMMENT 'Avatar URL',
    phone VARCHAR(20) COMMENT 'Phone number',
    gender TINYINT DEFAULT 0 COMMENT 'Gender: 0-Unknown, 1-Male, 2-Female',
    birthday DATE COMMENT 'Birthday',
    location VARCHAR(100) COMMENT 'Location',
    bio TEXT COMMENT 'Bio',
    school VARCHAR(100) COMMENT 'School',
    follower_count INT DEFAULT 0 COMMENT 'Follower count',
    following_count INT DEFAULT 0 COMMENT 'Following count',
    product_count INT DEFAULT 0 COMMENT 'Product count',
    average_rating DECIMAL(3,2) DEFAULT 0.00 COMMENT 'Average rating',
    status TINYINT DEFAULT 1 COMMENT 'Status: 0-Disabled, 1-Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User table';

-- Create user follow table
CREATE TABLE user_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Follow record ID',
    follower_id BIGINT NOT NULL COMMENT 'Follower ID',
    followed_id BIGINT NOT NULL COMMENT 'Followed user ID',
    is_deleted TINYINT DEFAULT 0 COMMENT 'Is deleted: 0-No, 1-Yes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Follow time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    
    UNIQUE KEY uk_follower_followed (follower_id, followed_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_followed_id (followed_id),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_created_at (created_at),
    
    FOREIGN KEY (follower_id) REFERENCES system_user(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES system_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User follow table';

-- Insert test user data (English)
INSERT INTO system_user (username, password, email, nickname, follower_count, following_count, average_rating, status) VALUES
('alice', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'alice@example.com', 'Alice Smith', 1, 2, 4.5, 1),
('bob', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'bob@example.com', 'Bob Johnson', 2, 2, 4.2, 1),
('charlie', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'charlie@example.com', 'Charlie Brown', 1, 0, 4.8, 1),
('diana', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'diana@example.com', 'Diana Wilson', 1, 1, 4.0, 1),
('eve', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBPdVdEjh/..BG', 'eve@example.com', 'Eve Davis', 0, 0, 3.9, 0);

-- Insert test follow relationships
INSERT INTO user_follow (follower_id, followed_id, is_deleted) VALUES
(1, 2, 0),  -- Alice follows Bob
(1, 3, 0),  -- Alice follows Charlie
(2, 1, 0),  -- Bob follows Alice
(2, 4, 0),  -- Bob follows Diana
(4, 2, 0);  -- Diana follows Bob

-- Update user statistics
UPDATE system_user SET 
    follower_count = (
        SELECT COUNT(*) FROM user_follow 
        WHERE followed_id = system_user.id AND is_deleted = 0
    ),
    following_count = (
        SELECT COUNT(*) FROM user_follow 
        WHERE follower_id = system_user.id AND is_deleted = 0
    );

-- Display results
SELECT 'Database initialization completed!' AS message;
SELECT 'User table records:' AS info, COUNT(*) AS count FROM system_user;
SELECT 'Follow relationship records:' AS info, COUNT(*) AS count FROM user_follow WHERE is_deleted = 0;

-- Show test data
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
