package com.shiwu.admin.controller;

import com.shiwu.admin.model.DashboardStatsVO;
import com.shiwu.admin.model.StatsPeriod;
import com.shiwu.admin.service.DashboardService;
import com.shiwu.common.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 仪表盘控制器测试类
 * 测试API接口和权限验证
 */
public class DashboardControllerTest {
    
    private DashboardController dashboardController;
    private DashboardService mockDashboardService;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    
    @BeforeEach
    public void setUp() throws Exception {
        dashboardController = new DashboardController();
        mockDashboardService = mock(DashboardService.class);
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        
        // 设置响应writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        // 通过反射注入mock service
        Field serviceField = DashboardController.class.getDeclaredField("dashboardService");
        serviceField.setAccessible(true);
        serviceField.set(dashboardController, mockDashboardService);
    }
    
    /**
     * 测试获取统计数据API - 成功场景
     * BCDE原则中的Correct：测试正确的API调用
     */
    @Test
    public void testGetStatsSuccess() throws ServletException, IOException {
        // Given: 模拟有效的管理员token和统计数据
        String validToken = "valid-admin-token";
        DashboardStatsVO mockStats = createMockStats();
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats");
        when(mockRequest.getParameter("period")).thenReturn(null);
        when(mockDashboardService.getDashboardStats()).thenReturn(mockStats);
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("ADMIN");
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证响应
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
            verify(mockDashboardService).getDashboardStats();
            
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("\"success\":true"), "响应应该包含成功标识");
        }
    }
    
    /**
     * 测试获取统计数据API - 带时间段参数
     * BCDE原则中的Correct：测试参数处理
     */
    @Test
    public void testGetStatsWithPeriod() throws ServletException, IOException {
        // Given: 模拟有效的管理员token和时间段参数
        String validToken = "valid-admin-token";
        DashboardStatsVO mockStats = createMockStats();
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats");
        when(mockRequest.getParameter("period")).thenReturn("LAST_7_DAYS");
        when(mockDashboardService.getDashboardStats(StatsPeriod.LAST_7_DAYS)).thenReturn(mockStats);
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("SUPER_ADMIN");
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证调用了带参数的方法
            verify(mockDashboardService).getDashboardStats(StatsPeriod.LAST_7_DAYS);
        }
    }
    
    /**
     * 测试刷新统计数据API - 成功场景
     * BCDE原则中的Correct：测试正确的刷新操作
     */
    @Test
    public void testRefreshStatsSuccess() throws ServletException, IOException {
        // Given: 模拟有效的管理员token和刷新成功
        String validToken = "valid-admin-token";
        DashboardStatsVO mockStats = createMockStats();
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats/refresh");
        when(mockDashboardService.refreshStatsCache()).thenReturn(true);
        when(mockDashboardService.getDashboardStats()).thenReturn(mockStats);
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("ADMIN");
            
            // When: 调用POST接口
            dashboardController.doPost(mockRequest, mockResponse);
            
            // Then: 验证刷新操作
            verify(mockDashboardService).refreshStatsCache();
            verify(mockDashboardService).getDashboardStats();
            
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("统计数据刷新成功"), "响应应该包含刷新成功消息");
        }
    }
    
    /**
     * 测试获取实时统计数据API
     * BCDE原则中的Correct：测试实时数据获取
     */
    @Test
    public void testGetRealTimeStats() throws ServletException, IOException {
        // Given: 模拟有效的管理员token和实时统计数据
        String validToken = "valid-admin-token";
        DashboardStatsVO mockStats = createMockStats();
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats/realtime");
        when(mockDashboardService.getRealTimeStats()).thenReturn(mockStats);
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("SUPER_ADMIN");
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证实时数据获取
            verify(mockDashboardService).getRealTimeStats();
            
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("实时统计数据"), "响应应该包含实时数据标识");
        }
    }
    
    /**
     * 测试无效token访问
     * BCDE原则中的Error：测试认证失败
     */
    @Test
    public void testInvalidTokenAccess() throws ServletException, IOException {
        // Given: 模拟无效token
        String invalidToken = "invalid-token";
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats");
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(invalidToken)).thenReturn(false);
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证返回未授权错误
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("未授权访问"), "应该返回未授权错误");
            
            // 验证不会调用service
            verify(mockDashboardService, never()).getDashboardStats();
        }
    }
    
    /**
     * 测试非管理员角色访问
     * BCDE原则中的Error：测试权限不足
     */
    @Test
    public void testNonAdminRoleAccess() throws ServletException, IOException {
        // Given: 模拟有效token但非管理员角色
        String validToken = "valid-user-token";
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats");
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("USER");
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证返回权限不足错误
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("权限不足"), "应该返回权限不足错误");
            
            // 验证不会调用service
            verify(mockDashboardService, never()).getDashboardStats();
        }
    }
    
    /**
     * 测试缺少Authorization头
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testMissingAuthorizationHeader() throws ServletException, IOException {
        // Given: 没有Authorization头
        when(mockRequest.getHeader("Authorization")).thenReturn(null);
        when(mockRequest.getPathInfo()).thenReturn("/stats");
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(null)).thenReturn(false);
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证返回未授权错误
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("未授权访问"), "应该返回未授权错误");
        }
    }
    
    /**
     * 测试不存在的接口路径
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testNonExistentPath() throws ServletException, IOException {
        // Given: 模拟有效的管理员token但不存在的路径
        String validToken = "valid-admin-token";
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/nonexistent");
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("ADMIN");
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证返回接口不存在错误
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("接口不存在"), "应该返回接口不存在错误");
        }
    }
    
    /**
     * 测试服务异常处理
     * BCDE原则中的Error：测试异常处理
     */
    @Test
    public void testServiceException() throws ServletException, IOException {
        // Given: 模拟有效token但service抛出异常
        String validToken = "valid-admin-token";
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats");
        when(mockDashboardService.getDashboardStats()).thenThrow(new RuntimeException("Service error"));
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("ADMIN");
            
            // When: 调用GET接口
            dashboardController.doGet(mockRequest, mockResponse);
            
            // Then: 验证返回服务器错误
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("获取统计数据失败"), "应该返回获取数据失败错误");
        }
    }
    
    /**
     * 测试刷新缓存失败
     * BCDE原则中的Error：测试刷新失败场景
     */
    @Test
    public void testRefreshCacheFailed() throws ServletException, IOException {
        // Given: 模拟有效token但刷新失败
        String validToken = "valid-admin-token";
        
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(mockRequest.getPathInfo()).thenReturn("/stats/refresh");
        when(mockDashboardService.refreshStatsCache()).thenReturn(false);
        
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken(validToken)).thenReturn(true);
            mockedJwtUtil.when(() -> JwtUtil.getRoleFromToken(validToken)).thenReturn("ADMIN");
            
            // When: 调用POST接口
            dashboardController.doPost(mockRequest, mockResponse);
            
            // Then: 验证返回刷新失败错误
            String responseContent = responseWriter.toString();
            assertTrue(responseContent.contains("刷新统计数据失败"), "应该返回刷新失败错误");
        }
    }
    
    /**
     * 创建模拟统计数据
     */
    private DashboardStatsVO createMockStats() {
        DashboardStatsVO stats = new DashboardStatsVO();
        stats.setLastUpdateTime(LocalDateTime.now());
        
        // 创建总览统计
        DashboardStatsVO.OverviewStats overview = new DashboardStatsVO.OverviewStats();
        overview.setTotalUsers(100L);
        overview.setTotalProducts(200L);
        overview.setTotalActiveUsers(50L);
        overview.setTotalPendingProducts(10L);
        overview.setAverageRating(4.5);
        overview.setTotalFollowRelations(80L);
        stats.setOverview(overview);
        
        // 创建用户统计
        DashboardStatsVO.UserStats userStats = new DashboardStatsVO.UserStats();
        userStats.setNewUsersToday(5L);
        userStats.setNewUsersThisWeek(20L);
        userStats.setNewUsersThisMonth(60L);
        userStats.setBannedUsers(3L);
        userStats.setMutedUsers(2L);
        userStats.setUserGrowthRate(15.5);
        stats.setUserStats(userStats);
        
        // 创建商品统计
        DashboardStatsVO.ProductStats productStats = new DashboardStatsVO.ProductStats();
        productStats.setNewProductsToday(8L);
        productStats.setNewProductsThisWeek(30L);
        productStats.setNewProductsThisMonth(100L);
        productStats.setOnSaleProducts(150L);
        productStats.setSoldProducts(25L);
        productStats.setRemovedProducts(5L);
        productStats.setProductGrowthRate(25.0);
        stats.setProductStats(productStats);
        
        // 创建活动统计
        DashboardStatsVO.ActivityStats activityStats = new DashboardStatsVO.ActivityStats();
        activityStats.setAdminLoginCount(3L);
        activityStats.setAuditLogCount(15L);
        activityStats.setUserLoginCount(120L);
        activityStats.setSystemErrors(1L);
        activityStats.setSystemUptime(99.9);
        stats.setActivityStats(activityStats);
        
        return stats;
    }
}
