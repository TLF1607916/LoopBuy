package com.shiwu.admin.service;

import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.model.DashboardStatsVO;
import com.shiwu.admin.model.StatsPeriod;
import com.shiwu.admin.service.impl.DashboardServiceImpl;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.user.dao.FollowDao;
import com.shiwu.user.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.reset;

/**
 * 仪表盘服务测试类
 * 测试统计数据计算和缓存功能
 */
public class DashboardServiceTest {
    
    @Mock
    private UserDao mockUserDao;
    
    @Mock
    private ProductDao mockProductDao;
    
    @Mock
    private AuditLogDao mockAuditLogDao;
    
    @Mock
    private FollowDao mockFollowDao;
    
    private DashboardServiceImpl dashboardService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dashboardService = new DashboardServiceImpl(mockUserDao, mockProductDao, mockAuditLogDao, mockFollowDao);
    }
    
    /**
     * 测试获取仪表盘统计数据
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetDashboardStats() {
        // Given: 模拟DAO返回数据
        setupMockDaoReturns();

        // When: 调用获取统计数据方法
        DashboardStatsVO stats = dashboardService.getDashboardStats();

        // Then: 验证结果
        assertNotNull(stats, "统计数据不应为空");
        assertNotNull(stats.getOverview(), "总览统计不应为空");
        assertNotNull(stats.getUserStats(), "用户统计不应为空");
        assertNotNull(stats.getProductStats(), "商品统计不应为空");
        assertNotNull(stats.getActivityStats(), "活动统计不应为空");
        assertNotNull(stats.getLastUpdateTime(), "更新时间不应为空");

        // 验证总览统计数据
        DashboardStatsVO.OverviewStats overview = stats.getOverview();
        assertEquals(100L, overview.getTotalUsers(), "总用户数应该匹配");
        assertEquals(200L, overview.getTotalProducts(), "总商品数应该匹配");
        assertEquals(50L, overview.getTotalActiveUsers(), "活跃用户数应该匹配");
        assertEquals(10L, overview.getTotalPendingProducts(), "待审核商品数应该匹配");
        assertEquals(4.5, overview.getAverageRating(), 0.01, "平均评分应该匹配");
        assertEquals(80L, overview.getTotalFollowRelations(), "关注关系数应该匹配");
    }
    
    /**
     * 测试缓存机制
     * BCDE原则中的Correct：测试正确的缓存逻辑
     */
    @Test
    public void testCacheMechanism() {
        // Given: 模拟DAO返回数据
        setupMockDaoReturns();
        
        // When: 第一次调用
        DashboardStatsVO stats1 = dashboardService.getDashboardStats();
        
        // When: 第二次调用（应该使用缓存）
        DashboardStatsVO stats2 = dashboardService.getDashboardStats();
        
        // Then: 验证DAO只被调用一次（使用了缓存）
        verify(mockUserDao, times(1)).getTotalUserCount();
        verify(mockProductDao, times(1)).getTotalProductCount();
        
        // 验证返回的是同一个对象（缓存）
        assertSame(stats1, stats2, "应该返回缓存的统计数据");
    }
    
    /**
     * 测试刷新统计数据缓存
     * BCDE原则中的Correct：测试正确的缓存刷新逻辑
     */
    @Test
    public void testRefreshStatsCache() {
        // Given: 模拟DAO返回数据
        setupMockDaoReturns();
        
        // When: 刷新缓存
        boolean result = dashboardService.refreshStatsCache();
        
        // Then: 验证刷新成功
        assertTrue(result, "缓存刷新应该成功");
        
        // 验证DAO被调用
        verify(mockUserDao).getTotalUserCount();
        verify(mockProductDao).getTotalProductCount();
        verify(mockFollowDao).getTotalFollowCount();
    }
    
    /**
     * 测试获取实时统计数据
     * BCDE原则中的Correct：测试实时数据获取
     */
    @Test
    public void testGetRealTimeStats() {
        // Given: 模拟DAO返回数据
        setupMockDaoReturns();
        
        // When: 获取实时统计数据
        DashboardStatsVO stats = dashboardService.getRealTimeStats();
        
        // Then: 验证结果
        assertNotNull(stats, "实时统计数据不应为空");
        
        // 验证DAO被调用（不使用缓存）
        verify(mockUserDao).getTotalUserCount();
        verify(mockProductDao).getTotalProductCount();
    }
    
    /**
     * 测试指定时间段统计数据
     * BCDE原则中的Correct：测试时间段参数处理
     */
    @Test
    public void testGetDashboardStatsWithPeriod() {
        // Given: 模拟DAO返回数据
        setupMockDaoReturns();
        
        // When: 获取指定时间段统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats(StatsPeriod.LAST_7_DAYS);
        
        // Then: 验证结果
        assertNotNull(stats, "指定时间段统计数据不应为空");
        
        // 验证DAO被调用
        verify(mockUserDao).getTotalUserCount();
        verify(mockProductDao).getTotalProductCount();
    }
    
    /**
     * 测试用户统计数据计算
     * BCDE原则中的Correct：测试用户统计计算逻辑
     */
    @Test
    public void testUserStatsCalculation() {
        // Given: 重置所有mock，设置特定的用户统计数据
        reset(mockUserDao, mockProductDao, mockAuditLogDao, mockFollowDao);

        // 设置用户统计相关的mock
        when(mockUserDao.getNewUserCount(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(5L)  // 今日新增
            .thenReturn(20L) // 本周新增
            .thenReturn(60L) // 本月新增（第一次调用）
            .thenReturn(60L) // 本月新增（第二次调用，用于增长率计算）
            .thenReturn(40L); // 上月新增
        when(mockUserDao.getUserCountByStatus(1)).thenReturn(3L); // 封禁用户
        when(mockUserDao.getUserCountByStatus(2)).thenReturn(2L); // 禁言用户

        // 设置其他必要的mock（使用默认值）
        setupBasicMockReturns();

        // When: 获取统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats();

        // Then: 验证用户统计数据
        DashboardStatsVO.UserStats userStats = stats.getUserStats();
        assertNotNull(userStats, "用户统计不应为空");
        assertEquals(5L, userStats.getNewUsersToday(), "今日新增用户数应该匹配");
        assertEquals(20L, userStats.getNewUsersThisWeek(), "本周新增用户数应该匹配");
        assertEquals(60L, userStats.getNewUsersThisMonth(), "本月新增用户数应该匹配");
        assertEquals(3L, userStats.getBannedUsers(), "封禁用户数应该匹配");
        assertEquals(2L, userStats.getMutedUsers(), "禁言用户数应该匹配");

        // 验证增长率计算 (60-40)/40 * 100 = 50%
        assertEquals(50.0, userStats.getUserGrowthRate(), 0.01, "用户增长率应该匹配");
    }
    
    /**
     * 测试商品统计数据计算
     * BCDE原则中的Correct：测试商品统计计算逻辑
     */
    @Test
    public void testProductStatsCalculation() {
        // Given: 重置所有mock，设置特定的商品统计数据
        reset(mockUserDao, mockProductDao, mockAuditLogDao, mockFollowDao);

        // 设置商品统计相关的mock
        when(mockProductDao.getNewProductCount(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(8L)   // 今日新增
            .thenReturn(30L)  // 本周新增
            .thenReturn(100L) // 本月新增（第一次调用）
            .thenReturn(100L) // 本月新增（第二次调用，用于增长率计算）
            .thenReturn(80L); // 上月新增
        when(mockProductDao.getProductCountByStatus(Product.STATUS_ONSALE)).thenReturn(150L);
        when(mockProductDao.getProductCountByStatus(Product.STATUS_SOLD)).thenReturn(25L);
        when(mockProductDao.getProductCountByStatus(Product.STATUS_DELISTED)).thenReturn(5L);

        // 设置其他必要的mock（使用默认值）
        setupBasicMockReturns();

        // When: 获取统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats();

        // Then: 验证商品统计数据
        DashboardStatsVO.ProductStats productStats = stats.getProductStats();
        assertNotNull(productStats, "商品统计不应为空");
        assertEquals(8L, productStats.getNewProductsToday(), "今日新增商品数应该匹配");
        assertEquals(30L, productStats.getNewProductsThisWeek(), "本周新增商品数应该匹配");
        assertEquals(100L, productStats.getNewProductsThisMonth(), "本月新增商品数应该匹配");
        assertEquals(150L, productStats.getOnSaleProducts(), "在售商品数应该匹配");
        assertEquals(25L, productStats.getSoldProducts(), "已售商品数应该匹配");
        assertEquals(5L, productStats.getRemovedProducts(), "已下架商品数应该匹配");

        // 验证增长率计算 (100-80)/80 * 100 = 25%
        assertEquals(25.0, productStats.getProductGrowthRate(), 0.01, "商品增长率应该匹配");
    }
    
    /**
     * 测试趋势数据计算
     * BCDE原则中的Correct：测试趋势数据处理
     */
    @Test
    public void testTrendDataCalculation() {
        // Given: 重置所有mock，设置特定的趋势数据
        reset(mockUserDao, mockProductDao, mockAuditLogDao, mockFollowDao);

        // 设置其他必要的mock（使用默认值）
        setupBasicMockReturns();

        // 模拟趋势数据（在基础mock之后设置，避免被覆盖）
        List<Map<String, Object>> userTrendData = new ArrayList<>();
        Map<String, Object> trendItem = new HashMap<>();
        trendItem.put("date", "2024-01-01");
        trendItem.put("count", 10L);
        userTrendData.add(trendItem);

        when(mockUserDao.getUserGrowthTrend(30)).thenReturn(userTrendData);
        when(mockProductDao.getProductGrowthTrend(30)).thenReturn(new ArrayList<>());
        when(mockAuditLogDao.getActivityTrend(30)).thenReturn(new ArrayList<>());

        // When: 获取统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats();

        // Then: 验证趋势数据
        assertNotNull(stats.getUserTrend(), "用户趋势数据不应为空");
        assertEquals(1, stats.getUserTrend().size(), "用户趋势数据数量应该匹配");

        DashboardStatsVO.TrendData trendData = stats.getUserTrend().get(0);
        assertEquals("2024-01-01", trendData.getDate(), "趋势数据日期应该匹配");
        assertEquals(10L, trendData.getValue(), "趋势数据值应该匹配");
        assertEquals("新增用户", trendData.getLabel(), "趋势数据标签应该匹配");
    }
    
    /**
     * 测试异常处理
     * BCDE原则中的Error：测试错误条件
     */
    @Test
    public void testExceptionHandling() {
        // Given: 模拟DAO抛出异常
        when(mockUserDao.getTotalUserCount()).thenThrow(new RuntimeException("Database error"));
        
        // When: 获取统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats();
        
        // Then: 应该返回默认值而不是抛出异常
        assertNotNull(stats, "即使发生异常也应该返回统计数据");
        assertNotNull(stats.getOverview(), "总览统计应该有默认值");
        assertEquals(0L, stats.getOverview().getTotalUsers(), "异常时应该返回默认值0");
    }
    
    /**
     * 测试增长率计算边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testGrowthRateCalculationBorderCases() {
        // Given: 重置所有mock，设置边界条件数据
        reset(mockUserDao, mockProductDao, mockAuditLogDao, mockFollowDao);

        // 模拟边界条件：当前月10个，上月0个
        when(mockUserDao.getNewUserCount(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(5L)  // 今日新增
            .thenReturn(20L) // 本周新增
            .thenReturn(10L) // 本月新增（第一次调用）
            .thenReturn(10L) // 本月新增（第二次调用，用于增长率计算）
            .thenReturn(0L); // 上月新增（之前值为0）

        // 设置其他必要的mock（使用默认值）
        setupBasicMockReturns();

        // When: 获取统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats();

        // Then: 验证边界条件处理
        DashboardStatsVO.UserStats userStats = stats.getUserStats();
        assertEquals(100.0, userStats.getUserGrowthRate(), 0.01, "之前值为0时增长率应该为100%");
    }
    
    /**
     * 设置Mock DAO的返回值（用于主要测试）
     */
    private void setupMockDaoReturns() {
        // 用户相关mock
        when(mockUserDao.getTotalUserCount()).thenReturn(100L);
        when(mockUserDao.getActiveUserCount()).thenReturn(50L);
        when(mockUserDao.getAverageRating()).thenReturn(4.5);
        when(mockUserDao.getNewUserCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(10L);
        when(mockUserDao.getUserCountByStatus(anyInt())).thenReturn(5L);
        when(mockUserDao.getUserGrowthTrend(anyInt())).thenReturn(new ArrayList<>());

        // 商品相关mock
        when(mockProductDao.getTotalProductCount()).thenReturn(200L);
        when(mockProductDao.getProductCountByStatus(Product.STATUS_PENDING_REVIEW)).thenReturn(10L);
        when(mockProductDao.getProductCountByStatus(Product.STATUS_ONSALE)).thenReturn(150L);
        when(mockProductDao.getProductCountByStatus(Product.STATUS_DELISTED)).thenReturn(40L);
        when(mockProductDao.getNewProductCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(15L);
        when(mockProductDao.getProductGrowthTrend(anyInt())).thenReturn(new ArrayList<>());

        // 关注关系mock
        when(mockFollowDao.getTotalFollowCount()).thenReturn(80L);

        // 审计日志mock
        when(mockAuditLogDao.getAuditLogCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(5L);
        when(mockAuditLogDao.getAdminLoginCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(3L);
        when(mockAuditLogDao.getSystemErrorCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(1L);
        when(mockAuditLogDao.getActivityTrend(anyInt())).thenReturn(new ArrayList<>());
    }

    /**
     * 设置基础Mock DAO的返回值（用于特定测试）
     */
    private void setupBasicMockReturns() {
        // 用户相关基础mock
        when(mockUserDao.getTotalUserCount()).thenReturn(100L);
        when(mockUserDao.getActiveUserCount()).thenReturn(50L);
        when(mockUserDao.getAverageRating()).thenReturn(4.5);
        when(mockUserDao.getUserGrowthTrend(anyInt())).thenReturn(new ArrayList<>());

        // 商品相关基础mock
        when(mockProductDao.getTotalProductCount()).thenReturn(200L);
        when(mockProductDao.getProductCountByStatus(Product.STATUS_PENDING_REVIEW)).thenReturn(10L);
        when(mockProductDao.getProductGrowthTrend(anyInt())).thenReturn(new ArrayList<>());

        // 关注关系基础mock
        when(mockFollowDao.getTotalFollowCount()).thenReturn(80L);

        // 审计日志基础mock
        when(mockAuditLogDao.getAuditLogCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(5L);
        when(mockAuditLogDao.getAdminLoginCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(3L);
        when(mockAuditLogDao.getSystemErrorCount(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(1L);
        when(mockAuditLogDao.getActivityTrend(anyInt())).thenReturn(new ArrayList<>());
    }
}
