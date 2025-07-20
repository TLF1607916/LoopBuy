package com.shiwu.common.util;

import com.shiwu.test.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON工具类测试
 * 测试JSON序列化和反序列化功能
 * 
 * 测试覆盖：
 * 1. 基本序列化/反序列化
 * 2. 复杂对象处理
 * 3. 特殊字符处理
 * 4. 边界条件测试
 * 5. 异常情况测试
 */
@DisplayName("JSON工具类测试")
public class JsonUtilTest extends TestBase {

    @Test
    @DisplayName("对象转JSON字符串 - 基本对象")
    public void testToJson_BasicObject() {
        // Given
        TestObject obj = new TestObject();
        obj.setId(1L);
        obj.setName("test");
        obj.setPrice(new BigDecimal("99.99"));
        obj.setActive(true);
        
        // When
        String json = JsonUtil.toJson(obj);
        
        // Then
        assertNotNull(json, "JSON字符串不应为null");
        assertFalse(json.isEmpty(), "JSON字符串不应为空");
        assertTrue(json.contains("\"id\":1"), "JSON应包含id字段");
        assertTrue(json.contains("\"name\":\"test\""), "JSON应包含name字段");
        assertTrue(json.contains("\"active\":true"), "JSON应包含active字段");
        assertTrue(json.contains("\"price\":99.99"), "JSON应包含price字段");
    }

    @Test
    @DisplayName("JSON字符串转对象 - 基本对象")
    public void testFromJson_BasicObject() {
        // Given
        String json = "{\"id\":1,\"name\":\"test\",\"price\":99.99,\"active\":true}";
        
        // When
        TestObject obj = JsonUtil.fromJson(json, TestObject.class);
        
        // Then
        assertNotNull(obj, "反序列化的对象不应为null");
        assertEquals(1L, obj.getId(), "ID应该匹配");
        assertEquals("test", obj.getName(), "名称应该匹配");
        assertEquals(new BigDecimal("99.99"), obj.getPrice(), "价格应该匹配");
        assertTrue(obj.isActive(), "活跃状态应该匹配");
    }

    @Test
    @DisplayName("null对象转JSON")
    public void testToJson_Null() {
        // When
        String json = JsonUtil.toJson(null);
        
        // Then
        assertEquals("null", json, "null对象应该转换为字符串'null'");
    }

    @Test
    @DisplayName("异常情况 - null JSON字符串")
    public void testFromJson_NullJson() {
        // When & Then
        assertThrows(Exception.class, () -> {
            JsonUtil.fromJson(null, TestObject.class);
        }, "null JSON字符串应该抛出异常");
    }

    @Test
    @DisplayName("异常情况 - 空JSON字符串")
    public void testFromJson_EmptyJson() {
        // When & Then
        assertThrows(Exception.class, () -> {
            JsonUtil.fromJson("", TestObject.class);
        }, "空JSON字符串应该抛出异常");
    }

    @Test
    @DisplayName("异常情况 - 无效JSON字符串")
    public void testFromJson_InvalidJson() {
        // Given
        String[] invalidJsons = {
            "{invalid json}",
            "{\"key\":}",
            "{\"key\":\"value\",}",
            "not json at all",
            "{\"key\":\"value\"" // 缺少结束括号
        };
        
        // When & Then
        for (String invalidJson : invalidJsons) {
            assertThrows(Exception.class, () -> {
                JsonUtil.fromJson(invalidJson, TestObject.class);
            }, "无效JSON字符串应该抛出异常: " + invalidJson);
        }
    }

