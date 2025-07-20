package com.shiwu.common.test;

/**
 * 测试配置类
 * 定义测试中使用的常量和配置
 */
public class TestConfig {
    
    // 测试数据常量
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_PASSWORD = "testpassword123";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PHONE = "13800138000";
    public static final String TEST_NICKNAME = "测试用户";
    
    // 测试ID常量
    public static final Long TEST_USER_ID = 1L;
    public static final Long TEST_ADMIN_ID = 1L;
    public static final Long TEST_PRODUCT_ID = 1L;
    public static final Long TEST_ORDER_ID = 1L;
    public static final Long TEST_CATEGORY_ID = 1L;
    
    // 边界值常量
    public static final Long BOUNDARY_ID_ZERO = 0L;
    public static final Long BOUNDARY_ID_NEGATIVE = -1L;
    public static final Long BOUNDARY_ID_MAX = Long.MAX_VALUE;
    public static final Long BOUNDARY_ID_NONEXISTENT = 99999L;
    
    // 测试状态常量
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_INACTIVE = 0;
    public static final Integer STATUS_DELETED = -1;
    
    // 测试字符串常量
    public static final String EMPTY_STRING = "";
    public static final String NULL_STRING = null;
    public static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
    public static final String CHINESE_CHARS = "中文测试字符串";
    public static final String MIXED_CHARS = "Mixed中英文123!@#";
    
    // 测试超长字符串
    public static final String LONG_STRING_100 = createLongString("测试", 100);
    public static final String LONG_STRING_1000 = createLongString("测试", 1000);
    
    // 测试SQL注入字符串
    public static final String SQL_INJECTION_1 = "'; DROP TABLE users; --";
    public static final String SQL_INJECTION_2 = "1' OR '1'='1";
    public static final String SQL_INJECTION_3 = "<script>alert('xss')</script>";
    
    /**
     * 创建长字符串的辅助方法
     */
    private static String createLongString(String base, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(base);
        }
        return sb.toString();
    }
    
    /**
     * 获取所有边界ID值
     */
    public static Long[] getBoundaryIds() {
        return new Long[]{
            BOUNDARY_ID_ZERO,
            BOUNDARY_ID_NEGATIVE,
            BOUNDARY_ID_MAX,
            BOUNDARY_ID_NONEXISTENT
        };
    }
    
    /**
     * 获取所有测试字符串
     */
    public static String[] getTestStrings() {
        return new String[]{
            NULL_STRING,
            EMPTY_STRING,
            SPECIAL_CHARS,
            CHINESE_CHARS,
            MIXED_CHARS,
            LONG_STRING_100,
            SQL_INJECTION_1,
            SQL_INJECTION_2,
            SQL_INJECTION_3
        };
    }
    
    /**
     * 获取所有测试状态值
     */
    public static Integer[] getTestStatuses() {
        return new Integer[]{
            null,
            STATUS_DELETED,
            STATUS_INACTIVE,
            STATUS_ACTIVE,
            999,
            Integer.MAX_VALUE,
            Integer.MIN_VALUE
        };
    }
}
