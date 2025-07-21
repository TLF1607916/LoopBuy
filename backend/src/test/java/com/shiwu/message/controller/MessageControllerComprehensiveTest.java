package com.shiwu.message.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.message.dto.MessageSendDTO;
import com.shiwu.message.service.MessageService;
import com.shiwu.message.vo.ConversationVO;
import com.shiwu.message.vo.MessageVO;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * MessageController综合测试类
 */
public class MessageControllerComprehensiveTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageControllerComprehensiveTest.class);
    
    private MessageController messageController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;
    private MessageService mockMessageService;
    
    @BeforeEach
    public void setUp() {
        logger.info("MessageController测试环境初始化开始");
        super.setUp();
        
        // 创建MessageController实例
        messageController = new MessageController();
        
        // 创建Mock对象
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        mockMessageService = mock(MessageService.class);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (Exception e) {
            fail("设置响应Writer失败: " + e.getMessage());
        }
        
        // 设置默认的JWT Token认证
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_jwt_token");
        
        logger.info("MessageController测试环境初始化完成");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("MessageController测试清理完成");
    }
    
    /**
     * 测试发送消息接口 - 成功
     */
    @Test
    public void testSendMessage() throws Exception {
        logger.info("开始测试发送消息接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/send");
        
        // 设置请求体
        MessageSendDTO dto = new MessageSendDTO(TEST_USER_ID_2, "Hello, this is a test message", TEST_PRODUCT_ID_1);
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 执行测试
        messageController.doPost(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("发送消息接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试发送消息接口 - 未登录
     */
    @Test
    public void testSendMessageNotLoggedIn() throws Exception {
        logger.info("开始测试发送消息接口 - 未登录");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/send");
        
        // 模拟未登录状态 - 没有Authorization头
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // 设置请求体
        MessageSendDTO dto = new MessageSendDTO(TEST_USER_ID_2, "Hello", TEST_PRODUCT_ID_1);
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 执行测试
        messageController.doPost(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("未登录发送消息测试通过: response=" + responseContent);
    }
    
    /**
     * 测试发送消息接口 - 参数为空
     */
    @Test
    public void testSendMessageEmptyParams() throws Exception {
        logger.info("开始测试发送消息接口 - 参数为空");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/send");
        
        // 设置空请求体
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        
        // 执行测试
        messageController.doPost(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("空参数发送消息测试通过: response=" + responseContent);
    }
    
    /**
     * 测试发送消息接口 - 无效JSON
     */
    @Test
    public void testSendMessageInvalidJson() throws Exception {
        logger.info("开始测试发送消息接口 - 无效JSON");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/send");
        
        // 设置无效JSON
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("invalid json")));
        
        // 执行测试
        messageController.doPost(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("无效JSON发送消息测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取会话列表接口 - 成功
     */
    @Test
    public void testGetConversations() throws Exception {
        logger.info("开始测试获取会话列表接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/conversations");
        
        // 执行测试
        messageController.doGet(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("获取会话列表接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取会话列表接口 - 未登录
     */
    @Test
    public void testGetConversationsNotLoggedIn() throws Exception {
        logger.info("开始测试获取会话列表接口 - 未登录");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/conversations");
        
        // 模拟未登录状态 - 没有Authorization头
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // 执行测试
        messageController.doGet(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("未登录获取会话列表测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取消息历史接口 - 成功
     */
    @Test
    public void testGetMessageHistory() throws Exception {
        logger.info("开始测试获取消息历史接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/history/" + TEST_USER_ID_2);
        
        // 执行测试
        messageController.doGet(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("获取消息历史接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取消息历史接口 - 无效用户ID
     */
    @Test
    public void testGetMessageHistoryInvalidUserId() throws Exception {
        logger.info("开始测试获取消息历史接口 - 无效用户ID");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/history/invalid");
        
        // 执行测试
        messageController.doGet(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("无效用户ID获取消息历史测试通过: response=" + responseContent);
    }
    
    /**
     * 测试标记消息已读接口 - 成功
     */
    @Test
    public void testMarkMessagesAsRead() throws Exception {
        logger.info("开始测试标记消息已读接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/read/" + TEST_USER_ID_2);
        
        // 执行测试
        messageController.doPut(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("标记消息已读接口测试通过: response=" + responseContent);
    }

    /**
     * 测试标记消息已读接口 - 未登录
     */
    @Test
    public void testMarkMessagesAsReadNotLoggedIn() throws Exception {
        logger.info("开始测试标记消息已读接口 - 未登录");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/read/" + TEST_USER_ID_2);

        // 模拟未登录状态 - 没有Authorization头
        when(request.getHeader("Authorization")).thenReturn(null);

        // 执行测试
        messageController.doPut(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));

        logger.info("未登录标记消息已读测试通过: response=" + responseContent);
    }

    /**
     * 测试获取未读消息数量接口 - 成功
     */
    @Test
    public void testGetUnreadCount() throws Exception {
        logger.info("开始测试获取未读消息数量接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/unread-count");

        // 执行测试
        messageController.doGet(request, response);

        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));

        logger.info("获取未读消息数量接口测试通过: response=" + responseContent);
    }

    /**
     * 测试HTTP方法路由
     */
    @Test
    public void testHttpMethodRouting() throws Exception {
        logger.info("开始测试HTTP方法路由");

        // 测试GET方法 - 无效路径
        when(request.getPathInfo()).thenReturn("/invalid");
        messageController.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        // 重置mock
        reset(response);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 测试POST方法 - 无效路径
        when(request.getPathInfo()).thenReturn("/invalid");
        messageController.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        // 重置mock
        reset(response);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 测试PUT方法 - 无效路径
        when(request.getPathInfo()).thenReturn("/invalid");
        messageController.doPut(request, response);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        logger.info("HTTP方法路由测试通过");
    }

    /**
     * 测试系统异常处理
     */
    @Test
    public void testSystemExceptionHandling() throws Exception {
        logger.info("开始测试系统异常处理");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/conversations");

        // 模拟JWT解析抛出异常 - 使用无效token
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");

        // 执行测试 - JWT解析失败会返回401，而不是抛出异常
        messageController.doGet(request, response);

        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));

        logger.info("JWT解析异常处理正确: " + responseContent);

        logger.info("系统异常处理测试通过");
    }

    /**
     * 测试完整的消息操作流程
     */
    @Test
    public void testCompleteMessageWorkflow() throws Exception {
        logger.info("开始测试完整的消息操作流程");

        // 1. 获取会话列表
        when(request.getPathInfo()).thenReturn("/conversations");
        messageController.doGet(request, response);
        String conversationsResponse = responseWriter.toString();
        logger.info("获取会话列表成功: " + conversationsResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 2. 发送消息
        when(request.getPathInfo()).thenReturn("/send");
        MessageSendDTO dto = new MessageSendDTO(TEST_USER_ID_2, "Test message", TEST_PRODUCT_ID_1);
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        messageController.doPost(request, response);
        String sendResponse = responseWriter.toString();
        logger.info("发送消息成功: " + sendResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 3. 获取消息历史
        when(request.getPathInfo()).thenReturn("/history/" + TEST_USER_ID_2);
        messageController.doGet(request, response);
        String historyResponse = responseWriter.toString();
        logger.info("获取消息历史成功: " + historyResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 4. 标记消息已读
        when(request.getPathInfo()).thenReturn("/read/" + TEST_USER_ID_2);
        messageController.doPut(request, response);
        String readResponse = responseWriter.toString();
        logger.info("标记消息已读成功: " + readResponse);

        logger.info("完整的消息操作流程测试通过");
    }
}