    @Test
    @DisplayName("List对象转JSON")
    public void testToJson_List() {
        // Given
        List<TestObject> list = new ArrayList<>();
        TestObject obj1 = new TestObject();
        obj1.setId(1L);
        obj1.setName("test1");
        TestObject obj2 = new TestObject();
        obj2.setId(2L);
        obj2.setName("test2");
        list.add(obj1);
        list.add(obj2);
        
        // When
        String json = JsonUtil.toJson(list);
        
        // Then
        assertNotNull(json, "List的JSON字符串不应为null");
        assertTrue(json.startsWith("["), "List的JSON应以[开头");
        assertTrue(json.endsWith("]"), "List的JSON应以]结尾");
        assertTrue(json.contains("\"name\":\"test1\""), "JSON应包含第一个对象");
        assertTrue(json.contains("\"name\":\"test2\""), "JSON应包含第二个对象");
    }

    @Test
    @DisplayName("Map对象转JSON")
    public void testToJson_Map() {
        // Given
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("name", "test");
        map.put("active", true);
        map.put("price", 99.99);
        
        // When
        String json = JsonUtil.toJson(map);
        
        // Then
        assertNotNull(json, "Map的JSON字符串不应为null");
        assertTrue(json.startsWith("{"), "Map的JSON应以{开头");
        assertTrue(json.endsWith("}"), "Map的JSON应以}结尾");
        assertTrue(json.contains("\"id\":1"), "JSON应包含id字段");
        assertTrue(json.contains("\"name\":\"test\""), "JSON应包含name字段");
    }

    @Test
    @DisplayName("复杂嵌套对象转JSON")
    public void testToJson_NestedObject() {
        // Given
        ComplexTestObject complex = new ComplexTestObject();
        complex.setId(1L);
        complex.setName("complex");
        
        TestObject nested = new TestObject();
        nested.setId(2L);
        nested.setName("nested");
        complex.setNestedObject(nested);
        
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        complex.setTags(tags);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("version", "1.0");
        metadata.put("author", "test");
        complex.setMetadata(metadata);
        
        // When
        String json = JsonUtil.toJson(complex);
        
        // Then
        assertNotNull(json, "复杂对象的JSON字符串不应为null");
        assertTrue(json.contains("\"name\":\"complex\""), "JSON应包含外层对象");
        assertTrue(json.contains("\"nestedObject\""), "JSON应包含嵌套对象");
        assertTrue(json.contains("\"name\":\"nested\""), "JSON应包含嵌套对象的字段");
        assertTrue(json.contains("\"tags\""), "JSON应包含数组字段");
        assertTrue(json.contains("\"tag1\""), "JSON应包含数组元素");
        assertTrue(json.contains("\"metadata\""), "JSON应包含Map字段");
    }

    @Test
    @DisplayName("特殊字符处理")
    public void testSpecialCharacters() {
        // Given
        TestObject obj = new TestObject();
        obj.setId(1L);
        obj.setName("test\"with\\special/characters\n\r\t");
        
        // When
        String json = JsonUtil.toJson(obj);
        TestObject parsed = JsonUtil.fromJson(json, TestObject.class);
        
        // Then
        assertNotNull(json, "包含特殊字符的JSON不应为null");
        assertNotNull(parsed, "解析后的对象不应为null");
        assertEquals(obj.getName(), parsed.getName(), "特殊字符应该正确处理");
    }

    @Test
    @DisplayName("中文字符处理")
    public void testChineseCharacters() {
        // Given
        TestObject obj = new TestObject();
        obj.setId(1L);
        obj.setName("测试中文字符");
        
        // When
        String json = JsonUtil.toJson(obj);
        TestObject parsed = JsonUtil.fromJson(json, TestObject.class);
        
        // Then
        assertNotNull(json, "包含中文字符的JSON不应为null");
        assertNotNull(parsed, "解析后的对象不应为null");
        assertEquals("测试中文字符", parsed.getName(), "中文字符应该正确处理");
    }

