package com.shiwu.common.util;

import com.shiwu.test.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 * 测试JWT token的生成、验证、解析等功能
 * 
 * 测试覆盖：
 * 1. 正常功能测试
 * 2. 边界条件测试
 * 3. 异常情况测试
 * 4. 安全性测试
 */
@DisplayName("JWT工具类测试")
public class JwtUtilTest extends TestBase {

    @Test
    @DisplayName("生成用户JWT Token - 正常情况")
    public void testGenerateToken_Normal() {
        // Given
        Long userId = TEST_USER_ID_1;
        String username = TEST_USERNAME_1;
        
        // When
        String token = JwtUtil.generateToken(userId, username);
        
        // Then
        assertNotNull(token, "生成的token不应为null");
        assertFalse(token.isEmpty(), "生成的token不应为空");
        assertTrue(token.contains("."), "JWT token应包含点分隔符");
        
        // JWT token应该有3个部分（header.payload.signature）
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token应该有3个部分");
        
        // 验证token有效性
        assertTrue(JwtUtil.validateToken(token), "生成的token应该有效");
    }

    @Test
    @DisplayName("生成管理员JWT Token - 正常情况")
    public void testGenerateAdminToken_Normal() {
        // Given
        Long adminId = TEST_ADMIN_ID;
        String username = TEST_ADMIN_USERNAME;
        String role = "ADMIN";

        // When
        String token = JwtUtil.generateToken(adminId, username, role);

        // Then
        assertNotNull(token, "生成的管理员token不应为null");
        assertFalse(token.isEmpty(), "生成的管理员token不应为空");
        assertTrue(token.contains("."), "JWT token应包含点分隔符");

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token应该有3个部分");

        // 验证token有效性
        assertTrue(JwtUtil.validateToken(token), "生成的管理员token应该有效");
    }

    @Test
    @DisplayName("验证有效的JWT Token")
    public void testValidateToken_Valid() {
        // Given
        Long userId = TEST_USER_ID_1;
        String username = TEST_USERNAME_1;
        String token = JwtUtil.generateToken(userId, username);
        
        // When
        boolean isValid = JwtUtil.validateToken(token);
        
        // Then
        assertTrue(isValid, "有效的token应该通过验证");
    }

    @Test
    @DisplayName("验证无效的JWT Token")
    public void testValidateToken_Invalid() {
        // Given
        String[] invalidTokens = {
            "invalid.token.here",
            "not.a.jwt",
            "header.payload", // 缺少签名
            "too.many.parts.here.invalid",
            "eyJhbGciOiJIUzI1NiJ9.invalid.signature" // 无效签名
        };
        
        // When & Then
        for (String invalidToken : invalidTokens) {
            boolean isValid = JwtUtil.validateToken(invalidToken);
            assertFalse(isValid, "无效的token应该验证失败: " + invalidToken);
        }
    }

    @Test
    @DisplayName("验证边界情况Token")
    public void testValidateToken_EdgeCases() {
        // When & Then
        assertFalse(JwtUtil.validateToken(null), "null token应该验证失败");
        assertFalse(JwtUtil.validateToken(""), "空字符串token应该验证失败");
        assertFalse(JwtUtil.validateToken("   "), "空白字符串token应该验证失败");
    }

    @Test
    @DisplayName("从Token中获取用户ID - 正常情况")
    public void testGetUserIdFromToken_Normal() {
        // Given
        Long expectedUserId = 123L;
        String username = "testuser";
        String token = JwtUtil.generateToken(expectedUserId, username);
        
        // When
        Long actualUserId = JwtUtil.getUserIdFromToken(token);
        
        // Then
        assertNotNull(actualUserId, "用户ID不应为null");
        assertEquals(expectedUserId, actualUserId, "用户ID应该匹配");
    }

    @Test
    @DisplayName("从Token中获取用户名- 正常情况")
    public void testGetUsernameFromToken_Normal() {
        // Given
        Long userId = 123L;
        String expectedUsername = "testuser";
        String token = JwtUtil.generateToken(userId, expectedUsername);
        
        // When
        String actualUsername = JwtUtil.getUsernameFromToken(token);
        
        // Then
        assertNotNull(actualUsername, "用户名不应为null");
        assertEquals(expectedUsername, actualUsername, "用户名应该匹配");
    }

    @Test
    @DisplayName("从管理员Token中获取管理员ID")
    public void testGetAdminIdFromToken_Normal() {
        // Given
        Long expectedAdminId = 456L;
        String username = "admin";
        String role = "ADMIN";
        String token = JwtUtil.generateToken(expectedAdminId, username, role);

        // When
        Long actualAdminId = JwtUtil.getUserIdFromToken(token); // 管理员ID也是通过getUserIdFromToken获取

        // Then
        assertNotNull(actualAdminId, "管理员ID不应为null");
        assertEquals(expectedAdminId, actualAdminId, "管理员ID应该匹配");
    }

