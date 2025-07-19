-- ====================================================================
-- 通知系统数据库表设计
-- ====================================================================
-- 用于Task4_2_1_2: 商品审核通过粉丝通知功能
-- 
-- 功能说明：
-- 1. 当商品首次审核通过(PENDING_REVIEW -> ONSALE)时
-- 2. 为卖家的所有粉丝生成动态通知
-- 3. 支持多种通知类型和状态管理
-- ====================================================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 通知表 (notification)
-- ====================================================================
-- 存储系统生成的各种通知信息
CREATE TABLE IF NOT EXISTS notification (
    -- 主键字段
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 接收者信息
    recipient_id BIGINT UNSIGNED NOT NULL COMMENT '接收者用户ID',
    
    -- 通知内容
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content VARCHAR(500) NOT NULL COMMENT '通知内容',
    
    -- 通知类型和来源
    notification_type VARCHAR(50) NOT NULL COMMENT '通知类型：PRODUCT_APPROVED-商品审核通过, ORDER_STATUS-订单状态变更, MESSAGE_RECEIVED-新消息, SYSTEM_NOTICE-系统公告',
    source_type VARCHAR(50) NOT NULL COMMENT '来源类型：PRODUCT-商品, ORDER-订单, MESSAGE-消息, SYSTEM-系统',
    source_id BIGINT UNSIGNED COMMENT '来源实体ID（如商品ID、订单ID等）',
    
    -- 关联信息
    related_user_id BIGINT UNSIGNED COMMENT '相关用户ID（如发送者、卖家等）',
    related_user_name VARCHAR(50) COMMENT '相关用户名称（冗余字段，提高查询效率）',
    
    -- 跳转链接
    action_url VARCHAR(255) COMMENT '点击通知后的跳转链接',
    
    -- 状态字段
    is_read TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    read_time DATETIME COMMENT '阅读时间',
    
    -- 优先级
    priority TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '优先级：1-普通，2-重要，3-紧急',
    
    -- 过期时间
    expire_time DATETIME COMMENT '过期时间（可选）',
    
    -- 系统字段
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0-正常，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引设计
    INDEX idx_recipient_id (recipient_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_source_type_id (source_type, source_id),
    INDEX idx_related_user_id (related_user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time),
    INDEX idx_priority (priority),
    INDEX idx_expire_time (expire_time),
    
    -- 复合索引
    INDEX idx_recipient_read_time (recipient_id, is_read, create_time DESC),
    INDEX idx_recipient_type (recipient_id, notification_type),
    
    -- 外键约束
    FOREIGN KEY (recipient_id) REFERENCES system_user(id) ON DELETE CASCADE,
    FOREIGN KEY (related_user_id) REFERENCES system_user(id) ON DELETE SET NULL
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ====================================================================
-- 2. 通知模板表 (notification_template) - 可选扩展
-- ====================================================================
-- 存储通知模板，便于统一管理通知格式
CREATE TABLE IF NOT EXISTS notification_template (
    -- 主键字段
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 模板信息
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板代码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    
    -- 模板内容
    title_template VARCHAR(200) NOT NULL COMMENT '标题模板（支持占位符）',
    content_template VARCHAR(1000) NOT NULL COMMENT '内容模板（支持占位符）',
    
    -- 模板配置
    notification_type VARCHAR(50) NOT NULL COMMENT '通知类型',
    priority TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '默认优先级',
    expire_hours INT UNSIGNED COMMENT '过期小时数（null表示不过期）',
    
    -- 状态字段
    is_active TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    
    -- 系统字段
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_template_code (template_code),
    INDEX idx_notification_type (notification_type),
    INDEX idx_is_active (is_active)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- ====================================================================
-- 3. 初始化通知模板数据
-- ====================================================================

-- 商品审核通过通知模板
INSERT INTO notification_template (
    template_code, 
    template_name, 
    title_template, 
    content_template, 
    notification_type, 
    priority, 
    expire_hours
) VALUES (
    'PRODUCT_APPROVED_FOLLOWER',
    '关注的卖家发布新商品',
    '您关注的 {sellerName} 发布了新商品',
    '您关注的卖家 {sellerName} 刚刚发布了新商品《{productTitle}》，快来看看吧！',
    'PRODUCT_APPROVED',
    1,
    168  -- 7天过期
) ON DUPLICATE KEY UPDATE 
    template_name = VALUES(template_name),
    title_template = VALUES(title_template),
    content_template = VALUES(content_template),
    update_time = CURRENT_TIMESTAMP;

-- 系统公告模板
INSERT INTO notification_template (
    template_code, 
    template_name, 
    title_template, 
    content_template, 
    notification_type, 
    priority, 
    expire_hours
) VALUES (
    'SYSTEM_NOTICE',
    '系统公告',
    '系统公告：{title}',
    '{content}',
    'SYSTEM_NOTICE',
    2,
    NULL  -- 不过期
) ON DUPLICATE KEY UPDATE 
    template_name = VALUES(template_name),
    title_template = VALUES(title_template),
    content_template = VALUES(content_template),
    update_time = CURRENT_TIMESTAMP;

-- 新消息通知模板
INSERT INTO notification_template (
    template_code, 
    template_name, 
    title_template, 
    content_template, 
    notification_type, 
    priority, 
    expire_hours
) VALUES (
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

-- ====================================================================
-- 4. 创建索引优化查询性能
-- ====================================================================

-- 为高频查询创建额外的复合索引
CREATE INDEX IF NOT EXISTS idx_notification_unread_priority 
ON notification (recipient_id, is_read, priority DESC, create_time DESC);

-- 为通知清理任务创建索引
CREATE INDEX IF NOT EXISTS idx_notification_cleanup 
ON notification (is_deleted, expire_time, create_time);

-- ====================================================================
-- 5. 创建视图简化查询
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
-- 6. 设置权限和约束
-- ====================================================================

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 添加表注释
ALTER TABLE notification COMMENT = '通知表 - 存储系统各类通知信息，支持商品审核通过粉丝通知等功能';
ALTER TABLE notification_template COMMENT = '通知模板表 - 统一管理通知格式和内容模板';

-- 完成提示
SELECT 'Notification schema created successfully!' as status;