    @Test
    @DisplayName("Unicode字符处理")
    public void testUnicodeCharacters() {
        // Given
        String[] unicodeStrings = {
            "emoji😀🎉🔥",
            "русский текст",
            "日本語テキスト",
            "العربية",
            "🌟✨💫⭐"
        };
        
        // When & Then
        for (String unicodeString : unicodeStrings) {
            TestObject obj = new TestObject();
            obj.setId(1L);
            obj.setName(unicodeString);
            
            String json = JsonUtil.toJson(obj);
            TestObject parsed = JsonUtil.fromJson(json, TestObject.class);
            
            assertNotNull(json, "包含Unicode字符的JSON不应为null: " + unicodeString);
            assertNotNull(parsed, "解析后的对象不应为null: " + unicodeString);
            assertEquals(unicodeString, parsed.getName(), "Unicode字符应该正确处理: " + unicodeString);
        }
    }

    @Test
    @DisplayName("日期时间处理")
    public void testDateTimeHandling() {
        // Given
        DateTimeTestObject obj = new DateTimeTestObject();
        obj.setId(1L);
        obj.setCreateTime(LocalDateTime.now());
        
        // When
        String json = JsonUtil.toJson(obj);
        DateTimeTestObject parsed = JsonUtil.fromJson(json, DateTimeTestObject.class);
        
        // Then
        assertNotNull(json, "包含日期时间的JSON不应为null");
        assertNotNull(parsed, "解析后的对象不应为null");
        assertNotNull(parsed.getCreateTime(), "日期时间字段不应为null");
    }

    @Test
    @DisplayName("BigDecimal精度处理")
    public void testBigDecimalPrecision() {
        // Given
        TestObject obj = new TestObject();
        obj.setId(1L);
        obj.setPrice(new BigDecimal("123.456789"));
        
        // When
        String json = JsonUtil.toJson(obj);
        TestObject parsed = JsonUtil.fromJson(json, TestObject.class);
        
        // Then
        assertNotNull(parsed, "解析后的对象不应为null");
        assertNotNull(parsed.getPrice(), "价格字段不应为null");
        assertEquals(0, obj.getPrice().compareTo(parsed.getPrice()), "BigDecimal精度应该保持");
    }

    @Test
    @DisplayName("空集合处理")
    public void testEmptyCollections() {
        // Given
        ComplexTestObject obj = new ComplexTestObject();
        obj.setId(1L);
        obj.setName("test");
        obj.setTags(new ArrayList<>()); // 空List
        obj.setMetadata(new HashMap<>()); // 空Map
        
        // When
        String json = JsonUtil.toJson(obj);
        ComplexTestObject parsed = JsonUtil.fromJson(json, ComplexTestObject.class);
        
        // Then
        assertNotNull(parsed, "解析后的对象不应为null");
        assertNotNull(parsed.getTags(), "空List不应为null");
        assertTrue(parsed.getTags().isEmpty(), "List应该为空");
        assertNotNull(parsed.getMetadata(), "空Map不应为null");
        assertTrue(parsed.getMetadata().isEmpty(), "Map应该为空");
    }

    @Test
    @DisplayName("null字段处理")
    public void testNullFields() {
        // Given
        TestObject obj = new TestObject();
        obj.setId(1L);
        obj.setName(null); // null字段
        obj.setPrice(null); // null字段
        obj.setActive(true);
        
        // When
        String json = JsonUtil.toJson(obj);
        TestObject parsed = JsonUtil.fromJson(json, TestObject.class);
        
        // Then
        assertNotNull(parsed, "解析后的对象不应为null");
        assertEquals(1L, parsed.getId(), "非null字段应该正确");
        assertNull(parsed.getName(), "null字段应该保持null");
        assertNull(parsed.getPrice(), "null字段应该保持null");
        assertTrue(parsed.isActive(), "非null字段应该正确");
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

    public static class ComplexTestObject {
        private Long id;
        private String name;
        private TestObject nestedObject;
        private List<String> tags;
        private Map<String, Object> metadata;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public TestObject getNestedObject() { return nestedObject; }
        public void setNestedObject(TestObject nestedObject) { this.nestedObject = nestedObject; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    public static class DateTimeTestObject {
        private Long id;
        private LocalDateTime createTime;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    }
}
