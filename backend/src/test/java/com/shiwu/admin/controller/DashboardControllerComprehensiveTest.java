package com.shiwu.admin.controller;

import com.shiwu.admin.model.DashboardStatsVO;
import com.shiwu.admin.model.StatsPeriod;
import com.shiwu.admin.service.DashboardService;
import com.shiwu.common.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DashboardController综合测试
 * 
 * 测试仪表盘控制器的所有核心功能，包括：
 * 1. 获取统计数据接口（UC-15核心功能）
 * 2. 刷新统计数据接口
 * 3. 获取实时统计数据接口
 * 4. 管理员权限验证
 * 5. JWT Token验证
 * 6. 参数解析和验证
 * 7. 错误处理和异常情况
 * 8. HTTP方法路由
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class DashboardControllerComprehensiveTest {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardControllerComprehensiveTest.class);
    
    private DashboardController dashboardController;
    
    @Mock
    private DashboardService mockDashboardService;
    
    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private HttpServletResponse mockResponse;
    
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    
    // 测试数据常量
    private static final String TEST_JWT_TOKEN = "valid_jwt_token";
    private static final String TEST_ADMIN_ROLE = "ADMIN";
    private static final String TEST_SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    
    @BeforeEach
    void setUp() throws Exception {
        logger.info("DashboardController测试环境初始化开始");
        
        MockitoAnnotations.openMocks(this);
        
        // 创建DashboardController实例
        dashboardController = new DashboardController();
        
        // 使用反射注入Mock的DashboardService
        Field dashboardServiceField = DashboardController.class.getDeclaredField("dashboardService");
        dashboardServiceField.setAccessible(true);
        dashboardServiceField.set(dashboardController, mockDashboardService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        logger.info("DashboardController测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        logger.info("DashboardController测试清理完成");
    }
    
    /**
     * 测试获取统计数据接口（UC-15核心功能）
     */
    @Test
    void testHandleGetStats() throws Exception {
        logger.info("开始测试获取统计数据接口");
        
        // 准备Mock返回数据
        DashboardStatsVO statsVO = new DashboardStatsVO();

        // 设置总览统计
        DashboardStatsVO.OverviewStats overview = new DashboardStatsVO.OverviewStats();
        overview.setTotalUsers(1000L);
        overview.setTotalProducts(500L);
        overview.setTotalActiveUsers(800L);
        overview.setTotalPendingProducts(15L);
        overview.setAverageRating(4.5);
        statsVO.setOverview(overview);

        // 设置用户统计
        DashboardStatsVO.UserStats userStats = new DashboardStatsVO.UserStats();
        userStats.setNewUsersToday(50L);
        userStats.setNewUsersThisWeek(300L);
        userStats.setNewUsersThisMonth(1200L);
        statsVO.setUserStats(userStats);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ROLE);
            
            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getParameter("period")).thenReturn("LAST_7_DAYS");
            when(mockDashboardService.getDashboardStats(StatsPeriod.LAST_7_DAYS)).thenReturn(statsVO);
            
            // 执行测试
            dashboardController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
            verify(mockDashboardService).getDashboardStats(StatsPeriod.LAST_7_DAYS);
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("\"totalUsers\":1000"), "响应应包含用户总数");
            assertTrue(responseJson.contains("\"totalProducts\":500"), "响应应包含商品总数");
            assertTrue(responseJson.contains("\"newUsersToday\":50"), "响应应包含今日新用户数");
            
            logger.info("获取统计数据接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试获取默认统计数据
     */
    @Test
    void testHandleGetStatsDefault() throws Exception {
        logger.info("开始测试获取默认统计数据");
        
        // 准备Mock返回数据
        DashboardStatsVO statsVO = new DashboardStatsVO();

        // 设置总览统计
        DashboardStatsVO.OverviewStats overview = new DashboardStatsVO.OverviewStats();
        overview.setTotalUsers(1200L);
        overview.setTotalProducts(600L);
        overview.setTotalActiveUsers(900L);
        statsVO.setOverview(overview);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_SUPER_ADMIN_ROLE);
            
            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getParameter("period")).thenReturn(null); // 无时间段参数
            when(mockDashboardService.getDashboardStats()).thenReturn(statsVO);
            
            // 执行测试
            dashboardController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockDashboardService).getDashboardStats(); // 调用无参数版本
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("\"totalUsers\":1200"), "响应应包含用户总数");
            
            logger.info("获取默认统计数据测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试刷新统计数据接口
     */
    @Test
    void testHandleRefreshStats() throws Exception {
        logger.info("开始测试刷新统计数据接口");
        
        // 准备Mock返回数据
        DashboardStatsVO refreshedStatsVO = new DashboardStatsVO();

        // 设置总览统计
        DashboardStatsVO.OverviewStats refreshedOverview = new DashboardStatsVO.OverviewStats();
        refreshedOverview.setTotalUsers(1100L);
        refreshedOverview.setTotalProducts(550L);
        refreshedOverview.setTotalActiveUsers(850L);
        refreshedStatsVO.setOverview(refreshedOverview);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ROLE);
            
            when(mockRequest.getPathInfo()).thenReturn("/stats/refresh");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockDashboardService.refreshStatsCache()).thenReturn(true);
            when(mockDashboardService.getDashboardStats()).thenReturn(refreshedStatsVO);
            
            // 执行测试
            dashboardController.doPost(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockDashboardService).refreshStatsCache();
            verify(mockDashboardService).getDashboardStats();
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("统计数据刷新成功"), "响应应包含成功消息");
            assertTrue(responseJson.contains("\"totalUsers\":1100"), "响应应包含刷新后的用户总数");
            
            logger.info("刷新统计数据接口测试通过: response={}", responseJson);
        }
    }
    
    /**
     * 测试获取实时统计数据接口
     */
    @Test
    void testHandleGetRealTimeStats() throws Exception {
        logger.info("开始测试获取实时统计数据接口");
        
        // 准备Mock返回数据
        DashboardStatsVO realTimeStatsVO = new DashboardStatsVO();

        // 设置总览统计
        DashboardStatsVO.OverviewStats realTimeOverview = new DashboardStatsVO.OverviewStats();
        realTimeOverview.setTotalUsers(1050L);
        realTimeOverview.setTotalProducts(525L);
        realTimeOverview.setTotalActiveUsers(850L);
        realTimeOverview.setTotalPendingProducts(12L);
        realTimeStatsVO.setOverview(realTimeOverview);

        // 设置活动统计
        DashboardStatsVO.ActivityStats activityStats = new DashboardStatsVO.ActivityStats();
        activityStats.setUserLoginCount(45L);
        activityStats.setAdminLoginCount(5L);
        realTimeStatsVO.setActivityStats(activityStats);
        
        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_SUPER_ADMIN_ROLE);
            
            when(mockRequest.getPathInfo()).thenReturn("/stats/realtime");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockDashboardService.getRealTimeStats()).thenReturn(realTimeStatsVO);
            
            // 执行测试
            dashboardController.doGet(mockRequest, mockResponse);
            
            // 验证结果
            verify(mockDashboardService).getRealTimeStats();
            
            String responseJson = responseWriter.toString();
            assertNotNull(responseJson, "响应不应为空");
            assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
            assertTrue(responseJson.contains("实时统计数据"), "响应应包含实时数据消息");
            assertTrue(responseJson.contains("\"totalActiveUsers\":850"), "响应应包含活跃用户数");
            assertTrue(responseJson.contains("\"userLoginCount\":45"), "响应应包含用户登录数");
            
            logger.info("获取实时统计数据接口测试通过: response={}", responseJson);
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

            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer invalid_token");

            dashboardController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效Token应返回失败");
            assertTrue(responseJson.contains("未授权访问"), "应返回未授权错误");

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);
        }

        // 测试非管理员角色
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn("USER");

            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);

            dashboardController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "非管理员角色应返回失败");
            assertTrue(responseJson.contains("权限不足"), "应返回权限不足错误");
        }

        logger.info("管理员权限验证测试通过");
    }

    /**
     * 测试刷新统计数据失败情况
     */
    @Test
    void testRefreshStatsFailure() throws Exception {
        logger.info("开始测试刷新统计数据失败情况");

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ROLE);

            when(mockRequest.getPathInfo()).thenReturn("/stats/refresh");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockDashboardService.refreshStatsCache()).thenReturn(false); // 刷新失败

            // 执行测试
            dashboardController.doPost(mockRequest, mockResponse);

            // 验证结果
            verify(mockDashboardService).refreshStatsCache();
            verify(mockDashboardService, never()).getDashboardStats(); // 失败时不应调用获取数据

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "刷新失败应返回失败");
            assertTrue(responseJson.contains("刷新统计数据失败"), "应返回刷新失败错误");

            logger.info("刷新统计数据失败情况测试通过: response={}", responseJson);
        }
    }

    /**
     * 测试HTTP方法路由
     */
    @Test
    void testHttpMethodRouting() throws Exception {
        logger.info("开始测试HTTP方法路由");

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ROLE);

            // 测试无效路径 - GET
            when(mockRequest.getPathInfo()).thenReturn("/invalid");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);

            dashboardController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");
            assertTrue(responseJson.contains("接口不存在"), "应返回接口不存在错误");

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 测试无效路径 - POST
            when(mockRequest.getPathInfo()).thenReturn("/invalid");

            dashboardController.doPost(mockRequest, mockResponse);

            responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");
            assertTrue(responseJson.contains("接口不存在"), "应返回接口不存在错误");

            // 重置响应Writer
            responseWriter.getBuffer().setLength(0);

            // 测试null路径
            when(mockRequest.getPathInfo()).thenReturn(null);

            dashboardController.doGet(mockRequest, mockResponse);

            responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "null路径应返回失败");
            assertTrue(responseJson.contains("接口不存在"), "应返回接口不存在错误");
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
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ROLE);

            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getParameter("period")).thenReturn("LAST_7_DAYS");
            when(mockDashboardService.getDashboardStats(StatsPeriod.LAST_7_DAYS))
                    .thenThrow(new RuntimeException("系统异常"));

            dashboardController.doGet(mockRequest, mockResponse);

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":false"), "系统异常应返回失败");
            assertTrue(responseJson.contains("获取统计数据失败"), "应返回获取数据失败错误");
        }

        logger.info("系统异常处理测试通过");
    }

    /**
     * 测试参数验证
     */
    @Test
    void testParameterValidation() throws Exception {
        logger.info("开始测试参数验证");

        // 准备Mock返回数据
        DashboardStatsVO statsVO = new DashboardStatsVO();

        // 设置总览统计
        DashboardStatsVO.OverviewStats overview = new DashboardStatsVO.OverviewStats();
        overview.setTotalUsers(900L);
        overview.setTotalProducts(450L);
        statsVO.setOverview(overview);

        // 设置Mock行为
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(TEST_JWT_TOKEN)).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getRoleFromToken(TEST_JWT_TOKEN)).thenReturn(TEST_ADMIN_ROLE);

            // 测试无效的时间段参数
            when(mockRequest.getPathInfo()).thenReturn("/stats");
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_JWT_TOKEN);
            when(mockRequest.getParameter("period")).thenReturn("INVALID_PERIOD");
            when(mockDashboardService.getDashboardStats()).thenReturn(statsVO); // 应该调用默认方法

            dashboardController.doGet(mockRequest, mockResponse);

            // 验证结果 - 无效参数应该使用默认方法
            verify(mockDashboardService).getDashboardStats(); // 调用无参数版本
            verify(mockDashboardService, never()).getDashboardStats(any(StatsPeriod.class)); // 不应调用有参数版本

            String responseJson = responseWriter.toString();
            assertTrue(responseJson.contains("\"success\":true"), "无效参数应使用默认值并成功");
            assertTrue(responseJson.contains("\"totalUsers\":900"), "响应应包含用户总数");
        }

        logger.info("参数验证测试通过");
    }

    /**
     * 测试Token格式验证
     */
    @Test
    void testTokenFormatValidation() throws Exception {
        logger.info("开始测试Token格式验证");

        // 测试无Authorization头
        when(mockRequest.getPathInfo()).thenReturn("/stats");
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        dashboardController.doGet(mockRequest, mockResponse);

        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "无Authorization头应返回失败");
        assertTrue(responseJson.contains("未授权访问"), "应返回未授权错误");

        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);

        // 测试错误的Token格式
        when(mockRequest.getHeader("Authorization")).thenReturn("InvalidFormat " + TEST_JWT_TOKEN);

        dashboardController.doGet(mockRequest, mockResponse);

        responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "错误Token格式应返回失败");
        assertTrue(responseJson.contains("未授权访问"), "应返回未授权错误");

        logger.info("Token格式验证测试通过");
    }
}
