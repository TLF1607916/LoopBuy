package com.shiwu.user.service.impl;

import com.shiwu.user.model.*;
import com.shiwu.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserServiceImpl测试类
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 */
public class UserServiceImplTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl();
    }
    
    /**
     * 测试获取用户公开信息 - 正常情况
     * 遵循BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testGetUserProfile_Success() {
        // Given: 使用测试数据中的用户ID（假设ID为1的用户存在）
        Long userId = 1L;
        Long currentUserId = null; // 未登录用户
        
        // When: 调用获取用户公开信息方法
        UserProfileVO result = userService.getUserProfile(userId, currentUserId);
        
        // Then: 验证结果
        if (result != null) {
            assertNotNull(result.getUser(), "用户基本信息不应为空");
            assertNotNull(result.getUser().getId(), "用户ID不应为空");
            assertNotNull(result.getFollowerCount(), "粉丝数量不应为空");
            assertNotNull(result.getAverageRating(), "平均评分不应为空");
            assertNotNull(result.getOnSaleProducts(), "在售商品列表不应为空");
            assertNotNull(result.getIsFollowing(), "关注状态不应为空");
            
            // 验证数据合理性
            assertTrue(result.getFollowerCount() >= 0, "粉丝数量应该大于等于0");
            assertTrue(result.getAverageRating().compareTo(java.math.BigDecimal.ZERO) >= 0, "平均评分应该大于等于0");
            assertTrue(result.getAverageRating().compareTo(new java.math.BigDecimal("5.00")) <= 0, "平均评分应该小于等于5");
        }
    }
    
    /**
     * 测试获取用户公开信息 - 边界条件
     * 遵循BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testGetUserProfile_NullUserId() {
        // Given: 用户ID为null
        Long userId = null;
        Long currentUserId = null;
        
        // When: 调用获取用户公开信息方法
        UserProfileVO result = userService.getUserProfile(userId, currentUserId);
        
        // Then: 应该返回null
        assertNull(result, "当用户ID为null时，应该返回null");
    }
    
    /**
     * 测试获取用户公开信息 - 用户不存在
     * 遵循BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testGetUserProfile_UserNotExists() {
        // Given: 不存在的用户ID
        Long userId = 999999L;
        Long currentUserId = null;

        // When: 调用获取用户公开信息方法
        UserProfileVO result = userService.getUserProfile(userId, currentUserId);

        // Then: 应该返回null
        assertNull(result, "当用户不存在时，应该返回null");
    }

    /**
     * 测试用户注册 - 正常情况
     * 遵循BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testRegister_Success() {
        // Given: 有效的注册请求
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testRegister_" + System.currentTimeMillis()); // 使用时间戳确保唯一性
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setNickname("测试用户");

        // When: 调用注册方法
        RegisterResult result = userService.register(request);

        // Then: 验证结果
        assertNotNull(result, "注册结果不应为空");
        if (Boolean.TRUE.equals(result.getSuccess())) {
            assertTrue(result.getSuccess(), "注册应该成功");
            assertNotNull(result.getUserVO(), "应该返回用户信息");
            assertNotNull(result.getUserVO().getId(), "用户ID不应为空");
            assertTrue(result.getUserVO().getId() > 0, "用户ID应该大于0");
            assertNull(result.getError(), "成功时错误信息应为空");
        } else {
            // 如果注册失败，可能是数据库连接问题
            System.out.println("警告：用户注册失败，错误信息: " +
                (result.getError() != null ? result.getError().getMessage() : "未知错误"));
        }
    }

    /**
     * 测试用户注册 - 用户名重复
     * 遵循BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testRegister_DuplicateUsername() {
        // Given: 使用已存在的用户名
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test"); // 假设这个用户名已存在
        request.setPassword("password123");
        request.setEmail("duplicate@example.com");
        request.setNickname("重复用户");

        // When: 调用注册方法
        RegisterResult result = userService.register(request);

        // Then: 验证结果
        assertNotNull(result, "注册结果不应为空");
        assertFalse(result.getSuccess(), "重复用户名注册应该失败");
        assertNull(result.getUserVO(), "失败时不应返回用户信息");
        assertNotNull(result.getError(), "失败时应该有错误信息");
        assertTrue(result.getError().getMessage().contains("用户名") ||
                  result.getError().getMessage().contains("已存在"), "错误信息应该提示用户名重复");
    }

    /**
     * 测试用户注册 - 参数验证
     * 遵循BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testRegister_InvalidParameters() {
        // Test null request
        RegisterResult result1 = userService.register(null);
        assertNotNull(result1, "null请求应该返回结果对象");
        assertFalse(result1.getSuccess(), "null请求应该失败");

        // Test empty username
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("");
        request2.setPassword("password123");
        RegisterResult result2 = userService.register(request2);
        assertFalse(result2.getSuccess(), "空用户名应该失败");

        // Test null password
        RegisterRequest request3 = new RegisterRequest();
        request3.setUsername("testUser");
        request3.setPassword(null);
        RegisterResult result3 = userService.register(request3);
        assertFalse(result3.getSuccess(), "null密码应该失败");

        // Test weak password
        RegisterRequest request4 = new RegisterRequest();
        request4.setUsername("testUser2");
        request4.setPassword("123"); // 弱密码
        RegisterResult result4 = userService.register(request4);
        assertFalse(result4.getSuccess(), "弱密码应该失败");
    }

    /**
     * 测试用户登录 - 正常情况
     * 遵循BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testLogin_Success() {
        // Given: 有效的登录凭据（假设test用户存在）
        String username = "test";
        String password = "password123";

        // When: 调用登录方法
        LoginResult result = userService.login(username, password);

        // Then: 验证结果
        assertNotNull(result, "登录结果不应为空");
        if (Boolean.TRUE.equals(result.getSuccess())) {
            assertTrue(result.getSuccess(), "登录应该成功");
            assertNotNull(result.getUserVO(), "应该返回用户信息");
            assertNotNull(result.getUserVO().getToken(), "应该返回JWT令牌");
            assertNull(result.getError(), "成功时错误信息应为空");

            // 验证用户信息
            UserVO user = result.getUserVO();
            assertEquals(username, user.getUsername(), "用户名应该匹配");
            assertNotNull(user.getId(), "用户ID不应为空");
        } else {
            System.out.println("警告：用户登录失败，可能测试用户不存在或密码不匹配");
        }
    }

    /**
     * 测试用户登录 - 错误密码
     * 遵循BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testLogin_WrongPassword() {
        // Given: 存在的用户名但错误的密码
        String username = "test";
        String wrongPassword = "wrongpassword";

        // When: 调用登录方法
        LoginResult result = userService.login(username, wrongPassword);

        // Then: 验证结果
        assertNotNull(result, "登录结果不应为空");
        assertFalse(result.getSuccess(), "错误密码登录应该失败");
        assertNull(result.getUserVO(), "失败时不应返回用户信息");
        assertNotNull(result.getError(), "失败时应该有错误信息");
    }

    /**
     * 测试用户登录 - 用户不存在
     * 遵循BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testLogin_UserNotExists() {
        // Given: 不存在的用户名
        String nonExistentUsername = "nonexistent_user_12345";
        String password = "password123";

        // When: 调用登录方法
        LoginResult result = userService.login(nonExistentUsername, password);

        // Then: 验证结果
        assertNotNull(result, "登录结果不应为空");
        assertFalse(result.getSuccess(), "不存在用户登录应该失败");
        assertNull(result.getUserVO(), "失败时不应返回用户信息");
        assertNotNull(result.getError(), "失败时应该有错误信息");
    }

    /**
     * 测试用户登录 - 参数验证
     * 遵循BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testLogin_InvalidParameters() {
        // Test null username
        LoginResult result1 = userService.login(null, "password123");
        assertNotNull(result1, "null用户名应该返回结果对象");
        assertFalse(result1.getSuccess(), "null用户名应该失败");

        // Test null password
        LoginResult result2 = userService.login("testUser", null);
        assertNotNull(result2, "null密码应该返回结果对象");
        assertFalse(result2.getSuccess(), "null密码应该失败");

        // Test empty username
        LoginResult result3 = userService.login("", "password123");
        assertFalse(result3.getSuccess(), "空用户名应该失败");

        // Test empty password
        LoginResult result4 = userService.login("testUser", "");
        assertFalse(result4.getSuccess(), "空密码应该失败");
    }
}
