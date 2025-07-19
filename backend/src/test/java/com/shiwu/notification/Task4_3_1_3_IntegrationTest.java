package com.shiwu.notification;

import com.shiwu.common.result.Result;
import com.shiwu.notification.service.NotificationService;
import com.shiwu.notification.service.impl.NotificationServiceImpl;
import com.shiwu.notification.vo.NotificationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task4_3_1_3集成测试
 * 
 * 测试获取通知列表和未读计数的API功能：
 * 1. 获取通知列表 - 支持分页和过滤
 * 2. 获取未读通知数量
 * 3. 标记通知已读
 * 4. 批量标记通知已读
 * 5. 参数验证和边界测试
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
public class Task4_3_1_3_IntegrationTest {
    
    private NotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl();
    }
    
    /**
     * 测试1：获取通知列表 - 基础功能
     */
    @Test
    void testGetNotificationList_Basic() {
        System.out.println("=== 测试1：获取通知列表 - 基础功能 ===");
        
        // Given: 测试用户
        Long userId = 1L; // alice
        
        // When: 获取第一页通知列表
        Result<List<NotificationVO>> result = notificationService.getUserNotifications(userId, 1, 10, false);
        
        // Then: 验证结果
        assertTrue(result.isSuccess(), "获取通知列表应该成功");
        assertNotNull(result.getData(), "通知列表不应为空");
        
        List<NotificationVO> notifications = result.getData();
        System.out.println("✅ 获取通知列表成功，总数: " + notifications.size());
        
        // 验证通知数据结构
        if (!notifications.isEmpty()) {
            NotificationVO notification = notifications.get(0);
            assertNotNull(notification.getId(), "通知ID不应为空");
            assertNotNull(notification.getTitle(), "通知标题不应为空");
            assertNotNull(notification.getContent(), "通知内容不应为空");
            assertNotNull(notification.getNotificationType(), "通知类型不应为空");
            assertNotNull(notification.getCreateTime(), "创建时间不应为空");
            
            System.out.println("   - 通知ID: " + notification.getId());
            System.out.println("   - 通知标题: " + notification.getTitle());
            System.out.println("   - 通知类型: " + notification.getNotificationType());
            System.out.println("   - 是否已读: " + (Boolean.TRUE.equals(notification.getIsRead()) ? "是" : "否"));
        }
        
        System.out.println("✅ 测试1通过");
    }
    
    /**
     * 测试2：获取未读通知数量
     */
    @Test
    void testGetUnreadNotificationCount() {
        System.out.println("\n=== 测试2：获取未读通知数量 ===");
        
        // Given: 测试用户
        Long userId = 1L; // alice
        
        // When: 获取未读通知数量
        Result<Integer> result = notificationService.getUnreadNotificationCount(userId);
        
        // Then: 验证结果
        assertTrue(result.isSuccess(), "获取未读通知数量应该成功");
        assertNotNull(result.getData(), "未读数量不应为空");
        assertTrue(result.getData() >= 0, "未读数量应该大于等于0");
        
        int unreadCount = result.getData();
        System.out.println("✅ 获取未读通知数量成功: " + unreadCount);
        
        // 验证只获取未读通知的功能
        Result<List<NotificationVO>> unreadResult = 
            notificationService.getUserNotifications(userId, 1, 10, true);
        assertTrue(unreadResult.isSuccess(), "获取未读通知列表应该成功");
        
        List<NotificationVO> unreadNotifications = unreadResult.getData();
        System.out.println("   - 未读通知列表数量: " + unreadNotifications.size());
        
        // 验证未读通知列表中的通知确实是未读的
        for (NotificationVO notification : unreadNotifications) {
            assertFalse(Boolean.TRUE.equals(notification.getIsRead()),
                        "未读通知列表中的通知应该是未读状态");
        }
        
        System.out.println("✅ 测试2通过");
    }
    
    /**
     * 测试3：分页功能测试
     */
    @Test
    void testNotificationListPagination() {
        System.out.println("\n=== 测试3：分页功能测试 ===");
        
        // Given: 测试用户
        Long userId = 1L; // alice
        
        // When: 获取第一页（每页5条）
        Result<List<NotificationVO>> page1Result = 
            notificationService.getUserNotifications(userId, 1, 5, false);
        
        // Then: 验证第一页结果
        assertTrue(page1Result.isSuccess(), "获取第一页应该成功");
        List<NotificationVO> page1Notifications = page1Result.getData();
        assertTrue(page1Notifications.size() <= 5, "第一页数量应该不超过5条");
        
        System.out.println("✅ 第一页通知数量: " + page1Notifications.size());
        
        // When: 获取第二页（每页5条）
        Result<List<NotificationVO>> page2Result = 
            notificationService.getUserNotifications(userId, 2, 5, false);
        
        // Then: 验证第二页结果
        assertTrue(page2Result.isSuccess(), "获取第二页应该成功");
        List<NotificationVO> page2Notifications = page2Result.getData();
        
        System.out.println("✅ 第二页通知数量: " + page2Notifications.size());
        
        // 验证分页数据不重复（如果两页都有数据）
        if (!page1Notifications.isEmpty() && !page2Notifications.isEmpty()) {
            Long firstPageFirstId = page1Notifications.get(0).getId();
            Long secondPageFirstId = page2Notifications.get(0).getId();
            assertNotEquals(firstPageFirstId, secondPageFirstId, "不同页的通知ID应该不同");
        }
        
        System.out.println("✅ 测试3通过");
    }
    
    /**
     * 测试4：标记通知已读功能
     */
    @Test
    void testMarkNotificationAsRead() {
        System.out.println("\n=== 测试4：标记通知已读功能 ===");
        
        // Given: 测试用户和未读通知
        Long userId = 1L; // alice
        
        // 获取一个未读通知
        Result<List<NotificationVO>> unreadResult = 
            notificationService.getUserNotifications(userId, 1, 1, true);
        assertTrue(unreadResult.isSuccess(), "获取未读通知应该成功");
        
        List<NotificationVO> unreadNotifications = unreadResult.getData();
        if (unreadNotifications.isEmpty()) {
            System.out.println("⚠️ 没有未读通知，跳过标记已读测试");
            return;
        }
        
        NotificationVO unreadNotification = unreadNotifications.get(0);
        Long notificationId = unreadNotification.getId();
        
        System.out.println("   - 选择通知ID: " + notificationId);
        System.out.println("   - 标记前状态: " + (Boolean.TRUE.equals(unreadNotification.getIsRead()) ? "已读" : "未读"));
        
        // When: 标记通知已读
        Result<Void> markResult = notificationService.markNotificationAsRead(notificationId, userId);
        
        // Then: 验证标记结果
        assertTrue(markResult.isSuccess(), "标记通知已读应该成功");
        
        // 验证通知状态已更新
        Result<List<NotificationVO>> afterResult = 
            notificationService.getUserNotifications(userId, 1, 10, false);
        assertTrue(afterResult.isSuccess(), "重新获取通知列表应该成功");
        
        // 查找刚才标记的通知
        NotificationVO markedNotification = afterResult.getData().stream()
            .filter(n -> n.getId().equals(notificationId))
            .findFirst()
            .orElse(null);
        
        assertNotNull(markedNotification, "应该能找到标记的通知");
        assertTrue(Boolean.TRUE.equals(markedNotification.getIsRead()), "通知应该已标记为已读");
        assertNotNull(markedNotification.getReadTime(), "已读时间应该不为空");
        
        System.out.println("✅ 标记通知已读成功");
        System.out.println("   - 标记后状态: 已读");
        System.out.println("   - 已读时间: " + markedNotification.getReadTime());
        System.out.println("✅ 测试4通过");
    }
    
    /**
     * 测试5：批量标记通知已读功能
     */
    @Test
    void testBatchMarkNotificationsAsRead() {
        System.out.println("\n=== 测试5：批量标记通知已读功能 ===");
        
        // Given: 测试用户
        Long userId = 2L; // bob
        
        // 获取用户的未读通知数量（标记前）
        Result<Integer> beforeCountResult = notificationService.getUnreadNotificationCount(userId);
        assertTrue(beforeCountResult.isSuccess(), "获取未读数量应该成功");
        int beforeCount = beforeCountResult.getData();
        
        System.out.println("   - 标记前未读数量: " + beforeCount);
        
        // When: 批量标记所有通知已读
        Result<Integer> batchResult = notificationService.batchMarkNotificationsAsRead(userId, null);
        
        // Then: 验证批量标记结果
        assertTrue(batchResult.isSuccess(), "批量标记通知已读应该成功");
        int markedCount = batchResult.getData();
        
        System.out.println("✅ 批量标记通知已读成功，标记数量: " + markedCount);
        
        // 验证未读数量已减少
        Result<Integer> afterCountResult = notificationService.getUnreadNotificationCount(userId);
        assertTrue(afterCountResult.isSuccess(), "获取未读数量应该成功");
        int afterCount = afterCountResult.getData();
        
        System.out.println("   - 标记后未读数量: " + afterCount);
        assertTrue(afterCount <= beforeCount, "标记后未读数量应该减少或保持不变");
        
        System.out.println("✅ 测试5通过");
    }
    
    /**
     * 测试6：参数验证和边界测试
     */
    @Test
    void testParameterValidationAndBoundary() {
        System.out.println("\n=== 测试6：参数验证和边界测试 ===");
        
        // 测试空用户ID
        Result<List<NotificationVO>> nullUserResult = 
            notificationService.getUserNotifications(null, 1, 10, false);
        assertFalse(nullUserResult.isSuccess(), "空用户ID应该失败");
        System.out.println("✅ 空用户ID验证通过");
        
        // 测试无效页码
        Result<List<NotificationVO>> invalidPageResult = 
            notificationService.getUserNotifications(1L, 0, 10, false);
        // 注意：服务层可能会自动修正无效参数，所以这里主要验证不会抛异常
        assertNotNull(invalidPageResult, "无效页码应该有响应");
        System.out.println("✅ 无效页码验证通过");
        
        // 测试不存在的用户
        Result<List<NotificationVO>> nonExistentUserResult = 
            notificationService.getUserNotifications(999999L, 1, 10, false);
        assertTrue(nonExistentUserResult.isSuccess(), "不存在用户应该返回空列表");
        assertTrue(nonExistentUserResult.getData().isEmpty(), "不存在用户的通知列表应该为空");
        System.out.println("✅ 不存在用户验证通过");
        
        // 测试未读数量 - 不存在的用户
        Result<Integer> nonExistentUserCountResult =
            notificationService.getUnreadNotificationCount(999999L);
        assertTrue(nonExistentUserCountResult.isSuccess(), "不存在用户的未读数量查询应该成功");
        assertEquals(0, nonExistentUserCountResult.getData().intValue(), "不存在用户的未读数量应该为0");
        System.out.println("✅ 不存在用户未读数量验证通过");
        
        System.out.println("✅ 测试6通过");
    }
    
    /**
     * 测试7：通知类型过滤功能
     */
    @Test
    void testNotificationTypeFiltering() {
        System.out.println("\n=== 测试7：通知类型过滤功能 ===");
        
        // Given: 测试用户
        Long userId = 1L; // alice
        
        // When: 获取所有通知
        Result<List<NotificationVO>> allResult = 
            notificationService.getUserNotifications(userId, 1, 20, false);
        assertTrue(allResult.isSuccess(), "获取所有通知应该成功");
        
        List<NotificationVO> allNotifications = allResult.getData();
        System.out.println("✅ 所有通知数量: " + allNotifications.size());
        
        // 统计各种类型的通知数量
        long productNotifications = allNotifications.stream()
            .filter(n -> "PRODUCT_APPROVED".equals(n.getNotificationType()) || 
                        "PRODUCT_PUBLISHED".equals(n.getNotificationType()))
            .count();
        long messageNotifications = allNotifications.stream()
            .filter(n -> "MESSAGE_RECEIVED".equals(n.getNotificationType()))
            .count();
        long orderNotifications = allNotifications.stream()
            .filter(n -> "ORDER_STATUS".equals(n.getNotificationType()))
            .count();
        long systemNotifications = allNotifications.stream()
            .filter(n -> "SYSTEM_NOTICE".equals(n.getNotificationType()))
            .count();
        
        System.out.println("   - 商品相关通知: " + productNotifications);
        System.out.println("   - 消息通知: " + messageNotifications);
        System.out.println("   - 订单通知: " + orderNotifications);
        System.out.println("   - 系统通知: " + systemNotifications);
        
        // 验证通知类型的多样性
        long totalCategorized = productNotifications + messageNotifications + orderNotifications + systemNotifications;
        assertTrue(totalCategorized > 0, "应该有各种类型的通知");
        
        System.out.println("✅ 测试7通过");
    }
}
