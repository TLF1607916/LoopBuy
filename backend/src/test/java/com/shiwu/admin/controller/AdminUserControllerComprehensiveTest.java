package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.AdminUserManageDTO;
import com.shiwu.admin.model.AdminUserQueryDTO;
import com.shiwu.admin.service.AdminService;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.user.service.AdminUserService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminUserController综合测试
 * 
 * 测试管理员用户管理控制器的所有核心功能，包括：
 * 1. 查询用户列表接口
 * 2. 获取用户详情接口
 * 3. 封禁用户接口（Task4_2_1_2核心功能）
 * 4. 禁言用户接口
 * 5. 解封用户接口
 * 6. 解除禁言接口
 * 7. 批量封禁用户接口
 * 8. 批量禁言用户接口
 * 9. 管理员权限验证
 * 10. JWT Token验证
 * 11. 参数解析和验证
 * 12. 错误处理和异常情况
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class AdminUserControllerComprehensiveTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminUserControllerComprehensiveTest.class);
    
    private AdminUserController adminUserController;
    
    @Mock
    private AdminUserService mockAdminUserService;
    
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
    private static final Long TEST_USER_ID = 100L;
    private static final String TEST_JWT_TOKEN = "valid_jwt_token";
    private static final String TEST_IP_ADDRESS = "192.168.1.100";
    private static final String TEST_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
    private static final String TEST_BAN_REASON = "Violation of community rules";
    private static final String TEST_MUTE_REASON = "Inappropriate language";
    
    @BeforeEach
    void setUp() throws Exception {
        logger.info("AdminUserController测试环境初始化开始");
        
        MockitoAnnotations.openMocks(this);
        
        // 创建AdminUserController实例，注入Mock的Service
        adminUserController = new AdminUserController(mockAdminUserService, mockAdminService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        // 创建ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        logger.info("AdminUserController测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        logger.info("AdminUserController测试清理完成");
    }
    
    /**
     * 测试查询用户列表接口
     */
    @Test
    void testHandleGetUsers() throws Exception {
        logger.info("开始测试查询用户列表接口");
        
        // 准备Mock返回数据
        Map<String, Object> userList = new HashMap<>();
        userList.put("users", new Object[0]);
        userList.put("total", 0);
        userList.put("pageNum", 1);
        userList.put("pageSize", 20);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getParameter("keyword")).thenReturn("test");
            when(mockRequest.getParameter("status")).thenReturn("1");
            when(mockRequest.getParameter("pageNum")).thenReturn("1");
            when(mockRequest.getParameter("pageSize")).thenReturn("20");
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.findUsers(any(AdminUserQueryDTO.class))).thenReturn(userList);
            
            // 执行测试
            adminUserController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
            verify(mockAdminUserService).findUsers(any(AdminUserQueryDTO.class));
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("\"total\":0"), "响应应包含总数");
            
            logger.info("查询用户列表接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试获取用户详情接口
     */
    @Test
    void testHandleGetUserDetail() throws Exception {
        logger.info("开始测试获取用户详情接口");
        
        // 准备Mock返回数据
        Map<String, Object> userDetail = new HashMap<>();
        userDetail.put("id", TEST_USER_ID);
        userDetail.put("username", "testuser");
        userDetail.put("status", 1);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID);
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.getUserDetail(TEST_USER_ID, TEST_ADMIN_ID)).thenReturn(userDetail);
            
            // 执行测试
            adminUserController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminUserService).getUserDetail(TEST_USER_ID, TEST_ADMIN_ID);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("testuser"), "响应应包含用户名");
            
            logger.info("获取用户详情接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试封禁用户接口（Task4_2_1_2核心功能）
     */
    @Test
    void testHandleBanUser() throws Exception {
        logger.info("开始测试封禁用户接口");
        
        // 准备测试数据
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setReason(TEST_BAN_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        
        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID + "/ban");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.banUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_BAN_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);
            
            // 执行测试
            adminUserController.doPut(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminUserService).banUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_BAN_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("用户封禁成功"), "响应应包含成功消息");
            
            logger.info("封禁用户接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试禁言用户接口
     */
    @Test
    void testHandleMuteUser() throws Exception {
        logger.info("开始测试禁言用户接口");
        
        // 准备测试数据
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setReason(TEST_MUTE_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        
        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID + "/mute");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.muteUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_MUTE_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);
            
            // 执行测试
            adminUserController.doPut(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAdminUserService).muteUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_MUTE_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("用户禁言成功"), "响应应包含成功消息");
            
            logger.info("禁言用户接口测试通过: response={}", responseJson);
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
     * 测试解封用户接口
     */
    @Test
    void testHandleUnbanUser() throws Exception {
        logger.info("开始测试解封用户接口");

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID + "/unban");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.unbanUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);

            // 执行测试
            adminUserController.doPut(mockRequest, mockResponse);

            // 验证结果
            verify(mockAdminUserService).unbanUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT);

            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("用户解封成功"), "响应应包含成功消息");

            logger.info("解封用户接口测试通过: response={}", responseJson);
        }
    }

    /**
     * 测试解除禁言接口
     */
    @Test
    void testHandleUnmuteUser() throws Exception {
        logger.info("开始测试解除禁言接口");

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID + "/unmute");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.unmuteUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);

            // 执行测试
            adminUserController.doPut(mockRequest, mockResponse);

            // 验证结果
            verify(mockAdminUserService).unmuteUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT);

            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("用户解除禁言成功"), "响应应包含成功消息");

            logger.info("解除禁言接口测试通过: response={}", responseJson);
        }
    }

    /**
     * 测试批量封禁用户接口
     */
    @Test
    void testHandleBatchBanUsers() throws Exception {
        logger.info("开始测试批量封禁用户接口");

        // 准备测试数据
        List<Long> userIds = Arrays.asList(100L, 101L, 102L);
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setUserIds(userIds);
        manageDTO.setReason(TEST_BAN_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);

        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);

        // 准备Mock返回数据
        Map<String, Object> batchResult = new HashMap<>();
        batchResult.put("successCount", 3);
        batchResult.put("failureCount", 0);
        batchResult.put("totalCount", 3);

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/batch-ban");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.batchBanUsers(userIds, TEST_ADMIN_ID, TEST_BAN_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(batchResult);

            // 执行测试
            adminUserController.doPost(mockRequest, mockResponse);

            // 验证结果
            verify(mockAdminUserService).batchBanUsers(userIds, TEST_ADMIN_ID, TEST_BAN_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT);

            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("批量封禁操作完成"), "响应应包含成功消息");
            assertTrue(responseJson.contains("\"successCount\":3"), "响应应包含成功数量");

            logger.info("批量封禁用户接口测试通过: response={}", responseJson);
        }
    }

    /**
     * 测试批量禁言用户接口
     */
    @Test
    void testHandleBatchMuteUsers() throws Exception {
        logger.info("开始测试批量禁言用户接口");

        // 准备测试数据
        List<Long> userIds = Arrays.asList(100L, 101L);
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setUserIds(userIds);
        manageDTO.setReason(TEST_MUTE_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);

        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);

        // 准备Mock返回数据
        Map<String, Object> batchResult = new HashMap<>();
        batchResult.put("successCount", 2);
        batchResult.put("failureCount", 0);
        batchResult.put("totalCount", 2);

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/batch-mute");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.batchMuteUsers(userIds, TEST_ADMIN_ID, TEST_MUTE_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(batchResult);

            // 执行测试
            adminUserController.doPost(mockRequest, mockResponse);

            // 验证结果
            verify(mockAdminUserService).batchMuteUsers(userIds, TEST_ADMIN_ID, TEST_MUTE_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT);

            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("批量禁言操作完成"), "响应应包含成功消息");
            assertTrue(responseJson.contains("\"successCount\":2"), "响应应包含成功数量");

            logger.info("批量禁言用户接口测试通过: response={}", responseJson);
        }
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

            adminUserController.doGet(mockRequest, mockResponse);

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

            adminUserController.doGet(mockRequest, mockResponse);

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

        // 测试无效用户ID格式
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/invalid_id");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            adminUserController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效用户ID应返回失败");
            assertTrue(responseJson.contains("400"), "应返回400错误");

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);
        }

        // 测试批量操作时空用户ID列表
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setUserIds(Arrays.asList()); // 空列表
        manageDTO.setReason(TEST_BAN_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        ServletInputStream inputStream = createMockInputStream(requestBody);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/batch-ban");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            adminUserController.doPost(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "空用户ID列表应返回失败");
            assertTrue(responseJson.contains("用户ID列表不能为空"), "应返回用户ID列表不能为空错误");
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

            adminUserController.doPut(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");
            // 实际可能返回不同的错误码，只要是失败即可
            logger.info("HTTP路由测试响应: {}", responseJson);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 测试null路径
            when(mockRequest.getPathInfo()).thenReturn(null);

            adminUserController.doPost(mockRequest, mockResponse);

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
            when(mockAdminUserService.findUsers(any(AdminUserQueryDTO.class)))
                    .thenThrow(new RuntimeException("系统异常"));

            adminUserController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "系统异常应返回失败");
            assertTrue(responseJson.contains("500"), "应返回500错误");
        }

        logger.info("系统异常处理测试通过");
    }

    /**
     * 测试用户不存在情况
     */
    @Test
    void testUserNotFound() throws Exception {
        logger.info("开始测试用户不存在情况");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID);
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.getUserDetail(TEST_USER_ID, TEST_ADMIN_ID)).thenReturn(null);

            adminUserController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "用户不存在应返回失败");
            assertTrue(responseJson.contains("用户不存在"), "应返回用户不存在错误");
        }

        logger.info("用户不存在情况测试通过");
    }

    /**
     * 测试操作失败情况
     */
    @Test
    void testOperationFailure() throws Exception {
        logger.info("开始测试操作失败情况");

        // 准备测试数据
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setReason(TEST_BAN_REASON);
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        ServletInputStream inputStream = createMockInputStream(requestBody);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID + "/ban");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAdminUserService.banUser(TEST_USER_ID, TEST_ADMIN_ID, TEST_BAN_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(false); // 操作失败

            adminUserController.doPut(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "操作失败应返回失败");
            assertTrue(responseJson.contains("封禁失败"), "应返回封禁失败错误");
        }

        logger.info("操作失败情况测试通过");
    }

    /**
     * 测试完整的用户管理流程
     */
    @Test
    void testCompleteUserManagementWorkflow() throws Exception {
        logger.info("开始测试完整的用户管理流程");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            // 1. 查询用户列表
            Map<String, Object> userList = new HashMap<>();
            userList.put("users", new Object[0]);
            userList.put("total", 1);

            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockAdminUserService.findUsers(any(AdminUserQueryDTO.class))).thenReturn(userList);

            adminUserController.doGet(mockRequest, mockResponse);

            String listResponse = responseWriter.toString();
            assertTrue(listResponse.contains("\"success\":true"), "查询用户列表应该成功");
            logger.info("查询用户列表成功: {}", listResponse);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 2. 获取用户详情
            Map<String, Object> userDetail = new HashMap<>();
            userDetail.put("id", TEST_USER_ID);
            userDetail.put("username", "problematic_user");
            userDetail.put("status", 1); // 正常状态

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID);
            when(mockAdminUserService.getUserDetail(TEST_USER_ID, TEST_ADMIN_ID)).thenReturn(userDetail);

            adminUserController.doGet(mockRequest, mockResponse);

            String detailResponse = responseWriter.toString();
            assertTrue(detailResponse.contains("\"success\":true"), "获取用户详情应该成功");
            assertTrue(detailResponse.contains("problematic_user"), "应包含用户名");
            logger.info("获取用户详情成功: {}", detailResponse);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 3. 封禁用户
            AdminUserManageDTO manageDTO = new AdminUserManageDTO();
            manageDTO.setReason("Repeated violations of community guidelines");
            String requestBody = objectMapper.writeValueAsString(manageDTO);
            ServletInputStream inputStream = createMockInputStream(requestBody);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID + "/ban");
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminUserService.banUser(TEST_USER_ID, TEST_ADMIN_ID, "Repeated violations of community guidelines", TEST_IP_ADDRESS, TEST_USER_AGENT))
                    .thenReturn(true);

            adminUserController.doPut(mockRequest, mockResponse);

            String banResponse = responseWriter.toString();
            assertTrue(banResponse.contains("\"success\":true"), "封禁用户应该成功");
            assertTrue(banResponse.contains("用户封禁成功"), "应包含成功消息");
            logger.info("封禁用户成功: {}", banResponse);
        }

        logger.info("完整的用户管理流程测试通过");
    }
}
