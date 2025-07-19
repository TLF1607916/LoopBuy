-- ====================================================================
-- 消息/会话模块数据表设计 (Task4_1_1_1)
-- ====================================================================
--
-- 设计原则：
-- 1. 严格遵循 .cursor-rules.json 中的数据库规范
-- 2. 表名和字段名使用 lowercase_snake_case
-- 3. 表名使用单数形式
-- 4. 包含强制字段：id, create_time, update_time, is_deleted
-- 5. 不使用物理外键约束，在应用层管理关系
-- 6. 支持逻辑删除
--
-- 功能支持：
-- - UC-11: 发送和接收商品咨询
-- - 用户间的实时消息通信
-- - 会话管理和消息历史
-- - 消息状态管理（已读/未读）
--
-- ====================================================================

USE shiwu;

-- ====================================================================
-- 1. 会话表 (conversation)
-- ====================================================================
-- 用于管理用户间的对话会话，每个会话可能关联一个商品
CREATE TABLE IF NOT EXISTS conversation (
    -- 强制字段
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 业务字段
    conversation_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话唯一标识符，格式：smaller_user_id_larger_user_id_product_id',
    participant1_id BIGINT UNSIGNED NOT NULL COMMENT '参与者1的用户ID（较小的用户ID）',
    participant2_id BIGINT UNSIGNED NOT NULL COMMENT '参与者2的用户ID（较大的用户ID）',
    product_id BIGINT UNSIGNED COMMENT '关联的商品ID（可选，用于商品咨询）',
    
    -- 会话状态字段
    last_message TEXT COMMENT '最后一条消息内容',
    last_message_time DATETIME COMMENT '最后一条消息时间',
    unread_count1 INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '参与者1的未读消息数量',
    unread_count2 INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '参与者2的未读消息数量',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态：ACTIVE-活跃，ARCHIVED-已归档，BLOCKED-已屏蔽',
    
    -- 强制字段
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    
    -- 索引
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_participant1_id (participant1_id),
    INDEX idx_participant2_id (participant2_id),
    INDEX idx_product_id (product_id),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_status (status),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_create_time (create_time)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- ====================================================================
-- 2. 消息表 (message)
-- ====================================================================
-- 存储用户间的具体消息内容
CREATE TABLE IF NOT EXISTS message (
    -- 强制字段
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 关联字段
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID，关联conversation表',
    sender_id BIGINT UNSIGNED NOT NULL COMMENT '发送者用户ID',
    receiver_id BIGINT UNSIGNED NOT NULL COMMENT '接收者用户ID',
    product_id BIGINT UNSIGNED COMMENT '关联的商品ID（可选）',
    
    -- 消息内容字段
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型：TEXT-文本消息，IMAGE-图片消息，SYSTEM-系统消息',
    
    -- 状态字段
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    
    -- 强制字段
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    
    -- 索引
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
-- 3. 插入测试数据
-- ====================================================================

-- 插入测试会话数据
INSERT INTO conversation (conversation_id, participant1_id, participant2_id, product_id, last_message, last_message_time, unread_count1, unread_count2, status) VALUES
('1_2_1', 1, 2, 1, 'Is this iPhone still available?', '2024-01-15 10:30:00', 0, 1, 'ACTIVE'),
('1_3_2', 1, 3, 2, 'How is the MacBook configuration?', '2024-01-15 11:00:00', 1, 0, 'ACTIVE'),
('2_4_3', 2, 4, 3, 'Are there other colors for these shoes?', '2024-01-15 14:20:00', 0, 2, 'ACTIVE');

-- 插入测试消息数据
INSERT INTO message (conversation_id, sender_id, receiver_id, product_id, content, message_type, is_read) VALUES
-- 会话1：用户1和用户2关于商品1的对话
('1_2_1', 1, 2, 1, 'Hello, is this iPhone 12 Pro Max still available?', 'TEXT', 1),
('1_2_1', 2, 1, 1, 'Yes, it is in excellent condition, almost new', 'TEXT', 1),
('1_2_1', 1, 2, 1, 'Can we meet in person? Which campus are you at?', 'TEXT', 1),
('1_2_1', 2, 1, 1, 'Sure, I am at East Campus, how about you?', 'TEXT', 0),

-- 会话2：用户1和用户3关于商品2的对话
('1_3_2', 1, 3, 2, 'How is the configuration of this MacBook Pro?', 'TEXT', 1),
('1_3_2', 3, 1, 2, 'M1 chip, 13 inch, 256GB storage, brand new unopened', 'TEXT', 0),

-- 会话3：用户2和用户4关于商品3的对话
('2_4_3', 2, 4, 3, 'Are there other colors for these Nike sneakers?', 'TEXT', 1),
('2_4_3', 4, 2, 3, 'Only this black pair left, size 42', 'TEXT', 1),
('2_4_3', 2, 4, 3, 'OK, is the price negotiable?', 'TEXT', 0);

-- ====================================================================
-- 4. 数据表说明
-- ====================================================================

/*
表设计说明：

1. conversation 表：
   - 管理用户间的会话，每个会话有唯一的conversation_id
   - conversation_id格式：smaller_user_id_larger_user_id_product_id
   - 支持未读消息计数，分别记录两个参与者的未读数量
   - 记录最后一条消息和时间，便于会话列表排序

2. message 表：
   - 存储具体的消息内容
   - 通过conversation_id关联到会话
   - 支持多种消息类型（文本、图片、系统消息）
   - 记录消息的读取状态

3. 索引设计：
   - 主要查询场景：按会话查询消息、按用户查询会话
   - 复合索引：conversation_id + create_time 用于消息历史查询
   - 单列索引：用户ID、商品ID、状态等常用查询字段

4. 数据完整性：
   - 不使用物理外键，在应用层保证数据一致性
   - 使用逻辑删除，保留历史数据
   - 所有时间字段使用DATETIME类型

5. 扩展性：
   - 消息类型可扩展（文本、图片、文件等）
   - 会话状态可扩展（活跃、归档、屏蔽等）
   - 支持群聊扩展（通过修改participant字段设计）
*/
