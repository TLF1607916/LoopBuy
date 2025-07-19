package com.shiwu.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonUtil测试类
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 */
public class JsonUtilTest {

    /**
     * 测试用的简单POJO类
     */
    public static class TestUser {
        private String username;
        private Integer age;
        private String email;

        public TestUser() {}

        public TestUser(String username, Integer age, String email) {
            this.username = username;
            this.age = age;
            this.email = email;
        }

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestUser testUser = (TestUser) obj;
            return java.util.Objects.equals(username, testUser.username) &&
                   java.util.Objects.equals(age, testUser.age) &&
                   java.util.Objects.equals(email, testUser.email);
        }
    }

    /**
     * 测试对象转JSON - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testToJson_Success() {
        // Given: 一个测试对象
        TestUser user = new TestUser("testUser", 25, "test@example.com");
        
        // When: 转换为JSON
        String json = JsonUtil.toJson(user);
        
        // Then: 验证结果
        assertNotNull(json, "JSON字符串不应为空");
        assertTrue(json.contains("testUser"), "JSON应包含用户名");
        assertTrue(json.contains("25"), "JSON应包含年龄");
        assertTrue(json.contains("test@example.com"), "JSON应包含邮箱");
    }

    /**
     * 测试对象转JSON - 边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testToJson_NullObject() {
        // Given: null对象
        Object obj = null;
        
        // When: 转换为JSON
        String json = JsonUtil.toJson(obj);
        
        // Then: 应该返回"null"字符串
        assertEquals("null", json, "null对象应该转换为\"null\"字符串");
    }

    /**
     * 测试对象转JSON - 空对象
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testToJson_EmptyObject() {
        // Given: 空的测试对象
        TestUser emptyUser = new TestUser();
        
        // When: 转换为JSON
        String json = JsonUtil.toJson(emptyUser);
        
        // Then: 验证结果
        assertNotNull(json, "空对象的JSON不应为空");
        assertTrue(json.contains("null"), "空对象的字段应为null");
    }

    /**
     * 测试JSON转对象 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testFromJson_Success() {
        // Given: 一个有效的JSON字符串
        String json = "{\"username\":\"testUser\",\"age\":25,\"email\":\"test@example.com\"}";
        
        // When: 转换为对象
        TestUser user = JsonUtil.fromJson(json, TestUser.class);
        
        // Then: 验证结果
        assertNotNull(user, "转换后的对象不应为空");
        assertEquals("testUser", user.getUsername(), "用户名应该正确");
        assertEquals(Integer.valueOf(25), user.getAge(), "年龄应该正确");
        assertEquals("test@example.com", user.getEmail(), "邮箱应该正确");
    }

    /**
     * 测试JSON转对象 - 错误输入
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testFromJson_InvalidJson() {
        // Given: 无效的JSON字符串
        String invalidJson = "{invalid json}";
        
        // When: 转换为对象
        TestUser user = JsonUtil.fromJson(invalidJson, TestUser.class);
        
        // Then: 应该返回null
        assertNull(user, "无效JSON应该返回null");
    }

    /**
     * 测试JSON转对象 - null输入
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testFromJson_NullJson() {
        // Given: null JSON字符串
        String json = null;
        
        // When: 转换为对象
        TestUser user = JsonUtil.fromJson(json, TestUser.class);
        
        // Then: 应该返回null
        assertNull(user, "null JSON应该返回null");
    }

    /**
     * 测试JSON转对象 - 空字符串
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testFromJson_EmptyJson() {
        // Given: 空JSON字符串
        String json = "";
        
        // When: 转换为对象
        TestUser user = JsonUtil.fromJson(json, TestUser.class);
        
        // Then: 应该返回null
        assertNull(user, "空JSON字符串应该返回null");
    }

    /**
     * 测试往返转换 - 对象->JSON->对象
     * BCDE原则中的Design：测试完整的转换流程
     */
    @Test
    public void testRoundTrip_ObjectToJsonToObject() {
        // Given: 原始对象
        TestUser originalUser = new TestUser("roundTripUser", 30, "roundtrip@example.com");
        
        // When: 对象->JSON->对象
        String json = JsonUtil.toJson(originalUser);
        TestUser convertedUser = JsonUtil.fromJson(json, TestUser.class);
        
        // Then: 验证往返转换后对象相等
        assertNotNull(json, "JSON不应为空");
        assertNotNull(convertedUser, "转换后的对象不应为空");
        assertEquals(originalUser, convertedUser, "往返转换后对象应该相等");
    }

    /**
     * 测试复杂对象转换
     * BCDE原则中的Design：测试更复杂的场景
     */
    @Test
    public void testComplexObject() {
        // Given: 包含null值的对象
        TestUser userWithNulls = new TestUser("userWithNulls", null, null);
        
        // When: 转换
        String json = JsonUtil.toJson(userWithNulls);
        TestUser converted = JsonUtil.fromJson(json, TestUser.class);
        
        // Then: 验证null值处理
        assertNotNull(json, "JSON不应为空");
        assertNotNull(converted, "转换后的对象不应为空");
        assertEquals("userWithNulls", converted.getUsername(), "用户名应该正确");
        assertNull(converted.getAge(), "年龄应该为null");
        assertNull(converted.getEmail(), "邮箱应该为null");
    }
}
