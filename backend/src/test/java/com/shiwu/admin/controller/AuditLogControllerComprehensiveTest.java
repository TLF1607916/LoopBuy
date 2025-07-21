package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.dto.AuditLogQueryDTO;
import com.shiwu.admin.service.AdminService;
import com.shiwu.admin.service.AuditLogService;
import com.shiwu.admin.vo.AuditLogVO;
import com.shiwu.common.util.JwtUtil;
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
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuditLogController综合测试
 * 
 * 测试审计日志控制器的所有核心功能，包括：
 * 1. 查询审计日志列表接口（NFR-SEC-03核心功能）
 * 2. 获取审计日志详情接口
 * 3. 获取可用操作类型接口
 * 4. 获取可用目标类型接口
 * 5. 获取统计数据接口
 * 6. 获取趋势数据接口
 * 7. 导出审计日志接口
 * 8. 管理员权限验证
 * 9. JWT Token验证
 * 10. 参数解析和验证
 * 11. 错误处理和异常情况
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class AuditLogControllerComprehensiveTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditLogControllerComprehensiveTest.class);
    
    private AuditLogController auditLogController;
    
    @Mock
    private AuditLogService mockAuditLogService;

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
    private static final Long TEST_LOG_ID = 100L;
    private static final String TEST_JWT_TOKEN = "valid_jwt_token";
    private static final String TEST_IP_ADDRESS = "192.168.1.100";
    private static final String TEST_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
    
    @BeforeEach
    void setUp() throws Exception {
        logger.info("AuditLogController测试环境初始化开始");
        
        MockitoAnnotations.openMocks(this);
        
        // 创建AuditLogController实例，注入Mock的Service
        auditLogController = new AuditLogController(mockAuditLogService);

        // 使用反射注入Mock的AdminService
        try {
            Field adminServiceField = AuditLogController.class.getDeclaredField("adminService");
            adminServiceField.setAccessible(true);
            adminServiceField.set(auditLogController, mockAdminService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock AdminService", e);
        }
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        // 创建ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        logger.info("AuditLogController测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        logger.info("AuditLogController测试清理完成");
    }
    
    /**
     * 测试查询审计日志列表接口（NFR-SEC-03核心功能）
     */
    @Test
    void testHandleGetAuditLogs() throws Exception {
        logger.info("开始测试查询审计日志列表接口");
        
        // 准备Mock返回数据
        Map<String, Object> auditLogList = new HashMap<>();
        auditLogList.put("logs", new Object[0]);
        auditLogList.put("total", 0);
        auditLogList.put("page", 1);
        auditLogList.put("pageSize", 20);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getParameter("action")).thenReturn("USER_BAN");
            when(mockRequest.getParameter("targetType")).thenReturn("USER");
            when(mockRequest.getParameter("page")).thenReturn("1");
            when(mockRequest.getParameter("pageSize")).thenReturn("20");
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(auditLogList);
            
            // 执行测试
            auditLogController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
            verify(mockAuditLogService).getAuditLogs(any(AuditLogQueryDTO.class));
            verify(mockAuditLogService).logAction(eq(TEST_ADMIN_ID), any(), any(), isNull(), 
                                                 eq("查看审计日志"), eq(TEST_IP_ADDRESS), eq(TEST_USER_AGENT), eq(true));
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("\"total\":0"), "响应应包含总数");
            
            logger.info("查询审计日志列表接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试获取审计日志详情接口
     */
    @Test
    void testHandleGetAuditLogDetail() throws Exception {
        logger.info("开始测试获取审计日志详情接口");
        
        // 准备Mock返回数据
        AuditLogVO auditLogVO = new AuditLogVO();
        auditLogVO.setId(TEST_LOG_ID);
        auditLogVO.setAction("USER_BAN");
        auditLogVO.setTargetType("USER");
        auditLogVO.setTargetId(100L);
        auditLogVO.setDetails("封禁用户");
        auditLogVO.setIpAddress(TEST_IP_ADDRESS);
        auditLogVO.setResult(1);
        auditLogVO.setCreateTime(LocalDateTime.now());
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_LOG_ID);
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getAuditLogDetail(TEST_LOG_ID)).thenReturn(auditLogVO);
            
            // 执行测试
            auditLogController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAuditLogService).getAuditLogDetail(TEST_LOG_ID);
            verify(mockAuditLogService).logAction(eq(TEST_ADMIN_ID), any(), any(), isNull(), 
                                                 eq("查看审计日志"), eq(TEST_IP_ADDRESS), eq(TEST_USER_AGENT), eq(true));
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("USER_BAN"), "响应应包含操作类型");
            assertTrue(responseJson.contains("封禁用户"), "响应应包含描述");
            
            logger.info("获取审计日志详情接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试获取可用操作类型接口
     */
    @Test
    void testHandleGetAvailableActions() throws Exception {
        logger.info("开始测试获取可用操作类型接口");
        
        // 准备Mock返回数据
        Map<String, String> action1 = new HashMap<>();
        action1.put("code", "USER_BAN");
        action1.put("description", "封禁用户");

        Map<String, String> action2 = new HashMap<>();
        action2.put("code", "USER_UNBAN");
        action2.put("description", "解封用户");

        List<Map<String, String>> actions = Arrays.asList(action1, action2);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/actions");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getAvailableActions()).thenReturn(actions);
            
            // 执行测试
            auditLogController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAuditLogService).getAvailableActions();
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("USER_BAN"), "响应应包含操作类型");
            assertTrue(responseJson.contains("封禁用户"), "响应应包含操作描述");
            
            logger.info("获取可用操作类型接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试获取可用目标类型接口
     */
    @Test
    void testHandleGetAvailableTargetTypes() throws Exception {
        logger.info("开始测试获取可用目标类型接口");
        
        // 准备Mock返回数据
        Map<String, String> target1 = new HashMap<>();
        target1.put("code", "USER");
        target1.put("description", "用户");

        Map<String, String> target2 = new HashMap<>();
        target2.put("code", "PRODUCT");
        target2.put("description", "商品");

        List<Map<String, String>> targetTypes = Arrays.asList(target1, target2);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);
            
            when(mockRequest.getPathInfo()).thenReturn("/target-types");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getAvailableTargetTypes()).thenReturn(targetTypes);
            
            // 执行测试
            auditLogController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockAuditLogService).getAvailableTargetTypes();
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("USER"), "响应应包含目标类型");
            assertTrue(responseJson.contains("用户"), "响应应包含目标类型描述");
            
            logger.info("获取可用目标类型接口测试通过: response={}", responseJson);
        }
    }

    /**
     * 测试获取统计数据接口
     */
    @Test
    void testHandleGetStats() throws Exception {
        logger.info("开始测试获取统计数据接口");

        // 准备Mock返回数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOperations", 1000);
        stats.put("successOperations", 950);
        stats.put("failedOperations", 50);
        stats.put("topActions", Arrays.asList("USER_BAN", "PRODUCT_APPROVE"));

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getParameter("days")).thenReturn("7");
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getOperationStats(7)).thenReturn(stats);

            // 执行测试
            auditLogController.doGet(mockRequest, mockResponse);

            // 验证结果
            verify(mockAuditLogService).getOperationStats(7);

            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("\"totalOperations\":1000"), "响应应包含总操作数");
            assertTrue(responseJson.contains("\"successOperations\":950"), "响应应包含成功操作数");

            logger.info("获取统计数据接口测试通过: response={}", responseJson);
        }
    }

    /**
     * 测试获取趋势数据接口
     */
    @Test
    void testHandleGetTrend() throws Exception {
        logger.info("开始测试获取趋势数据接口");

        // 准备Mock返回数据
        Map<String, Object> trend1 = new HashMap<>();
        trend1.put("date", "2024-01-15");
        trend1.put("count", 100);

        Map<String, Object> trend2 = new HashMap<>();
        trend2.put("date", "2024-01-16");
        trend2.put("count", 120);

        List<Map<String, Object>> trendData = Arrays.asList(trend1, trend2);

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/trend");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getParameter("days")).thenReturn("7");
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getActivityTrend(7)).thenReturn(trendData);

            // 执行测试
            auditLogController.doGet(mockRequest, mockResponse);

            // 验证结果
            verify(mockAuditLogService).getActivityTrend(7);

            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("2024-01-15"), "响应应包含日期");
            assertTrue(responseJson.contains("\"count\":100"), "响应应包含计数");

            logger.info("获取趋势数据接口测试通过: response={}", responseJson);
        }
    }

    /**
     * 测试导出审计日志接口
     */
    @Test
    void testHandleExportAuditLogs() throws Exception {
        logger.info("开始测试导出审计日志接口");

        // 准备测试数据 - 手动构建JSON，避免offset字段
        String requestBody = "{\"action\":\"USER_BAN\",\"targetType\":\"USER\"}";

        // 创建Mock ServletInputStream
        ServletInputStream inputStream = createMockInputStream(requestBody);

        // 准备Mock返回数据
        AuditLogVO exportLog = new AuditLogVO();
        exportLog.setId(1L);
        exportLog.setAction("USER_BAN");
        exportLog.setTargetType("USER");
        exportLog.setDetails("导出的审计日志");
        List<AuditLogVO> exportResult = Arrays.asList(exportLog);

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/export");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getInputStream()).thenReturn(inputStream);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.exportAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(exportResult);

            // 执行测试
            auditLogController.doPost(mockRequest, mockResponse);

            // 验证结果
            verify(mockAuditLogService).exportAuditLogs(any(AuditLogQueryDTO.class));
            verify(mockAuditLogService).logAction(eq(TEST_ADMIN_ID), any(), any(), isNull(),
                                                 eq("导出审计日志"), eq(TEST_IP_ADDRESS), eq(TEST_USER_AGENT), eq(true));

            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("USER_BAN"), "响应应包含操作类型");
            assertTrue(responseJson.contains("导出的审计日志"), "响应应包含详情");

            logger.info("导出审计日志接口测试通过: response={}", responseJson);
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

            auditLogController.doGet(mockRequest, mockResponse);

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
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(false); // 权限不足

            auditLogController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "权限不足应返回失败");
            assertTrue(responseJson.contains("权限不足"), "应返回权限不足错误");
        }

        logger.info("管理员权限验证测试通过");
    }

    /**
     * 测试参数验证
     */
    @Test
    void testParameterValidation() throws Exception {
        logger.info("开始测试参数验证");

        // 测试无效日志ID格式
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/invalid_id");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            auditLogController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效日志ID应返回失败");
            assertTrue(responseJson.contains("400"), "应返回400错误");

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);
        }

        // 测试统计数据参数验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockRequest.getParameter("days")).thenReturn("invalid"); // 无效的天数参数
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalOperations", 0);
            when(mockAuditLogService.getOperationStats(7)).thenReturn(defaultStats); // 应该使用默认值7

            auditLogController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":true"), "无效参数应使用默认值并成功");
            verify(mockAuditLogService).getOperationStats(7); // 验证使用了默认值
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
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            auditLogController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");
            // 实际可能返回不同的错误码，只要是失败即可
            logger.info("HTTP路由测试响应: {}", responseJson);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 测试null路径
            when(mockRequest.getPathInfo()).thenReturn(null);

            auditLogController.doPost(mockRequest, mockResponse);

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
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getAuditLogs(any(AuditLogQueryDTO.class)))
                    .thenThrow(new RuntimeException("系统异常"));

            auditLogController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "系统异常应返回失败");
            assertTrue(responseJson.contains("500"), "应返回500错误");
        }

        logger.info("系统异常处理测试通过");
    }

    /**
     * 测试日志不存在情况
     */
    @Test
    void testLogNotFound() throws Exception {
        logger.info("开始测试日志不存在情况");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_LOG_ID);
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);
            when(mockAuditLogService.getAuditLogDetail(TEST_LOG_ID)).thenReturn(null);

            auditLogController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "日志不存在应返回失败");
            assertTrue(responseJson.contains("审计日志不存在"), "应返回日志不存在错误");
        }

        logger.info("日志不存在情况测试通过");
    }

    /**
     * 测试完整的审计日志查询流程
     */
    @Test
    void testCompleteAuditLogWorkflow() throws Exception {
        logger.info("开始测试完整的审计日志查询流程");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ID);

            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
            when(mockAdminService.hasPermission(TEST_ADMIN_ID, "ADMIN")).thenReturn(true);

            // 1. 查询审计日志列表
            Map<String, Object> auditLogList = new HashMap<>();
            auditLogList.put("logs", new Object[0]);
            auditLogList.put("total", 1);

            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockAuditLogService.getAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(auditLogList);

            auditLogController.doGet(mockRequest, mockResponse);

            String listResponse = responseWriter.toString();
            assertTrue(listResponse.contains("\"success\":true"), "查询审计日志列表应该成功");
            logger.info("查询审计日志列表成功: {}", listResponse);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 2. 获取审计日志详情
            AuditLogVO auditLogVO = new AuditLogVO();
            auditLogVO.setId(TEST_LOG_ID);
            auditLogVO.setAction("USER_BAN");
            auditLogVO.setTargetType("USER");
            auditLogVO.setDetails("封禁违规用户");
            auditLogVO.setResult(1);

            when(mockRequest.getPathInfo()).thenReturn("/" + TEST_LOG_ID);
            when(mockAuditLogService.getAuditLogDetail(TEST_LOG_ID)).thenReturn(auditLogVO);

            auditLogController.doGet(mockRequest, mockResponse);

            String detailResponse = responseWriter.toString();
            assertTrue(detailResponse.contains("\"success\":true"), "获取审计日志详情应该成功");
            assertTrue(detailResponse.contains("USER_BAN"), "应包含操作类型");
            logger.info("获取审计日志详情成功: {}", detailResponse);

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 3. 获取统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalOperations", 100);
            stats.put("successOperations", 95);

            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getParameter("days")).thenReturn("7");
            when(mockAuditLogService.getOperationStats(7)).thenReturn(stats);

            auditLogController.doGet(mockRequest, mockResponse);

            String statsResponse = responseWriter.toString();
            assertTrue(statsResponse.contains("\"success\":true"), "获取统计数据应该成功");
            assertTrue(statsResponse.contains("\"totalOperations\":100"), "应包含总操作数");
            logger.info("获取统计数据成功: {}", statsResponse);
        }

        logger.info("完整的审计日志查询流程测试通过");
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
}
