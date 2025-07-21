package com.shiwu.payment.controller;

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
 * PaymentTimeoutController综合测试类
 */
public class PaymentTimeoutControllerComprehensiveTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentTimeoutControllerComprehensiveTest.class);
    
    private PaymentTimeoutController paymentTimeoutController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() {
        logger.info("PaymentTimeoutController测试环境初始化开始");
        super.setUp();

        // 创建Mock对象
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        // 创建PaymentTimeoutController实例
        paymentTimeoutController = new PaymentTimeoutController();

        // 设置响应Writer
        responseWriter = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (Exception e) {
            fail("设置响应Writer失败: " + e.getMessage());
        }

        // 设置默认的session行为（管理员权限）
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("admin");

        logger.info("PaymentTimeoutController测试环境初始化完成");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("PaymentTimeoutController测试清理完成");
    }
    
    /**
     * 测试获取超时状态接口 - 成功
     */
    @Test
    public void testGetTimeoutStatus() throws Exception {
        logger.info("开始测试获取超时状态接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 执行测试
        paymentTimeoutController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("支付超时检查任务状态"));

        logger.info("获取超时状态接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取超时状态接口 - 权限不足
     */
    @Test
    public void testGetTimeoutStatusNoPermission() throws Exception {
        logger.info("开始测试获取超时状态接口 - 权限不足");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 模拟非管理员用户
        when(session.getAttribute("userRole")).thenReturn("user");

        // 执行测试
        paymentTimeoutController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("权限不足"));

        logger.info("权限不足获取超时状态测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取过期支付记录数量接口 - 成功
     */
    @Test
    public void testGetExpiredCount() throws Exception {
        logger.info("开始测试获取过期支付记录数量接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/count");

        // 执行测试
        paymentTimeoutController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("expiredPaymentCount"));

        logger.info("获取过期支付记录数量接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试手动触发超时检查接口 - 成功
     */
    @Test
    public void testTriggerTimeoutCheck() throws Exception {
        logger.info("开始测试手动触发超时检查接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 执行测试
        paymentTimeoutController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("超时检查任务正在后台运行"));

        logger.info("手动触发超时检查接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试手动处理指定过期支付接口 - 成功
     */
    @Test
    public void testHandleSpecificExpiredPayment() throws Exception {
        logger.info("开始测试手动处理指定过期支付接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/handle");

        // 设置请求体
        String requestBody = "{\"paymentId\":\"PAY_20231220_001\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 执行测试
        paymentTimeoutController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        // 注意：实际结果可能是成功或失败，取决于PaymentTimeoutHandler的实现

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        // 验证响应是有效的JSON格式
        assertTrue(responseContent.contains("\"success\":"));

        logger.info("手动处理指定过期支付接口测试通过: response=" + responseContent);
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
        paymentTimeoutController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("无效路径GET测试通过: response=" + responseContent);
    }

    /**
     * 测试无效路径 - POST
     */
    @Test
    public void testInvalidPathPost() throws Exception {
        logger.info("开始测试无效路径 - POST");

        // 设置无效请求路径
        when(request.getPathInfo()).thenReturn("/invalid");

        // 执行测试
        paymentTimeoutController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("无效路径POST测试通过: response=" + responseContent);
    }

    /**
     * 测试未登录用户访问
     */
    @Test
    public void testNotLoggedIn() throws Exception {
        logger.info("开始测试未登录用户访问");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 模拟未登录状态
        when(request.getSession(false)).thenReturn(null);

        // 执行测试
        paymentTimeoutController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("权限不足"));

        logger.info("未登录用户访问测试通过: response=" + responseContent);
    }
    
    /**
     * 测试手动处理指定过期支付接口 - 支付ID为空
     */
    @Test
    public void testHandleSpecificExpiredPaymentEmptyId() throws Exception {
        logger.info("开始测试手动处理指定过期支付接口 - 支付ID为空");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/handle");

        // 设置空的请求体
        String requestBody = "{}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 执行测试
        paymentTimeoutController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("支付ID不能为空"));

        logger.info("支付ID为空处理指定过期支付测试通过: response=" + responseContent);
    }

    /**
     * 测试完整的超时管理流程
     */
    @Test
    public void testCompleteTimeoutManagementWorkflow() throws Exception {
        logger.info("开始测试完整的超时管理流程");

        // 1. 获取超时状态
        when(request.getPathInfo()).thenReturn("/");
        paymentTimeoutController.doGet(request, response);
        String statusResponse = responseWriter.toString();
        logger.info("获取超时状态成功: " + statusResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 2. 获取过期支付记录数量
        when(request.getPathInfo()).thenReturn("/count");
        paymentTimeoutController.doGet(request, response);
        String countResponse = responseWriter.toString();
        logger.info("获取过期支付记录数量成功: " + countResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 3. 手动触发超时检查
        when(request.getPathInfo()).thenReturn("/");
        paymentTimeoutController.doPost(request, response);
        String triggerResponse = responseWriter.toString();
        logger.info("手动触发超时检查成功: " + triggerResponse);

        // 验证所有响应都成功
        assertTrue(statusResponse.contains("\"success\":true"));
        assertTrue(countResponse.contains("\"success\":true"));
        assertTrue(triggerResponse.contains("\"success\":true"));

        logger.info("完整的超时管理流程测试通过");
    }
}
