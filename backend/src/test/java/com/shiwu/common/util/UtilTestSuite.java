package com.shiwu.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工具类测试套件
 * 不依赖数据库的纯工具类测试
 */
@DisplayName("工具类测试套件")
public class UtilTestSuite {

    @Test
    @DisplayName("密码工具类 - 基本功能测试")
    public void testPasswordUtil() {
        // 测试密码加密
        String password = "testPassword123";
        String encrypted = PasswordUtil.encrypt(password);
        
        assertNotNull(encrypted, "加密后的密码不应为null");
        assertNotEquals(password, encrypted, "加密后的密码应与原密码不同");
        assertTrue(encrypted.startsWith("$2a$"), "应该是BCrypt格式");
        
        // 测试密码验证
        assertTrue(PasswordUtil.matches(password, encrypted), "正确密码应该验证通过");
        assertFalse(PasswordUtil.matches("wrongPassword", encrypted), "错误密码应该验证失败");
        
        // 测试null处理
        assertNull(PasswordUtil.encrypt(null), "null密码应该返回null");
        assertFalse(PasswordUtil.matches(null, encrypted), "null密码应该验证失败");
        assertFalse(PasswordUtil.matches(password, null), "null哈希应该验证失败");
        
        // 测试BCrypt格式检查
        assertTrue(PasswordUtil.isBCryptHash(encrypted), "应该识别为BCrypt格式");
        assertFalse(PasswordUtil.isBCryptHash("plaintext"), "普通文本不应该识别为BCrypt格式");
        
        // 测试密码强度
        assertTrue(PasswordUtil.isStrongPassword("Password123"), "强密码应该通过验证");
        assertFalse(PasswordUtil.isStrongPassword("weak"), "弱密码应该验证失败");
        assertFalse(PasswordUtil.isStrongPassword(null), "null密码应该验证失败");
    }

    @Test
    @DisplayName("JWT工具类 - 基本功能测试")
    public void testJwtUtil() {
        // 测试token生成
        Long userId = 123L;
        String username = "testuser";
        String token = JwtUtil.generateToken(userId, username);
        
        assertNotNull(token, "生成的token不应为null");
        assertFalse(token.isEmpty(), "生成的token不应为空");
        assertTrue(token.contains("."), "JWT token应包含点分隔符");
        
        // 验证token结构
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token应该有3个部分");
        
        // 测试token验证
        assertTrue(JwtUtil.validateToken(token), "有效的token应该通过验证");
        assertFalse(JwtUtil.validateToken("invalid.token.here"), "无效的token应该验证失败");
        assertFalse(JwtUtil.validateToken(null), "null token应该验证失败");
        assertFalse(JwtUtil.validateToken(""), "空token应该验证失败");
        
        // 测试信息提取
        assertEquals(userId, JwtUtil.getUserIdFromToken(token), "应该能正确提取用户ID");
        assertEquals(username, JwtUtil.getUsernameFromToken(token), "应该能正确提取用户名");
        
        // 测试带角色的token
        String role = "ADMIN";
        String adminToken = JwtUtil.generateToken(userId, username, role);
        assertNotNull(adminToken, "管理员token不应为null");
        assertEquals(role, JwtUtil.getRoleFromToken(adminToken), "应该能正确提取角色");
        
        // 测试无效token的信息提取
        assertNull(JwtUtil.getUserIdFromToken("invalid"), "无效token应该返回null");
        assertNull(JwtUtil.getUsernameFromToken("invalid"), "无效token应该返回null");
        assertNull(JwtUtil.getRoleFromToken("invalid"), "无效token应该返回null");
        
        // 测试null参数
        assertNull(JwtUtil.generateToken(null, username), "null用户ID应该返回null");
        assertNull(JwtUtil.generateToken(userId, null), "null用户名应该返回null");
    }

