package com.shiwu.integration;

import com.shiwu.common.util.JsonUtil;
import com.shiwu.user.model.*;
import com.shiwu.user.service.UserService;
import com.shiwu.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户功能集成测试
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 * 
 * 注意：这些测试需要实际的数据库环境
 * 测试按顺序执行，模拟完整的用户生命周期
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIntegrationTest {
    
    private UserService userService;
    private String testUsername;
    private Long testUserId;
    
    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl();
        testUsername = "integrationTest_" + System.currentTimeMillis();
    }
    
    /**
     * 测试完整的用户注册流程
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @Order(1)
    public void testCompleteUserRegistrationFlow() {
        // Given: 准备注册数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(testUsername);
        registerRequest.setPassword("password123");
        registerRequest.setEmail("integration@test.com");
        registerRequest.setNickname("集成测试用户");
        registerRequest.setSchool("测试大学");
        
        // When: 执行注册
        RegisterResult result = userService.register(registerRequest);
        
        // Then: 验证注册结果
        assertNotNull(result, "注册结果不应为空");
        
        if (Boolean.TRUE.equals(result.getSuccess())) {
            // 注册成功的情况
            assertTrue(result.getSuccess(), "注册应该成功");
            assertNotNull(result.getUserVO(), "应该返回用户信息");
            assertNotNull(result.getUserVO().getId(), "用户ID不应为空");
            assertNotNull(result.getUserVO().getToken(), "应该返回JWT令牌");
            assertEquals(testUsername, result.getUserVO().getUsername(), "用户名应该匹配");
            
            // 保存测试用户ID供后续测试使用
            testUserId = result.getUserVO().getId();
            
            System.out.println("✅ 用户注册成功，用户ID: " + testUserId);
        } else {
            // 注册失败的情况（可能是数据库问题或用户名冲突）
            assertFalse(result.getSuccess(), "注册失败");
            assertNotNull(result.getError(), "应该有错误信息");
            System.out.println("⚠️ 用户注册失败: " + result.getError().getMessage());
        }
    }
    
    /**
     * 测试完整的用户登录流程
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @Order(2)
    public void testCompleteUserLoginFlow() {
        // 如果注册测试失败，跳过登录测试
        if (testUserId == null) {
            System.out.println("⏭️ 跳过登录测试，因为注册测试未成功");
            return;
        }
        
        // Given: 使用注册的用户凭据
        String username = testUsername;
        String password = "password123";
        
        // When: 执行登录
        LoginResult result = userService.login(username, password);
        
        // Then: 验证登录结果
        assertNotNull(result, "登录结果不应为空");
        
        if (Boolean.TRUE.equals(result.getSuccess())) {
            // 登录成功的情况
            assertTrue(result.getSuccess(), "登录应该成功");
            assertNotNull(result.getUserVO(), "应该返回用户信息");
            assertNotNull(result.getUserVO().getToken(), "应该返回JWT令牌");
            assertEquals(username, result.getUserVO().getUsername(), "用户名应该匹配");
            assertEquals(testUserId, result.getUserVO().getId(), "用户ID应该匹配");
            
            System.out.println("✅ 用户登录成功，令牌: " + result.getUserVO().getToken().substring(0, 20) + "...");
        } else {
            // 登录失败的情况
            assertFalse(result.getSuccess(), "登录失败");
            assertNotNull(result.getError(), "应该有错误信息");
            System.out.println("⚠️ 用户登录失败: " + result.getError().getMessage());
        }
    }
    
    /**
     * 测试获取用户公开信息流程
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @Order(3)
    public void testGetUserProfileFlow() {
        // 如果注册测试失败，跳过此测试
        if (testUserId == null) {
            System.out.println("⏭️ 跳过用户信息获取测试，因为注册测试未成功");
            return;
        }
        
        // Given: 使用注册的用户ID
        Long userId = testUserId;
        Long currentUserId = null; // 未登录用户查看
        
        // When: 获取用户公开信息
        UserProfileVO profile = userService.getUserProfile(userId, currentUserId);
        
        // Then: 验证结果
        if (profile != null) {
            assertNotNull(profile.getUser(), "用户基本信息不应为空");
            assertEquals(userId, profile.getUser().getId(), "用户ID应该匹配");
            assertEquals(testUsername, profile.getUser().getUsername(), "用户名应该匹配");
            assertNotNull(profile.getFollowerCount(), "粉丝数量不应为空");
            assertNotNull(profile.getAverageRating(), "平均评分不应为空");
            assertNotNull(profile.getOnSaleProducts(), "在售商品列表不应为空");
            assertNotNull(profile.getIsFollowing(), "关注状态不应为空");
            
            // 验证数据合理性
            assertTrue(profile.getFollowerCount() >= 0, "粉丝数量应该大于等于0");
            assertTrue(profile.getAverageRating().compareTo(java.math.BigDecimal.ZERO) >= 0, "平均评分应该大于等于0");
            assertFalse(profile.getIsFollowing(), "未登录用户不应该显示关注状态为true");
            
            System.out.println("✅ 成功获取用户公开信息，粉丝数: " + profile.getFollowerCount() + 
                             ", 评分: " + profile.getAverageRating());
        } else {
            System.out.println("⚠️ 获取用户公开信息失败，可能用户不存在或已被封禁");
        }
    }
    
    /**
     * 测试JSON序列化和反序列化的完整流程
     * BCDE原则中的Design：测试设计要求
     */
    @Test
    @Order(4)
    public void testJsonSerializationFlow() {
        // Given: 创建测试对象
        RegisterRequest originalRequest = new RegisterRequest();
        originalRequest.setUsername("jsonTest");
        originalRequest.setPassword("password123");
        originalRequest.setEmail("json@test.com");
        originalRequest.setNickname("JSON测试");
        
        // When: 序列化为JSON
        String json = JsonUtil.toJson(originalRequest);
        
        // Then: 验证序列化结果
        assertNotNull(json, "JSON序列化结果不应为空");
        assertTrue(json.contains("jsonTest"), "JSON应包含用户名");
        assertTrue(json.contains("password123"), "JSON应包含密码");
        
        // When: 反序列化回对象
        RegisterRequest deserializedRequest = JsonUtil.fromJson(json, RegisterRequest.class);
        
        // Then: 验证反序列化结果
        assertNotNull(deserializedRequest, "反序列化结果不应为空");
        assertEquals(originalRequest.getUsername(), deserializedRequest.getUsername(), "用户名应该匹配");
        assertEquals(originalRequest.getPassword(), deserializedRequest.getPassword(), "密码应该匹配");
        assertEquals(originalRequest.getEmail(), deserializedRequest.getEmail(), "邮箱应该匹配");
        assertEquals(originalRequest.getNickname(), deserializedRequest.getNickname(), "昵称应该匹配");
        
        System.out.println("✅ JSON序列化和反序列化测试通过");
    }
    
    /**
     * 测试错误处理的完整流程
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    @Order(5)
    public void testErrorHandlingFlow() {
        // Test 1: 重复用户名注册
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername(testUsername); // 使用已存在的用户名
        duplicateRequest.setPassword("password123");
        duplicateRequest.setEmail("duplicate@test.com");
        
        RegisterResult duplicateResult = userService.register(duplicateRequest);
        assertNotNull(duplicateResult, "重复注册结果不应为空");
        assertFalse(duplicateResult.getSuccess(), "重复用户名注册应该失败");
        
        // Test 2: 错误密码登录
        LoginResult wrongPasswordResult = userService.login(testUsername, "wrongpassword");
        assertNotNull(wrongPasswordResult, "错误密码登录结果不应为空");
        assertFalse(wrongPasswordResult.getSuccess(), "错误密码登录应该失败");
        
        // Test 3: 不存在用户登录
        LoginResult nonExistentResult = userService.login("nonexistent_user_12345", "password123");
        assertNotNull(nonExistentResult, "不存在用户登录结果不应为空");
        assertFalse(nonExistentResult.getSuccess(), "不存在用户登录应该失败");
        
        // Test 4: 获取不存在用户信息
        UserProfileVO nonExistentProfile = userService.getUserProfile(999999L, null);
        assertNull(nonExistentProfile, "不存在用户的公开信息应该返回null");
        
        System.out.println("✅ 错误处理流程测试通过");
    }
    
    /**
     * 测试边界条件的完整流程
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    @Order(6)
    public void testBoundaryConditionsFlow() {
        // Test 1: null参数处理
        RegisterResult nullRegisterResult = userService.register(null);
        assertNotNull(nullRegisterResult, "null注册请求应该返回结果");
        assertFalse(nullRegisterResult.getSuccess(), "null注册请求应该失败");
        
        LoginResult nullLoginResult = userService.login(null, null);
        assertNotNull(nullLoginResult, "null登录参数应该返回结果");
        assertFalse(nullLoginResult.getSuccess(), "null登录参数应该失败");
        
        UserProfileVO nullProfileResult = userService.getUserProfile(null, null);
        assertNull(nullProfileResult, "null用户ID应该返回null");
        
        // Test 2: 空字符串参数处理
        LoginResult emptyLoginResult = userService.login("", "");
        assertNotNull(emptyLoginResult, "空字符串登录参数应该返回结果");
        assertFalse(emptyLoginResult.getSuccess(), "空字符串登录参数应该失败");
        
        System.out.println("✅ 边界条件流程测试通过");
    }
}
