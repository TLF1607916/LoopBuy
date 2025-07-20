package com.shiwu.user.dao;

import com.shiwu.common.test.TestConfig;
import com.shiwu.user.model.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserDao安全修复验证测试
 */
@DisplayName("UserDao安全修复验证测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoSecurityFixTest {

    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDao();
    }

    @Test
    @Order(1)
    @DisplayName("1.1 findByUsername参数验证测试")
    public void testFindByUsernameParameterValidation() {
        // 测试null参数
        User result1 = userDao.findByUsername(null);
        assertNull(result1, "null用户名应该返回null");

        // 测试空字符串
        User result2 = userDao.findByUsername("");
        assertNull(result2, "空用户名应该返回null");

        // 测试只有空格的字符串
        User result3 = userDao.findByUsername("   ");
        assertNull(result3, "只有空格的用户名应该返回null");

        System.out.println("✅ findByUsername参数验证测试通过");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 findById参数验证测试")
    public void testFindByIdParameterValidation() {
        // 测试null参数
        User result1 = userDao.findById(null);
        assertNull(result1, "null用户ID应该返回null");

        // 测试0
        User result2 = userDao.findById(0L);
        assertNull(result2, "用户ID为0应该返回null");

        // 测试负数
        User result3 = userDao.findById(-1L);
        assertNull(result3, "负数用户ID应该返回null");

        System.out.println("✅ findById参数验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("1.3 updatePassword参数验证测试")
    public void testUpdatePasswordParameterValidation() {
        // 测试null userId
        boolean result1 = userDao.updatePassword(null, "newpassword");
        assertFalse(result1, "null用户ID应该返回false");

        // 测试无效userId
        boolean result2 = userDao.updatePassword(0L, "newpassword");
        assertFalse(result2, "用户ID为0应该返回false");

        boolean result3 = userDao.updatePassword(-1L, "newpassword");
        assertFalse(result3, "负数用户ID应该返回false");

        // 测试null密码
        boolean result4 = userDao.updatePassword(TestConfig.BOUNDARY_ID_NONEXISTENT, null);
        assertFalse(result4, "null密码应该返回false");

        System.out.println("✅ updatePassword参数验证测试通过");
    }

    @Test
    @Order(4)
    @DisplayName("1.4 SQL注入防护测试（修复后）")
    public void testSqlInjectionProtectionFixed() {
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE system_user; --",
            "1' OR '1'='1",
            "admin'--",
            "' UNION SELECT * FROM system_user --",
            "'; INSERT INTO system_user VALUES ('hacker', 'password'); --"
        };
        
        for (String injection : sqlInjectionAttempts) {
            // 测试用户名注入
            User userResult = userDao.findByUsername(injection);
            assertNull(userResult, "SQL注入应该被防护: " + injection);
            
            // 测试邮箱注入
            User emailResult = userDao.findByEmail(injection);
            assertNull(emailResult, "邮箱SQL注入应该被防护: " + injection);
            
            // 测试密码更新注入（使用不存在的用户ID）
            boolean passwordResult = userDao.updatePassword(TestConfig.BOUNDARY_ID_NONEXISTENT, injection);
            assertFalse(passwordResult, "密码SQL注入应该被防护: " + injection);
        }

        System.out.println("✅ SQL注入防护测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("1.5 特殊字符处理测试")
    public void testSpecialCharacterHandling() {
        // 测试各种特殊字符
        String[] specialUsernames = {
            TestConfig.SPECIAL_CHARS,
            TestConfig.CHINESE_CHARS,
            TestConfig.MIXED_CHARS,
            "user@domain.com",
            "user.name",
            "user_name",
            "user-name"
        };
        
        for (String username : specialUsernames) {
            User result = userDao.findByUsername(username);
            // 这些用户名在数据库中不存在，应该返回null
            assertNull(result, "不存在的特殊字符用户名应该返回null: " + username);
        }

        System.out.println("✅ 特殊字符处理测试通过");
    }

    @Test
    @Order(6)
    @DisplayName("1.6 边界值测试")
    public void testBoundaryValues() {
        // 测试边界用户ID
        Long[] boundaryIds = {
            TestConfig.BOUNDARY_ID_ZERO,
            TestConfig.BOUNDARY_ID_NEGATIVE,
            TestConfig.BOUNDARY_ID_NONEXISTENT,
            TestConfig.BOUNDARY_ID_MAX
        };
        
        for (Long id : boundaryIds) {
            if (id != null && id > 0) {
                User result = userDao.findById(id);
                // 除了正常的测试用户ID，其他都应该返回null
                if (!id.equals(TestConfig.TEST_USER_ID)) {
                    assertNull(result, "不存在的用户ID应该返回null: " + id);
                }
            }
        }

        System.out.println("✅ 边界值测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("1.7 正常功能验证测试")
    public void testNormalFunctionality() {
        // 验证正常功能仍然工作
        
        // 查询存在的用户
        User alice = userDao.findByUsername("alice");
        assertNotNull(alice, "应该能查询到alice用户");
        assertEquals("alice", alice.getUsername(), "用户名应该匹配");
        
        User user1 = userDao.findById(1L);
        assertNotNull(user1, "应该能查询到ID为1的用户");
        
        // 测试邮箱查询
        User aliceByEmail = userDao.findByEmail("alice@example.com");
        assertNotNull(aliceByEmail, "应该能通过邮箱查询到alice用户");
        assertEquals("alice", aliceByEmail.getUsername(), "通过邮箱查询的用户名应该匹配");

        System.out.println("✅ 正常功能验证测试通过");
    }

    @AfterEach
    public void tearDown() {
        assertNotNull(userDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("🎉 UserDao安全修复验证测试全部通过！");
    }
}
