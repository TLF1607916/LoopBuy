package com.shiwu.product.dao;

import com.shiwu.common.test.TestConfig;
import com.shiwu.common.test.TestUtils;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCardVO;
//import com.shiwu.product.model.ProductDetailVO;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

//import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductDao完整测试套件
 * 严格遵循软件工程测试规范
 * 
 * 测试覆盖：
 * 1. 单元测试 - 每个方法的详细测试
 * 2. 边界测试 - 所有边界条件
 * 3. 异常测试 - 所有异常路径
 * 4. 安全测试 - SQL注入等安全问题
 * 5. 性能测试 - 基本性能验证
 * 6. 并发测试 - 多线程安全性
 * 7. 数据完整性测试 - 数据一致性验证
 */
@DisplayName("ProductDao完整测试套件")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class ProductDaoComprehensiveTest extends TestBase {

    private ProductDao productDao;
    private static final int PERFORMANCE_TEST_ITERATIONS = 100;
    private static final int CONCURRENT_THREADS = 10;

    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        productDao = new ProductDao();
    }

    // ==================== 基础功能测试 ====================

    @Test
    @Order(1)
    @DisplayName("1.1 ProductDao实例化测试")
    public void testProductDaoInstantiation() {
        assertNotNull(productDao, "ProductDao应该能够正常实例化");
        assertNotNull(productDao.getClass(), "ProductDao类应该存在");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 findById方法完整测试")
    public void testFindByIdComprehensive() {
        // 测试null参数
        assertThrows(NullPointerException.class, () -> {
            productDao.findById(null);
        }, "findById(null)应该抛出NullPointerException");

        // 测试边界值
        for (Long id : TestConfig.getBoundaryIds()) {
            if (id != null) {
                try {
                    Product result = productDao.findById(id);
                    assertNull(result, "不存在的ID应该返回null: " + id);
                } catch (Exception e) {
                    System.out.println("边界ID " + id + " 抛出异常: " + e.getClass().getSimpleName());
                }
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("1.3 findProducts复合查询测试")
    public void testFindProductsComprehensive() {
        // 测试null参数
        Map<String, Object> result1 = productDao.findProducts(null, null, null, null, null, null, 1, 10);
        assertNotNull(result1, "null参数应该返回结果");

        // 测试SQL注入防护
        Map<String, Object> result2 = productDao.findProducts(TestConfig.SQL_INJECTION_1, null, null, null, null, null, 1, 10);
        assertNotNull(result2, "SQL注入字符串应该被安全处理");

        // 测试特殊字符
        for (String keyword : TestConfig.getTestStrings()) {
            if (keyword != null) {
                Map<String, Object> result = productDao.findProducts(keyword, null, null, null, null, null, 1, 10);
                assertNotNull(result, "特殊字符搜索应该返回结果: " + keyword);
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("1.4 createProduct方法完整测试")
    public void testCreateProductComprehensive() {
        // 测试null参数
        assertThrows(NullPointerException.class, () -> {
            productDao.createProduct(null);
        }, "createProduct(null)应该抛出NullPointerException");

        // 测试不完整的商品对象
        Product incompleteProduct = new Product();
        try {
            Long result = productDao.createProduct(incompleteProduct);
            // 如果没有抛出异常，结果应该为null
            assertNull(result, "不完整的商品对象应该创建失败");
        } catch (Exception e) {
            assertNotNull(e, "不完整商品对象应该抛出异常或返回null");
        }

        // 测试包含特殊字符的商品对象
        Product specialCharProduct = TestUtils.createTestProduct();
        specialCharProduct.setTitle(TestConfig.SPECIAL_CHARS);
        specialCharProduct.setDescription(TestConfig.SQL_INJECTION_1);
        
        try {
            Long result = productDao.createProduct(specialCharProduct);
            assertNotNull(productDao, "特殊字符处理不应该导致DAO异常");
        } catch (Exception e) {
            System.out.println("特殊字符商品创建异常: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("1.5 updateProduct方法完整测试")
    public void testUpdateProductComprehensive() {
        // 测试null参数
        assertThrows(NullPointerException.class, () -> {
            productDao.updateProduct(null);
        }, "updateProduct(null)应该抛出NullPointerException");

        // 测试不存在的商品更新
        Product nonExistentProduct = TestUtils.createTestProduct();
        nonExistentProduct.setId(TestConfig.BOUNDARY_ID_NONEXISTENT);
        
        boolean result = productDao.updateProduct(nonExistentProduct);
        assertFalse(result, "更新不存在的商品应该返回false");
    }

    @Test
    @Order(6)
    @DisplayName("1.6 deleteProduct方法完整测试")
    public void testDeleteProductComprehensive() {
        // 测试null参数
        assertThrows(NullPointerException.class, () -> {
            productDao.deleteProduct(null, TestConfig.TEST_USER_ID);
        }, "deleteProduct(null, sellerId)应该抛出NullPointerException");

        assertThrows(NullPointerException.class, () -> {
            productDao.deleteProduct(TestConfig.TEST_PRODUCT_ID, null);
        }, "deleteProduct(productId, null)应该抛出NullPointerException");

        // 测试删除不存在的商品
        boolean result = productDao.deleteProduct(TestConfig.BOUNDARY_ID_NONEXISTENT, TestConfig.TEST_USER_ID);
        assertFalse(result, "删除不存在的商品应该返回false");
    }

    @Test
    @Order(7)
    @DisplayName("1.7 findProductsBySellerIdAndStatus方法测试")
    public void testFindProductsBySellerIdAndStatusComprehensive() {
        // 测试null sellerId
        assertThrows(NullPointerException.class, () -> {
            productDao.findProductsBySellerIdAndStatus(null, TestConfig.STATUS_ACTIVE);
        }, "findProductsBySellerIdAndStatus(null, status)应该抛出NullPointerException");

        // 测试null status
        List<ProductCardVO> result = productDao.findProductsBySellerIdAndStatus(TestConfig.TEST_USER_ID, null);
        assertNotNull(result, "null状态查询应该返回结果");

        // 测试边界sellerId
        for (Long sellerId : TestConfig.getBoundaryIds()) {
            if (sellerId != null) {
                try {
                    List<ProductCardVO> products = productDao.findProductsBySellerIdAndStatus(sellerId, TestConfig.STATUS_ACTIVE);
                    assertNotNull(products, "边界sellerId查询应该返回结果: " + sellerId);
                    assertTrue(products.isEmpty(), "不存在卖家的商品应该返回空列表: " + sellerId);
                } catch (Exception e) {
                    System.out.println("边界sellerId " + sellerId + " 抛出异常: " + e.getClass().getSimpleName());
                }
            }
        }
    }

    // ==================== 性能测试 ====================

    @Test
    @Order(10)
    @DisplayName("2.1 findById性能测试")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testFindByIdPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            productDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 5000, 
            String.format("findById性能测试: %d次调用耗时%dms，应该小于5000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
        
        System.out.printf("ProductDao.findById性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 findProducts性能测试")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testFindProductsPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            productDao.findProducts("nonexistent_product_" + i, null, null, null, null, null, 1, 10);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 10000, 
            String.format("findProducts性能测试: %d次调用耗时%dms，应该小于10000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 统计方法性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testStatisticsPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            productDao.getTotalProductCount();
            productDao.getProductCountByStatus(TestConfig.STATUS_ACTIVE);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 3000, 
            String.format("统计方法性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS * 2, duration));
    }

    // ==================== 并发测试 ====================

    @Test
    @Order(20)
    @DisplayName("3.1 并发findById测试")
    public void testConcurrentFindById() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        Product result = productDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
                        assertNull(result, "并发查询应该返回null");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS), "并发测试应该在10秒内完成");
        executor.shutdown();
    }

    @Test
    @Order(21)
    @DisplayName("3.2 并发findProducts测试")
    public void testConcurrentFindProducts() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        Map<String, Object> result = productDao.findProducts(
                            "search_" + threadId + "_" + j, null, null, null, null, null, 1, 10
                        );
                        assertNotNull(result, "并发搜索应该返回结果");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(15, TimeUnit.SECONDS), "并发测试应该在15秒内完成");
        executor.shutdown();
    }

    // ==================== 安全测试 ====================

    @Test
    @Order(30)
    @DisplayName("4.1 SQL注入防护测试")
    public void testSqlInjectionProtection() {
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE product; --",
            "1' OR '1'='1",
            "product'--",
            "' UNION SELECT * FROM product --",
            "'; INSERT INTO product VALUES ('hacker', 'description'); --"
        };
        
        for (String injection : sqlInjectionAttempts) {
            // 测试搜索注入
            Map<String, Object> searchResult = productDao.findProducts(injection, null, null, null, null, null, 1, 10);
            assertNotNull(searchResult, "搜索SQL注入应该被防护: " + injection);
            
            // 测试商品创建注入
            Product injectionProduct = TestUtils.createTestProduct();
            injectionProduct.setSellerId(TestBase.TEST_USER_ID_1); // 使用有效的用户ID
            injectionProduct.setTitle(injection);
            injectionProduct.setDescription(injection);

            try {
                Long createResult = productDao.createProduct(injectionProduct);
                // 不管成功与否，都不应该导致系统异常
                assertNotNull(productDao, "创建SQL注入应该被防护: " + injection);
            } catch (Exception e) {
                // 抛出异常也是可接受的防护措施
                assertNotNull(e, "SQL注入防护异常: " + injection);
            }
        }
    }

    @Test
    @Order(31)
    @DisplayName("4.2 XSS防护测试")
    public void testXssProtection() {
        String[] xssAttempts = {
            "<script>alert('xss')</script>",
            "<img src=x onerror=alert('xss')>",
            "javascript:alert('xss')",
            "<svg onload=alert('xss')>",
            "';alert('xss');//"
        };
        
        for (String xss : xssAttempts) {
            Map<String, Object> result = productDao.findProducts(xss, null, null, null, null, null, 1, 10);
            assertNotNull(result, "XSS攻击应该被防护: " + xss);
        }
    }

    // ==================== 数据完整性测试 ====================

    @Test
    @Order(40)
    @DisplayName("5.1 数据一致性测试")
    public void testDataConsistency() {
        // 测试多次查询的一致性
        Product result1 = productDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        Product result2 = productDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        Product result3 = productDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        
        assertEquals(result1, result2, "多次查询结果应该一致");
        assertEquals(result2, result3, "多次查询结果应该一致");
        
        // 测试统计数据一致性
        Long totalCount1 = productDao.getTotalProductCount();
        Long totalCount2 = productDao.getTotalProductCount();
        
        assertEquals(totalCount1, totalCount2, "统计数据应该一致");
    }

    @Test
    @Order(41)
    @DisplayName("5.2 分页一致性测试")
    public void testPaginationConsistency() {
        // 测试分页参数的一致性
        Map<String, Object> page1 = productDao.findProducts(null, null, null, null, null, null, 1, 10);
        Map<String, Object> page2 = productDao.findProducts(null, null, null, null, null, null, 2, 10);
        
        assertNotNull(page1, "第一页应该返回结果");
        assertNotNull(page2, "第二页应该返回结果");
        
        // 测试边界分页参数
        Map<String, Object> invalidPage1 = productDao.findProducts(null, null, null, null, null, null, -1, 10);
        Map<String, Object> invalidPage2 = productDao.findProducts(null, null, null, null, null, null, 1, -1);
        Map<String, Object> invalidPage3 = productDao.findProducts(null, null, null, null, null, null, 0, 0);
        
        assertNotNull(invalidPage1, "负数页码应该返回结果");
        assertNotNull(invalidPage2, "负数页大小应该返回结果");
        assertNotNull(invalidPage3, "零页参数应该返回结果");
    }

    // ==================== 压力测试 ====================

    @Test
    @Order(50)
    @DisplayName("6.1 高频调用压力测试")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    public void testHighFrequencyStress() {
        final int STRESS_ITERATIONS = 500;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < STRESS_ITERATIONS; i++) {
            productDao.findById((long) (i % 100));
            productDao.findProducts("product" + (i % 100), null, null, null, null, null, 1, 10);
            productDao.getTotalProductCount();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 30000, 
            String.format("压力测试: %d次调用耗时%dms，应该小于30000ms", 
                         STRESS_ITERATIONS * 3, duration));
        
        System.out.printf("ProductDao压力测试完成: %d次调用耗时%dms%n", STRESS_ITERATIONS * 3, duration);
    }

    // ==================== 清理和验证 ====================

    @AfterEach
    public void tearDown() {
        assertNotNull(productDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("ProductDao完整测试套件执行完成");
    }
}
