package com.shiwu.payment.controller;

import com.shiwu.common.util.JsonUtil;
import com.shiwu.payment.model.*;
import com.shiwu.payment.service.PaymentService;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * PaymentController综合测试类
 */
public class PaymentControllerComprehensiveTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerComprehensiveTest.class);
    
    private PaymentController paymentController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;
    private PaymentService mockPaymentService;
    
    @BeforeEach
    public void setUp() {
        logger.info("PaymentController测试环境初始化开始");
        super.setUp();
        
        // 创建Mock对象
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        mockPaymentService = mock(PaymentService.class);
        
        // 创建PaymentController实例，使用Mock service
        paymentController = new PaymentController(mockPaymentService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (Exception e) {
            fail("设置响应Writer失败: " + e.getMessage());
        }
        
        // 设置默认的session行为
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3001L);
        
        logger.info("PaymentController测试环境初始化完成");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("PaymentController测试清理完成");
    }
    
    /**
     * 测试创建支付接口 - 成功
     */
    @Test
    public void testCreatePayment() throws Exception {
        logger.info("开始测试创建支付接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");
        
        // 设置请求体
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(1001L, 1002L));
        dto.setTotalAmount(new BigDecimal("299.99"));
        dto.setPaymentMethod(1); // 支付宝
        
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 模拟service返回成功
        PaymentVO paymentVO = createMockPaymentVO();
        when(mockPaymentService.createPayment(any(PaymentDTO.class), eq(3001L)))
            .thenReturn(PaymentOperationResult.success(paymentVO));
        
        // 执行测试
        paymentController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"paymentId\":\"PAY_"));
        
        logger.info("创建支付接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试创建支付接口 - 未登录
     */
    @Test
    public void testCreatePaymentNotLoggedIn() throws Exception {
        logger.info("开始测试创建支付接口 - 未登录");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");
        
        // 模拟未登录状态
        when(request.getSession(false)).thenReturn(null);
        
        // 设置请求体
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(1001L));
        dto.setTotalAmount(new BigDecimal("99.99"));
        dto.setPaymentMethod(1);
        
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 执行测试
        paymentController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("用户未登录"));
        
        logger.info("未登录创建支付测试通过: response=" + responseContent);
    }
    
    /**
     * 测试创建支付接口 - 请求参数为空
     */
    @Test
    public void testCreatePaymentEmptyParams() throws Exception {
        logger.info("开始测试创建支付接口 - 请求参数为空");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");
        
        // 设置空请求体
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        
        // 执行测试
        paymentController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("系统错误，请稍后重试"));
        
        logger.info("空参数创建支付测试通过: response=" + responseContent);
    }
    
    /**
     * 测试处理支付接口 - 成功
     */
    @Test
    public void testProcessPayment() throws Exception {
        logger.info("开始测试处理支付接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/process");
        
        // 设置请求体
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("paymentId", "PAY_20231220_001");
        requestMap.put("paymentPassword", "123456");
        
        String requestBody = JsonUtil.toJson(requestMap);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 模拟service返回成功
        PaymentVO paymentVO = createMockPaymentVO();
        paymentVO.setPaymentStatus(2); // 支付成功
        paymentVO.setPaymentStatusText("支付成功");
        when(mockPaymentService.processPayment("PAY_20231220_001", "123456", 3001L))
            .thenReturn(PaymentOperationResult.success(paymentVO));
        
        // 执行测试
        paymentController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("支付成功"));
        
        logger.info("处理支付接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试处理支付接口 - 参数不完整
     */
    @Test
    public void testProcessPaymentIncompleteParams() throws Exception {
        logger.info("开始测试处理支付接口 - 参数不完整");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/process");
        
        // 设置不完整的请求体（缺少paymentPassword）
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("paymentId", "PAY_20231220_001");
        
        String requestBody = JsonUtil.toJson(requestMap);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 执行测试
        paymentController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求参数不完整"));
        
        logger.info("参数不完整处理支付测试通过: response=" + responseContent);
    }
    
    /**
     * 测试查询支付状态接口 - 成功
     */
    @Test
    public void testGetPaymentStatus() throws Exception {
        logger.info("开始测试查询支付状态接口");
        
        // 设置请求路径和参数
        when(request.getPathInfo()).thenReturn("/status");
        when(request.getParameter("paymentId")).thenReturn("PAY_20231220_001");
        
        // 模拟service返回成功
        PaymentVO paymentVO = createMockPaymentVO();
        when(mockPaymentService.getPaymentStatus("PAY_20231220_001", 3001L))
            .thenReturn(PaymentOperationResult.success(paymentVO));
        
        // 执行测试
        paymentController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"paymentId\":\"PAY_"));
        
        logger.info("查询支付状态接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试查询支付状态接口 - 支付ID为空
     */
    @Test
    public void testGetPaymentStatusEmptyId() throws Exception {
        logger.info("开始测试查询支付状态接口 - 支付ID为空");
        
        // 设置请求路径，不设置paymentId参数
        when(request.getPathInfo()).thenReturn("/status");
        when(request.getParameter("paymentId")).thenReturn(null);
        
        // 执行测试
        paymentController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("支付ID不能为空"));
        
        logger.info("支付ID为空查询状态测试通过: response=" + responseContent);
    }
    
    /**
     * 创建模拟PaymentVO对象
     */
    private PaymentVO createMockPaymentVO() {
        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setPaymentId("PAY_20231220_001");
        paymentVO.setOrderIds(Arrays.asList(1001L, 1002L));
        paymentVO.setPaymentStatus(1); // 待支付
        paymentVO.setPaymentStatusText("待支付");
        paymentVO.setPaymentAmount(new BigDecimal("299.99"));
        paymentVO.setPaymentMethod(1);
        paymentVO.setPaymentMethodText("支付宝");
        paymentVO.setPaymentUrl("/payment/page?paymentId=PAY_20231220_001");
        paymentVO.setExpireTime(LocalDateTime.now().plusMinutes(15));
        return paymentVO;
    }

    /**
     * 测试取消支付接口 - 成功
     */
    @Test
    public void testCancelPayment() throws Exception {
        logger.info("开始测试取消支付接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/cancel");

        // 设置请求体
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("paymentId", "PAY_20231220_001");

        String requestBody = JsonUtil.toJson(requestMap);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 模拟service返回成功
        when(mockPaymentService.cancelPayment("PAY_20231220_001", 3001L))
            .thenReturn(PaymentOperationResult.success("取消支付成功"));

        // 执行测试
        paymentController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));

        logger.info("取消支付接口测试通过: response=" + responseContent);
    }

    /**
     * 测试取消支付接口 - 支付ID为空
     */
    @Test
    public void testCancelPaymentEmptyId() throws Exception {
        logger.info("开始测试取消支付接口 - 支付ID为空");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/cancel");

        // 设置空的请求体
        Map<String, Object> requestMap = new HashMap<>();

        String requestBody = JsonUtil.toJson(requestMap);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 执行测试
        paymentController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("支付ID不能为空"));

        logger.info("支付ID为空取消支付测试通过: response=" + responseContent);
    }

    /**
     * 测试获取用户支付记录接口 - 成功
     */
    @Test
    public void testGetUserPayments() throws Exception {
        logger.info("开始测试获取用户支付记录接口");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 模拟service返回成功
        when(mockPaymentService.getUserPayments(3001L))
            .thenReturn(PaymentOperationResult.success(Arrays.asList(createMockPaymentVO())));

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"data\":["));

        logger.info("获取用户支付记录接口测试通过: response=" + responseContent);
    }

    /**
     * 测试获取用户支付记录接口 - 未登录
     */
    @Test
    public void testGetUserPaymentsNotLoggedIn() throws Exception {
        logger.info("开始测试获取用户支付记录接口 - 未登录");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 模拟未登录状态
        when(request.getSession(false)).thenReturn(null);

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("用户未登录"));

        logger.info("未登录获取支付记录测试通过: response=" + responseContent);
    }

    /**
     * 测试根据订单ID获取支付信息接口 - 成功
     */
    @Test
    public void testGetPaymentByOrderIds() throws Exception {
        logger.info("开始测试根据订单ID获取支付信息接口");

        // 设置请求路径和参数
        when(request.getPathInfo()).thenReturn("/by-orders");
        when(request.getParameter("orderIds")).thenReturn("[1001,1002]");

        // 模拟service返回成功
        when(mockPaymentService.getPaymentByOrderIds(any(), eq(3001L)))
            .thenReturn(PaymentOperationResult.success(Arrays.asList(createMockPaymentVO())));

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));

        logger.info("根据订单ID获取支付信息接口测试通过: response=" + responseContent);
    }

    /**
     * 测试根据订单ID获取支付信息接口 - 订单ID列表为空
     */
    @Test
    public void testGetPaymentByOrderIdsEmpty() throws Exception {
        logger.info("开始测试根据订单ID获取支付信息接口 - 订单ID列表为空");

        // 设置请求路径，不设置orderIds参数
        when(request.getPathInfo()).thenReturn("/by-orders");
        when(request.getParameter("orderIds")).thenReturn(null);

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("订单ID列表不能为空"));

        logger.info("订单ID列表为空测试通过: response=" + responseContent);
    }

    /**
     * 测试支付页面接口 - 成功
     */
    @Test
    public void testGetPaymentPage() throws Exception {
        logger.info("开始测试支付页面接口");

        // 设置请求路径和参数
        when(request.getPathInfo()).thenReturn("/page");
        when(request.getParameter("paymentId")).thenReturn("PAY_20231220_001");

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"paymentId\":\"PAY_20231220_001\""));
        assertTrue(responseContent.contains("支付页面"));

        logger.info("支付页面接口测试通过: response=" + responseContent);
    }

    /**
     * 测试支付页面接口 - 支付ID为空
     */
    @Test
    public void testGetPaymentPageEmptyId() throws Exception {
        logger.info("开始测试支付页面接口 - 支付ID为空");

        // 设置请求路径，不设置paymentId参数
        when(request.getPathInfo()).thenReturn("/page");
        when(request.getParameter("paymentId")).thenReturn(null);

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("支付ID不能为空"));

        logger.info("支付ID为空获取支付页面测试通过: response=" + responseContent);
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
        paymentController.doGet(request, response);

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
        paymentController.doPost(request, response);

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
     * 测试空路径 - GET
     */
    @Test
    public void testNullPathGet() throws Exception {
        logger.info("开始测试空路径 - GET");

        // 设置空请求路径
        when(request.getPathInfo()).thenReturn(null);

        // 模拟service返回成功
        when(mockPaymentService.getUserPayments(3001L))
            .thenReturn(PaymentOperationResult.success(Arrays.asList()));

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));

        logger.info("空路径GET测试通过: response=" + responseContent);
    }

    /**
     * 测试空路径 - POST
     */
    @Test
    public void testNullPathPost() throws Exception {
        logger.info("开始测试空路径 - POST");

        // 设置空请求路径
        when(request.getPathInfo()).thenReturn(null);

        // 设置请求体
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(1001L));
        dto.setTotalAmount(new BigDecimal("99.99"));
        dto.setPaymentMethod(1);

        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 模拟service返回成功
        PaymentVO paymentVO = createMockPaymentVO();
        when(mockPaymentService.createPayment(any(PaymentDTO.class), eq(3001L)))
            .thenReturn(PaymentOperationResult.success(paymentVO));

        // 执行测试
        paymentController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));

        logger.info("空路径POST测试通过: response=" + responseContent);
    }

    /**
     * 测试Service异常处理
     */
    @Test
    public void testServiceException() throws Exception {
        logger.info("开始测试Service异常处理");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 模拟service抛出异常
        when(mockPaymentService.getUserPayments(3001L))
            .thenThrow(new RuntimeException("数据库连接失败"));

        // 执行测试
        paymentController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("系统错误，请稍后重试"));

        logger.info("Service异常处理测试通过: response=" + responseContent);
    }

    /**
     * 测试JSON解析异常
     */
    @Test
    public void testJsonParseException() throws Exception {
        logger.info("开始测试JSON解析异常");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 设置无效JSON
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("invalid json")));

        // 执行测试
        paymentController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("系统错误，请稍后重试"));

        logger.info("JSON解析异常测试通过: response=" + responseContent);
    }

    /**
     * 测试完整的支付流程
     */
    @Test
    public void testCompletePaymentWorkflow() throws Exception {
        logger.info("开始测试完整的支付流程");

        // 1. 创建支付
        when(request.getPathInfo()).thenReturn("/");
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(1001L, 1002L));
        dto.setTotalAmount(new BigDecimal("299.99"));
        dto.setPaymentMethod(1);

        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        PaymentVO paymentVO = createMockPaymentVO();
        when(mockPaymentService.createPayment(any(PaymentDTO.class), eq(3001L)))
            .thenReturn(PaymentOperationResult.success(paymentVO));

        paymentController.doPost(request, response);
        String createResponse = responseWriter.toString();
        logger.info("创建支付成功: " + createResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 2. 查询支付状态
        when(request.getPathInfo()).thenReturn("/status");
        when(request.getParameter("paymentId")).thenReturn("PAY_20231220_001");
        when(mockPaymentService.getPaymentStatus("PAY_20231220_001", 3001L))
            .thenReturn(PaymentOperationResult.success(paymentVO));

        paymentController.doGet(request, response);
        String statusResponse = responseWriter.toString();
        logger.info("查询支付状态成功: " + statusResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 3. 处理支付
        when(request.getPathInfo()).thenReturn("/process");
        Map<String, Object> processMap = new HashMap<>();
        processMap.put("paymentId", "PAY_20231220_001");
        processMap.put("paymentPassword", "123456");

        String processBody = JsonUtil.toJson(processMap);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(processBody)));

        paymentVO.setPaymentStatus(2);
        paymentVO.setPaymentStatusText("支付成功");
        when(mockPaymentService.processPayment("PAY_20231220_001", "123456", 3001L))
            .thenReturn(PaymentOperationResult.success(paymentVO));

        paymentController.doPost(request, response);
        String processResponse = responseWriter.toString();
        logger.info("处理支付成功: " + processResponse);

        // 验证所有响应都成功
        assertTrue(createResponse.contains("\"success\":true"));
        assertTrue(statusResponse.contains("\"success\":true"));
        assertTrue(processResponse.contains("\"success\":true"));
        assertTrue(processResponse.contains("支付成功"));

        logger.info("完整的支付流程测试通过");
    }
}
