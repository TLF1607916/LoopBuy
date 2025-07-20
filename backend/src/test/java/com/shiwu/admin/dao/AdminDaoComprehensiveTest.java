package com.shiwu.admin.dao;

import com.shiwu.admin.model.Administrator;
import com.shiwu.common.test.TestConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminDao完整测试套件
 * 严格遵循软件工程测试规范
 */
@DisplayName("AdminDao完整测试套件")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class AdminDaoComprehensiveTest {

    private AdminDao adminDao;
    private static final int PERFORMANCE_TEST_ITERATIONS = 30;
    private static final int CONCURRENT_THREADS = 3;

    @BeforeEach
    public void setUp() {
        adminDao = new AdminDao();
    }

    // ==================== 基础功能测试 ====================

    @Test
    @Order(1)
    @DisplayName("1.1 AdminDao实例化测试")
    public void testAdminDaoInstantiation() {
        assertNotNull(adminDao, "AdminDao应该能够正常实例化");
        assertNotNull(adminDao.getClass(), "AdminDao类应该存在");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 findByUsername方法完整测试")
    public void testFindByUsernameComprehensive() {
        // 测试null参数
        Administrator result1 = adminDao.findByUsername(null);
        assertNull(result1, "findByUsername(null)应该返回null");

        // 测试空字符串
        Administrator result2 = adminDao.findByUsername("");
        assertNull(result2, "空字符串应该返回null");

        // 测试只有空格的字符串
        Administrator result3 = adminDao.findByUsername("   ");
        assertNull(result3, "只有空格的字符串应该返回null");

        // 测试不存在的用户名
        Administrator result4 = adminDao.findByUsername("nonexistent_admin");
        assertNull(result4, "不存在的用户名应该返回null");

        // 测试特殊字符
        for (String specialUsername : new String[]{
            TestConfig.SPECIAL_CHARS,
            TestConfig.SQL_INJECTION_1,
            TestConfig.CHINESE_CHARS
        }) {
            Administrator result = adminDao.findByUsername(specialUsername);
            assertNull(result, "特殊字符用户名应该返回null: " + specialUsername);
        }
    }

    @Test
    @Order(3)
    @DisplayName("1.3 findById方法完整测试")
    public void testFindByIdComprehensive() {
        // 测试null参数
        Administrator result1 = adminDao.findById(null);
        assertNull(result1, "findById(null)应该返回null");

        // 测试边界值
        for (Long id : TestConfig.getBoundaryIds()) {
            if (id != null && id > 0) {
                Administrator result = adminDao.findById(id);
                assertNull(result, "不存在的ID应该返回null: " + id);
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("1.4 updateLastLoginInfo方法完整测试")
    public void testUpdateLastLoginInfoComprehensive() {
        // 测试null参数
        boolean result1 = adminDao.updateLastLoginInfo(null);
        assertFalse(result1, "updateLastLoginInfo(null)应该返回false");

        // 测试不存在的管理员ID
        boolean result2 = adminDao.updateLastLoginInfo(TestConfig.BOUNDARY_ID_NONEXISTENT);
        assertFalse(result2, "不存在的管理员ID更新应该返回false");

        // 测试边界值
        for (Long id : TestConfig.getBoundaryIds()) {
            if (id != null && id > 0) {
                boolean result = adminDao.updateLastLoginInfo(id);
                assertFalse(result, "边界值管理员ID更新应该返回false: " + id);
            }
        }
    }

    // ==================== 性能测试 ====================

    @Test
    @Order(10)
    @DisplayName("2.1 findByUsername性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testFindByUsernamePerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            adminDao.findByUsername("nonexistent_admin_" + i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 3000, 
            String.format("findByUsername性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
        
        System.out.printf("AdminDao.findByUsername性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 findById性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testFindByIdPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            adminDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT + i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 3000, 
            String.format("findById性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 updateLastLoginInfo性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testUpdateLastLoginInfoPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            adminDao.updateLastLoginInfo(TestConfig.BOUNDARY_ID_NONEXISTENT + i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 3000, 
            String.format("updateLastLoginInfo性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    // ==================== 并发测试 ====================

    @Test
    @Order(20)
    @DisplayName("3.1 并发查询管理员测试")
    public void testConcurrentFindAdmin() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        adminDao.findByUsername("test_admin_" + threadId + "_" + j);
                        adminDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT + threadId);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS), "并发测试应该在10秒内完成");
        executor.shutdown();
    }

    @Test
    @Order(21)
    @DisplayName("3.2 并发更新登录信息测试")
    public void testConcurrentUpdateLoginInfo() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 3; j++) {
                        adminDao.updateLastLoginInfo(TestConfig.BOUNDARY_ID_NONEXISTENT + threadId);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(8, TimeUnit.SECONDS), "并发更新测试应该在8秒内完成");
        executor.shutdown();
    }

    // ==================== 安全测试 ====================

    @Test
    @Order(30)
    @DisplayName("4.1 SQL注入防护测试")
    public void testSqlInjectionProtection() {
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE administrator; --",
            "1' OR '1'='1",
            "admin'--",
            "' UNION SELECT * FROM administrator --",
            "'; INSERT INTO administrator VALUES (1, 'hack', 'password', 'hack@test.com', 'Hacker', 'ADMIN', 1, NOW(), 0, 0, NOW(), NOW()); --"
        };
        
        for (String injection : sqlInjectionAttempts) {
            // 测试用户名查询注入
            Administrator result = adminDao.findByUsername(injection);
            assertNull(result, "SQL注入应该被防护: " + injection);
        }
    }

    // ==================== 数据完整性测试 ====================

    @Test
    @Order(40)
    @DisplayName("5.1 数据一致性测试")
    public void testDataConsistency() {
        // 测试多次查询的一致性
        Administrator result1 = adminDao.findByUsername("nonexistent_admin");
        Administrator result2 = adminDao.findByUsername("nonexistent_admin");
        Administrator result3 = adminDao.findByUsername("nonexistent_admin");
        
        assertEquals(result1, result2, "多次查询结果应该一致");
        assertEquals(result2, result3, "多次查询结果应该一致");
        
        // 测试ID查询一致性
        Administrator id1 = adminDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        Administrator id2 = adminDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        
        assertEquals(id1, id2, "ID查询结果应该一致");
    }

    @Test
    @Order(41)
    @DisplayName("5.2 参数验证边界测试")
    public void testParameterValidationBoundaries() {
        // 测试用户名边界
        String[] usernames = {
            "",
            " ",
            "a",
            TestConfig.LONG_STRING_100,
            TestConfig.LONG_STRING_1000
        };
        
        for (String username : usernames) {
            Administrator result = adminDao.findByUsername(username);
            assertNull(result, "边界用户名应该返回null: " + username);
        }
        
        // 测试更新登录信息边界
        Long[] ids = {0L, -1L, Long.MAX_VALUE, Long.MIN_VALUE};
        for (Long id : ids) {
            boolean result = adminDao.updateLastLoginInfo(id);
            assertFalse(result, "边界ID更新应该返回false: " + id);
        }
    }

    // ==================== 压力测试 ====================

    @Test
    @Order(50)
    @DisplayName("6.1 高频调用压力测试")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testHighFrequencyStress() {
        final int STRESS_ITERATIONS = 100;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < STRESS_ITERATIONS; i++) {
            adminDao.findByUsername("stress_test_" + i);
            adminDao.findById((long) (i % 50));
            if (i % 5 == 0) {
                adminDao.updateLastLoginInfo((long) i);
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 10000, 
            String.format("压力测试: %d次调用耗时%dms，应该小于10000ms", 
                         STRESS_ITERATIONS * 2 + STRESS_ITERATIONS / 5, duration));
        
        System.out.printf("AdminDao压力测试完成: %d次调用耗时%dms%n", 
                         STRESS_ITERATIONS * 2 + STRESS_ITERATIONS / 5, duration);
    }

    // ==================== 清理和验证 ====================

    @AfterEach
    public void tearDown() {
        assertNotNull(adminDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("AdminDao完整测试套件执行完成");
    }
}