    @Test
    @DisplayName("JSON工具类 - 基本功能测试")
    public void testJsonUtil() {
        // 测试对象转JSON
        TestObject obj = new TestObject();
        obj.setId(1L);
        obj.setName("test");
        obj.setPrice(new BigDecimal("99.99"));
        obj.setActive(true);
        
        String json = JsonUtil.toJson(obj);
        assertNotNull(json, "JSON字符串不应为null");
        assertFalse(json.isEmpty(), "JSON字符串不应为空");
        assertTrue(json.contains("\"id\":1"), "JSON应包含id字段");
        assertTrue(json.contains("\"name\":\"test\""), "JSON应包含name字段");
        
        // 测试JSON转对象
        TestObject parsed = JsonUtil.fromJson(json, TestObject.class);
        assertNotNull(parsed, "解析后的对象不应为null");
        assertEquals(obj.getId(), parsed.getId(), "ID应该匹配");
        assertEquals(obj.getName(), parsed.getName(), "名称应该匹配");
        assertEquals(0, obj.getPrice().compareTo(parsed.getPrice()), "价格应该匹配");
        assertEquals(obj.isActive(), parsed.isActive(), "活跃状态应该匹配");
        
        // 测试null对象
        assertEquals("null", JsonUtil.toJson(null), "null对象应该转换为字符串'null'");
        
        // 测试特殊字符
        TestObject specialObj = new TestObject();
        specialObj.setId(2L);
        specialObj.setName("测试中文\"特殊'字符");
        String specialJson = JsonUtil.toJson(specialObj);
        TestObject specialParsed = JsonUtil.fromJson(specialJson, TestObject.class);
        assertEquals(specialObj.getName(), specialParsed.getName(), "特殊字符应该正确处理");
        
        // 测试异常情况 - JsonUtil返回null而不是抛出异常
        TestObject invalidResult = JsonUtil.fromJson("invalid json", TestObject.class);
        assertNull(invalidResult, "无效JSON应该返回null");

        TestObject nullResult = JsonUtil.fromJson(null, TestObject.class);
        assertNull(nullResult, "null JSON应该返回null");
    }

    @Test
    @DisplayName("工具类集成测试")
    public void testUtilIntegration() {
        // 创建用户信息
        String password = "UserPassword123";
        String hashedPassword = PasswordUtil.encrypt(password);
        
        // 验证密码
        assertTrue(PasswordUtil.matches(password, hashedPassword), "密码验证应该成功");
        
        // 生成JWT token
        Long userId = 456L;
        String username = "integrationUser";
        String token = JwtUtil.generateToken(userId, username);
        
        // 验证token并提取信息
        assertTrue(JwtUtil.validateToken(token), "token应该有效");
        assertEquals(userId, JwtUtil.getUserIdFromToken(token), "用户ID应该匹配");
        assertEquals(username, JwtUtil.getUsernameFromToken(token), "用户名应该匹配");
        
        // 创建用户对象并序列化
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUsername(username);
        userInfo.setPasswordHash(hashedPassword);
        userInfo.setToken(token);
        
        String userJson = JsonUtil.toJson(userInfo);
        assertNotNull(userJson, "用户JSON不应为null");
        
        // 反序列化并验证
        UserInfo parsedUser = JsonUtil.fromJson(userJson, UserInfo.class);
        assertNotNull(parsedUser, "解析后的用户不应为null");
        assertEquals(userInfo.getUserId(), parsedUser.getUserId(), "用户ID应该匹配");
        assertEquals(userInfo.getUsername(), parsedUser.getUsername(), "用户名应该匹配");
        assertEquals(userInfo.getPasswordHash(), parsedUser.getPasswordHash(), "密码哈希应该匹配");
        assertEquals(userInfo.getToken(), parsedUser.getToken(), "token应该匹配");
        
        // 验证反序列化后的密码和token仍然有效
        assertTrue(PasswordUtil.matches(password, parsedUser.getPasswordHash()), "反序列化后密码验证应该成功");
        assertTrue(JwtUtil.validateToken(parsedUser.getToken()), "反序列化后token应该有效");
    }

    // 测试用的内部类
    public static class TestObject {
        private Long id;
        private String name;
        private BigDecimal price;
        private boolean active;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    public static class UserInfo {
        private Long userId;
        private String username;
        private String passwordHash;
        private String token;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
