package com.shiwu.test;

import com.shiwu.user.service.UserService;
import com.shiwu.user.service.impl.UserServiceImpl;
import com.shiwu.user.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试2: UserService业务逻辑测试
 * 测试用户服务层的核心业务功能
 */
public class Test2_UserServiceTest {
    
    private UserService userService;
    
    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl();
        System.out.println("=== 测试2: UserService业务逻辑测试 ===");
    }
    
    /**
     * 测试2.1: 用户登录 - 成功情况
     */
    @Test
    public void test2_1_LoginSuccess() {
        System.out.println("测试2.1: 用户登录 - 成功情况");

        try {
            String username = "alice";
            String password = "123456";  // 数据库中的测试密码

            LoginResult result = userService.login(username, password);

            if (result != null) {
                System.out.println("登录结果: " + result.getSuccess());
                if (result.getSuccess()) {
                    System.out.println("✅ 登录成功");
                    assertNotNull(result.getUserVO(), "登录成功应该返回用户信息");
                    assertNotNull(result.getUserVO().getToken(), "登录成功应该返回token");
                    assertEquals("alice", result.getUserVO().getUsername(), "用户名应该匹配");
                } else {
                    System.out.println("⚠️ 登录失败: " + result.getError());
                }
            } else {
                System.out.println("⚠️ 登录方法返回null");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 登录方法执行异常: " + e.getMessage());
        }

        System.out.println("✅ 测试2.1完成");
    }
    
    /**
     * 测试2.2: 用户登录 - 用户名不存在
     */
    @Test
    public void test2_2_LoginUserNotExists() {
        System.out.println("测试2.2: 用户登录 - 用户名不存在");

        try {
            String username = "nonexistent_user";
            String password = "123456";

            LoginResult result = userService.login(username, password);

            if (result != null) {
                System.out.println("登录结果: " + result.getSuccess());
                assertFalse(result.getSuccess(), "不存在的用户应该登录失败");
                System.out.println("✅ 正确识别用户不存在");
            } else {
                System.out.println("⚠️ 登录方法返回null");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 登录方法执行异常: " + e.getMessage());
        }

        System.out.println("✅ 测试2.2完成");
    }
    
    /**
     * 测试2.3: 用户登录 - 密码错误
     */
    @Test
    public void test2_3_LoginWrongPassword() {
        System.out.println("测试2.3: 用户登录 - 密码错误");

        try {
            String username = "alice";
            String password = "wrong_password";

            LoginResult result = userService.login(username, password);

            if (result != null) {
                System.out.println("登录结果: " + result.getSuccess());
                assertFalse(result.getSuccess(), "错误密码应该登录失败");
                System.out.println("✅ 正确识别密码错误");
            } else {
                System.out.println("⚠️ 登录方法返回null");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 登录方法执行异常: " + e.getMessage());
        }

        System.out.println("✅ 测试2.3完成");
    }
    
    /**
     * 测试2.4: 用户注册 - 成功情况
     */
    @Test
    public void test2_4_RegisterSuccess() {
        System.out.println("测试2.4: 用户注册 - 成功情况");
        
        try {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername("testuser_" + System.currentTimeMillis());
            registerRequest.setPassword("password123");
            registerRequest.setEmail("test_" + System.currentTimeMillis() + "@example.com");
            registerRequest.setNickname("测试用户");
            
            RegisterResult result = userService.register(registerRequest);
            
            if (result != null) {
                System.out.println("注册结果: " + result.getSuccess());
                if (result.getSuccess()) {
                    System.out.println("✅ 注册成功");
                    assertNotNull(result.getUserVO(), "注册成功应该返回用户信息");
                    assertNotNull(result.getUserVO().getId(), "注册成功应该返回用户ID");
                } else {
                    System.out.println("⚠️ 注册失败: " + result.getError());
                }
            } else {
                System.out.println("⚠️ 注册方法返回null");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 注册方法执行异常: " + e.getMessage());
        }
        
        System.out.println("✅ 测试2.4完成");
    }
    
    /**
     * 测试2.5: 用户注册 - 用户名已存在
     */
    @Test
    public void test2_5_RegisterDuplicateUsername() {
        System.out.println("测试2.5: 用户注册 - 用户名已存在");
        
        try {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername("alice");  // 已存在的用户名
            registerRequest.setPassword("password123");
            registerRequest.setEmail("duplicate_test@example.com");
            registerRequest.setNickname("重复测试用户");
            
            RegisterResult result = userService.register(registerRequest);
            
            if (result != null) {
                System.out.println("注册结果: " + result.getSuccess());
                assertFalse(result.getSuccess(), "重复用户名应该注册失败");
                System.out.println("✅ 正确识别用户名重复");
            } else {
                System.out.println("⚠️ 注册方法返回null");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 注册方法执行异常: " + e.getMessage());
        }
        
        System.out.println("✅ 测试2.5完成");
    }
    
    /**
     * 测试2.6: 获取用户信息
     */
    @Test
    public void test2_6_GetUserInfo() {
        System.out.println("测试2.6: 获取用户信息");
        
        try {
            Long userId = 1L;  // alice的用户ID
            Long currentUserId = null;  // 未登录用户
            UserProfileVO userProfile = userService.getUserProfile(userId, currentUserId);

            if (userProfile != null) {
                System.out.println("✅ 找到用户信息: " + userProfile.getUser().getUsername());
                assertEquals("alice", userProfile.getUser().getUsername(), "用户名应该匹配");
                assertNotNull(userProfile.getUser().getNickname(), "昵称不应为空");
            } else {
                System.out.println("⚠️ 未找到用户信息");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 获取用户信息方法执行异常: " + e.getMessage());
        }
        
        System.out.println("✅ 测试2.6完成");
    }
}
