package com.shiwu.notification;

import com.shiwu.common.result.Result;
import com.shiwu.notification.dao.NotificationDao;
import com.shiwu.notification.model.Notification;
import com.shiwu.notification.service.NotificationService;
import com.shiwu.notification.service.impl.NotificationServiceImpl;
import com.shiwu.notification.vo.NotificationVO;
import com.shiwu.product.service.AdminProductService;
import com.shiwu.product.service.impl.AdminProductServiceImpl;
import com.shiwu.user.dao.UserFollowDao;
import com.shiwu.user.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Task4_2_1_2集成测试
 * 
 * 测试商品审核通过粉丝通知功能的完整流程
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
@DisplayName("Task4_2_1_2 - 商品审核通过粉丝通知功能集成测试")
class Task4_2_1_2_IntegrationTest {
    
    @Mock
    private NotificationDao notificationDao;
    
    @Mock
    private UserFollowDao userFollowDao;
    
    @Mock
    private UserDao userDao;
    
    private NotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationServiceImpl(notificationDao, userFollowDao, userDao);
    }
    
    @Test
    @DisplayName("核心功能1: 商品审核通过时为粉丝创建通知")
    void testCreateProductApprovedNotifications_Success() {
        // Given: 准备测试数据
        Long productId = 100L;
        Long sellerId = 1L;
        String productTitle = "iPhone 14 Pro Max 256GB 深空黑色";
        
        // Mock卖家信息
        com.shiwu.user.model.User seller = new com.shiwu.user.model.User();
        seller.setId(sellerId);
        seller.setUsername("apple_seller");
        when(userDao.findPublicInfoById(sellerId)).thenReturn(seller);
        
        // Mock粉丝列表
        List<Long> followerIds = Arrays.asList(2L, 3L, 4L, 5L);
        when(userFollowDao.getFollowerIds(sellerId)).thenReturn(followerIds);
        
        // Mock批量创建通知成功
        when(notificationDao.batchCreateNotifications(any())).thenReturn(4);
        
        // When: 执行创建通知
        Result<Integer> result = notificationService.createProductApprovedNotifications(productId, sellerId, productTitle);
        
        // Then: 验证结果
        assertTrue(result.isSuccess(), "创建商品审核通过通知应该成功");
        assertEquals(4, result.getData().intValue(), "应该为4个粉丝创建通知");
        
        // 验证调用
        verify(userDao).findPublicInfoById(sellerId);
        verify(userFollowDao).getFollowerIds(sellerId);
        verify(notificationDao).batchCreateNotifications(argThat(notifications -> {
            List<Notification> notificationList = (List<Notification>) notifications;
            return notificationList.size() == 4 &&
                   notificationList.stream().allMatch(n -> 
                       n.getNotificationType().equals(Notification.TYPE_PRODUCT_APPROVED) &&
                       n.getSourceType().equals(Notification.SOURCE_PRODUCT) &&
                       n.getSourceId().equals(productId) &&
                       n.getRelatedUserId().equals(sellerId) &&
                       n.getTitle().contains("apple_seller") &&
                       n.getContent().contains(productTitle)
                   );
        }));
        
        System.out.println("✅ 商品审核通过粉丝通知创建测试通过");
    }
    
    @Test
    @DisplayName("核心功能2: 卖家没有粉丝时不创建通知")
    void testCreateProductApprovedNotifications_NoFollowers() {
        // Given: 准备测试数据
        Long productId = 101L;
        Long sellerId = 2L;
        String productTitle = "MacBook Pro 16寸";
        
        // Mock卖家信息
        com.shiwu.user.model.User seller = new com.shiwu.user.model.User();
        seller.setId(sellerId);
        seller.setUsername("mac_seller");
        when(userDao.findPublicInfoById(sellerId)).thenReturn(seller);
        
        // Mock空粉丝列表
        when(userFollowDao.getFollowerIds(sellerId)).thenReturn(Arrays.asList());
        
        // When: 执行创建通知
        Result<Integer> result = notificationService.createProductApprovedNotifications(productId, sellerId, productTitle);
        
        // Then: 验证结果
        assertTrue(result.isSuccess(), "没有粉丝时应该成功返回");
        assertEquals(0, result.getData().intValue(), "没有粉丝时应该返回0");
        
        // 验证不会调用批量创建
        verify(notificationDao, never()).batchCreateNotifications(any());
        
        System.out.println("✅ 无粉丝情况测试通过");
    }
    
    @Test
    @DisplayName("核心功能3: 卖家不存在时返回错误")
    void testCreateProductApprovedNotifications_SellerNotFound() {
        // Given: 准备测试数据
        Long productId = 102L;
        Long sellerId = 999L;
        String productTitle = "不存在的商品";
        
        // Mock卖家不存在
        when(userDao.findPublicInfoById(sellerId)).thenReturn(null);
        
        // When: 执行创建通知
        Result<Integer> result = notificationService.createProductApprovedNotifications(productId, sellerId, productTitle);
        
        // Then: 验证结果
        assertFalse(result.isSuccess(), "卖家不存在时应该返回失败");
        assertTrue(result.getMessage().contains("卖家不存在"), "错误信息应该包含卖家不存在");
        
        // 验证不会调用后续操作
        verify(userFollowDao, never()).getFollowerIds(any());
        verify(notificationDao, never()).batchCreateNotifications(any());
        
        System.out.println("✅ 卖家不存在测试通过");
    }
    
    @Test
    @DisplayName("核心功能4: 通知内容格式验证")
    void testNotificationContentFormat() {
        // Given: 准备测试数据
        Long productId = 103L;
        Long sellerId = 3L;
        String productTitle = "iPad Pro 12.9寸 2TB Wi-Fi + 蜂窝网络版";
        String sellerName = "ipad_store";
        
        // Mock卖家信息
        com.shiwu.user.model.User seller = new com.shiwu.user.model.User();
        seller.setId(sellerId);
        seller.setUsername(sellerName);
        when(userDao.findPublicInfoById(sellerId)).thenReturn(seller);
        
        // Mock粉丝列表
        List<Long> followerIds = Arrays.asList(10L);
        when(userFollowDao.getFollowerIds(sellerId)).thenReturn(followerIds);
        
        // Mock批量创建通知成功
        when(notificationDao.batchCreateNotifications(any())).thenReturn(1);
        
        // When: 执行创建通知
        Result<Integer> result = notificationService.createProductApprovedNotifications(productId, sellerId, productTitle);
        
        // Then: 验证通知内容格式
        assertTrue(result.isSuccess());
        
        verify(notificationDao).batchCreateNotifications(argThat(notifications -> {
            List<Notification> notificationList = (List<Notification>) notifications;
            Notification notification = notificationList.get(0);
            
            // 验证通知内容格式
            String expectedTitle = "您关注的 " + sellerName + " 发布了新商品";
            String expectedContent = "您关注的卖家 " + sellerName + " 刚刚发布了新商品《" + productTitle + "》，快来看看吧！";
            String expectedActionUrl = "/product/" + productId;
            
            return notification.getTitle().equals(expectedTitle) &&
                   notification.getContent().equals(expectedContent) &&
                   notification.getActionUrl().equals(expectedActionUrl) &&
                   notification.getPriority().equals(Notification.PRIORITY_NORMAL) &&
                   notification.getExpireTime() != null; // 应该设置过期时间
        }));
        
        System.out.println("✅ 通知内容格式验证测试通过");
    }
    
    @Test
    @DisplayName("核心功能5: 获取用户通知列表")
    void testGetUserNotifications() {
        // Given: 准备测试数据
        Long userId = 1L;
        int page = 1;
        int size = 10;
        boolean onlyUnread = false;
        
        // Mock通知列表
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setRecipientId(userId);
        notification1.setTitle("您关注的 apple_seller 发布了新商品");
        notification1.setContent("您关注的卖家 apple_seller 刚刚发布了新商品《iPhone 15》，快来看看吧！");
        notification1.setNotificationType(Notification.TYPE_PRODUCT_APPROVED);
        notification1.setIsRead(false);
        notification1.setPriority(Notification.PRIORITY_NORMAL);
        
        List<Notification> notifications = Arrays.asList(notification1);
        when(notificationDao.findNotificationsByUserId(userId, page, size, onlyUnread)).thenReturn(notifications);
        
        // When: 执行获取通知列表
        Result<List<NotificationVO>> result = notificationService.getUserNotifications(userId, page, size, onlyUnread);
        
        // Then: 验证结果
        assertTrue(result.isSuccess(), "获取通知列表应该成功");
        assertEquals(1, result.getData().size(), "应该返回1条通知");
        
        NotificationVO vo = result.getData().get(0);
        assertEquals(notification1.getId(), vo.getId());
        assertEquals(notification1.getTitle(), vo.getTitle());
        assertEquals(notification1.getContent(), vo.getContent());
        assertEquals("商品上架", vo.getTypeText()); // 验证类型文本转换
        assertEquals("普通", vo.getPriorityText()); // 验证优先级文本转换
        
        verify(notificationDao).findNotificationsByUserId(userId, page, size, onlyUnread);
        
        System.out.println("✅ 获取用户通知列表测试通过");
    }
    
    @Test
    @DisplayName("核心功能6: 获取未读通知数量")
    void testGetUnreadNotificationCount() {
        // Given: 准备测试数据
        Long userId = 1L;
        int expectedCount = 5;
        
        when(notificationDao.getUnreadNotificationCount(userId)).thenReturn(expectedCount);
        
        // When: 执行获取未读数量
        Result<Integer> result = notificationService.getUnreadNotificationCount(userId);
        
        // Then: 验证结果
        assertTrue(result.isSuccess(), "获取未读通知数量应该成功");
        assertEquals(expectedCount, result.getData().intValue(), "未读数量应该正确");
        
        verify(notificationDao).getUnreadNotificationCount(userId);
        
        System.out.println("✅ 获取未读通知数量测试通过");
    }
    
    @Test
    @DisplayName("核心功能7: 标记通知已读")
    void testMarkNotificationAsRead() {
        // Given: 准备测试数据
        Long notificationId = 1L;
        Long userId = 1L;
        
        when(notificationDao.markNotificationAsRead(notificationId, userId)).thenReturn(true);
        
        // When: 执行标记已读
        Result<Void> result = notificationService.markNotificationAsRead(notificationId, userId);
        
        // Then: 验证结果
        assertTrue(result.isSuccess(), "标记通知已读应该成功");
        
        verify(notificationDao).markNotificationAsRead(notificationId, userId);
        
        System.out.println("✅ 标记通知已读测试通过");
    }
    
    @Test
    @DisplayName("边界测试: 参数验证")
    void testParameterValidation() {
        // 测试1: 商品ID为空
        Result<Integer> result1 = notificationService.createProductApprovedNotifications(null, 1L, "商品");
        assertFalse(result1.isSuccess());
        assertTrue(result1.getMessage().contains("商品ID和卖家ID不能为空"));
        
        // 测试2: 卖家ID为空
        Result<Integer> result2 = notificationService.createProductApprovedNotifications(1L, null, "商品");
        assertFalse(result2.isSuccess());
        assertTrue(result2.getMessage().contains("商品ID和卖家ID不能为空"));
        
        // 测试3: 商品标题为空
        Result<Integer> result3 = notificationService.createProductApprovedNotifications(1L, 1L, "");
        assertFalse(result3.isSuccess());
        assertTrue(result3.getMessage().contains("商品标题不能为空"));
        
        // 测试4: 用户ID为空
        Result<List<NotificationVO>> result4 = notificationService.getUserNotifications(null, 1, 10, false);
        assertFalse(result4.isSuccess());
        assertTrue(result4.getMessage().contains("用户ID不能为空"));
        
        System.out.println("✅ 参数验证测试通过");
    }
}
