package com.shiwu.notification;

import com.shiwu.common.result.Result;
import com.shiwu.message.dto.MessageSendDTO;
import com.shiwu.message.service.MessageService;
import com.shiwu.message.service.impl.MessageServiceImpl;
import com.shiwu.message.vo.MessageVO;
import com.shiwu.notification.service.NotificationService;
import com.shiwu.notification.service.impl.NotificationServiceImpl;
import com.shiwu.notification.vo.NotificationVO;
import com.shiwu.order.model.OrderOperationResult;
import com.shiwu.order.service.OrderService;
import com.shiwu.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task4_3_1_2集成测试
 * 
 * 测试在关键业务点嵌入通知创建逻辑的功能：
 * 1. 消息发送时创建通知
 * 2. 订单状态变更时创建通知
 * 3. 商品审核通过时创建通知（已在Task4_2_1_2中实现）
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
public class Task4_3_1_2_IntegrationTest {
    
    private MessageService messageService;
    private OrderService orderService;
    private NotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        messageService = new MessageServiceImpl();
        orderService = new OrderServiceImpl();
        notificationService = new NotificationServiceImpl();
    }
    
    /**
     * 测试消息发送时创建通知
     */
    @Test
    void testMessageSendNotification() {
        System.out.println("=== 测试消息发送时创建通知 ===");
        
        // Given: 准备消息发送数据
        Long senderId = 1L; // alice
        Long receiverId = 2L; // bob
        
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(receiverId);
        dto.setContent("你好，我对你的商品很感兴趣！");
        dto.setMessageType("TEXT");
        
        // 获取发送前的通知数量
        Result<Integer> beforeCountResult = notificationService.getUnreadNotificationCount(receiverId);
        int beforeCount = beforeCountResult.isSuccess() ? beforeCountResult.getData() : 0;
        
        // When: 发送消息
        Result<MessageVO> result = messageService.sendMessage(senderId, dto);
        
        // Then: 验证消息发送成功
        assertTrue(result.isSuccess(), "消息发送应该成功");
        assertNotNull(result.getData(), "消息数据不应为空");
        
        // 等待通知创建完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 验证通知已创建
        Result<Integer> afterCountResult = notificationService.getUnreadNotificationCount(receiverId);
        assertTrue(afterCountResult.isSuccess(), "获取未读通知数量应该成功");
        
        int afterCount = afterCountResult.getData();
        assertTrue(afterCount > beforeCount, 
                  String.format("接收者应该收到新的通知，发送前: %d, 发送后: %d", beforeCount, afterCount));
        
        // 验证通知内容
        Result<List<NotificationVO>> notificationsResult = 
            notificationService.getUserNotifications(receiverId, 1, 5, true);
        assertTrue(notificationsResult.isSuccess(), "获取通知列表应该成功");
        
        List<NotificationVO> notifications = notificationsResult.getData();
        assertFalse(notifications.isEmpty(), "应该有未读通知");
        
        // 查找消息通知
        NotificationVO messageNotification = notifications.stream()
            .filter(n -> "MESSAGE_RECEIVED".equals(n.getNotificationType()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(messageNotification, "应该找到消息通知");
        assertTrue(messageNotification.getTitle().contains("新消息"), "通知标题应该包含'新消息'");
        assertTrue(messageNotification.getContent().contains("你好"), "通知内容应该包含消息摘要");
        
        System.out.println("✅ 消息发送通知测试通过");
        System.out.println("   - 消息ID: " + result.getData().getMessageId());
        System.out.println("   - 通知标题: " + messageNotification.getTitle());
        System.out.println("   - 通知内容: " + messageNotification.getContent());
    }
    
    /**
     * 测试订单状态变更时创建通知
     */
    @Test
    void testOrderStatusUpdateNotification() {
        System.out.println("\n=== 测试订单状态变更时创建通知 ===");
        
        // Given: 准备订单数据
        Long orderId = 1L; // 使用测试数据中的订单
        Long buyerId = 1L; // alice (买家)
        Long sellerId = 2L; // bob (卖家)
        Integer newStatus = 2; // 待收货
        
        // 获取卖家变更前的通知数量
        Result<Integer> beforeCountResult = notificationService.getUnreadNotificationCount(sellerId);
        int beforeCount = beforeCountResult.isSuccess() ? beforeCountResult.getData() : 0;
        
        // When: 买家更新订单状态（例如：确认发货）
        OrderOperationResult result = orderService.updateOrderStatus(orderId, newStatus, buyerId);
        
        // Then: 验证订单状态更新成功
        assertTrue(result.isSuccess(), "订单状态更新应该成功");
        
        // 等待通知创建完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 验证卖家收到通知
        Result<Integer> afterCountResult = notificationService.getUnreadNotificationCount(sellerId);
        assertTrue(afterCountResult.isSuccess(), "获取未读通知数量应该成功");
        
        int afterCount = afterCountResult.getData();
        assertTrue(afterCount > beforeCount, 
                  String.format("卖家应该收到新的通知，变更前: %d, 变更后: %d", beforeCount, afterCount));
        
        // 验证通知内容
        Result<List<NotificationVO>> notificationsResult = 
            notificationService.getUserNotifications(sellerId, 1, 5, true);
        assertTrue(notificationsResult.isSuccess(), "获取通知列表应该成功");
        
        List<NotificationVO> notifications = notificationsResult.getData();
        assertFalse(notifications.isEmpty(), "应该有未读通知");
        
        // 查找订单状态通知
        NotificationVO orderNotification = notifications.stream()
            .filter(n -> "ORDER_STATUS".equals(n.getNotificationType()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(orderNotification, "应该找到订单状态通知");
        assertTrue(orderNotification.getTitle().contains("订单状态"), "通知标题应该包含'订单状态'");
        // 检查通知内容是否包含订单相关信息（更宽松的验证）
        assertTrue(orderNotification.getContent().contains("订单") ||
                  orderNotification.getContent().contains("状态"), "通知内容应该包含订单或状态信息");
        
        System.out.println("✅ 订单状态变更通知测试通过");
        System.out.println("   - 订单ID: " + orderId);
        System.out.println("   - 新状态: 待收货");
        System.out.println("   - 通知标题: " + orderNotification.getTitle());
        System.out.println("   - 通知内容: " + orderNotification.getContent());
    }
    
    /**
     * 测试商品审核通过时创建通知（验证已有功能）
     */
    @Test
    void testProductApprovedNotification() {
        System.out.println("\n=== 测试商品审核通过时创建通知 ===");
        
        // Given: 准备商品审核数据
        Long productId = 1L;
        Long sellerId = 3L; // charlie
        String productTitle = "测试商品";
        
        // When: 创建商品审核通过通知
        Result<Integer> result = notificationService.createProductApprovedNotifications(
            productId, sellerId, productTitle);
        
        // Then: 验证通知创建成功
        assertTrue(result.isSuccess(), "商品审核通过通知创建应该成功");
        assertTrue(result.getData() > 0, "应该为粉丝创建了通知");
        
        System.out.println("✅ 商品审核通过通知测试通过");
        System.out.println("   - 商品ID: " + productId);
        System.out.println("   - 卖家ID: " + sellerId);
        System.out.println("   - 通知数量: " + result.getData());
    }
    
    /**
     * 测试通知系统的整体功能
     */
    @Test
    void testNotificationSystemOverall() {
        System.out.println("\n=== 测试通知系统整体功能 ===");
        
        // Given: 测试用户
        Long userId = 1L; // alice
        
        // When: 获取用户的所有通知
        Result<List<NotificationVO>> result = 
            notificationService.getUserNotifications(userId, 1, 10, false);
        
        // Then: 验证通知系统正常工作
        assertTrue(result.isSuccess(), "获取通知列表应该成功");
        assertNotNull(result.getData(), "通知列表不应为空");
        
        List<NotificationVO> notifications = result.getData();
        System.out.println("✅ 通知系统整体功能测试通过");
        System.out.println("   - 用户ID: " + userId);
        System.out.println("   - 通知总数: " + notifications.size());
        
        // 统计各类型通知数量
        long productNotifications = notifications.stream()
            .filter(n -> "PRODUCT_APPROVED".equals(n.getNotificationType()) || 
                        "PRODUCT_PUBLISHED".equals(n.getNotificationType()))
            .count();
        long messageNotifications = notifications.stream()
            .filter(n -> "MESSAGE_RECEIVED".equals(n.getNotificationType()))
            .count();
        long orderNotifications = notifications.stream()
            .filter(n -> "ORDER_STATUS".equals(n.getNotificationType()))
            .count();
        
        System.out.println("   - 商品相关通知: " + productNotifications);
        System.out.println("   - 消息通知: " + messageNotifications);
        System.out.println("   - 订单通知: " + orderNotifications);
        
        // 验证通知类型覆盖
        assertTrue(productNotifications > 0 || messageNotifications > 0 || orderNotifications > 0,
                  "应该至少有一种类型的通知");
    }
}
