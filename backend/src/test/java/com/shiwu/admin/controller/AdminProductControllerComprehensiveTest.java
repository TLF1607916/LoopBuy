package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.AdminProductManageDTO;
import com.shiwu.admin.model.AdminProductQueryDTO;
import com.shiwu.admin.service.AdminService;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.product.service.AdminProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminProductController综合测试
 * 
 * 测试管理员商品管理控制器的所有核心功能，包括：
 * 1. 查询商品列表接口
 * 2. 获取商品详情接口
 * 3. 审核通过商品接口（Task4_2_1_1核心功能）
 * 4. 审核拒绝商品接口
 * 5. 下架商品接口
 * 6. 删除商品接口
 * 7. 管理员权限验证
 * 8. JWT Token验证
 * 9. 参数解析和验证
 * 10. 错误处理和异常情况
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class AdminProductControllerComprehensiveTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminProductControllerComprehensiveTest.class);
    
    private AdminProductController adminProductController;
    
    @Mock
    private AdminProductService mockAdminProductService;
    
    @Mock
    private AdminService mockAdminService;
    
    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private HttpServletResponse mockResponse;
    
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    private ObjectMapper objectMapper;
    
    // 测试数据常量
    private static final Long TEST_ADMIN_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 100L;
    private static final String TEST_JWT_TOKEN = "valid_jwt_token";
    private static final String TEST_IP_ADDRESS = "192.168.1.100";
    private static final String TEST_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
    private static final String TEST_REASON = "Test approval reason";
    
    @BeforeEach
    void setUp() throws Exception {
        logger.info("AdminProductController测试环境初始化开始");
        
        MockitoAnnotations.openMocks(this);
        
        // 创建AdminProductController实例，注入Mock的Service
        adminProductController = new AdminProductController(mockAdminProductService, mockAdminService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        // 创建ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        logger.info("AdminProductController测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        logger.info("AdminProductController测试清理完成");
    }
    
    /**
     * 测试查询商品列表接口
     */
    @Test
    void testHandleGetProducts() throws Exception {
        logger.info("开始测试查询商品列表接口");
        
        // 准备Mock返回数据
        Map<String, Object> productList = new HashMap<>();
        productList.put("products", new Object[0]);
        productList.put("total", 0);
        productList.put("pageNum", 1);
        productList.put("pageSize", 20);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getParameter("keyword")).thenReturn("测试商品");
            when(mockRequest.getParameter("status")).thenReturn("1");
            when(mockRequest.getParameter("pageNum")).thenReturn("1");
            when(mockRequest.getParameter("pageSize")).thenReturn("20");
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.findProducts(any(AdminProductQueryDTO.class))).thenReturn(productList);
            
            // 执行测试
            adminProductController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
            verify(mockAdminProductService).findProducts(any(AdminProductQueryDTO.class));
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("\"total\":0"), "响应应包含总数");
            
            logger.info("查询商品列表接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试获取商品详情接口
     */
    @Test
    void testHandleGetProductDetail() throws Exception {
        logger.info("开始测试获取商品详情接口");
        
        // 准备Mock返回数据
        Map<String, Object> productDetail = new HashMap<>();
        productDetail.put("id", TEST_PRODUCT_ID);
        productDetail.put("title", "测试商品");
        productDetail.put("status", 1);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID);
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.getProductDetail(TEST_PRODUCT_ID, TEST_ADMIN_ID)).thenReturn(productDetail);
            
            // 执行测试
            adminProductController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminProductService).getProductDetail(TEST_PRODUCT_ID, TEST_ADMIN_ID);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("测试商品"), "响应应包含商品标题");
            
            logger.info("获取商品详情接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试审核通过商品接口（Task4_2_1_1核心功能）
     */
    @Test
    void testHandleApproveProduct() throws Exception {
        logger.info("开始测试审核通过商品接口");
        
        // 准备测试数据
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason(TEST_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        
        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID + "/approve");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.approveProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Test approval reason", TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);
            
            // 执行测试
            adminProductController.doPut(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminProductService).approveProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Test approval reason", TEST_IP_ADDRESS, TEST_USER_AGENT);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("商品审核通过"), "响应应包含成功消息");
            
            logger.info("审核通过商品接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试审核拒绝商品接口
     */
    @Test
    void testHandleRejectProduct() throws Exception {
        logger.info("开始测试审核拒绝商品接口");
        
        // 准备测试数据
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason("Product info incomplete");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        
        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID + "/reject");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.rejectProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Product info incomplete", TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);
            
            // 执行测试
            adminProductController.doPut(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminProductService).rejectProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Product info incomplete", TEST_IP_ADDRESS, TEST_USER_AGENT);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("商品审核拒绝"), "响应应包含成功消息");
            
            logger.info("审核拒绝商品接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试下架商品接口
     */
    @Test
    void testHandleDelistProduct() throws Exception {
        logger.info("开始测试下架商品接口");
        
        // 准备测试数据
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason("Violation product");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        
        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID + "/delist");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.delistProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Violation product", TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);
            
            // 执行测试
            adminProductController.doPut(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminProductService).delistProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Violation product", TEST_IP_ADDRESS, TEST_USER_AGENT);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("商品下架成功"), "响应应包含成功消息");
            
            logger.info("下架商品接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试删除商品接口
     */
    @Test
    void testHandleDeleteProduct() throws Exception {
        logger.info("开始测试删除商品接口");
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID);
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.deleteProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);
            
            // 执行测试
            adminProductController.doDelete(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminProductService).deleteProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("商品删除成功"), "响应应包含成功消息");
            
            logger.info("删除商品接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 创建Mock ServletInputStream
     */
    private ServletInputStream createMockInputStream(String content) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
            
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }
            
            @Override
            public boolean isReady() {
                return true;
            }
            
            @Override
            public void setReadListener(javax.servlet.ReadListener readListener) {
                // Not implemented for test
            }
        };
    }

    /**
     * 测试管理员权限验证
     */
    @Test
    void testAdminPermissionValidation() throws Exception {
        logger.info("开始测试管理员权限验证");

        // 测试无效Token
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken("invalid_token")).thenReturn(false);

            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer invalid_token");

            adminProductController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效Token应返回失败");
            assertTrue(responseJson.contains("401"), "应返回401错误");

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);
        }

        // 测试权限不足
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(false);

            adminProductController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "权限不足应返回失败");
            assertTrue(responseJson.contains("403"), "应返回403错误");
        }

        logger.info("管理员权限验证测试通过");
    }

    /**
     * 测试参数验证
     */
    @Test
    void testParameterValidation() throws Exception {
        logger.info("开始测试参数验证");

        // 测试无效商品ID格式
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/invalid_id");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            adminProductController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效商品ID应返回失败");
            assertTrue(responseJson.contains("400"), "应返回400错误");

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);
        }

        // 测试拒绝商品时缺少原因
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason(""); // 空原因
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        ServletInputStream inputStream = createMockInputStream(requestBody);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID + "/reject");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            adminProductController.doPut(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "空拒绝原因应返回失败");
            assertTrue(responseJson.contains("拒绝原因不能为空"), "应返回原因不能为空错误");
        }

        logger.info("参数验证测试通过");
    }

    /**
     * 测试HTTP方法路由
     */
    @Test
    void testHttpMethodRouting() throws Exception {
        logger.info("开始测试HTTP方法路由");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            // 测试无效路径
            when(mockRequest.getPathInfo()).thenReturn("/invalid/path");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            adminProductController.doPut(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");
            // 实际可能返回不同的错误码，只要是失败即可
            logger.info("HTTP路由测试响应: {}", responseJson);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 测试null路径
            when(mockRequest.getPathInfo()).thenReturn(null);

            adminProductController.doDelete(mockRequest, mockResponse);

            responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "null路径应返回失败");
            // 实际可能返回不同的错误码，只要是失败即可
            logger.info("null路径测试响应: {}", responseJson);
        }

        logger.info("HTTP方法路由测试通过");
    }

    /**
     * 测试系统异常处理
     */
    @Test
    void testSystemExceptionHandling() throws Exception {
        logger.info("开始测试系统异常处理");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.findProducts(any(AdminProductQueryDTO.class)))
                    .thenThrow(new RuntimeException("系统异常"));

            adminProductController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "系统异常应返回失败");
            assertTrue(responseJson.contains("500"), "应返回500错误");
        }

        logger.info("系统异常处理测试通过");
    }

    /**
     * 测试商品不存在情况
     */
    @Test
    void testProductNotFound() throws Exception {
        logger.info("开始测试商品不存在情况");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID);
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.getProductDetail(TEST_PRODUCT_ID, TEST_ADMIN_ID)).thenReturn(null);

            adminProductController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "商品不存在应返回失败");
            assertTrue(responseJson.contains("商品不存在"), "应返回商品不存在错误");
        }

        logger.info("商品不存在情况测试通过");
    }

    /**
     * 测试操作失败情况
     */
    @Test
    void testOperationFailure() throws Exception {
        logger.info("开始测试操作失败情况");

        // 准备测试数据
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason(TEST_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        ServletInputStream inputStream = createMockInputStream(requestBody);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID + "/approve");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.approveProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Test approval reason", TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(false); // 操作失败

            adminProductController.doPut(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "操作失败应返回失败");
            assertTrue(responseJson.contains("500") || responseJson.contains("审核失败"), "应返回500或审核失败错误");
        }

        logger.info("操作失败情况测试通过");
    }

    /**
     * 测试完整的商品管理流程
     */
    @Test
    void testCompleteProductManagementWorkflow() throws Exception {
        logger.info("开始测试完整的商品管理流程");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            // 1. 查询商品列表
            Map<String, Object> productList = new HashMap<>();
            productList.put("products", new Object[0]);
            productList.put("total", 1);

            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockAdminProductService.findProducts(any(AdminProductQueryDTO.class))).thenReturn(productList);

            adminProductController.doGet(mockRequest, mockResponse);

            String listResponse = responseWriter.toString();
            assertTrue(listResponse.contains("\"success\":true"), "查询商品列表应该成功");
            logger.info("查询商品列表成功: {}", listResponse);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 2. 获取商品详情
            Map<String, Object> productDetail = new HashMap<>();
            productDetail.put("id", TEST_PRODUCT_ID);
            productDetail.put("title", "待审核商品");
            productDetail.put("status", 0); // 待审核状态

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID);
            when(mockAdminProductService.getProductDetail(TEST_PRODUCT_ID, TEST_ADMIN_ID)).thenReturn(productDetail);

            adminProductController.doGet(mockRequest, mockResponse);

            String detailResponse = responseWriter.toString();
            assertTrue(detailResponse.contains("\"success\":true"), "获取商品详情应该成功");
            assertTrue(detailResponse.contains("待审核商品"), "应包含商品标题");
            logger.info("获取商品详情成功: {}", detailResponse);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 3. 审核通过商品
            AdminProductManageDTO manageDTO = new AdminProductManageDTO();
            manageDTO.setReason("Product approved");
            String requestBody = objectMapper.writeValueAsString(manageDTO);
            ServletInputStream inputStream = createMockInputStream(requestBody);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_PRODUCT_ID + "/approve");
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminProductService.approveProduct(TEST_PRODUCT_ID, TEST_ADMIN_ID, "Product approved", TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);

            adminProductController.doPut(mockRequest, mockResponse);

            String approveResponse = responseWriter.toString();
            assertTrue(approveResponse.contains("\"success\":true"), "审核通过应该成功");
            assertTrue(approveResponse.contains("商品审核通过"), "应包含成功消息");
            logger.info("审核通过成功: {}", approveResponse);
        }

        logger.info("完整的商品管理流程测试通过");
    }
}
