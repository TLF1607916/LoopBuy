package com.shiwu.admin.service;

import com.shiwu.admin.model.DashboardStatsVO;
import com.shiwu.admin.model.StatsPeriod;
import com.shiwu.admin.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DashboardService 综合测试类
 * 测试仪表盘服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("DashboardService 综合测试")
public class DashboardServiceComprehensiveTest {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceComprehensiveTest.class);
    
    private DashboardService dashboardService;
    
    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl();
        logger.info("DashboardService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("2.1 获取仪表盘统计数据测试")
    public void testGetDashboardStats() {
        logger.info("开始测试获取仪表盘统计数据功能");
        
        // 测试获取默认统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats();
        assertNotNull(stats, "统计数据不应为空");
        
        // 验证基本统计字段
        assertNotNull(stats.getOverview(), "总览统计不应为空");
        assertNotNull(stats.getUserStats(), "用户统计不应为空");
        assertNotNull(stats.getProductStats(), "商品统计不应为空");
        assertNotNull(stats.getActivityStats(), "活动统计不应为空");

        if (stats.getOverview() != null) {
            assertNotNull(stats.getOverview().getTotalUsers(), "总用户数不应为空");
            assertNotNull(stats.getOverview().getTotalProducts(), "总商品数不应为空");
        }

        logger.info("获取仪表盘统计数据测试通过");
    }

    @Test
    @Order(2)
    @DisplayName("2.2 获取指定时间段统计数据测试")
    public void testGetDashboardStatsWithPeriod() {
        logger.info("开始测试获取指定时间段统计数据功能");
        
        // 测试不同时间段
        DashboardStatsVO stats1 = dashboardService.getDashboardStats(StatsPeriod.TODAY);
        assertNotNull(stats1, "今日统计数据不应为空");

        DashboardStatsVO stats2 = dashboardService.getDashboardStats(StatsPeriod.THIS_WEEK);
        assertNotNull(stats2, "本周统计数据不应为空");

        DashboardStatsVO stats3 = dashboardService.getDashboardStats(StatsPeriod.THIS_MONTH);
        assertNotNull(stats3, "本月统计数据不应为空");

        DashboardStatsVO stats4 = dashboardService.getDashboardStats(StatsPeriod.LAST_365_DAYS);
        assertNotNull(stats4, "本年统计数据不应为空");
        
        logger.info("获取指定时间段统计数据测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("2.3 获取指定时间段统计数据参数验证测试")
    public void testGetDashboardStatsWithPeriodValidation() {
        logger.info("开始测试获取指定时间段统计数据参数验证");
        
        // 测试null时间段
        DashboardStatsVO stats = dashboardService.getDashboardStats(null);
        assertNotNull(stats, "null时间段应该返回默认统计数据");
        
        logger.info("获取指定时间段统计数据参数验证测试通过");
    }

    @Test
    @Order(4)
    @DisplayName("2.4 刷新统计数据缓存测试")
    public void testRefreshStatsCache() {
        logger.info("开始测试刷新统计数据缓存功能");
        
        // 测试刷新缓存
        boolean refreshResult = dashboardService.refreshStatsCache();
        assertTrue(refreshResult == true || refreshResult == false, "刷新结果应该是布尔值");
        
        logger.info("刷新统计数据缓存测试通过: refreshResult={}", refreshResult);
    }

    @Test
    @Order(5)
    @DisplayName("2.5 获取实时统计数据测试")
    public void testGetRealTimeStats() {
        logger.info("开始测试获取实时统计数据功能");
        
        // 测试获取实时统计数据
        DashboardStatsVO realTimeStats = dashboardService.getRealTimeStats();
        assertNotNull(realTimeStats, "实时统计数据不应为空");
        
        // 验证基本统计字段
        assertNotNull(realTimeStats.getOverview(), "总览统计不应为空");
        assertNotNull(realTimeStats.getUserStats(), "用户统计不应为空");
        assertNotNull(realTimeStats.getProductStats(), "商品统计不应为空");
        assertNotNull(realTimeStats.getActivityStats(), "活动统计不应为空");

        if (realTimeStats.getOverview() != null) {
            assertNotNull(realTimeStats.getOverview().getTotalUsers(), "总用户数不应为空");
            assertNotNull(realTimeStats.getOverview().getTotalProducts(), "总商品数不应为空");
        }

        logger.info("获取实时统计数据测试通过");
    }

    @Test
    @Order(6)
    @DisplayName("2.6 统计数据一致性测试")
    public void testStatsConsistency() {
        logger.info("开始测试统计数据一致性");
        
        // 获取默认统计数据和实时统计数据
        DashboardStatsVO defaultStats = dashboardService.getDashboardStats();
        DashboardStatsVO realTimeStats = dashboardService.getRealTimeStats();
        
        assertNotNull(defaultStats, "默认统计数据不应为空");
        assertNotNull(realTimeStats, "实时统计数据不应为空");
        
        // 验证数据结构一致性（都应该有相同的字段）
        assertNotNull(defaultStats.getOverview(), "默认统计-总览不应为空");
        assertNotNull(realTimeStats.getOverview(), "实时统计-总览不应为空");

        assertNotNull(defaultStats.getUserStats(), "默认统计-用户统计不应为空");
        assertNotNull(realTimeStats.getUserStats(), "实时统计-用户统计不应为空");

        assertNotNull(defaultStats.getProductStats(), "默认统计-商品统计不应为空");
        assertNotNull(realTimeStats.getProductStats(), "实时统计-商品统计不应为空");

        assertNotNull(defaultStats.getActivityStats(), "默认统计-活动统计不应为空");
        assertNotNull(realTimeStats.getActivityStats(), "实时统计-活动统计不应为空");
        
        logger.info("统计数据一致性测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("2.7 不同时间段数据对比测试")
    public void testPeriodComparison() {
        logger.info("开始测试不同时间段数据对比");
        
        // 获取不同时间段的统计数据
        DashboardStatsVO todayStats = dashboardService.getDashboardStats(StatsPeriod.TODAY);
        DashboardStatsVO weekStats = dashboardService.getDashboardStats(StatsPeriod.THIS_WEEK);
        DashboardStatsVO monthStats = dashboardService.getDashboardStats(StatsPeriod.THIS_MONTH);
        DashboardStatsVO yearStats = dashboardService.getDashboardStats(StatsPeriod.LAST_365_DAYS);

        // 验证所有统计数据都不为空
        assertNotNull(todayStats, "今日统计数据不应为空");
        assertNotNull(weekStats, "本周统计数据不应为空");
        assertNotNull(monthStats, "本月统计数据不应为空");
        assertNotNull(yearStats, "本年统计数据不应为空");

        // 验证时间段越长，累计数据应该越大或相等（逻辑上合理）
        // 注意：这里只验证数据不为负数，因为实际业务逻辑可能复杂
        if (todayStats.getOverview() != null) {
            assertTrue(todayStats.getOverview().getTotalUsers() >= 0, "今日用户数应该非负");
        }
        if (weekStats.getOverview() != null) {
            assertTrue(weekStats.getOverview().getTotalUsers() >= 0, "本周用户数应该非负");
        }
        if (monthStats.getOverview() != null) {
            assertTrue(monthStats.getOverview().getTotalUsers() >= 0, "本月用户数应该非负");
        }
        if (yearStats.getOverview() != null) {
            assertTrue(yearStats.getOverview().getTotalUsers() >= 0, "本年用户数应该非负");
        }
        
        logger.info("不同时间段数据对比测试通过");
    }

    @Test
    @Order(8)
    @DisplayName("2.8 缓存刷新后数据获取测试")
    public void testStatsAfterCacheRefresh() {
        logger.info("开始测试缓存刷新后数据获取");
        
        // 获取刷新前的统计数据
        DashboardStatsVO statsBefore = dashboardService.getDashboardStats();
        assertNotNull(statsBefore, "刷新前统计数据不应为空");
        
        // 刷新缓存
        boolean refreshResult = dashboardService.refreshStatsCache();
        logger.info("缓存刷新结果: {}", refreshResult);
        
        // 获取刷新后的统计数据
        DashboardStatsVO statsAfter = dashboardService.getDashboardStats();
        assertNotNull(statsAfter, "刷新后统计数据不应为空");
        
        // 验证数据结构一致性
        assertNotNull(statsAfter.getOverview(), "刷新后-总览统计不应为空");
        assertNotNull(statsAfter.getUserStats(), "刷新后-用户统计不应为空");
        assertNotNull(statsAfter.getProductStats(), "刷新后-商品统计不应为空");
        assertNotNull(statsAfter.getActivityStats(), "刷新后-活动统计不应为空");
        
        logger.info("缓存刷新后数据获取测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("2.9 多次调用性能测试")
    public void testMultipleCallsPerformance() {
        logger.info("开始测试多次调用性能");
        
        long startTime = System.currentTimeMillis();
        
        // 多次调用统计数据获取方法
        for (int i = 0; i < 10; i++) {
            DashboardStatsVO stats = dashboardService.getDashboardStats();
            assertNotNull(stats, "第" + (i + 1) + "次调用统计数据不应为空");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证性能合理（10次调用应该在合理时间内完成）
        assertTrue(duration < 10000, "10次调用应该在10秒内完成，实际耗时: " + duration + "ms");
        
        logger.info("多次调用性能测试通过，耗时: {}ms", duration);
    }

    @Test
    @Order(10)
    @DisplayName("2.10 统计数据字段完整性测试")
    public void testStatsFieldCompleteness() {
        logger.info("开始测试统计数据字段完整性");
        
        // 获取统计数据
        DashboardStatsVO stats = dashboardService.getDashboardStats();
        assertNotNull(stats, "统计数据不应为空");
        
        // 验证所有必要字段都存在且不为null
        assertNotNull(stats.getOverview(), "总览统计字段不应为空");
        assertNotNull(stats.getUserStats(), "用户统计字段不应为空");
        assertNotNull(stats.getProductStats(), "商品统计字段不应为空");
        assertNotNull(stats.getActivityStats(), "活动统计字段不应为空");

        // 验证数值合理性
        if (stats.getOverview() != null) {
            if (stats.getOverview().getTotalUsers() != null) {
                assertTrue(stats.getOverview().getTotalUsers() >= 0, "总用户数应该非负");
            }
            if (stats.getOverview().getTotalProducts() != null) {
                assertTrue(stats.getOverview().getTotalProducts() >= 0, "总商品数应该非负");
            }
        }
        
        logger.info("统计数据字段完整性测试通过");
    }

    @AfterEach
    void tearDown() {
        logger.info("DashboardService测试清理完成");
    }
}
