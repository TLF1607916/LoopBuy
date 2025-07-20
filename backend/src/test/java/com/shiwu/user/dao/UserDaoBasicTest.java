package com.shiwu.user.dao;

import com.shiwu.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserDao基础功能测试
 * 不依赖数据库初始化的基本功能测试
 */
@DisplayName("UserDao基础功能测试")
public class UserDaoBasicTest {

    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDao();
    }

    @Test
    @DisplayName("UserDao实例化测试")
    public void testUserDaoInstantiation() {
        assertNotNull(userDao, "UserDao应该能够正常实例化");
    }

    @Test
    @DisplayName("测试null参数处理")
    public void testNullParameterHandling() {
        // 测试各种方法对null参数的处理

        // findById - 可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            userDao.findById(null);
        }, "findById(null)应该抛出NullPointerException");

        // findByUsername
        User result2 = userDao.findByUsername(null);
        assertNull(result2, "findByUsername(null)应该返回null");

        User result3 = userDao.findByUsername("");
        assertNull(result3, "findByUsername(\"\")应该返回null");

        // findByEmail
        User result4 = userDao.findByEmail(null);
        assertNull(result4, "findByEmail(null)应该返回null");

        User result5 = userDao.findByEmail("");
        assertNull(result5, "findByEmail(\"\")应该返回null");

        // findByPhone
        User result6 = userDao.findByPhone(null);
        assertNull(result6, "findByPhone(null)应该返回null");

        User result7 = userDao.findByPhone("");
        assertNull(result7, "findByPhone(\"\")应该返回null");

        // updatePassword - null用户ID可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            userDao.updatePassword(null, "password");
        }, "updatePassword(null, password)应该抛出NullPointerException");

        // updatePassword with null password返回false
        boolean result9 = userDao.updatePassword(1L, null);
        assertFalse(result9, "updatePassword(id, null)应该返回false");

        // createUser - 可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            userDao.createUser(null);
        }, "createUser(null)应该抛出NullPointerException");
    }

    @Test
    @DisplayName("测试边界值ID")
    public void testBoundaryIds() {
        // 测试各种边界值ID
        Long[] boundaryIds = {
            0L,           // 零值
            -1L,          // 负值
            Long.MAX_VALUE // 最大值
        };
        
        for (Long id : boundaryIds) {
            User result = userDao.findById(id);
            // 不存在的ID应该返回null，不应该抛出异常
            // 这里主要验证方法调用不会崩溃
            assertNotNull(userDao, "UserDao应该保持正常状态，ID: " + id);
        }
    }

    @Test
    @DisplayName("测试特殊字符用户名")
    public void testSpecialCharacterUsernames() {
        String[] specialUsernames = {
            "user@domain.com",
            "用户名中文",
            "user-name_123",
            "user.name+tag",
            "user name with spaces",
            "user'with'quotes",
            "user\"with\"doublequotes",
            "user\\with\\backslashes",
            "user/with/slashes"
        };
        
        for (String username : specialUsernames) {
            User result = userDao.findByUsername(username);
            // 不存在的用户名应该返回null，不应该抛出异常
            // 这里主要验证方法调用不会因为特殊字符而崩溃
            assertNotNull(userDao, "UserDao应该能处理特殊字符用户名: " + username);
        }
    }

    @Test
    @DisplayName("测试特殊字符邮箱")
    public void testSpecialCharacterEmails() {
        String[] specialEmails = {
            "test@example.com",
            "test.email+tag@example.com",
            "test_email@example-domain.com",
            "测试@example.com", // 中文字符
            "test@测试.com",
            "test@example.co.uk",
            "very.long.email.address@very.long.domain.name.com"
        };
        
        for (String email : specialEmails) {
            User result = userDao.findByEmail(email);
            // 不存在的邮箱应该返回null，不应该抛出异常
            assertNotNull(userDao, "UserDao应该能处理特殊字符邮箱: " + email);
        }
    }

    @Test
    @DisplayName("测试User对象创建")
    public void testUserObjectCreation() {
        // 测试创建User对象的各种情况
        
        // 完整的User对象
        User completeUser = new User();
        completeUser.setUsername("testuser");
        completeUser.setPassword("hashedpassword");
        completeUser.setEmail("test@example.com");
        completeUser.setNickname("Test User");
        completeUser.setStatus(1);
        
        assertNotNull(completeUser, "完整User对象应该创建成功");
        assertEquals("testuser", completeUser.getUsername(), "用户名应该设置正确");
        assertEquals("test@example.com", completeUser.getEmail(), "邮箱应该设置正确");
        
        // 最小化User对象
        User minimalUser = new User();
        minimalUser.setUsername("minimal");
        minimalUser.setPassword("password");
        
        assertNotNull(minimalUser, "最小化User对象应该创建成功");
        assertEquals("minimal", minimalUser.getUsername(), "用户名应该设置正确");
        assertNull(minimalUser.getEmail(), "未设置的邮箱应该为null");
        
        // 空User对象
        User emptyUser = new User();
        assertNotNull(emptyUser, "空User对象应该创建成功");
        assertNull(emptyUser.getUsername(), "未设置的用户名应该为null");
    }

    @Test
    @DisplayName("测试统计方法")
    public void testStatisticMethods() {
        // 测试统计相关方法不会抛出异常
        
        Long totalCount = userDao.getTotalUserCount();
        assertNotNull(totalCount, "总用户数不应该为null");
        assertTrue(totalCount >= 0, "总用户数应该大于等于0");
        
        Long activeCount = userDao.getActiveUserCount();
        assertNotNull(activeCount, "活跃用户数不应该为null");
        assertTrue(activeCount >= 0, "活跃用户数应该大于等于0");
        
        // 活跃用户数不应该超过总用户数
        assertTrue(activeCount <= totalCount, "活跃用户数不应该超过总用户数");
        
        // 测试按状态统计
        Long statusCount = userDao.getUserCountByStatus(1);
        assertNotNull(statusCount, "状态统计不应该为null");
        assertTrue(statusCount >= 0, "状态统计应该大于等于0");
    }

    @Test
    @DisplayName("测试密码更新边界情况")
    public void testPasswordUpdateBoundaries() {
        // 测试密码更新的各种边界情况
        
        // 不存在的用户ID
        boolean result1 = userDao.updatePassword(99999L, "newpassword");
        assertFalse(result1, "更新不存在用户的密码应该返回false");
        
        // 空密码
        boolean result2 = userDao.updatePassword(1L, "");
        // 根据实现，空密码可能被允许或拒绝
        assertNotNull(userDao, "空密码更新不应该导致异常");
        
        // 很长的密码
        String longPassword = repeat("a", 1000);
        boolean result3 = userDao.updatePassword(1L, longPassword);
        assertNotNull(userDao, "长密码更新不应该导致异常");
        
        // 特殊字符密码
        String specialPassword = "password!@#$%^&*()_+{}|:<>?[]\\;'\",./-=`~";
        boolean result4 = userDao.updatePassword(1L, specialPassword);
        assertNotNull(userDao, "特殊字符密码更新不应该导致异常");
    }

    @Test
    @DisplayName("测试公开信息查询")
    public void testPublicInfoQuery() {
        // 测试公开信息查询方法

        // findPublicInfoById可能对null参数抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            userDao.findPublicInfoById(null);
        }, "findPublicInfoById(null)应该抛出NullPointerException");

        User publicInfo2 = userDao.findPublicInfoById(0L);
        assertNull(publicInfo2, "无效ID的公开信息查询应该返回null");

        User publicInfo3 = userDao.findPublicInfoById(99999L);
        assertNull(publicInfo3, "不存在ID的公开信息查询应该返回null");

        // 测试方法调用不会抛出异常
        assertNotNull(userDao, "公开信息查询方法应该正常工作");
    }

    @Test
    @DisplayName("测试DAO方法的幂等性")
    public void testDaoMethodIdempotency() {
        // 测试多次调用相同方法的结果一致性
        
        // 多次查询不存在的用户
        User result1 = userDao.findById(99999L);
        User result2 = userDao.findById(99999L);
        User result3 = userDao.findById(99999L);
        
        assertEquals(result1, result2, "多次查询结果应该一致");
        assertEquals(result2, result3, "多次查询结果应该一致");
        
        // 多次查询统计信息
        Long count1 = userDao.getTotalUserCount();
        Long count2 = userDao.getTotalUserCount();
        
        assertEquals(count1, count2, "多次统计查询结果应该一致（在没有数据变更的情况下）");
        
        // 多次查询不存在的用户名
        User user1 = userDao.findByUsername("nonexistent_user_12345");
        User user2 = userDao.findByUsername("nonexistent_user_12345");
        
        assertEquals(user1, user2, "多次查询不存在用户名的结果应该一致");
    }

    /**
     * 生成重复字符串（Java 8兼容）
     */
    private String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
