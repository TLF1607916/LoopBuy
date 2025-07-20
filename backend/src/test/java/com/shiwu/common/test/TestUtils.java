package com.shiwu.common.test;

import com.shiwu.user.model.User;
import com.shiwu.product.model.Product;
import com.shiwu.order.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * 测试工具类
 * 提供测试中常用的工具方法和对象创建方法
 */
public class TestUtils {
    
    private static final Random random = new Random();
    
    /**
     * 创建测试用户对象
     */
    public static User createTestUser() {
        User user = new User();
        user.setId(TestConfig.TEST_USER_ID);
        user.setUsername(TestConfig.TEST_USERNAME);
        user.setPassword(TestConfig.TEST_PASSWORD);
        user.setEmail(TestConfig.TEST_EMAIL);
        user.setPhone(TestConfig.TEST_PHONE);
        user.setNickname(TestConfig.TEST_NICKNAME);
        user.setStatus(TestConfig.STATUS_ACTIVE);
        user.setCreateTime(LocalDateTime.now());
        return user;
    }
    
    /**
     * 创建测试用户对象（带随机数据）
     */
    public static User createRandomTestUser() {
        User user = new User();
        user.setId(random.nextLong());
        user.setUsername("user" + random.nextInt(10000));
        user.setPassword("password" + random.nextInt(10000));
        user.setEmail("test" + random.nextInt(10000) + "@example.com");
        user.setPhone("138" + String.format("%08d", random.nextInt(100000000)));
        user.setNickname("用户" + random.nextInt(10000));
        user.setStatus(TestConfig.STATUS_ACTIVE);
        user.setCreateTime(LocalDateTime.now());
        return user;
    }
    
    /**
     * 创建测试商品对象
     */
    public static Product createTestProduct() {
        Product product = new Product();
        product.setId(TestConfig.TEST_PRODUCT_ID);
        product.setSellerId(TestConfig.TEST_USER_ID);
        product.setCategoryId(TestConfig.TEST_CATEGORY_ID.intValue());
        product.setTitle("测试商品");
        product.setDescription("这是一个测试商品的描述");
        product.setPrice(new BigDecimal("99.99"));
        product.setStatus(TestConfig.STATUS_ACTIVE);
        product.setCreateTime(LocalDateTime.now());
        return product;
    }
    
    /**
     * 创建测试商品对象（带随机数据）
     */
    public static Product createRandomTestProduct() {
        Product product = new Product();
        product.setId(random.nextLong());
        product.setSellerId(random.nextLong());
        product.setCategoryId(random.nextInt(10) + 1);
        product.setTitle("商品" + random.nextInt(10000));
        product.setDescription("商品描述" + random.nextInt(10000));
        product.setPrice(new BigDecimal(random.nextDouble() * 1000));
        product.setStatus(TestConfig.STATUS_ACTIVE);
        product.setCreateTime(LocalDateTime.now());
        return product;
    }
    
    /**
     * 创建测试订单对象
     */
    public static Order createTestOrder() {
        Order order = new Order();
        order.setId(TestConfig.TEST_ORDER_ID);
        order.setBuyerId(TestConfig.TEST_USER_ID);
        order.setSellerId(TestConfig.TEST_USER_ID + 1);
        order.setProductId(TestConfig.TEST_PRODUCT_ID);
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        order.setProductTitleSnapshot("测试商品快照");
        order.setProductDescriptionSnapshot("测试商品描述快照");
        order.setProductImageUrlsSnapshot("http://example.com/image.jpg");
        order.setStatus(TestConfig.STATUS_ACTIVE);
        order.setCreateTime(LocalDateTime.now());
        return order;
    }
    
    /**
     * 创建测试订单对象（带随机数据）
     */
    public static Order createRandomTestOrder() {
        Order order = new Order();
        order.setId(random.nextLong());
        order.setBuyerId(random.nextLong());
        order.setSellerId(random.nextLong());
        order.setProductId(random.nextLong());
        order.setPriceAtPurchase(new BigDecimal(random.nextDouble() * 1000));
        order.setProductTitleSnapshot("商品快照" + random.nextInt(10000));
        order.setProductDescriptionSnapshot("描述快照" + random.nextInt(10000));
        order.setProductImageUrlsSnapshot("http://example.com/image" + random.nextInt(10000) + ".jpg");
        order.setStatus(TestConfig.STATUS_ACTIVE);
        order.setCreateTime(LocalDateTime.now());
        return order;
    }
    
    /**
     * 验证两个对象是否相等（忽略时间字段）
     */
    public static boolean equalsIgnoreTime(User user1, User user2) {
        if (user1 == null && user2 == null) return true;
        if (user1 == null || user2 == null) return false;
        
        return user1.getId().equals(user2.getId()) &&
               user1.getUsername().equals(user2.getUsername()) &&
               user1.getEmail().equals(user2.getEmail()) &&
               user1.getPhone().equals(user2.getPhone()) &&
               user1.getNickname().equals(user2.getNickname()) &&
               user1.getStatus().equals(user2.getStatus());
    }
    
    /**
     * 验证两个商品对象是否相等（忽略时间字段）
     */
    public static boolean equalsIgnoreTime(Product product1, Product product2) {
        if (product1 == null && product2 == null) return true;
        if (product1 == null || product2 == null) return false;
        
        return product1.getId().equals(product2.getId()) &&
               product1.getSellerId().equals(product2.getSellerId()) &&
               product1.getCategoryId().equals(product2.getCategoryId()) &&
               product1.getTitle().equals(product2.getTitle()) &&
               product1.getDescription().equals(product2.getDescription()) &&
               product1.getPrice().equals(product2.getPrice()) &&
               product1.getStatus().equals(product2.getStatus());
    }
    
    /**
     * 验证两个订单对象是否相等（忽略时间字段）
     */
    public static boolean equalsIgnoreTime(Order order1, Order order2) {
        if (order1 == null && order2 == null) return true;
        if (order1 == null || order2 == null) return false;
        
        return order1.getId().equals(order2.getId()) &&
               order1.getBuyerId().equals(order2.getBuyerId()) &&
               order1.getSellerId().equals(order2.getSellerId()) &&
               order1.getProductId().equals(order2.getProductId()) &&
               order1.getPriceAtPurchase().equals(order2.getPriceAtPurchase()) &&
               order1.getStatus().equals(order2.getStatus());
    }
    
    /**
     * 生成随机字符串
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * 生成随机邮箱
     */
    public static String generateRandomEmail() {
        return generateRandomString(8) + "@" + generateRandomString(5) + ".com";
    }
    
    /**
     * 生成随机手机号
     */
    public static String generateRandomPhone() {
        return "138" + String.format("%08d", random.nextInt(100000000));
    }
    
    /**
     * 验证字符串是否为空或null
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 安全地比较两个对象
     */
    public static boolean safeEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) return true;
        if (obj1 == null || obj2 == null) return false;
        return obj1.equals(obj2);
    }
    
    /**
     * 创建长字符串
     */
    public static String createLongString(String base, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(base);
        }
        return sb.toString();
    }
    
    /**
     * 睡眠指定毫秒数（用于性能测试）
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 获取当前时间戳
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