    @Test
    @DisplayName("从管理员Token中获取角色")
    public void testGetRoleFromToken_Normal() {
        // Given
        Long adminId = 456L;
        String username = "admin";
        String expectedRole = "SUPER_ADMIN";
        String token = JwtUtil.generateToken(adminId, username, expectedRole);

        // When
        String actualRole = JwtUtil.getRoleFromToken(token);

        // Then
        assertNotNull(actualRole, "角色不应为null");
        assertEquals(expectedRole, actualRole, "角色应该匹配");
    }

    @Test
    @DisplayName("从无效Token中获取信息应返回null")
    public void testGetInfoFromInvalidToken() {
        // Given
        String[] invalidTokens = {
            "invalid.token.here",
            null,
            "",
            "not.a.jwt"
        };
        
        // When & Then
        for (String invalidToken : invalidTokens) {
            assertNull(JwtUtil.getUserIdFromToken(invalidToken),
                      "从无效token中获取用户ID应返回null: " + invalidToken);
            assertNull(JwtUtil.getUsernameFromToken(invalidToken),
                      "从无效token中获取用户名应返回null: " + invalidToken);
            assertNull(JwtUtil.getRoleFromToken(invalidToken),
                      "从无效token中获取角色应返回null: " + invalidToken);
        }
    }

    @Test
    @DisplayName("测试特殊字符的用户名")
    public void testSpecialCharactersInUsername() {
        // Given
        Long userId = 1L;
        String[] specialUsernames = {
            "test@user.com",
            "用户名中文",
            "user-name_123",
            "user.name+tag",
            "user name with spaces"
        };
        
        // When & Then
        for (String specialUsername : specialUsernames) {
            String token = JwtUtil.generateToken(userId, specialUsername);
            assertNotNull(token, "包含特殊字符的用户名应该能生成token: " + specialUsername);
            
            String extractedUsername = JwtUtil.getUsernameFromToken(token);
            assertEquals(specialUsername, extractedUsername, 
                        "特殊字符用户名应该正确提取: " + specialUsername);
        }
    }

    @Test
    @DisplayName("测试边界值用户ID")
    public void testBoundaryUserIds() {
        // Given
        String username = "testuser";
        Long[] boundaryIds = {
            1L,                    // 最小正值
            Long.MAX_VALUE,        // 最大值
            999999999L             // 大数值
        };
        
        // When & Then
        for (Long userId : boundaryIds) {
            String token = JwtUtil.generateToken(userId, username);
            assertNotNull(token, "边界用户ID应该能生成token: " + userId);
            
            Long extractedId = JwtUtil.getUserIdFromToken(token);
            assertEquals(userId, extractedId, "边界用户ID应该正确提取: " + userId);
        }
    }

    @Test
    @DisplayName("测试null参数异常")
    public void testNullParameterExceptions() {
        // Test generateToken with null parameters
        assertThrows(Exception.class, () -> {
            JwtUtil.generateToken(null, "username");
        }, "null用户ID应该抛出异常");
        
        assertThrows(Exception.class, () -> {
            JwtUtil.generateToken(1L, null);
        }, "null用户名应该抛出异常");
        
        // Test generateToken with null parameters for admin
        String result1 = JwtUtil.generateToken(null, "admin", "ADMIN");
        assertNull(result1, "null管理员ID应该返回null");

        String result2 = JwtUtil.generateToken(1L, null, "ADMIN");
        assertNull(result2, "null管理员用户名应该返回null");

        // null角色是允许的，不会抛出异常
        String result3 = JwtUtil.generateToken(1L, "admin", null);
        assertNotNull(result3, "null角色应该能正常生成token");
    }

    @Test
    @DisplayName("测试Token一致性")
    public void testTokenConsistency() {
        // Given
        Long userId = TEST_USER_ID_1;
        String username = TEST_USERNAME_1;
        
        // When - 多次生成token
        String token1 = JwtUtil.generateToken(userId, username);
        String token2 = JwtUtil.generateToken(userId, username);
        
        // Then - 虽然token可能不同（因为时间戳），但解析结果应该相同
        assertEquals(userId, JwtUtil.getUserIdFromToken(token1), "第一个token的用户ID应该正确");
        assertEquals(userId, JwtUtil.getUserIdFromToken(token2), "第二个token的用户ID应该正确");
        assertEquals(username, JwtUtil.getUsernameFromToken(token1), "第一个token的用户名应该正确");
        assertEquals(username, JwtUtil.getUsernameFromToken(token2), "第二个token的用户名应该正确");
    }

    @Test
    @DisplayName("测试Token安全性")
    public void testTokenSecurity() {
        // Given
        Long userId = TEST_USER_ID_1;
        String username = TEST_USERNAME_1;
        String token = JwtUtil.generateToken(userId, username);
        
        // When - 尝试修改token
        String[] tamperedTokens = {
            token.substring(0, token.length() - 5) + "XXXXX", // 修改签名
            token.replace(".", "X"), // 破坏结构
            token + "extra" // 添加额外内容
        };
        
        // Then - 修改后的token应该无效
        for (String tamperedToken : tamperedTokens) {
            assertFalse(JwtUtil.validateToken(tamperedToken), 
                       "被篡改的token应该验证失败: " + tamperedToken);
        }
    }
}
