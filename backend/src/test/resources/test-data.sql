-- LoopBuy测试数据初始化脚本
-- 用于为所有测试提供完整的测试数据集

-- 清理现有数据
SET FOREIGN_KEY_CHECKS = 0;

-- 清理所有表数据
TRUNCATE TABLE audit_log;
TRUNCATE TABLE message;
TRUNCATE TABLE conversation;
TRUNCATE TABLE notification;
TRUNCATE TABLE review;
TRUNCATE TABLE order_table;
TRUNCATE TABLE cart;
TRUNCATE TABLE product;
TRUNCATE TABLE user;
TRUNCATE TABLE administrator;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. 管理员数据
INSERT INTO administrator (id, username, password, email, real_name, role, status, last_login_time, login_count, create_time, update_time, deleted) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgwHNEM0bkEq6C8UpvdwXBqlF6', 'admin@loopbuy.com', '系统管理员', 'SUPER_ADMIN', 1, NOW(), 0, NOW(), NOW(), 0),
(2, 'moderator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgwHNEM0bkEq6C8UpvdwXBqlF6', 'mod@loopbuy.com', '内容审核员', 'MODERATOR', 1, NOW(), 0, NOW(), NOW(), 0);

-- 2. 用户数据
INSERT INTO user (id, username, password, email, phone, real_name, avatar_url, status, email_verified, phone_verified, last_login_time, login_count, create_time, update_time, deleted) VALUES
(1, 'testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgwHNEM0bkEq6C8UpvdwXBqlF6', 'test@example.com', '13800138001', '测试用户1', '/avatars/user1.jpg', 1, 1, 1, NOW(), 5, NOW(), NOW(), 0),
(2, 'alice', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgwHNEM0bkEq6C8UpvdwXBqlF6', 'alice@example.com', '13800138002', '爱丽丝', '/avatars/user2.jpg', 1, 1, 1, NOW(), 3, NOW(), NOW(), 0),
(3, 'bob', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgwHNEM0bkEq6C8UpvdwXBqlF6', 'bob@example.com', '13800138003', '鲍勃', '/avatars/user3.jpg', 1, 1, 0, NOW(), 2, NOW(), NOW(), 0),
(4, 'charlie', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgwHNEM0bkEq6C8UpvdwXBqlF6', 'charlie@example.com', '13800138004', '查理', '/avatars/user4.jpg', 0, 0, 0, NULL, 0, NOW(), NOW(), 0),
(5, 'diana', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgwHNEM0bkEq6C8UpvdwXBqlF6', 'diana@example.com', '13800138005', '戴安娜', '/avatars/user5.jpg', 1, 1, 1, NOW(), 1, NOW(), NOW(), 0);

-- 3. 商品数据
INSERT INTO product (id, seller_id, title, description, price, category, image_urls, status, view_count, favorite_count, create_time, update_time, deleted) VALUES
(100, 2, '待审核商品', '这是一个待审核的商品', 99.99, '电子产品', '["image1.jpg","image2.jpg"]', 0, 0, 0, NOW(), NOW(), 0),
(101, 2, '已上架商品', '这是一个已上架的商品', 199.99, '服装', '["image3.jpg","image4.jpg"]', 1, 50, 10, NOW(), NOW(), 0),
(102, 3, '已下架商品', '这是一个已下架的商品', 299.99, '家居', '["image5.jpg","image6.jpg"]', 2, 30, 5, NOW(), NOW(), 0),
(103, 3, '热门商品', '这是一个热门商品', 399.99, '数码', '["image7.jpg","image8.jpg"]', 1, 200, 50, NOW(), NOW(), 0),
(104, 4, '新品商品', '这是一个新品商品', 499.99, '运动', '["image9.jpg","image10.jpg"]', 1, 10, 2, NOW(), NOW(), 0);

-- 4. 购物车数据
INSERT INTO cart (id, user_id, product_id, quantity, create_time, update_time, deleted) VALUES
(1, 1, 101, 2, NOW(), NOW(), 0),
(2, 1, 103, 1, NOW(), NOW(), 0),
(3, 2, 102, 3, NOW(), NOW(), 0),
(4, 3, 104, 1, NOW(), NOW(), 0);

-- 5. 订单数据
INSERT INTO order_table (id, buyer_id, seller_id, product_id, price, status, payment_id, payment_time, shipping_time, delivery_time, create_time, update_time, deleted) VALUES
(1000, 1, 2, 101, 199.99, 'PENDING_PAYMENT', NULL, NULL, NULL, NULL, NOW(), NOW(), 0),
(1001, 1, 3, 103, 399.99, 'PAID', 'PAY_123456', NOW(), NULL, NULL, NOW(), NOW(), 0),
(1002, 2, 3, 102, 299.99, 'SHIPPED', 'PAY_123457', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
(1003, 3, 4, 104, 499.99, 'DELIVERED', 'PAY_123458', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 0);

-- 6. 评价数据
INSERT INTO review (id, order_id, reviewer_id, reviewee_id, product_id, rating, content, status, create_time, update_time, deleted) VALUES
(1, 1003, 3, 4, 104, 5, '商品质量很好，服务态度也不错！', 1, NOW(), NOW(), 0),
(2, 1002, 2, 3, 102, 4, '商品还可以，物流有点慢。', 1, NOW(), NOW(), 0);

-- 7. 会话数据
INSERT INTO conversation (id, participant1_id, participant2_id, last_message_id, last_message_content, last_message_time, unread_count1, unread_count2, status, create_time, update_time, deleted) VALUES
('CONV_TEST_1', 1, 2, 1, '你好，请问商品还有库存吗？', NOW(), 0, 1, 'ACTIVE', NOW(), NOW(), 0),
('CONV_TEST_2', 1, 3, 2, '订单什么时候发货？', NOW(), 1, 0, 'ACTIVE', NOW(), NOW(), 0),
('CONV_TEST_3', 2, 3, 3, '商品质量怎么样？', NOW(), 0, 0, 'ACTIVE', NOW(), NOW(), 0);

-- 8. 消息数据
INSERT INTO message (id, conversation_id, sender_id, receiver_id, content, message_type, is_read, create_time, update_time, deleted) VALUES
(1, 'CONV_TEST_1', 1, 2, '你好，请问商品还有库存吗？', 'TEXT', 1, NOW(), NOW(), 0),
(2, 'CONV_TEST_1', 2, 1, '有的，还有10件库存。', 'TEXT', 0, NOW(), NOW(), 0),
(3, 'CONV_TEST_2', 1, 3, '订单什么时候发货？', 'TEXT', 1, NOW(), NOW(), 0),
(4, 'CONV_TEST_2', 3, 1, '今天下午就会发货。', 'TEXT', 0, NOW(), NOW(), 0),
(5, 'CONV_TEST_3', 2, 3, '商品质量怎么样？', 'TEXT', 1, NOW(), NOW(), 0),
(6, 'CONV_TEST_3', 3, 2, '质量很好，值得购买。', 'TEXT', 1, NOW(), NOW(), 0);

-- 9. 通知数据
INSERT INTO notification (id, user_id, type, title, content, is_read, create_time, update_time, deleted) VALUES
(1, 1, 'ORDER', '订单支付成功', '您的订单1001已支付成功，商家将尽快发货。', 0, NOW(), NOW(), 0),
(2, 1, 'MESSAGE', '新消息', '您收到了来自alice的新消息。', 0, NOW(), NOW(), 0),
(3, 2, 'SYSTEM', '系统通知', '欢迎使用LoopBuy平台！', 1, NOW(), NOW(), 0),
(4, 3, 'ORDER', '订单已发货', '您的订单1002已发货，请注意查收。', 0, NOW(), NOW(), 0);

-- 10. 审计日志数据
INSERT INTO audit_log (id, admin_id, action, target_type, target_id, description, ip_address, user_agent, create_time, deleted) VALUES
(1, 1, 'USER_BAN', 'USER', 4, '封禁用户charlie，原因：违规行为', '192.168.1.100', 'Mozilla/5.0', NOW(), 0),
(2, 1, 'PRODUCT_APPROVE', 'PRODUCT', 101, '审核通过商品：已上架商品', '192.168.1.100', 'Mozilla/5.0', NOW(), 0),
(3, 2, 'PRODUCT_REJECT', 'PRODUCT', 100, '审核拒绝商品：待审核商品，原因：信息不完整', '192.168.1.101', 'Mozilla/5.0', NOW(), 0),
(4, 1, 'USER_UNBAN', 'USER', 4, '解封用户charlie', '192.168.1.100', 'Mozilla/5.0', NOW(), 0);

-- 提交事务
COMMIT;

-- 验证数据插入
SELECT 'Data initialization completed successfully' as status;
SELECT 
    (SELECT COUNT(*) FROM administrator) as admin_count,
    (SELECT COUNT(*) FROM user) as user_count,
    (SELECT COUNT(*) FROM product) as product_count,
    (SELECT COUNT(*) FROM order_table) as order_count,
    (SELECT COUNT(*) FROM message) as message_count,
    (SELECT COUNT(*) FROM audit_log) as audit_log_count;
