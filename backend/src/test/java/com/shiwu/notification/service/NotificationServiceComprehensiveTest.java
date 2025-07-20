package com.shiwu.notification.service;

import com.shiwu.common.result.Result;
import com.shiwu.notification.dao.NotificationDao;
import com.shiwu.notification.model.Notification;
import com.shiwu.notification.service.impl.NotificationServiceImpl;
import com.shiwu.notification.vo.NotificationVO;
import com.shiwu.user.dao.UserDao;
import com.shiwu.user.dao.UserFollowDao;
import com.shiwu.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationService综合测试
 * 
 * 测试通知系统的所有核心功能，包括：
 * 1. 单个通知创建
 * 2. 批量通知创建
 * 3. 商品审核通过粉丝通知（Task4_2_1_2核心功能）
 * 4. 用户通知查询
 * 5. 未读通知数量统计
 * 6. 通知已读标记
 * 7. 批量已读标记
 * 8. 系统公告创建
 * 9. 消息通知创建
 * 10. 各种边界情况和错误处理
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class NotificationServiceComprehensiveTest {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceComprehensiveTest.class);
    
    private NotificationService notificationService;
    private NotificationDao notificationDao;
    private UserFollowDao userFollowDao;
    private UserDao userDao;
    
    // 测试数据常量
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_SELLER_ID = 2L;
    private static final Long TEST_FOLLOWER_ID = 3L;
    private static final Long TEST_PRODUCT_ID = 100L;
    private static final String TEST_PRODUCT_TITLE = "测试商品标题";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_SELLER_NAME = "testseller";
    
    @BeforeEach
    void setUp() {
        logger.info("NotificationService测试环境初始化开始");
        
        // 创建Mock DAO对象
        notificationDao = new MockNotificationDao();
        userFollowDao = new MockUserFollowDao();
        userDao = new MockUserDao();
        
        // 创建Service实例
        notificationService = new NotificationServiceImpl(notificationDao, userFollowDao, userDao);
        
        logger.info("NotificationService测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        logger.info("NotificationService测试清理完成");
    }
    
    /**
     * 测试创建单个通知功能
     */
    @Test
    void testCreateNotification() {
        logger.info("开始测试创建单个通知功能");
        
        // 创建有效通知
        Notification notification = new Notification();
        notification.setRecipientId(TEST_USER_ID);
        notification.setTitle("测试通知标题");
        notification.setContent("测试通知内容");
        notification.setNotificationType(Notification.TYPE_SYSTEM_NOTICE);
        
        Result<Long> result = notificationService.createNotification(notification);
        
        assertNotNull(result, "创建通知应该返回结果对象");
        assertTrue(result.isSuccess(), "创建通知应该成功");
        assertNotNull(result.getData(), "创建通知应该返回通知ID");
        
        logger.info("创建单个通知测试通过: success={}, notificationId={}", result.isSuccess(), result.getData());
    }
    
    /**
     * 测试创建单个通知参数验证
     */
    @Test
    void testCreateNotificationValidation() {
        logger.info("开始测试创建单个通知参数验证");
        
        // 测试null通知对象
        Result<Long> result1 = notificationService.createNotification(null);
        assertNotNull(result1, "null通知对象应该返回结果对象");
        assertFalse(result1.isSuccess(), "null通知对象应该创建失败");
        
        // 测试null接收者ID
        Notification notification2 = new Notification();
        notification2.setTitle("测试标题");
        notification2.setContent("测试内容");
        Result<Long> result2 = notificationService.createNotification(notification2);
        assertNotNull(result2, "null接收者ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null接收者ID应该创建失败");
        
        // 测试空标题
        Notification notification3 = new Notification();
        notification3.setRecipientId(TEST_USER_ID);
        notification3.setContent("测试内容");
        Result<Long> result3 = notificationService.createNotification(notification3);
        assertNotNull(result3, "空标题应该返回结果对象");
        assertFalse(result3.isSuccess(), "空标题应该创建失败");
        
        // 测试空内容
        Notification notification4 = new Notification();
        notification4.setRecipientId(TEST_USER_ID);
        notification4.setTitle("测试标题");
        Result<Long> result4 = notificationService.createNotification(notification4);
        assertNotNull(result4, "空内容应该返回结果对象");
        assertFalse(result4.isSuccess(), "空内容应该创建失败");
        
        logger.info("创建单个通知参数验证测试通过");
    }
    
    /**
     * 测试批量创建通知功能
     */
    @Test
    void testBatchCreateNotifications() {
        logger.info("开始测试批量创建通知功能");
        
        // 创建通知列表
        List<Notification> notifications = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            Notification notification = new Notification();
            notification.setRecipientId((long) i);
            notification.setTitle("批量通知标题 " + i);
            notification.setContent("批量通知内容 " + i);
            notification.setNotificationType(Notification.TYPE_SYSTEM_NOTICE);
            notifications.add(notification);
        }
        
        Result<Integer> result = notificationService.batchCreateNotifications(notifications);
        
        assertNotNull(result, "批量创建通知应该返回结果对象");
        assertTrue(result.isSuccess(), "批量创建通知应该成功");
        assertEquals(3, result.getData().intValue(), "应该创建3个通知");
        
        logger.info("批量创建通知测试通过: success={}, count={}", result.isSuccess(), result.getData());
    }
    
    /**
     * 测试批量创建通知参数验证
     */
    @Test
    void testBatchCreateNotificationsValidation() {
        logger.info("开始测试批量创建通知参数验证");
        
        // 测试null列表
        Result<Integer> result1 = notificationService.batchCreateNotifications(null);
        assertNotNull(result1, "null列表应该返回结果对象");
        assertFalse(result1.isSuccess(), "null列表应该创建失败");
        
        // 测试空列表
        Result<Integer> result2 = notificationService.batchCreateNotifications(new ArrayList<>());
        assertNotNull(result2, "空列表应该返回结果对象");
        assertFalse(result2.isSuccess(), "空列表应该创建失败");
        
        // 测试包含无效通知的列表
        List<Notification> notifications = new ArrayList<>();
        notifications.add(null); // 无效通知
        
        Notification validNotification = new Notification();
        validNotification.setRecipientId(TEST_USER_ID);
        validNotification.setTitle("有效通知");
        validNotification.setContent("有效内容");
        notifications.add(validNotification);
        
        Result<Integer> result3 = notificationService.batchCreateNotifications(notifications);
        assertNotNull(result3, "包含无效通知的列表应该返回结果对象");
        assertTrue(result3.isSuccess(), "应该创建有效的通知");
        assertEquals(1, result3.getData().intValue(), "应该只创建1个有效通知");
        
        logger.info("批量创建通知参数验证测试通过");
    }
    
    /**
     * 测试创建商品审核通过粉丝通知功能（Task4_2_1_2核心功能）
     */
    @Test
    void testCreateProductApprovedNotifications() {
        logger.info("开始测试创建商品审核通过粉丝通知功能");
        
        Result<Integer> result = notificationService.createProductApprovedNotifications(
            TEST_PRODUCT_ID, TEST_SELLER_ID, TEST_PRODUCT_TITLE);
        
        assertNotNull(result, "创建商品审核通过通知应该返回结果对象");
        assertTrue(result.isSuccess(), "创建商品审核通过通知应该成功");
        assertTrue(result.getData() >= 0, "应该返回创建的通知数量");
        
        logger.info("创建商品审核通过粉丝通知测试通过: success={}, count={}", result.isSuccess(), result.getData());
    }
    
    /**
     * 测试创建商品审核通过粉丝通知参数验证
     */
    @Test
    void testCreateProductApprovedNotificationsValidation() {
        logger.info("开始测试创建商品审核通过粉丝通知参数验证");
        
        // 测试null商品ID
        Result<Integer> result1 = notificationService.createProductApprovedNotifications(
            null, TEST_SELLER_ID, TEST_PRODUCT_TITLE);
        assertNotNull(result1, "null商品ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null商品ID应该创建失败");
        
        // 测试null卖家ID
        Result<Integer> result2 = notificationService.createProductApprovedNotifications(
            TEST_PRODUCT_ID, null, TEST_PRODUCT_TITLE);
        assertNotNull(result2, "null卖家ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null卖家ID应该创建失败");
        
        // 测试空商品标题
        Result<Integer> result3 = notificationService.createProductApprovedNotifications(
            TEST_PRODUCT_ID, TEST_SELLER_ID, null);
        assertNotNull(result3, "空商品标题应该返回结果对象");
        assertFalse(result3.isSuccess(), "空商品标题应该创建失败");
        
        logger.info("创建商品审核通过粉丝通知参数验证测试通过");
    }
    
    /**
     * 测试获取用户通知列表功能
     */
    @Test
    void testGetUserNotifications() {
        logger.info("开始测试获取用户通知列表功能");
        
        Result<List<NotificationVO>> result = notificationService.getUserNotifications(
            TEST_USER_ID, 1, 10, false);
        
        assertNotNull(result, "获取用户通知应该返回结果对象");
        assertTrue(result.isSuccess(), "获取用户通知应该成功");
        assertNotNull(result.getData(), "应该返回通知列表");
        
        logger.info("获取用户通知列表测试通过: success={}, count={}", result.isSuccess(), result.getData().size());
    }
    
    /**
     * 测试获取用户通知列表参数验证
     */
    @Test
    void testGetUserNotificationsValidation() {
        logger.info("开始测试获取用户通知列表参数验证");
        
        // 测试null用户ID
        Result<List<NotificationVO>> result1 = notificationService.getUserNotifications(
            null, 1, 10, false);
        assertNotNull(result1, "null用户ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null用户ID应该获取失败");
        
        // 测试无效页码和页面大小（应该被自动修正）
        Result<List<NotificationVO>> result2 = notificationService.getUserNotifications(
            TEST_USER_ID, 0, 0, false);
        assertNotNull(result2, "无效分页参数应该返回结果对象");
        assertTrue(result2.isSuccess(), "无效分页参数应该被自动修正");
        
        logger.info("获取用户通知列表参数验证测试通过");
    }
    
    /**
     * 测试获取未读通知数量功能
     */
    @Test
    void testGetUnreadNotificationCount() {
        logger.info("开始测试获取未读通知数量功能");
        
        Result<Integer> result = notificationService.getUnreadNotificationCount(TEST_USER_ID);
        
        assertNotNull(result, "获取未读通知数量应该返回结果对象");
        assertTrue(result.isSuccess(), "获取未读通知数量应该成功");
        assertNotNull(result.getData(), "应该返回未读数量");
        assertTrue(result.getData() >= 0, "未读数量应该大于等于0");
        
        logger.info("获取未读通知数量测试通过: success={}, count={}", result.isSuccess(), result.getData());
    }
    
    /**
     * 测试获取未读通知数量参数验证
     */
    @Test
    void testGetUnreadNotificationCountValidation() {
        logger.info("开始测试获取未读通知数量参数验证");
        
        // 测试null用户ID
        Result<Integer> result = notificationService.getUnreadNotificationCount(null);
        assertNotNull(result, "null用户ID应该返回结果对象");
        assertFalse(result.isSuccess(), "null用户ID应该获取失败");
        
        logger.info("获取未读通知数量参数验证测试通过");
    }
    
    /**
     * 测试标记通知为已读功能
     */
    @Test
    void testMarkNotificationAsRead() {
        logger.info("开始测试标记通知为已读功能");

        // 先创建一个通知
        Notification notification = new Notification();
        notification.setRecipientId(TEST_USER_ID);
        notification.setTitle("测试通知");
        notification.setContent("测试内容");
        notification.setNotificationType(Notification.TYPE_SYSTEM_NOTICE);

        Result<Long> createResult = notificationService.createNotification(notification);
        assertTrue(createResult.isSuccess(), "创建通知应该成功");

        // 标记通知为已读
        Result<Void> result = notificationService.markNotificationAsRead(createResult.getData(), TEST_USER_ID);

        assertNotNull(result, "标记通知已读应该返回结果对象");
        assertTrue(result.isSuccess(), "标记通知已读应该成功");

        logger.info("标记通知为已读测试通过: success={}", result.isSuccess());
    }
    
    /**
     * 测试标记通知为已读参数验证
     */
    @Test
    void testMarkNotificationAsReadValidation() {
        logger.info("开始测试标记通知为已读参数验证");
        
        // 测试null通知ID
        Result<Void> result1 = notificationService.markNotificationAsRead(null, TEST_USER_ID);
        assertNotNull(result1, "null通知ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null通知ID应该标记失败");
        
        // 测试null用户ID
        Result<Void> result2 = notificationService.markNotificationAsRead(1L, null);
        assertNotNull(result2, "null用户ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null用户ID应该标记失败");
        
        logger.info("标记通知为已读参数验证测试通过");
    }

    /**
     * 测试批量标记通知为已读功能
     */
    @Test
    void testBatchMarkNotificationsAsRead() {
        logger.info("开始测试批量标记通知为已读功能");

        List<Long> notificationIds = Arrays.asList(1L, 2L, 3L);
        Result<Integer> result = notificationService.batchMarkNotificationsAsRead(TEST_USER_ID, notificationIds);

        assertNotNull(result, "批量标记通知已读应该返回结果对象");
        assertTrue(result.isSuccess(), "批量标记通知已读应该成功");
        assertNotNull(result.getData(), "应该返回标记成功的数量");
        assertTrue(result.getData() >= 0, "标记成功数量应该大于等于0");

        logger.info("批量标记通知为已读测试通过: success={}, count={}", result.isSuccess(), result.getData());
    }

    /**
     * 测试批量标记通知为已读参数验证
     */
    @Test
    void testBatchMarkNotificationsAsReadValidation() {
        logger.info("开始测试批量标记通知为已读参数验证");

        // 测试null用户ID
        Result<Integer> result1 = notificationService.batchMarkNotificationsAsRead(null, Arrays.asList(1L, 2L));
        assertNotNull(result1, "null用户ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null用户ID应该标记失败");

        // 测试null通知ID列表（应该标记所有未读通知）
        Result<Integer> result2 = notificationService.batchMarkNotificationsAsRead(TEST_USER_ID, null);
        assertNotNull(result2, "null通知ID列表应该返回结果对象");
        assertTrue(result2.isSuccess(), "null通知ID列表应该标记所有未读通知");

        logger.info("批量标记通知为已读参数验证测试通过");
    }

    /**
     * 测试创建系统公告功能
     */
    @Test
    void testCreateSystemNotice() {
        logger.info("开始测试创建系统公告功能");

        String title = "系统维护公告";
        String content = "系统将于今晚进行维护，预计维护时间2小时。";
        List<Long> targetUserIds = Arrays.asList(1L, 2L, 3L);
        Integer priority = Notification.PRIORITY_IMPORTANT;
        Integer expireHours = 24;

        Result<Integer> result = notificationService.createSystemNotice(
            title, content, targetUserIds, priority, expireHours);

        assertNotNull(result, "创建系统公告应该返回结果对象");
        assertTrue(result.isSuccess(), "创建系统公告应该成功");
        assertNotNull(result.getData(), "应该返回创建的通知数量");
        assertTrue(result.getData() > 0, "应该创建至少1个通知");

        logger.info("创建系统公告测试通过: success={}, count={}", result.isSuccess(), result.getData());
    }

    /**
     * 测试创建系统公告参数验证
     */
    @Test
    void testCreateSystemNoticeValidation() {
        logger.info("开始测试创建系统公告参数验证");

        // 测试null标题
        Result<Integer> result1 = notificationService.createSystemNotice(
            null, "测试内容", Arrays.asList(1L), null, null);
        assertNotNull(result1, "null标题应该返回结果对象");
        assertFalse(result1.isSuccess(), "null标题应该创建失败");

        // 测试空标题
        Result<Integer> result2 = notificationService.createSystemNotice(
            "", "测试内容", Arrays.asList(1L), null, null);
        assertNotNull(result2, "空标题应该返回结果对象");
        assertFalse(result2.isSuccess(), "空标题应该创建失败");

        // 测试null内容
        Result<Integer> result3 = notificationService.createSystemNotice(
            "测试标题", null, Arrays.asList(1L), null, null);
        assertNotNull(result3, "null内容应该返回结果对象");
        assertFalse(result3.isSuccess(), "null内容应该创建失败");

        // 测试空内容
        Result<Integer> result4 = notificationService.createSystemNotice(
            "测试标题", "", Arrays.asList(1L), null, null);
        assertNotNull(result4, "空内容应该返回结果对象");
        assertFalse(result4.isSuccess(), "空内容应该创建失败");

        logger.info("创建系统公告参数验证测试通过");
    }

    /**
     * 测试创建消息通知功能
     */
    @Test
    void testCreateMessageNotification() {
        logger.info("开始测试创建消息通知功能");

        Long recipientId = TEST_USER_ID;
        Long senderId = TEST_SELLER_ID;
        String senderName = TEST_SELLER_NAME;
        String messageContent = "这是一条测试消息内容";
        String conversationId = "CONV_123456";

        Result<Long> result = notificationService.createMessageNotification(
            recipientId, senderId, senderName, messageContent, conversationId);

        assertNotNull(result, "创建消息通知应该返回结果对象");
        assertTrue(result.isSuccess(), "创建消息通知应该成功");
        assertNotNull(result.getData(), "应该返回通知ID");

        logger.info("创建消息通知测试通过: success={}, notificationId={}", result.isSuccess(), result.getData());
    }

    /**
     * 测试创建消息通知参数验证
     */
    @Test
    void testCreateMessageNotificationValidation() {
        logger.info("开始测试创建消息通知参数验证");

        // 测试null接收者ID
        Result<Long> result1 = notificationService.createMessageNotification(
            null, TEST_SELLER_ID, TEST_SELLER_NAME, "测试内容", "CONV_123");
        assertNotNull(result1, "null接收者ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null接收者ID应该创建失败");

        // 测试null发送者ID
        Result<Long> result2 = notificationService.createMessageNotification(
            TEST_USER_ID, null, TEST_SELLER_NAME, "测试内容", "CONV_123");
        assertNotNull(result2, "null发送者ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null发送者ID应该创建失败");

        // 测试null发送者名称
        Result<Long> result3 = notificationService.createMessageNotification(
            TEST_USER_ID, TEST_SELLER_ID, null, "测试内容", "CONV_123");
        assertNotNull(result3, "null发送者名称应该返回结果对象");
        assertFalse(result3.isSuccess(), "null发送者名称应该创建失败");

        // 测试空发送者名称
        Result<Long> result4 = notificationService.createMessageNotification(
            TEST_USER_ID, TEST_SELLER_ID, "", "测试内容", "CONV_123");
        assertNotNull(result4, "空发送者名称应该返回结果对象");
        assertFalse(result4.isSuccess(), "空发送者名称应该创建失败");

        logger.info("创建消息通知参数验证测试通过");
    }

    /**
     * 测试通知系统完整业务流程
     */
    @Test
    void testNotificationCompleteWorkflow() {
        logger.info("开始测试通知系统完整业务流程");

        // 1. 创建商品审核通过通知
        Result<Integer> productResult = notificationService.createProductApprovedNotifications(
            TEST_PRODUCT_ID, TEST_SELLER_ID, TEST_PRODUCT_TITLE);
        assertTrue(productResult.isSuccess(), "创建商品审核通过通知应该成功");
        logger.info("创建商品审核通过通知: success={}", productResult.isSuccess());

        // 2. 创建消息通知
        Result<Long> messageResult = notificationService.createMessageNotification(
            TEST_USER_ID, TEST_SELLER_ID, TEST_SELLER_NAME, "测试消息", "CONV_123");
        assertTrue(messageResult.isSuccess(), "创建消息通知应该成功");
        logger.info("创建消息通知: success={}", messageResult.isSuccess());

        // 3. 获取用户通知列表
        Result<List<NotificationVO>> listResult = notificationService.getUserNotifications(
            TEST_USER_ID, 1, 10, false);
        assertTrue(listResult.isSuccess(), "获取通知列表应该成功");
        logger.info("获取通知列表: success={}", listResult.isSuccess());

        // 4. 获取未读通知数量
        Result<Integer> countResult = notificationService.getUnreadNotificationCount(TEST_USER_ID);
        assertTrue(countResult.isSuccess(), "获取未读数量应该成功");
        logger.info("获取未读数量: success={}", countResult.isSuccess());

        // 5. 标记通知为已读
        if (messageResult.getData() != null) {
            Result<Void> readResult = notificationService.markNotificationAsRead(
                messageResult.getData(), TEST_USER_ID);
            assertTrue(readResult.isSuccess(), "标记已读应该成功");
            logger.info("标记已读: success={}", readResult.isSuccess());
        }

        // 6. 创建系统公告
        Result<Integer> noticeResult = notificationService.createSystemNotice(
            "测试公告", "测试公告内容", Arrays.asList(TEST_USER_ID),
            Notification.PRIORITY_NORMAL, 24);
        assertTrue(noticeResult.isSuccess(), "创建系统公告应该成功");
        logger.info("创建系统公告: success={}", noticeResult.isSuccess());

        logger.info("通知系统完整业务流程测试通过");
    }

    /**
     * 测试通知类型和优先级
     */
    @Test
    void testNotificationTypesAndPriorities() {
        logger.info("开始测试通知类型和优先级");

        // 测试不同类型的通知
        String[] types = {
            Notification.TYPE_PRODUCT_APPROVED,
            Notification.TYPE_ORDER_STATUS,
            Notification.TYPE_MESSAGE_RECEIVED,
            Notification.TYPE_SYSTEM_NOTICE
        };

        Integer[] priorities = {
            Notification.PRIORITY_NORMAL,
            Notification.PRIORITY_IMPORTANT,
            Notification.PRIORITY_URGENT
        };

        for (String type : types) {
            for (Integer priority : priorities) {
                Notification notification = new Notification();
                notification.setRecipientId(TEST_USER_ID);
                notification.setTitle("测试通知 - " + type);
                notification.setContent("测试内容 - 优先级" + priority);
                notification.setNotificationType(type);
                notification.setPriority(priority);

                Result<Long> result = notificationService.createNotification(notification);
                assertTrue(result.isSuccess(), "创建" + type + "类型通知应该成功");
                logger.info("创建{}类型通知成功: priority={}", type, priority);
            }
        }

        logger.info("通知类型和优先级测试通过");
    }

    /**
     * 测试通知内容边界情况
     */
    @Test
    void testNotificationContentBoundaries() {
        logger.info("开始测试通知内容边界情况");

        // 测试很长的标题
        StringBuilder titleBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            titleBuilder.append("这是一个非常长的通知标题");
        }
        String longTitle = titleBuilder.toString();
        Notification notification1 = new Notification();
        notification1.setRecipientId(TEST_USER_ID);
        notification1.setTitle(longTitle);
        notification1.setContent("测试内容");
        notification1.setNotificationType(Notification.TYPE_SYSTEM_NOTICE);

        Result<Long> result1 = notificationService.createNotification(notification1);
        assertTrue(result1.isSuccess(), "长标题通知应该创建成功");
        logger.info("长标题通知测试: success={}, titleLength={}", result1.isSuccess(), longTitle.length());

        // 测试很长的内容
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            contentBuilder.append("这是一个非常长的通知内容");
        }
        String longContent = contentBuilder.toString();
        Notification notification2 = new Notification();
        notification2.setRecipientId(TEST_USER_ID);
        notification2.setTitle("测试标题");
        notification2.setContent(longContent);
        notification2.setNotificationType(Notification.TYPE_SYSTEM_NOTICE);

        Result<Long> result2 = notificationService.createNotification(notification2);
        assertTrue(result2.isSuccess(), "长内容通知应该创建成功");
        logger.info("长内容通知测试: success={}, contentLength={}", result2.isSuccess(), longContent.length());

        // 测试特殊字符
        Notification notification3 = new Notification();
        notification3.setRecipientId(TEST_USER_ID);
        notification3.setTitle("特殊字符测试 @#$%^&*()");
        notification3.setContent("包含特殊字符的内容：<>&\"'");
        notification3.setNotificationType(Notification.TYPE_SYSTEM_NOTICE);

        Result<Long> result3 = notificationService.createNotification(notification3);
        assertTrue(result3.isSuccess(), "特殊字符通知应该创建成功");
        logger.info("特殊字符通知测试: success={}", result3.isSuccess());

        logger.info("通知内容边界测试通过");
    }

    /**
     * 测试分页参数边界情况
     */
    @Test
    void testPaginationBoundaries() {
        logger.info("开始测试分页参数边界情况");

        // 测试大页码
        Result<List<NotificationVO>> result1 = notificationService.getUserNotifications(
            TEST_USER_ID, 999, 10, false);
        assertTrue(result1.isSuccess(), "大页码应该获取成功");
        logger.info("大页码测试: success={}", result1.isSuccess());

        // 测试大页面大小
        Result<List<NotificationVO>> result2 = notificationService.getUserNotifications(
            TEST_USER_ID, 1, 200, false);
        assertTrue(result2.isSuccess(), "大页面大小应该被自动修正");
        logger.info("大页面大小测试: success={}", result2.isSuccess());

        // 测试只获取未读通知
        Result<List<NotificationVO>> result3 = notificationService.getUserNotifications(
            TEST_USER_ID, 1, 10, true);
        assertTrue(result3.isSuccess(), "只获取未读通知应该成功");
        logger.info("只获取未读通知测试: success={}", result3.isSuccess());

        logger.info("分页参数边界测试通过");
    }

    /**
     * 测试通知过期时间功能
     */
    @Test
    void testNotificationExpiration() {
        logger.info("开始测试通知过期时间功能");

        // 测试设置过期时间的系统公告
        Result<Integer> result1 = notificationService.createSystemNotice(
            "短期公告", "这是一个短期公告", Arrays.asList(TEST_USER_ID),
            Notification.PRIORITY_NORMAL, 1); // 1小时过期
        assertTrue(result1.isSuccess(), "短期公告应该创建成功");
        logger.info("短期公告测试: success={}, expireHours=1", result1.isSuccess());

        // 测试不设置过期时间的公告
        Result<Integer> result2 = notificationService.createSystemNotice(
            "长期公告", "这是一个长期公告", Arrays.asList(TEST_USER_ID),
            Notification.PRIORITY_NORMAL, null); // 不过期
        assertTrue(result2.isSuccess(), "长期公告应该创建成功");
        logger.info("长期公告测试: success={}, expireHours=null", result2.isSuccess());

        // 测试0小时过期时间
        Result<Integer> result3 = notificationService.createSystemNotice(
            "即时公告", "这是一个即时公告", Arrays.asList(TEST_USER_ID),
            Notification.PRIORITY_NORMAL, 0); // 0小时过期
        assertTrue(result3.isSuccess(), "即时公告应该创建成功");
        logger.info("即时公告测试: success={}, expireHours=0", result3.isSuccess());

        logger.info("通知过期时间测试通过");
    }

    /**
     * 测试通知系统错误处理
     */
    @Test
    void testNotificationErrorHandling() {
        logger.info("开始测试通知系统错误处理");

        // 测试不存在的卖家ID
        Result<Integer> result1 = notificationService.createProductApprovedNotifications(
            TEST_PRODUCT_ID, 999L, TEST_PRODUCT_TITLE);
        assertNotNull(result1, "不存在的卖家ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "不存在的卖家ID应该创建失败");
        logger.info("不存在卖家ID测试: success={}", result1.isSuccess());

        // 测试标记不存在的通知为已读
        Result<Void> result2 = notificationService.markNotificationAsRead(999L, TEST_USER_ID);
        assertNotNull(result2, "标记不存在通知应该返回结果对象");
        // 注意：这里可能成功也可能失败，取决于具体实现
        logger.info("标记不存在通知测试: success={}", result2.isSuccess());

        // 测试获取不存在用户的通知
        Result<List<NotificationVO>> result3 = notificationService.getUserNotifications(
            999L, 1, 10, false);
        assertTrue(result3.isSuccess(), "获取不存在用户通知应该返回空列表");
        assertTrue(result3.getData().isEmpty(), "不存在用户应该返回空通知列表");
        logger.info("获取不存在用户通知测试: success={}, isEmpty={}",
                   result3.isSuccess(), result3.getData().isEmpty());

        logger.info("通知系统错误处理测试通过");
    }

    // ==================== Mock类定义 ====================

    /**
     * Mock NotificationDao
     */
    private static class MockNotificationDao extends NotificationDao {
        private Long nextId = 1L;
        private List<Notification> notifications = new ArrayList<>();

        @Override
        public Long createNotification(Notification notification) {
            notification.setId(nextId++);
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());
            notifications.add(notification);
            return notification.getId();
        }

        @Override
        public int batchCreateNotifications(List<Notification> notifications) {
            int count = 0;
            for (Notification notification : notifications) {
                if (createNotification(notification) != null) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public List<Notification> findNotificationsByUserId(Long userId, int page, int size, boolean onlyUnread) {
            List<Notification> result = new ArrayList<>();
            for (Notification notification : notifications) {
                if (notification.getRecipientId().equals(userId)) {
                    if (!onlyUnread || !Boolean.TRUE.equals(notification.getIsRead())) {
                        result.add(notification);
                    }
                }
            }

            // 简单分页
            int start = (page - 1) * size;
            int end = Math.min(start + size, result.size());
            if (start >= result.size()) {
                return new ArrayList<>();
            }
            return result.subList(start, end);
        }

        @Override
        public int getUnreadNotificationCount(Long userId) {
            int count = 0;
            for (Notification notification : notifications) {
                if (notification.getRecipientId().equals(userId) &&
                    !Boolean.TRUE.equals(notification.getIsRead())) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public boolean markNotificationAsRead(Long notificationId, Long userId) {
            for (Notification notification : notifications) {
                if (notification.getId().equals(notificationId) &&
                    notification.getRecipientId().equals(userId)) {
                    notification.markAsRead();
                    return true;
                }
            }
            return false;
        }

        @Override
        public int batchMarkNotificationsAsRead(Long userId, List<Long> notificationIds) {
            int count = 0;
            for (Notification notification : notifications) {
                if (notification.getRecipientId().equals(userId) &&
                    !Boolean.TRUE.equals(notification.getIsRead())) {
                    if (notificationIds == null || notificationIds.contains(notification.getId())) {
                        notification.markAsRead();
                        count++;
                    }
                }
            }
            return count;
        }
    }

    /**
     * Mock UserFollowDao
     */
    private static class MockUserFollowDao extends UserFollowDao {
        @Override
        public List<Long> getFollowerIds(Long userId) {
            // 模拟卖家有一些粉丝
            if (userId.equals(TEST_SELLER_ID)) {
                return Arrays.asList(TEST_USER_ID, TEST_FOLLOWER_ID, 4L, 5L);
            }
            return new ArrayList<>();
        }
    }

    /**
     * Mock UserDao
     */
    private static class MockUserDao extends UserDao {
        @Override
        public User findPublicInfoById(Long userId) {
            if (userId.equals(TEST_SELLER_ID)) {
                User user = new User();
                user.setId(TEST_SELLER_ID);
                user.setUsername(TEST_SELLER_NAME);
                return user;
            }
            if (userId.equals(TEST_USER_ID)) {
                User user = new User();
                user.setId(TEST_USER_ID);
                user.setUsername(TEST_USERNAME);
                return user;
            }
            return null; // 不存在的用户
        }

        @Override
        public List<Long> getAllActiveUserIds() {
            return Arrays.asList(1L, 2L, 3L, 4L, 5L);
        }
    }
}
