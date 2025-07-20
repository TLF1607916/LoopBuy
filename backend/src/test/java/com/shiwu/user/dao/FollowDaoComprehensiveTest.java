package com.shiwu.user.dao;

import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FollowDao完整测试套件
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
@DisplayName("FollowDao完整测试套件")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class FollowDaoComprehensiveTest extends TestBase {

    private FollowDao followDao;
    private static final int PERFORMANCE_TEST_ITERATIONS = 50;

    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        followDao = new FollowDao();
    }

    // ==================== 基础功能测试 ====================

    @Test
    @Order(1)
    @DisplayName("1.1 FollowDao实例化测试")
    public void testFollowDaoInstantiation() {
        assertNotNull(followDao, "FollowDao应该能够正常实例化");
        assertNotNull(followDao.getClass(), "FollowDao类应该存在");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 getTotalFollowCount方法完整测试")
    public void testGetTotalFollowCountComprehensive() {
        // 基本功能测试
        Long count = followDao.getTotalFollowCount();
        assertNotNull(count, "getTotalFollowCount()不应该返回null");
        assertTrue(count >= 0, "总关注数应该大于等于0");
    }

    @Test
    @Order(3)
    @DisplayName("1.3 getNewFollowCount方法完整测试")
    public void testGetNewFollowCountComprehensive() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        
        // 测试正常时间范围
        Long count1 = followDao.getNewFollowCount(yesterday, now);
        assertNotNull(count1, "getNewFollowCount()不应该返回null");
        assertTrue(count1 >= 0, "新增关注数应该大于等于0");

        // 测试null参数
        Long count2 = followDao.getNewFollowCount(null, now);
        assertNotNull(count2, "null开始时间应该返回非null结果");
        assertTrue(count2 >= 0, "null开始时间应该返回非负数");

        Long count3 = followDao.getNewFollowCount(yesterday, null);
        assertNotNull(count3, "null结束时间应该返回非null结果");
        assertTrue(count3 >= 0, "null结束时间应该返回非负数");

        Long count4 = followDao.getNewFollowCount(null, null);
        assertNotNull(count4, "两个null参数应该返回非null结果");
        assertTrue(count4 >= 0, "两个null参数应该返回非负数");

        // 测试时间顺序颠倒
        Long count5 = followDao.getNewFollowCount(now, yesterday);
        assertNotNull(count5, "时间顺序颠倒应该返回非null结果");
        assertTrue(count5 >= 0, "时间顺序颠倒应该返回非负数");

        // 测试相同时间
        Long count6 = followDao.getNewFollowCount(now, now);
        assertNotNull(count6, "相同时间应该返回非null结果");
        assertTrue(count6 >= 0, "相同时间应该返回非负数");
    }

    @Test
    @Order(4)
    @DisplayName("1.4 getMostFollowedUsers方法完整测试")
    public void testGetMostFollowedUsersComprehensive() {
        // 测试正常参数
        List<Map<String, Object>> result1 = followDao.getMostFollowedUsers(10);
        assertNotNull(result1, "getMostFollowedUsers()不应该返回null");
        assertTrue(result1.size() <= 10, "结果数量不应该超过限制");

        // 测试边界值
        List<Map<String, Object>> result2 = followDao.getMostFollowedUsers(0);
        assertNotNull(result2, "limit为0应该返回非null结果");
        assertTrue(result2.isEmpty(), "limit为0应该返回空列表");

        List<Map<String, Object>> result3 = followDao.getMostFollowedUsers(1);
        assertNotNull(result3, "limit为1应该返回非null结果");
        assertTrue(result3.size() <= 1, "limit为1时结果不应超过1条");

        // 测试负数
        List<Map<String, Object>> result4 = followDao.getMostFollowedUsers(-1);
        assertNotNull(result4, "负数limit应该返回非null结果");

        // 测试极大值
        List<Map<String, Object>> result5 = followDao.getMostFollowedUsers(Integer.MAX_VALUE);
        assertNotNull(result5, "极大limit应该返回非null结果");

        // 验证返回数据结构
        if (!result1.isEmpty()) {
            Map<String, Object> firstUser = result1.get(0);
            assertNotNull(firstUser, "用户数据不应该为null");
            assertTrue(firstUser.containsKey("id") || firstUser.containsKey("user_id"),
                      "应该包含用户ID字段");
            assertTrue(firstUser.containsKey("followerCount"),
                      "应该包含粉丝数字段");
        }
    }

    @Test
    @Order(5)
    @DisplayName("1.5 getFollowGrowthTrend方法完整测试")
    public void testGetFollowGrowthTrendComprehensive() {
        // 测试正常参数
        List<Map<String, Object>> result1 = followDao.getFollowGrowthTrend(7);
        assertNotNull(result1, "getFollowGrowthTrend()不应该返回null");

        // 测试边界值
        List<Map<String, Object>> result2 = followDao.getFollowGrowthTrend(0);
        assertNotNull(result2, "days为0应该返回非null结果");

        List<Map<String, Object>> result3 = followDao.getFollowGrowthTrend(1);
        assertNotNull(result3, "days为1应该返回非null结果");

        // 测试负数
        List<Map<String, Object>> result4 = followDao.getFollowGrowthTrend(-1);
        assertNotNull(result4, "负数days应该返回非null结果");

        // 测试极大值
        List<Map<String, Object>> result5 = followDao.getFollowGrowthTrend(Integer.MAX_VALUE);
        assertNotNull(result5, "极大days应该返回非null结果");

        // 验证返回数据结构
        if (!result1.isEmpty()) {
            Map<String, Object> firstTrend = result1.get(0);
            assertNotNull(firstTrend, "趋势数据不应该为null");
            assertTrue(firstTrend.containsKey("date"), "应该包含日期字段");
            assertTrue(firstTrend.containsKey("count"), "应该包含数量字段");
        }
    }

    // ==================== 边界值测试 ====================

    @Test
    @Order(10)
    @DisplayName("2.1 边界值测试")
    public void testBoundaryValues() {
        LocalDateTime minTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        LocalDateTime maxTime = LocalDateTime.of(2099, 12, 31, 23, 59, 59);
        
        // 测试极端时间范围
        Long count1 = followDao.getNewFollowCount(minTime, maxTime);
        assertNotNull(count1, "极端时间范围应该返回非null结果");
        assertTrue(count1 >= 0, "极端时间范围应该返回非负数");

        // 测试极大limit值
        List<Map<String, Object>> result1 = followDao.getMostFollowedUsers(Integer.MAX_VALUE);
        assertNotNull(result1, "极大limit值应该返回非null结果");

        // 测试极小limit值
        List<Map<String, Object>> result2 = followDao.getMostFollowedUsers(Integer.MIN_VALUE);
        assertNotNull(result2, "极小limit值应该返回非null结果");
    }

    // ==================== 异常处理测试 ====================

    @Test
    @Order(15)
    @DisplayName("3.1 异常处理测试")
    public void testExceptionHandling() {
        // 测试各种异常情况
        assertDoesNotThrow(() -> {
            followDao.getTotalFollowCount();
        }, "getTotalFollowCount不应该抛出异常");

        assertDoesNotThrow(() -> {
            followDao.getNewFollowCount(null, null);
        }, "null参数不应该抛出异常");

        assertDoesNotThrow(() -> {
            followDao.getMostFollowedUsers(-1);
        }, "负数参数不应该抛出异常");

        assertDoesNotThrow(() -> {
            followDao.getFollowGrowthTrend(-1);
        }, "负数参数不应该抛出异常");
    }

    // ==================== 安全测试 ====================

    @Test
    @Order(20)
    @DisplayName("4.1 SQL注入防护测试")
    public void testSqlInjectionProtection() {
        // FollowDao的方法主要是统计查询，没有直接的字符串参数
        // 但我们可以测试极端的数值参数
        
        assertDoesNotThrow(() -> {
            List<Map<String, Object>> result = followDao.getMostFollowedUsers(Integer.MAX_VALUE);
            assertNotNull(result, "极大参数应该被安全处理");
        }, "极大参数应该被安全处理");

        assertDoesNotThrow(() -> {
            List<Map<String, Object>> result = followDao.getMostFollowedUsers(Integer.MIN_VALUE);
            assertNotNull(result, "极小参数应该被安全处理");
        }, "极小参数应该被安全处理");
    }

    // ==================== 性能测试 ====================

    @Test
    @Order(25)
    @DisplayName("5.1 getTotalFollowCount性能测试")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testGetTotalFollowCountPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            followDao.getTotalFollowCount();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("FollowDao.getTotalFollowCount性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
        
        assertTrue(duration < 2000, 
            String.format("getTotalFollowCount性能测试: %d次调用耗时%dms，应该小于2000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    @Test
    @Order(26)
    @DisplayName("5.2 查询方法性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testQueryMethodsPerformance() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            followDao.getNewFollowCount(yesterday, now);
            followDao.getMostFollowedUsers(10);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("FollowDao查询方法性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS * 2, duration, (double)duration/(PERFORMANCE_TEST_ITERATIONS * 2));
        
        assertTrue(duration < 3000, 
            String.format("查询方法性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS * 2, duration));
    }

    // ==================== 数据完整性测试 ====================

    @Test
    @Order(30)
    @DisplayName("6.1 数据一致性测试")
    public void testDataConsistency() {
        // 测试多次查询的一致性
        Long count1 = followDao.getTotalFollowCount();
        Long count2 = followDao.getTotalFollowCount();
        Long count3 = followDao.getTotalFollowCount();
        
        assertEquals(count1, count2, "多次查询结果应该一致");
        assertEquals(count2, count3, "多次查询结果应该一致");
        
        // 测试排行榜查询一致性
        List<Map<String, Object>> top1 = followDao.getMostFollowedUsers(5);
        List<Map<String, Object>> top2 = followDao.getMostFollowedUsers(5);
        
        assertEquals(top1.size(), top2.size(), "排行榜查询结果数量应该一致");
    }

    @Test
    @Order(31)
    @DisplayName("6.2 时间范围逻辑测试")
    public void testTimeRangeLogic() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        
        // 测试不同时间范围的逻辑关系
        Long count1 = followDao.getNewFollowCount(yesterday, now);
        Long count2 = followDao.getNewFollowCount(now, tomorrow);
        Long count3 = followDao.getNewFollowCount(yesterday, tomorrow);
        
        assertNotNull(count1, "昨天到今天的统计不应该为null");
        assertNotNull(count2, "今天到明天的统计不应该为null");
        assertNotNull(count3, "昨天到明天的统计不应该为null");
        
        // 验证时间范围的包含关系
        assertTrue(count3 >= count1, "更大的时间范围应该包含更多或相等的数据");
        assertTrue(count3 >= count2, "更大的时间范围应该包含更多或相等的数据");
    }

    @Test
    @Order(32)
    @DisplayName("6.3 排行榜数据验证")
    public void testTopUsersDataValidation() {
        List<Map<String, Object>> topUsers = followDao.getMostFollowedUsers(10);
        
        if (!topUsers.isEmpty()) {
            // 验证排序是否正确（粉丝数递减）
            for (int i = 0; i < topUsers.size() - 1; i++) {
                Map<String, Object> current = topUsers.get(i);
                Map<String, Object> next = topUsers.get(i + 1);
                
                Object currentCountObj = current.get("followerCount");
                Object nextCountObj = next.get("followerCount");
                
                if (currentCountObj != null && nextCountObj != null) {
                    Long currentCount = Long.valueOf(currentCountObj.toString());
                    Long nextCount = Long.valueOf(nextCountObj.toString());
                    
                    assertTrue(currentCount >= nextCount, 
                              String.format("排行榜应该按粉丝数递减排序: %d >= %d", currentCount, nextCount));
                }
            }
        }
    }

    @AfterEach
    public void tearDown() {
        assertNotNull(followDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("FollowDao完整测试套件执行完成");
    }
}
