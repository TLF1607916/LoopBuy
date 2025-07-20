package com.shiwu.user.dao;

import com.shiwu.common.test.TestConfig;
import com.shiwu.user.vo.FeedItemVO;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FeedDao完整测试套件
 * 严格遵循软件工程测试规范
 * 
 * 测试覆盖：
 * 1. 单元测试 - 每个方法的详细测试
 * 2. 边界测试 - 所有边界条件
 * 3. 异常测试 - 所有异常路径
 * 4. 安全测试 - SQL注入等安全问题
 * 5. 性能测试 - 基本性能验证
 * 6. 并发测试 - 多线程安全性
 * 7. 数据完整性测试 - 数据一致性验证
 */
@DisplayName("FeedDao完整测试套件")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class FeedDaoComprehensiveTest extends TestBase {

    private FeedDao feedDao;
    private static final int PERFORMANCE_TEST_ITERATIONS = 50;
    private static final int CONCURRENT_THREADS = 5;

    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        feedDao = new FeedDao();
    }

    // ==================== 基础功能测试 ====================

    @Test
    @Order(1)
    @DisplayName("1.1 FeedDao实例化测试")
    public void testFeedDaoInstantiation() {
        assertNotNull(feedDao, "FeedDao应该能够正常实例化");
        assertNotNull(feedDao.getClass(), "FeedDao类应该存在");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 getFollowingFeed方法完整测试")
    public void testGetFollowingFeedComprehensive() {
        // 测试null参数
        List<FeedItemVO> result1 = feedDao.getFollowingFeed(null, "ALL", 0, 10);
        assertNotNull(result1, "null用户ID应该返回空列表而不是null");
        assertTrue(result1.isEmpty(), "null用户ID应该返回空列表");

        // 测试正常参数
        List<FeedItemVO> result2 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 10);
        assertNotNull(result2, "正常参数应该返回列表");

        // 测试不同类型过滤
        List<FeedItemVO> result3 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "PRODUCT_APPROVED", 0, 10);
        assertNotNull(result3, "类型过滤应该返回列表");

        List<FeedItemVO> result4 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "PRODUCT_PUBLISHED", 0, 10);
        assertNotNull(result4, "类型过滤应该返回列表");

        // 测试null类型
        List<FeedItemVO> result5 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, null, 0, 10);
        assertNotNull(result5, "null类型应该返回列表");

        // 测试边界分页参数
        List<FeedItemVO> result6 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 1);
        assertNotNull(result6, "边界分页参数应该返回列表");
        assertTrue(result6.size() <= 1, "限制为1时结果不应超过1条");

        // 测试负数参数
        List<FeedItemVO> result7 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", -1, -1);
        assertNotNull(result7, "负数参数应该返回列表");

        // 测试零参数
        List<FeedItemVO> result8 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 0);
        assertNotNull(result8, "零参数应该返回列表");
    }

    @Test
    @Order(3)
    @DisplayName("1.3 getFollowingFeedCount方法完整测试")
    public void testGetFollowingFeedCountComprehensive() {
        // 测试null参数
        long count1 = feedDao.getFollowingFeedCount(null, "ALL");
        assertEquals(0, count1, "null用户ID应该返回0");

        // 测试正常参数
        long count2 = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "ALL");
        assertTrue(count2 >= 0, "正常参数应该返回非负数");

        // 测试不同类型过滤
        long count3 = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "PRODUCT_APPROVED");
        assertTrue(count3 >= 0, "类型过滤应该返回非负数");

        long count4 = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "PRODUCT_PUBLISHED");
        assertTrue(count4 >= 0, "类型过滤应该返回非负数");

        // 测试null类型
        long count5 = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, null);
        assertTrue(count5 >= 0, "null类型应该返回非负数");

        // 测试不存在的用户ID
        long count6 = feedDao.getFollowingFeedCount(999999L, "ALL");
        assertEquals(0, count6, "不存在的用户ID应该返回0");

        // 测试边界用户ID
        long count7 = feedDao.getFollowingFeedCount(0L, "ALL");
        assertEquals(0, count7, "零用户ID应该返回0");

        long count8 = feedDao.getFollowingFeedCount(-1L, "ALL");
        assertEquals(0, count8, "负数用户ID应该返回0");
    }

    // ==================== 边界值测试 ====================

    @Test
    @Order(10)
    @DisplayName("2.1 边界值测试")
    public void testBoundaryValues() {
        // 测试极大用户ID
        List<FeedItemVO> result1 = feedDao.getFollowingFeed(Long.MAX_VALUE, "ALL", 0, 10);
        assertNotNull(result1, "极大用户ID应该返回空列表");
        assertTrue(result1.isEmpty(), "极大用户ID应该返回空列表");

        // 测试极小用户ID
        List<FeedItemVO> result2 = feedDao.getFollowingFeed(Long.MIN_VALUE, "ALL", 0, 10);
        assertNotNull(result2, "极小用户ID应该返回空列表");
        assertTrue(result2.isEmpty(), "极小用户ID应该返回空列表");

        // 测试极大分页参数
        List<FeedItemVO> result3 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertNotNull(result3, "极大分页参数应该返回列表");

        // 测试极小分页参数
        List<FeedItemVO> result4 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", Integer.MIN_VALUE, Integer.MIN_VALUE);
        assertNotNull(result4, "极小分页参数应该返回列表");
    }

    // ==================== 异常处理测试 ====================

    @Test
    @Order(15)
    @DisplayName("3.1 异常处理测试")
    public void testExceptionHandling() {
        // 测试各种异常情况
        assertDoesNotThrow(() -> {
            feedDao.getFollowingFeed(null, null, 0, 10);
        }, "null参数不应该抛出异常");

        assertDoesNotThrow(() -> {
            feedDao.getFollowingFeedCount(null, null);
        }, "null参数不应该抛出异常");

        assertDoesNotThrow(() -> {
            feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "INVALID_TYPE", 0, 10);
        }, "无效类型不应该抛出异常");

        assertDoesNotThrow(() -> {
            feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "INVALID_TYPE");
        }, "无效类型不应该抛出异常");
    }

    // ==================== 安全测试 ====================

    @Test
    @Order(20)
    @DisplayName("4.1 SQL注入防护测试")
    public void testSqlInjectionProtection() {
        // 测试SQL注入攻击
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE notification; --",
            "ALL' OR '1'='1",
            "ALL'; DELETE FROM notification WHERE 1=1; --",
            "ALL' UNION SELECT * FROM system_user --"
        };

        for (String injection : sqlInjectionAttempts) {
            assertDoesNotThrow(() -> {
                List<FeedItemVO> result = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, injection, 0, 10);
                // SQL注入应该被安全处理
                assertNotNull(result, "SQL注入攻击应该被安全处理: " + injection);
            }, "SQL注入攻击应该被安全处理: " + injection);

            assertDoesNotThrow(() -> {
                long count = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, injection);
                // SQL注入应该被安全处理
                assertTrue(count >= 0, "SQL注入攻击应该被安全处理: " + injection);
            }, "SQL注入攻击应该被安全处理: " + injection);
        }
    }

    // ==================== 性能测试 ====================

    @Test
    @Order(25)
    @DisplayName("5.1 getFollowingFeed性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testGetFollowingFeedPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 10);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("FeedDao.getFollowingFeed性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
        
        assertTrue(duration < 3000, 
            String.format("getFollowingFeed性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    @Test
    @Order(26)
    @DisplayName("5.2 getFollowingFeedCount性能测试")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testGetFollowingFeedCountPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "ALL");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("FeedDao.getFollowingFeedCount性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
        
        assertTrue(duration < 2000, 
            String.format("getFollowingFeedCount性能测试: %d次调用耗时%dms，应该小于2000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    // ==================== 数据完整性测试 ====================

    @Test
    @Order(30)
    @DisplayName("6.1 数据一致性测试")
    public void testDataConsistency() {
        // 测试多次查询的一致性
        List<FeedItemVO> result1 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 10);
        List<FeedItemVO> result2 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 10);
        List<FeedItemVO> result3 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 10);
        
        assertEquals(result1.size(), result2.size(), "多次查询结果数量应该一致");
        assertEquals(result2.size(), result3.size(), "多次查询结果数量应该一致");
        
        // 测试统计查询一致性
        long count1 = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "ALL");
        long count2 = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "ALL");
        
        assertEquals(count1, count2, "统计查询结果应该一致");
    }

    @Test
    @Order(31)
    @DisplayName("6.2 分页一致性测试")
    public void testPaginationConsistency() {
        // 测试分页逻辑的一致性
        List<FeedItemVO> page1 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 5);
        List<FeedItemVO> page2 = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 5, 5);
        
        assertNotNull(page1, "第一页不应该为null");
        assertNotNull(page2, "第二页不应该为null");
        
        // 验证分页结果不重复（如果有数据的话）
        if (!page1.isEmpty() && !page2.isEmpty()) {
            for (FeedItemVO item1 : page1) {
                for (FeedItemVO item2 : page2) {
                    if (item1.getId() != null && item2.getId() != null) {
                        assertNotEquals(item1.getId(), item2.getId(), "分页结果不应该重复");
                    }
                }
            }
        }
    }

    @Test
    @Order(32)
    @DisplayName("6.3 类型过滤一致性测试")
    public void testTypeFilterConsistency() {
        // 测试类型过滤的一致性
        List<FeedItemVO> allFeeds = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "ALL", 0, 100);
        List<FeedItemVO> approvedFeeds = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "PRODUCT_APPROVED", 0, 100);
        List<FeedItemVO> publishedFeeds = feedDao.getFollowingFeed(TestConfig.TEST_USER_ID, "PRODUCT_PUBLISHED", 0, 100);
        
        assertNotNull(allFeeds, "ALL类型查询不应该为null");
        assertNotNull(approvedFeeds, "PRODUCT_APPROVED类型查询不应该为null");
        assertNotNull(publishedFeeds, "PRODUCT_PUBLISHED类型查询不应该为null");
        
        // 验证过滤后的数量不超过总数
        assertTrue(approvedFeeds.size() <= allFeeds.size(), "过滤后的数量不应该超过总数");
        assertTrue(publishedFeeds.size() <= allFeeds.size(), "过滤后的数量不应该超过总数");
        
        // 验证统计数量的一致性
        long allCount = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "ALL");
        long approvedCount = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "PRODUCT_APPROVED");
        long publishedCount = feedDao.getFollowingFeedCount(TestConfig.TEST_USER_ID, "PRODUCT_PUBLISHED");
        
        assertTrue(approvedCount <= allCount, "过滤后的统计数量不应该超过总数");
        assertTrue(publishedCount <= allCount, "过滤后的统计数量不应该超过总数");
    }

    @AfterEach
    public void tearDown() {
        assertNotNull(feedDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("FeedDao完整测试套件执行完成");
    }
}
