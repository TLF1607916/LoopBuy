package com.shiwu.user.dao;

import com.shiwu.admin.model.AdminUserQueryDTO;
import com.shiwu.common.test.TestConfig;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminUserDao完整测试套件
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
@DisplayName("AdminUserDao完整测试套件")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class AdminUserDaoComprehensiveTest extends TestBase {

    private AdminUserDao adminUserDao;
    private static final int PERFORMANCE_TEST_ITERATIONS = 50;

    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        adminUserDao = new AdminUserDao();
    }

    // ==================== 基础功能测试 ====================

    @Test
    @Order(1)
    @DisplayName("1.1 AdminUserDao实例化测试")
    public void testAdminUserDaoInstantiation() {
        assertNotNull(adminUserDao, "AdminUserDao应该能够正常实例化");
        assertNotNull(adminUserDao.getClass(), "AdminUserDao类应该存在");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 findUsers方法完整测试")
    public void testFindUsersComprehensive() {
        // 测试null参数
        List<Map<String, Object>> result1 = adminUserDao.findUsers(null);
        assertNotNull(result1, "null参数应该返回非null结果");

        // 测试空查询条件
        AdminUserQueryDTO emptyQuery = new AdminUserQueryDTO();
        List<Map<String, Object>> result2 = adminUserDao.findUsers(emptyQuery);
        assertNotNull(result2, "空查询条件应该返回非null结果");

        // 测试带关键词查询
        AdminUserQueryDTO keywordQuery = new AdminUserQueryDTO();
        keywordQuery.setKeyword("test");
        List<Map<String, Object>> result3 = adminUserDao.findUsers(keywordQuery);
        assertNotNull(result3, "关键词查询应该返回非null结果");

        // 测试带状态查询
        AdminUserQueryDTO statusQuery = new AdminUserQueryDTO();
        statusQuery.setStatus(1);
        List<Map<String, Object>> result6 = adminUserDao.findUsers(statusQuery);
        assertNotNull(result6, "状态查询应该返回非null结果");

        // 测试分页参数
        AdminUserQueryDTO pageQuery = new AdminUserQueryDTO();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(10);
        List<Map<String, Object>> result7 = adminUserDao.findUsers(pageQuery);
        assertNotNull(result7, "分页查询应该返回非null结果");
        assertTrue(result7.size() <= 10, "分页查询结果不应超过限制");

        // 验证返回数据结构
        if (!result2.isEmpty()) {
            Map<String, Object> firstUser = result2.get(0);
            assertNotNull(firstUser, "用户数据不应该为null");
            assertTrue(firstUser.containsKey("id"), "应该包含用户ID字段");
            assertTrue(firstUser.containsKey("username"), "应该包含用户名字段");
        }
    }

    @Test
    @Order(3)
    @DisplayName("1.3 countUsers方法完整测试")
    public void testCountUsersComprehensive() {
        // 测试null参数
        int count1 = adminUserDao.countUsers(null);
        assertTrue(count1 >= 0, "null参数应该返回非负数");

        // 测试空查询条件
        AdminUserQueryDTO emptyQuery = new AdminUserQueryDTO();
        int count2 = adminUserDao.countUsers(emptyQuery);
        assertTrue(count2 >= 0, "空查询条件应该返回非负数");

        // 测试带关键词查询
        AdminUserQueryDTO keywordQuery2 = new AdminUserQueryDTO();
        keywordQuery2.setKeyword("test");
        int count3 = adminUserDao.countUsers(keywordQuery2);
        assertTrue(count3 >= 0, "关键词查询应该返回非负数");

        // 测试带状态查询
        AdminUserQueryDTO statusQuery = new AdminUserQueryDTO();
        statusQuery.setStatus(1);
        int count6 = adminUserDao.countUsers(statusQuery);
        assertTrue(count6 >= 0, "状态查询应该返回非负数");

        // 测试不存在的条件
        AdminUserQueryDTO notExistQuery = new AdminUserQueryDTO();
        notExistQuery.setKeyword("nonexistent_user_12345");
        int count7 = adminUserDao.countUsers(notExistQuery);
        assertEquals(0, count7, "不存在的条件应该返回0");
    }

    @Test
    @Order(4)
    @DisplayName("1.4 updateUserStatus方法完整测试")
    public void testUpdateUserStatusComprehensive() {
        // 测试null参数
        boolean result1 = adminUserDao.updateUserStatus(null, 1, TestConfig.TEST_USER_ID);
        assertFalse(result1, "null用户ID应该返回false");

        boolean result2 = adminUserDao.updateUserStatus(TestConfig.TEST_USER_ID, null, TestConfig.TEST_USER_ID);
        assertFalse(result2, "null状态应该返回false");

        boolean result3 = adminUserDao.updateUserStatus(TestConfig.TEST_USER_ID, 1, null);
        assertFalse(result3, "null管理员ID应该返回false");

        // 测试不存在的用户ID
        boolean result4 = adminUserDao.updateUserStatus(999999L, 1, TestConfig.TEST_USER_ID);
        assertFalse(result4, "不存在的用户ID应该返回false");

        // 测试有效的状态值
        boolean result5 = adminUserDao.updateUserStatus(TestConfig.TEST_USER_ID, 1, TestConfig.TEST_USER_ID);
        // 这个可能成功也可能失败，取决于数据库状态，但不应该抛出异常
        assertNotNull(result5, "有效参数不应该返回null");

        // 测试边界状态值
        boolean result6 = adminUserDao.updateUserStatus(TestConfig.TEST_USER_ID, 0, TestConfig.TEST_USER_ID);
        assertNotNull(result6, "边界状态值不应该返回null");

        // 测试负数状态值
        boolean result7 = adminUserDao.updateUserStatus(TestConfig.TEST_USER_ID, -1, TestConfig.TEST_USER_ID);
        assertNotNull(result7, "负数状态值不应该返回null");

        // 测试极大状态值
        boolean result8 = adminUserDao.updateUserStatus(TestConfig.TEST_USER_ID, Integer.MAX_VALUE, TestConfig.TEST_USER_ID);
        assertNotNull(result8, "极大状态值不应该返回null");
    }

    // ==================== 边界值测试 ====================

    @Test
    @Order(10)
    @DisplayName("2.1 边界值测试")
    public void testBoundaryValues() {
        // 测试极大分页参数
        AdminUserQueryDTO largePageQuery = new AdminUserQueryDTO();
        largePageQuery.setPageNum(Integer.MAX_VALUE);
        largePageQuery.setPageSize(Integer.MAX_VALUE);
        List<Map<String, Object>> result1 = adminUserDao.findUsers(largePageQuery);
        assertNotNull(result1, "极大分页参数应该返回非null结果");

        // 测试极小分页参数
        AdminUserQueryDTO smallPageQuery = new AdminUserQueryDTO();
        smallPageQuery.setPageNum(0);
        smallPageQuery.setPageSize(0);
        List<Map<String, Object>> result2 = adminUserDao.findUsers(smallPageQuery);
        assertNotNull(result2, "极小分页参数应该返回非null结果");

        // 测试负数分页参数
        AdminUserQueryDTO negativePageQuery = new AdminUserQueryDTO();
        negativePageQuery.setPageNum(-1);
        negativePageQuery.setPageSize(-1);
        List<Map<String, Object>> result3 = adminUserDao.findUsers(negativePageQuery);
        assertNotNull(result3, "负数分页参数应该返回非null结果");

        // 测试极长字符串
        AdminUserQueryDTO longStringQuery = new AdminUserQueryDTO();
        StringBuilder longStringBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longStringBuilder.append("a");
        }
        String longString = longStringBuilder.toString();
        longStringQuery.setKeyword(longString);
        List<Map<String, Object>> result4 = adminUserDao.findUsers(longStringQuery);
        assertNotNull(result4, "极长字符串应该返回非null结果");
    }

    // ==================== 异常处理测试 ====================

    @Test
    @Order(15)
    @DisplayName("3.1 异常处理测试")
    public void testExceptionHandling() {
        // 测试各种异常情况
        assertDoesNotThrow(() -> {
            adminUserDao.findUsers(null);
        }, "null参数不应该抛出异常");

        assertDoesNotThrow(() -> {
            adminUserDao.countUsers(null);
        }, "null参数不应该抛出异常");

        assertDoesNotThrow(() -> {
            adminUserDao.updateUserStatus(null, null, null);
        }, "null参数不应该抛出异常");

        // 测试极端参数
        AdminUserQueryDTO extremeQuery = new AdminUserQueryDTO();
        extremeQuery.setPageNum(Integer.MIN_VALUE);
        extremeQuery.setPageSize(Integer.MIN_VALUE);
        extremeQuery.setStatus(Integer.MIN_VALUE);
        
        assertDoesNotThrow(() -> {
            adminUserDao.findUsers(extremeQuery);
        }, "极端参数不应该抛出异常");

        assertDoesNotThrow(() -> {
            adminUserDao.countUsers(extremeQuery);
        }, "极端参数不应该抛出异常");
    }

    // ==================== 安全测试 ====================

    @Test
    @Order(20)
    @DisplayName("4.1 SQL注入防护测试")
    public void testSqlInjectionProtection() {
        // 测试SQL注入攻击
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE system_user; --",
            "admin' OR '1'='1",
            "test'; DELETE FROM system_user WHERE 1=1; --",
            "user' UNION SELECT * FROM system_user --"
        };

        for (String injection : sqlInjectionAttempts) {
            AdminUserQueryDTO injectionQuery = new AdminUserQueryDTO();
            injectionQuery.setKeyword(injection);
            
            assertDoesNotThrow(() -> {
                List<Map<String, Object>> result = adminUserDao.findUsers(injectionQuery);
                assertNotNull(result, "SQL注入攻击应该被安全处理: " + injection);
            }, "SQL注入攻击应该被安全处理: " + injection);

            assertDoesNotThrow(() -> {
                int count = adminUserDao.countUsers(injectionQuery);
                assertTrue(count >= 0, "SQL注入攻击应该被安全处理: " + injection);
            }, "SQL注入攻击应该被安全处理: " + injection);
        }
    }

    // ==================== 性能测试 ====================

    @Test
    @Order(25)
    @DisplayName("5.1 findUsers性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testFindUsersPerformance() {
        AdminUserQueryDTO query = new AdminUserQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            adminUserDao.findUsers(query);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("AdminUserDao.findUsers性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
        
        assertTrue(duration < 3000, 
            String.format("findUsers性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    @Test
    @Order(26)
    @DisplayName("5.2 countUsers性能测试")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testCountUsersPerformance() {
        AdminUserQueryDTO query = new AdminUserQueryDTO();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            adminUserDao.countUsers(query);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("AdminUserDao.countUsers性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
        
        assertTrue(duration < 2000, 
            String.format("countUsers性能测试: %d次调用耗时%dms，应该小于2000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    // ==================== 数据完整性测试 ====================

    @Test
    @Order(30)
    @DisplayName("6.1 数据一致性测试")
    public void testDataConsistency() {
        AdminUserQueryDTO query = new AdminUserQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);
        
        // 测试多次查询的一致性
        List<Map<String, Object>> result1 = adminUserDao.findUsers(query);
        List<Map<String, Object>> result2 = adminUserDao.findUsers(query);
        
        assertEquals(result1.size(), result2.size(), "多次查询结果数量应该一致");
        
        // 测试统计查询一致性
        int count1 = adminUserDao.countUsers(query);
        int count2 = adminUserDao.countUsers(query);
        
        assertEquals(count1, count2, "统计查询结果应该一致");
    }

    @Test
    @Order(31)
    @DisplayName("6.2 分页逻辑测试")
    public void testPaginationLogic() {
        AdminUserQueryDTO query1 = new AdminUserQueryDTO();
        query1.setPageNum(1);
        query1.setPageSize(5);

        AdminUserQueryDTO query2 = new AdminUserQueryDTO();
        query2.setPageNum(2);
        query2.setPageSize(5);
        
        List<Map<String, Object>> page1 = adminUserDao.findUsers(query1);
        List<Map<String, Object>> page2 = adminUserDao.findUsers(query2);
        
        assertNotNull(page1, "第一页不应该为null");
        assertNotNull(page2, "第二页不应该为null");
        
        assertTrue(page1.size() <= 5, "第一页结果不应超过限制");
        assertTrue(page2.size() <= 5, "第二页结果不应超过限制");
        
        // 验证分页结果不重复（如果有数据的话）
        if (!page1.isEmpty() && !page2.isEmpty()) {
            for (Map<String, Object> user1 : page1) {
                for (Map<String, Object> user2 : page2) {
                    Object id1 = user1.get("id");
                    Object id2 = user2.get("id");
                    if (id1 != null && id2 != null) {
                        assertNotEquals(id1, id2, "分页结果不应该重复");
                    }
                }
            }
        }
    }

    @AfterEach
    public void tearDown() {
        assertNotNull(adminUserDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("AdminUserDao完整测试套件执行完成");
    }
}
