package com.shiwu.notification.controller;

import com.shiwu.common.result.Result;
import com.shiwu.notification.service.NotificationService;
import com.shiwu.notification.vo.NotificationVO;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * NotificationController综合测试类
 */
public class NotificationControllerComprehensiveTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationControllerComprehensiveTest.class);
    
    private NotificationController notificationController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private NotificationService mockNotificationService;
    
    @BeforeEach
    public void setUp() {
        logger.info("NotificationController测试环境初始化开始");
        super.setUp();
        
        // 创建Mock对象
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        mockNotificationService = mock(NotificationService.class);
        
        // 创建NotificationController实例，使用Mock的service
        notificationController = new NotificationController(mockNotificationService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (Exception e) {
            fail("设置响应Writer失败: " + e.getMessage());
        }
        
        logger.info("NotificationController测试环境初始化完成");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("NotificationController测试清理完成");
    }
    
    /**
     * 测试获取通知列表接口 - 成功
     */
    @Test
    public void testGetNotificationList() throws Exception {
        logger.info("开始测试获取通知列表接口");
        
        // 设置请求路径和参数
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("page")).thenReturn("1");
        when(request.getParameter("size")).thenReturn("10");
        when(request.getParameter("onlyUnread")).thenReturn("false");
        
        // 模拟service返回
        List<NotificationVO> notifications = createMockNotifications();
        when(mockNotificationService.getUserNotifications(TEST_USER_ID_1, 1, 10, false))
            .thenReturn(Result.success(notifications));
        
        // 执行测试
        notificationController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"data\""));
        
        logger.info("获取通知列表接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取通知列表接口 - 未登录
     */
    @Test
    public void testGetNotificationListNotLoggedIn() throws Exception {
        logger.info("开始测试获取通知列表接口 - 未登录");
        
        // 设置请求路径，不设置用户ID
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn(null);
        
        // 执行测试
        notificationController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请先登录"));
        
        logger.info("未登录获取通知列表测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取通知列表接口 - 只获取未读
     */
    @Test
    public void testGetNotificationListOnlyUnread() throws Exception {
        logger.info("开始测试获取通知列表接口 - 只获取未读");
        
        // 设置请求路径和参数
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("page")).thenReturn("1");
        when(request.getParameter("size")).thenReturn("5");
        when(request.getParameter("onlyUnread")).thenReturn("true");
        
        // 模拟service返回
        List<NotificationVO> unreadNotifications = createMockUnreadNotifications();
        when(mockNotificationService.getUserNotifications(TEST_USER_ID_1, 1, 5, true))
            .thenReturn(Result.success(unreadNotifications));
        
        // 执行测试
        notificationController.doGet(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        
        logger.info("只获取未读通知测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取未读通知数量接口 - 成功
     */
    @Test
    public void testGetUnreadCount() throws Exception {
        logger.info("开始测试获取未读通知数量接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/unread-count");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        
        // 模拟service返回
        when(mockNotificationService.getUnreadNotificationCount(TEST_USER_ID_1))
            .thenReturn(Result.success(5));
        
        // 执行测试
        notificationController.doGet(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"data\":5"));
        
        logger.info("获取未读通知数量接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取未读通知数量接口 - 未登录
     */
    @Test
    public void testGetUnreadCountNotLoggedIn() throws Exception {
        logger.info("开始测试获取未读通知数量接口 - 未登录");
        
        // 设置请求路径，不设置用户ID
        when(request.getPathInfo()).thenReturn("/unread-count");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn(null);
        
        // 执行测试
        notificationController.doGet(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请先登录"));
        
        logger.info("未登录获取未读数量测试通过: response=" + responseContent);
    }
    
    /**
     * 测试标记通知已读接口 - 成功
     */
    @Test
    public void testMarkAsRead() throws Exception {
        logger.info("开始测试标记通知已读接口");
        
        // 设置请求路径和参数
        when(request.getPathInfo()).thenReturn("/mark-read");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("id")).thenReturn("123");
        
        // 模拟service返回
        when(mockNotificationService.markNotificationAsRead(123L, TEST_USER_ID_1))
            .thenReturn(Result.success(null));
        
        // 执行测试
        notificationController.doPut(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        
        logger.info("标记通知已读接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试标记通知已读接口 - 未登录
     */
    @Test
    public void testMarkAsReadNotLoggedIn() throws Exception {
        logger.info("开始测试标记通知已读接口 - 未登录");
        
        // 设置请求路径，不设置用户ID
        when(request.getPathInfo()).thenReturn("/mark-read");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getParameter("id")).thenReturn("123");
        
        // 执行测试
        notificationController.doPut(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请先登录"));
        
        logger.info("未登录标记已读测试通过: response=" + responseContent);
    }
    
    /**
     * 创建模拟通知列表
     */
    private List<NotificationVO> createMockNotifications() {
        List<NotificationVO> notifications = new ArrayList<>();
        
        NotificationVO notification1 = new NotificationVO();
        notification1.setId(1L);
        notification1.setTitle("商品审核通过");
        notification1.setContent("您的商品已通过审核");
        notification1.setNotificationType("PRODUCT_APPROVED");
        notification1.setIsRead(false);
        notification1.setCreateTime(LocalDateTime.now().minusHours(1));
        notifications.add(notification1);
        
        NotificationVO notification2 = new NotificationVO();
        notification2.setId(2L);
        notification2.setTitle("新消息");
        notification2.setContent("您收到了一条新消息");
        notification2.setNotificationType("MESSAGE_RECEIVED");
        notification2.setIsRead(true);
        notification2.setCreateTime(LocalDateTime.now().minusHours(2));
        notifications.add(notification2);
        
        return notifications;
    }
    
    /**
     * 创建模拟未读通知列表
     */
    private List<NotificationVO> createMockUnreadNotifications() {
        List<NotificationVO> notifications = new ArrayList<>();
        
        NotificationVO notification = new NotificationVO();
        notification.setId(1L);
        notification.setTitle("商品审核通过");
        notification.setContent("您的商品已通过审核");
        notification.setNotificationType("PRODUCT_APPROVED");
        notification.setIsRead(false);
        notification.setCreateTime(LocalDateTime.now().minusHours(1));
        notifications.add(notification);
        
        return notifications;
    }

    /**
     * 测试标记通知已读接口 - 通知ID为空
     */
    @Test
    public void testMarkAsReadEmptyId() throws Exception {
        logger.info("开始测试标记通知已读接口 - 通知ID为空");

        // 设置请求路径，不设置通知ID
        when(request.getPathInfo()).thenReturn("/mark-read");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("id")).thenReturn(null);

        // 执行测试
        notificationController.doPut(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("通知ID不能为空"));

        logger.info("通知ID为空测试通过: response=" + responseContent);
    }

    /**
     * 测试标记通知已读接口 - 通知ID格式错误
     */
    @Test
    public void testMarkAsReadInvalidId() throws Exception {
        logger.info("开始测试标记通知已读接口 - 通知ID格式错误");

        // 设置请求路径和无效ID
        when(request.getPathInfo()).thenReturn("/mark-read");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("id")).thenReturn("invalid");

        // 执行测试
        notificationController.doPut(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("通知ID格式错误"));

        logger.info("通知ID格式错误测试通过: response=" + responseContent);
    }

    /**
     * 测试批量标记已读接口 - 成功
     */
    @Test
    public void testMarkAllAsRead() throws Exception {
        logger.info("开始测试批量标记已读接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/mark-all-read");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("ids")).thenReturn(null);

        // 模拟service返回
        when(mockNotificationService.batchMarkNotificationsAsRead(TEST_USER_ID_1, null))
            .thenReturn(Result.success(3));

        // 执行测试
        notificationController.doPut(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"data\":3"));

        logger.info("批量标记已读接口测试通过: response=" + responseContent);
    }

    /**
     * 测试批量标记已读接口 - 指定ID列表
     */
    @Test
    public void testMarkAllAsReadWithIds() throws Exception {
        logger.info("开始测试批量标记已读接口 - 指定ID列表");

        // 设置请求路径和ID列表
        when(request.getPathInfo()).thenReturn("/mark-all-read");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("ids")).thenReturn("1,2,3");

        // 模拟service返回
        List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);
        when(mockNotificationService.batchMarkNotificationsAsRead(TEST_USER_ID_1, expectedIds))
            .thenReturn(Result.success(3));

        // 执行测试
        notificationController.doPut(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"data\":3"));

        logger.info("指定ID列表批量标记已读测试通过: response=" + responseContent);
    }

    /**
     * 测试批量标记已读接口 - ID格式错误
     */
    @Test
    public void testMarkAllAsReadInvalidIds() throws Exception {
        logger.info("开始测试批量标记已读接口 - ID格式错误");

        // 设置请求路径和无效ID列表
        when(request.getPathInfo()).thenReturn("/mark-all-read");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("ids")).thenReturn("1,invalid,3");

        // 执行测试
        notificationController.doPut(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("通知ID格式错误"));

        logger.info("ID格式错误批量标记已读测试通过: response=" + responseContent);
    }

    /**
     * 测试无效路径 - GET
     */
    @Test
    public void testInvalidPathGet() throws Exception {
        logger.info("开始测试无效路径 - GET");

        // 设置无效请求路径
        when(request.getPathInfo()).thenReturn("/invalid");

        // 执行测试
        notificationController.doGet(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("无效路径GET测试通过: response=" + responseContent);
    }

    /**
     * 测试无效路径 - PUT
     */
    @Test
    public void testInvalidPathPut() throws Exception {
        logger.info("开始测试无效路径 - PUT");

        // 设置无效请求路径
        when(request.getPathInfo()).thenReturn("/invalid");

        // 执行测试
        notificationController.doPut(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("无效路径PUT测试通过: response=" + responseContent);
    }

    /**
     * 测试空路径
     */
    @Test
    public void testNullPath() throws Exception {
        logger.info("开始测试空路径");

        // 设置空请求路径
        when(request.getPathInfo()).thenReturn(null);

        // 执行测试
        notificationController.doGet(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("空路径测试通过: response=" + responseContent);
    }

    /**
     * 测试参数验证 - 分页参数
     */
    @Test
    public void testParameterValidation() throws Exception {
        logger.info("开始测试参数验证 - 分页参数");

        // 设置请求路径和无效参数
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(request.getParameter("page")).thenReturn("-1");
        when(request.getParameter("size")).thenReturn("200");
        when(request.getParameter("onlyUnread")).thenReturn("invalid");

        // 模拟service返回（参数会被修正）
        List<NotificationVO> notifications = createMockNotifications();
        when(mockNotificationService.getUserNotifications(TEST_USER_ID_1, 1, 20, false))
            .thenReturn(Result.success(notifications));

        // 执行测试
        notificationController.doGet(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));

        logger.info("参数验证测试通过: response=" + responseContent);
    }

    /**
     * 测试JWT Token认证
     */
    @Test
    public void testJwtTokenAuth() throws Exception {
        logger.info("开始测试JWT Token认证");

        // 设置请求路径和JWT Token
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getParameter("page")).thenReturn("1");
        when(request.getParameter("size")).thenReturn("10");

        // 注意：由于JwtUtil是静态方法，这里无法完全模拟JWT验证
        // 在实际测试中，JWT验证可能会失败，导致使用X-User-Id作为备用
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());

        // 模拟service返回
        List<NotificationVO> notifications = createMockNotifications();
        when(mockNotificationService.getUserNotifications(TEST_USER_ID_1, 1, 10, false))
            .thenReturn(Result.success(notifications));

        // 执行测试
        notificationController.doGet(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));

        logger.info("JWT Token认证测试通过: response=" + responseContent);
    }

    /**
     * 测试Service异常处理
     */
    @Test
    public void testServiceException() throws Exception {
        logger.info("开始测试Service异常处理");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());

        // 模拟service返回错误
        when(mockNotificationService.getUserNotifications(TEST_USER_ID_1, 1, 20, false))
            .thenReturn(Result.error("数据库连接失败"));

        // 执行测试
        notificationController.doGet(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("数据库连接失败"));

        logger.info("Service异常处理测试通过: response=" + responseContent);
    }

    /**
     * 测试系统异常处理
     */
    @Test
    public void testSystemException() throws Exception {
        logger.info("开始测试系统异常处理");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());

        // 模拟service抛出异常
        when(mockNotificationService.getUserNotifications(any(), anyInt(), anyInt(), anyBoolean()))
            .thenThrow(new RuntimeException("系统异常"));

        // 执行测试
        notificationController.doGet(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("服务器内部错误"));

        logger.info("系统异常处理测试通过: response=" + responseContent);
    }

    /**
     * 测试完整的通知操作流程
     */
    @Test
    public void testCompleteNotificationWorkflow() throws Exception {
        logger.info("开始测试完整的通知操作流程");

        // 1. 获取未读通知数量
        when(request.getPathInfo()).thenReturn("/unread-count");
        when(request.getHeader("X-User-Id")).thenReturn(TEST_USER_ID_1.toString());
        when(mockNotificationService.getUnreadNotificationCount(TEST_USER_ID_1))
            .thenReturn(Result.success(5));

        notificationController.doGet(request, response);
        String unreadCountResponse = responseWriter.toString();
        logger.info("获取未读数量成功: " + unreadCountResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 2. 获取通知列表
        when(request.getPathInfo()).thenReturn("/list");
        when(request.getParameter("page")).thenReturn("1");
        when(request.getParameter("size")).thenReturn("10");
        when(request.getParameter("onlyUnread")).thenReturn("true");

        List<NotificationVO> notifications = createMockUnreadNotifications();
        when(mockNotificationService.getUserNotifications(TEST_USER_ID_1, 1, 10, true))
            .thenReturn(Result.success(notifications));

        notificationController.doGet(request, response);
        String listResponse = responseWriter.toString();
        logger.info("获取通知列表成功: " + listResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 3. 标记单个通知已读
        when(request.getPathInfo()).thenReturn("/mark-read");
        when(request.getParameter("id")).thenReturn("1");
        when(mockNotificationService.markNotificationAsRead(1L, TEST_USER_ID_1))
            .thenReturn(Result.success(null));

        notificationController.doPut(request, response);
        String markReadResponse = responseWriter.toString();
        logger.info("标记单个通知已读成功: " + markReadResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 4. 批量标记已读
        when(request.getPathInfo()).thenReturn("/mark-all-read");
        when(request.getParameter("ids")).thenReturn(null);
        when(mockNotificationService.batchMarkNotificationsAsRead(TEST_USER_ID_1, null))
            .thenReturn(Result.success(4));

        notificationController.doPut(request, response);
        String batchMarkResponse = responseWriter.toString();
        logger.info("批量标记已读成功: " + batchMarkResponse);

        // 验证所有响应都成功
        assertTrue(unreadCountResponse.contains("\"success\":true"));
        assertTrue(listResponse.contains("\"success\":true"));
        assertTrue(markReadResponse.contains("\"success\":true"));
        assertTrue(batchMarkResponse.contains("\"success\":true"));

        logger.info("完整的通知操作流程测试通过");
    }
}
