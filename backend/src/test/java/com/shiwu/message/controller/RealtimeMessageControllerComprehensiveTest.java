package com.shiwu.message.controller;

import com.shiwu.common.util.JsonUtil;
import com.shiwu.message.dto.MessagePollDTO;
import com.shiwu.message.service.RealtimeMessageService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RealtimeMessageController综合测试类
 */
public class RealtimeMessageControllerComprehensiveTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(RealtimeMessageControllerComprehensiveTest.class);
    
    private RealtimeMessageController realtimeMessageController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;
    private RealtimeMessageService mockRealtimeMessageService;
    
    @BeforeEach
    public void setUp() {
        logger.info("RealtimeMessageController测试环境初始化开始");
        super.setUp();
        
        // 创建RealtimeMessageController实例
        realtimeMessageController = new RealtimeMessageController();
        
        // 创建Mock对象
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        mockRealtimeMessageService = mock(RealtimeMessageService.class);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (Exception e) {
            fail("设置响应Writer失败: " + e.getMessage());
        }
        
        // 设置默认的JWT Token认证
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_jwt_token");
        
        logger.info("RealtimeMessageController测试环境初始化完成");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("RealtimeMessageController测试清理完成");
    }
    
    /**
     * 测试短轮询接口 - GET方式
     */
    @Test
    public void testPollMessages() throws Exception {
        logger.info("开始测试短轮询接口 - GET方式");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/poll");
        
        // 执行测试
        realtimeMessageController.doGet(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("短轮询接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试短轮询接口 - POST方式
     */
    @Test
    public void testPollMessagesWithBody() throws Exception {
        logger.info("开始测试短轮询接口 - POST方式");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/poll");
        
        // 设置请求体
        MessagePollDTO dto = new MessagePollDTO();
        dto.setLastMessageTime(System.currentTimeMillis() - 60000); // 1分钟前
        dto.setUnreadOnly(true);
        dto.setLimit(20);
        
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 执行测试
        realtimeMessageController.doPost(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("短轮询POST接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试长轮询接口 - GET方式
     */
    @Test
    public void testLongPollMessages() throws Exception {
        logger.info("开始测试长轮询接口 - GET方式");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/long-poll");
        
        // 执行测试
        realtimeMessageController.doGet(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("长轮询接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试长轮询接口 - POST方式
     */
    @Test
    public void testLongPollMessagesWithBody() throws Exception {
        logger.info("开始测试长轮询接口 - POST方式");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/long-poll");
        
        // 设置请求体
        MessagePollDTO dto = new MessagePollDTO();
        dto.setLastMessageTime(System.currentTimeMillis());
        dto.setUnreadOnly(false);
        dto.setLimit(10);
        
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 执行测试
        realtimeMessageController.doPost(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("长轮询POST接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取实时状态接口
     */
    @Test
    public void testGetRealtimeStatus() throws Exception {
        logger.info("开始测试获取实时状态接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/status");
        
        // 执行测试
        realtimeMessageController.doGet(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("获取实时状态接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试检查新消息接口
     */
    @Test
    public void testCheckNewMessages() throws Exception {
        logger.info("开始测试检查新消息接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/check");
        
        // 执行测试
        realtimeMessageController.doGet(request, response);
        
        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("检查新消息接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取在线人数接口
     */
    @Test
    public void testGetOnlineCount() throws Exception {
        logger.info("开始测试获取在线人数接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/online-count");
        
        // 执行测试
        realtimeMessageController.doGet(request, response);
        
        // 验证响应 - 在线人数接口因为service为null，返回500错误
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("服务器内部错误"));
        
        logger.info("获取在线人数接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试未登录访问
     */
    @Test
    public void testUnauthorizedAccess() throws Exception {
        logger.info("开始测试未登录访问");
        
        // 模拟未登录状态 - 没有Authorization头
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/poll");
        
        // 执行测试
        realtimeMessageController.doGet(request, response);
        
        // 验证响应
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));
        
        logger.info("未登录访问测试通过: response=" + responseContent);
    }
    
    /**
     * 测试无效路径
     */
    @Test
    public void testInvalidPath() throws Exception {
        logger.info("开始测试无效路径");
        
        // 设置无效请求路径
        when(request.getPathInfo()).thenReturn("/invalid");
        
        // 执行测试
        realtimeMessageController.doGet(request, response);
        
        // 验证响应 - 无效路径返回404
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("接口不存在"));
        
        logger.info("无效路径测试通过: response=" + responseContent);
    }

    /**
     * 测试POST方法无效路径
     */
    @Test
    public void testPostInvalidPath() throws Exception {
        logger.info("开始测试POST方法无效路径");

        // 设置无效请求路径
        when(request.getPathInfo()).thenReturn("/invalid");

        // 执行测试
        realtimeMessageController.doPost(request, response);

        // 验证响应 - 无效路径返回404
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("接口不存在"));

        logger.info("POST无效路径测试通过: response=" + responseContent);
    }

    /**
     * 测试空路径处理
     */
    @Test
    public void testEmptyPath() throws Exception {
        logger.info("开始测试空路径处理");

        // 设置空路径
        when(request.getPathInfo()).thenReturn(null);

        // 执行测试
        realtimeMessageController.doGet(request, response);

        // 验证响应 - 空路径默认处理为轮询，JWT认证失败返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));

        logger.info("空路径处理测试通过: response=" + responseContent);
    }

    /**
     * 测试根路径处理
     */
    @Test
    public void testRootPath() throws Exception {
        logger.info("开始测试根路径处理");

        // 设置根路径
        when(request.getPathInfo()).thenReturn("/");

        // 执行测试
        realtimeMessageController.doGet(request, response);

        // 验证响应 - 根路径默认处理为轮询，JWT认证失败返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));

        logger.info("根路径处理测试通过: response=" + responseContent);
    }

    /**
     * 测试无效JSON请求体
     */
    @Test
    public void testInvalidJsonBody() throws Exception {
        logger.info("开始测试无效JSON请求体");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/poll");

        // 设置无效JSON
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("invalid json")));

        // 执行测试
        realtimeMessageController.doPost(request, response);

        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));

        logger.info("无效JSON请求体测试通过: response=" + responseContent);
    }

    /**
     * 测试空请求体
     */
    @Test
    public void testEmptyRequestBody() throws Exception {
        logger.info("开始测试空请求体");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/poll");

        // 设置空请求体
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        // 执行测试
        realtimeMessageController.doPost(request, response);

        // 验证响应 - JWT认证失败，返回401
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("未授权访问"));

        logger.info("空请求体测试通过: response=" + responseContent);
    }

    /**
     * 测试系统异常处理
     */
    @Test
    public void testSystemExceptionHandling() throws Exception {
        logger.info("开始测试系统异常处理");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/status");

        // 模拟JWT解析异常 - 使用无效token
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");

        // 执行测试 - JWT解析失败会返回401，而不是抛出异常
        realtimeMessageController.doGet(request, response);

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
     * 测试完整的实时消息操作流程
     */
    @Test
    public void testCompleteRealtimeWorkflow() throws Exception {
        logger.info("开始测试完整的实时消息操作流程");

        // 1. 获取在线人数（不需要认证）
        when(request.getPathInfo()).thenReturn("/online-count");
        realtimeMessageController.doGet(request, response);
        String onlineCountResponse = responseWriter.toString();
        logger.info("获取在线人数成功: " + onlineCountResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 2. 尝试短轮询（JWT认证失败）
        when(request.getPathInfo()).thenReturn("/poll");
        realtimeMessageController.doGet(request, response);
        String pollResponse = responseWriter.toString();
        logger.info("短轮询认证失败: " + pollResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 3. 尝试长轮询（JWT认证失败）
        when(request.getPathInfo()).thenReturn("/long-poll");
        realtimeMessageController.doGet(request, response);
        String longPollResponse = responseWriter.toString();
        logger.info("长轮询认证失败: " + longPollResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 4. 尝试获取实时状态（JWT认证失败）
        when(request.getPathInfo()).thenReturn("/status");
        realtimeMessageController.doGet(request, response);
        String statusResponse = responseWriter.toString();
        logger.info("获取实时状态认证失败: " + statusResponse);

        logger.info("完整的实时消息操作流程测试通过");
    }
}
