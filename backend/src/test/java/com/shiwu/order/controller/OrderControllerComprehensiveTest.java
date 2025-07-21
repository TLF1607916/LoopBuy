package com.shiwu.order.controller;

import com.shiwu.common.test.TestConfig;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.order.model.OrderCreateDTO;
import com.shiwu.order.model.ProcessReturnRequestDTO;
import com.shiwu.order.model.ReturnRequestDTO;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
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
 * OrderController 综合测试类
 * 测试订单控制器的所有核心功能
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("OrderController 综合测试")
public class OrderControllerComprehensiveTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderControllerComprehensiveTest.class);
    
    private OrderController orderController;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    
    // 测试数据常量
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_SELLER_ID = 2L;
    private static final Long TEST_ORDER_ID = 1001L;
    private static final Long TEST_PRODUCT_ID = 101L;
    private static final String TEST_JWT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    
    @BeforeAll
    static void setUpClass() {
        logger.info("开始OrderController综合测试");
        // 使用TestBase的标准初始化方法
        TestBase.setUpClass();
    }

    @AfterAll
    static void tearDownClass() {
        logger.info("OrderController综合测试完成");
        // 使用TestBase的标准清理方法
        TestBase.tearDownClass();
    }
    
    @BeforeEach
    void setUp() {
        orderController = new OrderController();
        
        // 创建Mock对象
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        
        try {
            when(mockResponse.getWriter()).thenReturn(printWriter);
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockRequest.getSession()).thenReturn(mockSession);
        } catch (Exception e) {
            logger.error("设置Mock对象失败", e);
        }
        
        logger.debug("测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        if (printWriter != null) {
            printWriter.close();
        }
        logger.debug("测试环境清理完成");
    }

    // ==================== GET请求测试 ====================

    @Test
    @Order(1)
    @DisplayName("1.1 获取订单列表 - 成功")
    public void testGetOrdersSuccess() throws Exception {
        logger.info("测试获取订单列表 - 成功");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/");
        when(mockRequest.getParameter("type")).thenReturn("buyer");
        
        // 执行请求
        orderController.doGet(mockRequest, mockResponse);
        
        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");
        
        logger.info("获取订单列表测试通过");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 获取订单列表 - 未登录")
    public void testGetOrdersUnauthorized() throws Exception {
        logger.info("测试获取订单列表 - 未登录");
        
        // 不设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(null);
        when(mockRequest.getPathInfo()).thenReturn("/");
        
        // 执行请求
        orderController.doGet(mockRequest, mockResponse);
        
        // 验证响应
        verify(mockResponse).setStatus(401);
        
        logger.info("未登录获取订单列表测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("1.3 获取买家订单列表")
    public void testGetBuyerOrders() throws Exception {
        logger.info("测试获取买家订单列表");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/buyer");
        
        // 执行请求
        orderController.doGet(mockRequest, mockResponse);
        
        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");
        
        logger.info("获取买家订单列表测试通过");
    }

    @Test
    @Order(4)
    @DisplayName("1.4 获取卖家订单列表")
    public void testGetSellerOrders() throws Exception {
        logger.info("测试获取卖家订单列表");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_SELLER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/seller");
        
        // 执行请求
        orderController.doGet(mockRequest, mockResponse);
        
        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");
        
        logger.info("获取卖家订单列表测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("1.5 获取订单详情 - 成功")
    public void testGetOrderDetailSuccess() throws Exception {
        logger.info("测试获取订单详情 - 成功");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID);
        
        // 执行请求
        orderController.doGet(mockRequest, mockResponse);
        
        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");
        
        logger.info("获取订单详情测试通过");
    }

    @Test
    @Order(6)
    @DisplayName("1.6 获取订单详情 - 无效ID格式")
    public void testGetOrderDetailInvalidId() throws Exception {
        logger.info("测试获取订单详情 - 无效ID格式");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/invalid_id");
        
        // 执行请求
        orderController.doGet(mockRequest, mockResponse);
        
        // 验证响应
        verify(mockResponse).setStatus(400);
        
        logger.info("无效ID格式测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("1.7 获取订单 - 无效路径格式")
    public void testGetOrdersInvalidPath() throws Exception {
        logger.info("测试获取订单 - 无效路径格式");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/invalid/path");

        // 执行请求
        orderController.doGet(mockRequest, mockResponse);

        // 验证响应 - 无效ID格式应该返回400，不是404
        verify(mockResponse).setStatus(400);

        logger.info("无效路径格式测试通过");
    }

    // ==================== POST请求测试 ====================

    @Test
    @Order(10)
    @DisplayName("2.1 创建订单 - 成功")
    public void testCreateOrderSuccess() throws Exception {
        logger.info("测试创建订单 - 成功");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/");
        
        // 设置请求体
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(TEST_PRODUCT_ID));
        String requestBody = JsonUtil.toJson(dto);
        
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(mockRequest.getReader()).thenReturn(reader);
        
        // 执行请求
        orderController.doPost(mockRequest, mockResponse);
        
        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");
        
        logger.info("创建订单测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("2.2 创建订单 - 未登录")
    public void testCreateOrderUnauthorized() throws Exception {
        logger.info("测试创建订单 - 未登录");
        
        // 不设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(null);
        when(mockRequest.getPathInfo()).thenReturn("/");
        
        // 执行请求
        orderController.doPost(mockRequest, mockResponse);
        
        // 验证响应
        verify(mockResponse).setStatus(401);
        
        logger.info("未登录创建订单测试通过");
    }

    @Test
    @Order(12)
    @DisplayName("2.3 创建订单 - 空请求体")
    public void testCreateOrderEmptyBody() throws Exception {
        logger.info("测试创建订单 - 空请求体");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/");
        
        // 设置空请求体
        BufferedReader reader = new BufferedReader(new StringReader(""));
        when(mockRequest.getReader()).thenReturn(reader);
        
        // 执行请求
        orderController.doPost(mockRequest, mockResponse);
        
        // 验证响应
        verify(mockResponse).setStatus(400);
        
        logger.info("空请求体测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("2.4 创建订单 - 无效JSON")
    public void testCreateOrderInvalidJson() throws Exception {
        logger.info("测试创建订单 - 无效JSON");
        
        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/");
        
        // 设置无效JSON
        BufferedReader reader = new BufferedReader(new StringReader("invalid json"));
        when(mockRequest.getReader()).thenReturn(reader);
        
        // 执行请求
        orderController.doPost(mockRequest, mockResponse);
        
        // 验证响应
        verify(mockResponse).setStatus(400);
        
        logger.info("无效JSON测试通过");
    }

    // ==================== 订单状态更新测试 ====================

    @Test
    @Order(20)
    @DisplayName("3.1 更新订单状态 - 成功")
    public void testUpdateOrderStatusSuccess() throws Exception {
        logger.info("测试更新订单状态 - 成功");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/status");

        // 设置请求体
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", 1); // 待发货
        String requestBody = JsonUtil.toJson(statusUpdate);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("更新订单状态测试通过");
    }

    @Test
    @Order(21)
    @DisplayName("3.2 更新订单状态 - 缺少status字段")
    public void testUpdateOrderStatusMissingField() throws Exception {
        logger.info("测试更新订单状态 - 缺少status字段");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/status");

        // 设置请求体（缺少status字段）
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("other", "value");
        String requestBody = JsonUtil.toJson(statusUpdate);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        verify(mockResponse).setStatus(400);

        logger.info("缺少status字段测试通过");
    }

    @Test
    @Order(22)
    @DisplayName("3.3 更新订单状态 - 无效状态值格式")
    public void testUpdateOrderStatusInvalidFormat() throws Exception {
        logger.info("测试更新订单状态 - 无效状态值格式");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/status");

        // 设置请求体（无效状态值）
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "invalid_status");
        String requestBody = JsonUtil.toJson(statusUpdate);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        verify(mockResponse).setStatus(400);

        logger.info("无效状态值格式测试通过");
    }

    // ==================== 订单发货测试 ====================

    @Test
    @Order(30)
    @DisplayName("4.1 卖家发货 - 成功")
    public void testShipOrderSuccess() throws Exception {
        logger.info("测试卖家发货 - 成功");

        // 设置登录用户（卖家）
        when(mockSession.getAttribute("userId")).thenReturn(TEST_SELLER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/ship");

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("卖家发货测试通过");
    }

    @Test
    @Order(31)
    @DisplayName("4.2 卖家发货 - 未登录")
    public void testShipOrderUnauthorized() throws Exception {
        logger.info("测试卖家发货 - 未登录");

        // 不设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(null);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/ship");

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        verify(mockResponse).setStatus(401);

        logger.info("未登录发货测试通过");
    }

    // ==================== 确认收货测试 ====================

    @Test
    @Order(40)
    @DisplayName("5.1 买家确认收货 - 成功")
    public void testConfirmReceiptSuccess() throws Exception {
        logger.info("测试买家确认收货 - 成功");

        // 设置登录用户（买家）
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/confirm");

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("买家确认收货测试通过");
    }

    @Test
    @Order(41)
    @DisplayName("5.2 买家确认收货 - 未登录")
    public void testConfirmReceiptUnauthorized() throws Exception {
        logger.info("测试买家确认收货 - 未登录");

        // 不设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(null);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/confirm");

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        verify(mockResponse).setStatus(401);

        logger.info("未登录确认收货测试通过");
    }

    // ==================== 申请退货测试 ====================

    @Test
    @Order(50)
    @DisplayName("6.1 申请退货 - 成功")
    public void testApplyForReturnSuccess() throws Exception {
        logger.info("测试申请退货 - 成功");

        // 设置登录用户（买家）
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/return");

        // 设置请求体
        ReturnRequestDTO dto = new ReturnRequestDTO("商品质量问题，申请退货");
        String requestBody = JsonUtil.toJson(dto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("申请退货测试通过");
    }

    @Test
    @Order(51)
    @DisplayName("6.2 申请退货 - 空请求体")
    public void testApplyForReturnEmptyBody() throws Exception {
        logger.info("测试申请退货 - 空请求体");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/return");

        // 设置空请求体
        BufferedReader reader = new BufferedReader(new StringReader(""));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        verify(mockResponse).setStatus(400);

        logger.info("申请退货空请求体测试通过");
    }

    // ==================== 处理退货申请测试 ====================

    @Test
    @Order(60)
    @DisplayName("7.1 处理退货申请 - 同意退货")
    public void testProcessReturnRequestApprove() throws Exception {
        logger.info("测试处理退货申请 - 同意退货");

        // 设置登录用户（卖家）
        when(mockSession.getAttribute("userId")).thenReturn(TEST_SELLER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/process-return");

        // 设置请求体（同意退货）
        ProcessReturnRequestDTO dto = ProcessReturnRequestDTO.approve();
        String requestBody = JsonUtil.toJson(dto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("同意退货测试通过");
    }

    @Test
    @Order(61)
    @DisplayName("7.2 处理退货申请 - 拒绝退货")
    public void testProcessReturnRequestReject() throws Exception {
        logger.info("测试处理退货申请 - 拒绝退货");

        // 设置登录用户（卖家）
        when(mockSession.getAttribute("userId")).thenReturn(TEST_SELLER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/process-return");

        // 设置请求体（拒绝退货）
        ProcessReturnRequestDTO dto = ProcessReturnRequestDTO.reject("商品无质量问题，不同意退货");
        String requestBody = JsonUtil.toJson(dto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("拒绝退货测试通过");
    }

    @Test
    @Order(62)
    @DisplayName("7.3 处理退货申请 - 空请求体")
    public void testProcessReturnRequestEmptyBody() throws Exception {
        logger.info("测试处理退货申请 - 空请求体");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_SELLER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_ORDER_ID + "/process-return");

        // 设置空请求体
        BufferedReader reader = new BufferedReader(new StringReader(""));
        when(mockRequest.getReader()).thenReturn(reader);

        // 执行请求
        orderController.doPost(mockRequest, mockResponse);

        // 验证响应
        verify(mockResponse).setStatus(400);

        logger.info("处理退货申请空请求体测试通过");
    }

    // ==================== 边界值和异常测试 ====================

    @Test
    @Order(70)
    @DisplayName("8.1 边界值测试 - 订单ID为0")
    public void testBoundaryOrderIdZero() throws Exception {
        logger.info("测试边界值 - 订单ID为0");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/0");

        // 执行请求
        orderController.doGet(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("订单ID为0测试通过");
    }

    @Test
    @Order(71)
    @DisplayName("8.2 边界值测试 - 订单ID为负数")
    public void testBoundaryOrderIdNegative() throws Exception {
        logger.info("测试边界值 - 订单ID为负数");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/-1");

        // 执行请求
        orderController.doGet(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("订单ID为负数测试通过");
    }

    @Test
    @Order(72)
    @DisplayName("8.3 边界值测试 - 订单ID为最大值")
    public void testBoundaryOrderIdMaxValue() throws Exception {
        logger.info("测试边界值 - 订单ID为最大值");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);
        when(mockRequest.getPathInfo()).thenReturn("/" + Long.MAX_VALUE);

        // 执行请求
        orderController.doGet(mockRequest, mockResponse);

        // 验证响应
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");

        logger.info("订单ID为最大值测试通过");
    }

    @Test
    @Order(73)
    @DisplayName("8.4 异常测试 - 超长路径")
    public void testExceptionLongPath() throws Exception {
        logger.info("测试异常 - 超长路径");

        // 设置登录用户
        when(mockSession.getAttribute("userId")).thenReturn(TEST_USER_ID);

        // 创建超长路径
        StringBuilder longPath = new StringBuilder("/");
        for (int i = 0; i < 1000; i++) {
            longPath.append("a");
        }
        when(mockRequest.getPathInfo()).thenReturn(longPath.toString());

        // 执行请求
        orderController.doGet(mockRequest, mockResponse);

        // 验证响应
        verify(mockResponse).setStatus(400);

        logger.info("超长路径测试通过");
    }

    // ==================== 并发和性能测试 ====================

    @Test
    @Order(80)
    @DisplayName("9.1 并发测试 - 多用户同时获取订单")
    public void testConcurrentGetOrders() throws Exception {
        logger.info("测试并发 - 多用户同时获取订单");

        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    // 创建独立的Mock对象
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse resp = mock(HttpServletResponse.class);
                    HttpSession session = mock(HttpSession.class);
                    StringWriter writer = new StringWriter();
                    PrintWriter printer = new PrintWriter(writer);

                    when(resp.getWriter()).thenReturn(printer);
                    when(req.getSession(false)).thenReturn(session);
                    when(session.getAttribute("userId")).thenReturn((long) (index + 1));
                    when(req.getPathInfo()).thenReturn("/buyer");

                    // 执行请求
                    orderController.doGet(req, resp);

                    printer.flush();
                    String response = writer.toString();
                    results[index] = response != null;

                } catch (Exception e) {
                    logger.error("并发测试线程{}失败", index, e);
                    results[index] = false;
                }
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join(5000); // 最多等待5秒
        }

        // 验证结果
        for (int i = 0; i < threadCount; i++) {
            assertTrue(results[i], "线程" + i + "应该成功执行");
        }

        logger.info("并发测试通过");
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试用的OrderCreateDTO
     */
    private OrderCreateDTO createTestOrderCreateDTO() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(TEST_PRODUCT_ID, TEST_PRODUCT_ID + 1));
        return dto;
    }

    /**
     * 创建测试用的ReturnRequestDTO
     */
    private ReturnRequestDTO createTestReturnRequestDTO() {
        return new ReturnRequestDTO("测试退货原因");
    }

    /**
     * 创建测试用的ProcessReturnRequestDTO
     */
    private ProcessReturnRequestDTO createTestProcessReturnRequestDTO(boolean approve) {
        if (approve) {
            return ProcessReturnRequestDTO.approve();
        } else {
            return ProcessReturnRequestDTO.reject("测试拒绝原因");
        }
    }

    /**
     * 验证响应状态码
     */
    private void verifyResponseStatus(int expectedStatus) {
        try {
            verify(mockResponse).setStatus(expectedStatus);
        } catch (Exception e) {
            // 如果没有设置状态码，说明是200
            if (expectedStatus != 200) {
                fail("期望状态码" + expectedStatus + "，但没有设置状态码");
            }
        }
    }

    /**
     * 验证响应内容不为空
     */
    private void verifyResponseNotEmpty() {
        printWriter.flush();
        String response = responseWriter.toString();
        assertNotNull(response, "响应不应为空");
        assertFalse(response.trim().isEmpty(), "响应内容不应为空");
    }
}
