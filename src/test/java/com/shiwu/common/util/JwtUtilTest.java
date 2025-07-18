package com.shiwu.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil测试类
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 */
public class JwtUtilTest {

    /**
     * 测试JWT令牌生成 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testGenerateToken_Success() {
        // Given: 有效的用户ID和用户名
        Long userId = 123L;
        String username = "testUser";
        
        // When: 生成JWT令牌
        String token = JwtUtil.generateToken(userId, username);
        
        // Then: 验证结果
        assertNotNull(token, "生成的令牌不应为空");
        assertTrue(token.length() > 0, "令牌应该有内容");
        assertTrue(token.contains("."), "JWT令牌应该包含点分隔符");
        
        // JWT令牌应该有三个部分（header.payload.signature）
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT令牌应该有三个部分");
    }

    /**
     * 测试JWT令牌生成 - 边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testGenerateToken_NullInputs() {
        // Given & When & Then: 测试null输入
        assertNull(JwtUtil.generateToken(null, "testUser"), "null用户ID应该返回null");
        assertNull(JwtUtil.generateToken(123L, null), "null用户名应该返回null");
        assertNull(JwtUtil.generateToken(null, null), "双null应该返回null");
    }

    /**
     * 测试从令牌获取用户ID - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testGetUserIdFromToken_Success() {
        // Given: 生成一个有效的令牌
        Long originalUserId = 456L;
        String username = "testUser";
        String token = JwtUtil.generateToken(originalUserId, username);
        
        // When: 从令牌中获取用户ID
        Long extractedUserId = JwtUtil.getUserIdFromToken(token);
        
        // Then: 验证结果
        assertNotNull(extractedUserId, "提取的用户ID不应为空");
        assertEquals(originalUserId, extractedUserId, "提取的用户ID应该与原始用户ID相同");
    }

    /**
     * 测试从令牌获取用户名 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testGetUsernameFromToken_Success() {
        // Given: 生成一个有效的令牌
        Long userId = 789L;
        String originalUsername = "testUser123";
        String token = JwtUtil.generateToken(userId, originalUsername);
        
        // When: 从令牌中获取用户名
        String extractedUsername = JwtUtil.getUsernameFromToken(token);
        
        // Then: 验证结果
        assertNotNull(extractedUsername, "提取的用户名不应为空");
        assertEquals(originalUsername, extractedUsername, "提取的用户名应该与原始用户名相同");
    }

    /**
     * 测试令牌验证 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testValidateToken_Success() {
        // Given: 生成一个有效的令牌
        Long userId = 999L;
        String username = "validUser";
        String token = JwtUtil.generateToken(userId, username);
        
        // When: 验证令牌
        boolean isValid = JwtUtil.validateToken(token);
        
        // Then: 令牌应该有效
        assertTrue(isValid, "有效的令牌应该通过验证");
    }

    /**
     * 测试令牌验证 - 无效令牌
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testValidateToken_InvalidToken() {
        // Given: 无效的令牌
        String invalidToken = "invalid.token.here";
        
        // When: 验证令牌
        boolean isValid = JwtUtil.validateToken(invalidToken);
        
        // Then: 令牌应该无效
        assertFalse(isValid, "无效的令牌不应该通过验证");
    }

    /**
     * 测试令牌验证 - 边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testValidateToken_NullAndEmpty() {
        // Given & When & Then: 测试null和空字符串
        assertFalse(JwtUtil.validateToken(null), "null令牌不应该通过验证");
        assertFalse(JwtUtil.validateToken(""), "空令牌不应该通过验证");
        assertFalse(JwtUtil.validateToken("   "), "空白令牌不应该通过验证");
    }

    /**
     * 测试从无效令牌获取用户信息
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testGetUserInfoFromInvalidToken() {
        // Given: 无效的令牌
        String invalidToken = "invalid.token.here";
        
        // When & Then: 从无效令牌获取信息应该返回null
        assertNull(JwtUtil.getUserIdFromToken(invalidToken), "从无效令牌获取用户ID应该返回null");
        assertNull(JwtUtil.getUsernameFromToken(invalidToken), "从无效令牌获取用户名应该返回null");
        
        // 测试null令牌
        assertNull(JwtUtil.getUserIdFromToken(null), "从null令牌获取用户ID应该返回null");
        assertNull(JwtUtil.getUsernameFromToken(null), "从null令牌获取用户名应该返回null");
    }

    /**
     * 测试完整的令牌生命周期
     * BCDE原则中的Design：测试完整的设计流程
     */
    @Test
    public void testTokenLifecycle() {
        // Given: 用户信息
        Long userId = 12345L;
        String username = "lifecycleUser";
        
        // When: 生成令牌
        String token = JwtUtil.generateToken(userId, username);
        
        // Then: 验证完整的生命周期
        assertNotNull(token, "令牌生成不应为空");
        
        // 验证令牌有效性
        assertTrue(JwtUtil.validateToken(token), "生成的令牌应该有效");
        
        // 验证能正确提取用户信息
        assertEquals(userId, JwtUtil.getUserIdFromToken(token), "应该能正确提取用户ID");
        assertEquals(username, JwtUtil.getUsernameFromToken(token), "应该能正确提取用户名");
    }

    /**
     * 测试不同用户生成不同令牌
     * BCDE原则中的Design：测试令牌唯一性
     */
    @Test
    public void testDifferentUsersGenerateDifferentTokens() {
        // Given: 两个不同的用户
        Long userId1 = 111L;
        String username1 = "user1";
        Long userId2 = 222L;
        String username2 = "user2";
        
        // When: 为两个用户生成令牌
        String token1 = JwtUtil.generateToken(userId1, username1);
        String token2 = JwtUtil.generateToken(userId2, username2);
        
        // Then: 令牌应该不同
        assertNotNull(token1, "用户1的令牌不应为空");
        assertNotNull(token2, "用户2的令牌不应为空");
        assertNotEquals(token1, token2, "不同用户的令牌应该不同");
        
        // 验证能正确区分用户
        assertEquals(userId1, JwtUtil.getUserIdFromToken(token1), "令牌1应该对应用户1");
        assertEquals(userId2, JwtUtil.getUserIdFromToken(token2), "令牌2应该对应用户2");
    }
}
