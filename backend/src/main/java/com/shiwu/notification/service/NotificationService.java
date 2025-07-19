package com.shiwu.notification.service;

import com.shiwu.common.result.Result;
import com.shiwu.notification.model.Notification;
import com.shiwu.notification.vo.NotificationVO;

import java.util.List;

/**
 * 通知服务接口
 * 
 * 用于Task4_2_1_2: 商品审核通过粉丝通知功能
 * 提供通知的创建、查询、管理等功能
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public interface NotificationService {
    
    /**
     * 创建单个通知
     * 
     * @param notification 通知对象
     * @return 创建结果
     */
    Result<Long> createNotification(Notification notification);
    
    /**
     * 批量创建通知
     * 
     * @param notifications 通知列表
     * @return 创建结果，包含成功创建的数量
     */
    Result<Integer> batchCreateNotifications(List<Notification> notifications);
    
    /**
     * 为商品审核通过创建粉丝通知
     * 这是Task4_2_1_2的核心功能
     * 
     * @param productId 商品ID
     * @param sellerId 卖家ID
     * @param productTitle 商品标题
     * @return 创建结果，包含通知的粉丝数量
     */
    Result<Integer> createProductApprovedNotifications(Long productId, Long sellerId, String productTitle);
    
    /**
     * 获取用户的通知列表
     * 
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param onlyUnread 是否只获取未读通知
     * @return 通知列表
     */
    Result<List<NotificationVO>> getUserNotifications(Long userId, int page, int size, boolean onlyUnread);
    
    /**
     * 获取用户未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Result<Integer> getUnreadNotificationCount(Long userId);
    
    /**
     * 标记通知为已读
     * 
     * @param notificationId 通知ID
     * @param userId 用户ID（用于权限验证）
     * @return 操作结果
     */
    Result<Void> markNotificationAsRead(Long notificationId, Long userId);
    
    /**
     * 批量标记通知为已读
     * 
     * @param userId 用户ID
     * @param notificationIds 通知ID列表（可选，为空则标记所有未读通知）
     * @return 操作结果，包含标记成功的数量
     */
    Result<Integer> batchMarkNotificationsAsRead(Long userId, List<Long> notificationIds);
    
    /**
     * 创建系统公告通知
     * 
     * @param title 公告标题
     * @param content 公告内容
     * @param targetUserIds 目标用户ID列表（为空则发送给所有用户）
     * @param priority 优先级
     * @param expireHours 过期小时数（可选）
     * @return 创建结果
     */
    Result<Integer> createSystemNotice(String title, String content, List<Long> targetUserIds, 
                                     Integer priority, Integer expireHours);
    
    /**
     * 创建新消息通知
     * 
     * @param recipientId 接收者ID
     * @param senderId 发送者ID
     * @param senderName 发送者名称
     * @param messageContent 消息内容摘要
     * @param conversationId 会话ID
     * @return 创建结果
     */
    Result<Long> createMessageNotification(Long recipientId, Long senderId, String senderName, 
                                         String messageContent, String conversationId);
}
