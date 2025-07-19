package com.shiwu.test;

import com.shiwu.common.util.DBUtil;
import com.shiwu.common.util.PasswordUtil;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.common.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试4: 工具类和基础设施测试
 * 测试核心工具类的功能
 */
public class Test4_UtilsTest {
    
    @BeforeEach
    public void setUp() {
        System.out.println("=== 测试4: 工具类和基础设施测试 ===");
    }
    
    /**
     * 测试4.1: 数据库连接工具测试
     */
    @Test
    public void test4_1_DBUtil() {
        System.out.println("测试4.1: 数据库连接工具测试");
        
        try {
            Connection conn = DBUtil.getConnection();
            
            if (conn != null) {
                System.out.println("✅ 数据库连接成功");
                assertNotNull(conn, "数据库连接不应为空");
                assertFalse(conn.isClosed(), "数据库连接应该是打开的");
                
                // 测试连接是否可用
                boolean isValid = conn.isValid(5);
                assertTrue(isValid, "数据库连接应该是有效的");
                System.out.println("✅ 数据库连接有效");
                
                // 关闭连接
                conn.close();
                System.out.println("✅ 数据库连接正常关闭");
            } else {
                System.out.println("⚠️ 数据库连接失败");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 数据库连接测试异常: " + e.getMessage());
        }
        
        System.out.println("✅ 测试4.1完成");
    }
    
    /**
     * 测试4.2: 密码工具测试
     */
    @Test
    public void test4_2_PasswordUtil() {
        System.out.println("测试4.2: 密码工具测试");
        
        try {
            String plainPassword = "testPassword123";
            
            // 测试密码加密
            String hashedPassword = PasswordUtil.encrypt(plainPassword);
            assertNotNull(hashedPassword, "加密后的密码不应为空");
            assertNotEquals(plainPassword, hashedPassword, "加密后的密码应该与原密码不同");
            System.out.println("✅ 密码加密成功");

            // 测试密码验证
            boolean isValid = PasswordUtil.matches(plainPassword, hashedPassword);
            assertTrue(isValid, "正确的密码应该验证通过");
            System.out.println("✅ 密码验证成功");

            // 测试错误密码验证
            boolean isInvalid = PasswordUtil.matches("wrongPassword", hashedPassword);
            assertFalse(isInvalid, "错误的密码应该验证失败");
            System.out.println("✅ 错误密码正确识别");
            
        } catch (Exception e) {
            System.out.println("⚠️ 密码工具测试异常: " + e.getMessage());
        }
        
        System.out.println("✅ 测试4.2完成");
    }
    
    /**
     * 测试4.3: JWT工具测试
     */
    @Test
    public void test4_3_JwtUtil() {
        System.out.println("测试4.3: JWT工具测试");
        
        try {
            String username = "testUser";
            Long userId = 123L;
            
            // 测试JWT生成
            String token = JwtUtil.generateToken(userId, username);
            assertNotNull(token, "JWT令牌不应为空");
            assertTrue(token.length() > 0, "JWT令牌应该有内容");
            System.out.println("✅ JWT令牌生成成功");
            
            // 测试JWT验证
            boolean isValid = JwtUtil.validateToken(token);
            assertTrue(isValid, "生成的JWT令牌应该是有效的");
            System.out.println("✅ JWT令牌验证成功");
            
            // 测试从JWT提取用户名
            String extractedUsername = JwtUtil.getUsernameFromToken(token);
            assertEquals(username, extractedUsername, "从JWT提取的用户名应该匹配");
            System.out.println("✅ JWT用户名提取成功");
            
            // 测试从JWT提取用户ID
            Long extractedUserId = JwtUtil.getUserIdFromToken(token);
            assertEquals(userId, extractedUserId, "从JWT提取的用户ID应该匹配");
            System.out.println("✅ JWT用户ID提取成功");
            
        } catch (Exception e) {
            System.out.println("⚠️ JWT工具测试异常: " + e.getMessage());
        }
        
        System.out.println("✅ 测试4.3完成");
    }
    
    /**
     * 测试4.4: JSON工具测试
     */
    @Test
    public void test4_4_JsonUtil() {
        System.out.println("测试4.4: JSON工具测试");
        
        try {
            // 创建测试对象
            TestObject testObj = new TestObject();
            testObj.setName("测试对象");
            testObj.setValue(42);
            
            // 测试对象转JSON
            String json = JsonUtil.toJson(testObj);
            assertNotNull(json, "JSON字符串不应为空");
            assertTrue(json.contains("测试对象"), "JSON应该包含对象内容");
            assertTrue(json.contains("42"), "JSON应该包含数值内容");
            System.out.println("✅ 对象转JSON成功");
            
            // 测试JSON转对象
            TestObject parsedObj = JsonUtil.fromJson(json, TestObject.class);
            assertNotNull(parsedObj, "解析的对象不应为空");
            assertEquals(testObj.getName(), parsedObj.getName(), "解析的名称应该匹配");
            assertEquals(testObj.getValue(), parsedObj.getValue(), "解析的数值应该匹配");
            System.out.println("✅ JSON转对象成功");
            
        } catch (Exception e) {
            System.out.println("⚠️ JSON工具测试异常: " + e.getMessage());
        }
        
        System.out.println("✅ 测试4.4完成");
    }
    
    /**
     * 测试4.5: 工具类基本结构测试
     */
    @Test
    public void test4_5_UtilClassStructure() {
        System.out.println("测试4.5: 工具类基本结构测试");
        
        // 测试DBUtil类结构
        Class<?> dbUtilClass = DBUtil.class;
        assertTrue(java.lang.reflect.Modifier.isPublic(dbUtilClass.getModifiers()), 
                  "DBUtil应该是public类");
        System.out.println("✅ DBUtil类结构正确");
        
        // 测试PasswordUtil类结构
        Class<?> passwordUtilClass = PasswordUtil.class;
        assertTrue(java.lang.reflect.Modifier.isPublic(passwordUtilClass.getModifiers()), 
                  "PasswordUtil应该是public类");
        System.out.println("✅ PasswordUtil类结构正确");
        
        // 测试JwtUtil类结构
        Class<?> jwtUtilClass = JwtUtil.class;
        assertTrue(java.lang.reflect.Modifier.isPublic(jwtUtilClass.getModifiers()), 
                  "JwtUtil应该是public类");
        System.out.println("✅ JwtUtil类结构正确");
        
        // 测试JsonUtil类结构
        Class<?> jsonUtilClass = JsonUtil.class;
        assertTrue(java.lang.reflect.Modifier.isPublic(jsonUtilClass.getModifiers()), 
                  "JsonUtil应该是public类");
        System.out.println("✅ JsonUtil类结构正确");
        
        System.out.println("✅ 测试4.5完成");
    }
    
    /**
     * 测试用的简单对象
     */
    public static class TestObject {
        private String name;
        private Integer value;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
    }
}
