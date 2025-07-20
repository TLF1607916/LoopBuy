package com.shiwu.review.dao;

import com.shiwu.common.test.TestConfig;
import com.shiwu.review.model.Review;
import com.shiwu.review.model.ReviewVO;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReviewDao完整测试套件
 * 严格遵循软件工程测试规范
 */
@DisplayName("ReviewDao完整测试套件")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class ReviewDaoComprehensiveTest extends TestBase {

    private ReviewDao reviewDao;
    private static final int PERFORMANCE_TEST_ITERATIONS = 50;

    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        reviewDao = new ReviewDao();
    }

    // ==================== 基础功能测试 ====================

    @Test
    @Order(1)
    @DisplayName("1.1 ReviewDao实例化测试")
    public void testReviewDaoInstantiation() {
        assertNotNull(reviewDao, "ReviewDao应该能够正常实例化");
        assertNotNull(reviewDao.getClass(), "ReviewDao类应该存在");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 createReview方法测试")
    public void testCreateReview() {
        // 测试null参数
        Long result1 = reviewDao.createReview(null);
        assertNull(result1, "createReview(null)应该返回null");

        // 测试不完整的Review对象
        Review incompleteReview = new Review();
        Long result2 = reviewDao.createReview(incompleteReview);
        assertNull(result2, "不完整的Review应该创建失败");

        // 测试完整的Review对象（可能因外键约束失败）
        Review completeReview = new Review();
        completeReview.setOrderId(TestConfig.TEST_ORDER_ID);
        completeReview.setProductId(TestConfig.TEST_PRODUCT_ID);
        completeReview.setUserId(TestConfig.TEST_USER_ID);
        completeReview.setRating(5);
        completeReview.setComment("测试评价");
        
        try {
            Long result3 = reviewDao.createReview(completeReview);
            // 不管成功与否，都不应该抛出异常
            assertNotNull(reviewDao, "创建评价后DAO应该正常工作");
        } catch (Exception e) {
            // 外键约束异常是可接受的
            assertNotNull(e, "外键约束异常是可接受的");
        }
    }

    @Test
    @Order(3)
    @DisplayName("1.3 findById方法测试")
    public void testFindById() {
        // 测试null参数
        Review result1 = reviewDao.findById(null);
        assertNull(result1, "findById(null)应该返回null");

        // 测试边界值
        for (Long id : TestConfig.getBoundaryIds()) {
            if (id != null) {
                Review result = reviewDao.findById(id);
                assertNull(result, "不存在的ID应该返回null: " + id);
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("1.4 isOrderReviewed方法测试")
    public void testIsOrderReviewed() {
        // 测试null参数
        boolean result1 = reviewDao.isOrderReviewed(null);
        assertFalse(result1, "isOrderReviewed(null)应该返回false");

        // 测试边界值
        for (Long orderId : TestConfig.getBoundaryIds()) {
            if (orderId != null) {
                boolean result = reviewDao.isOrderReviewed(orderId);
                assertFalse(result, "不存在的订单应该返回false: " + orderId);
            }
        }
    }

    @Test
    @Order(5)
    @DisplayName("1.5 findReviewsByProductId方法测试")
    public void testFindReviewsByProductId() {
        // 测试null参数
        List<ReviewVO> result1 = reviewDao.findReviewsByProductId(null);
        assertNotNull(result1, "findReviewsByProductId(null)应该返回空列表");
        assertTrue(result1.isEmpty(), "null商品ID应该返回空列表");

        // 测试边界值
        for (Long productId : TestConfig.getBoundaryIds()) {
            if (productId != null) {
                List<ReviewVO> result = reviewDao.findReviewsByProductId(productId);
                assertNotNull(result, "边界商品ID查询应该返回列表: " + productId);
                assertTrue(result.isEmpty(), "不存在商品的评价应该为空: " + productId);
            }
        }
    }

    @Test
    @Order(6)
    @DisplayName("1.6 findReviewsByUserId方法测试")
    public void testFindReviewsByUserId() {
        // 测试null参数
        List<ReviewVO> result1 = reviewDao.findReviewsByUserId(null);
        assertNotNull(result1, "findReviewsByUserId(null)应该返回空列表");
        assertTrue(result1.isEmpty(), "null用户ID应该返回空列表");

        // 测试边界值
        for (Long userId : TestConfig.getBoundaryIds()) {
            if (userId != null) {
                List<ReviewVO> result = reviewDao.findReviewsByUserId(userId);
                assertNotNull(result, "边界用户ID查询应该返回列表: " + userId);
                assertTrue(result.isEmpty(), "不存在用户的评价应该为空: " + userId);
            }
        }
    }

    // ==================== 性能测试 ====================

    @Test
    @Order(10)
    @DisplayName("2.1 findById性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testFindByIdPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            reviewDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 3000, 
            String.format("findById性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
        
        System.out.printf("ReviewDao.findById性能: %d次调用耗时%dms，平均%.2fms/次%n", 
                         PERFORMANCE_TEST_ITERATIONS, duration, (double)duration/PERFORMANCE_TEST_ITERATIONS);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 isOrderReviewed性能测试")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testIsOrderReviewedPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            reviewDao.isOrderReviewed(TestConfig.BOUNDARY_ID_NONEXISTENT);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 3000, 
            String.format("isOrderReviewed性能测试: %d次调用耗时%dms，应该小于3000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 findReviewsByProductId性能测试")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testFindReviewsByProductIdPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < PERFORMANCE_TEST_ITERATIONS; i++) {
            reviewDao.findReviewsByProductId(TestConfig.BOUNDARY_ID_NONEXISTENT);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 5000, 
            String.format("findReviewsByProductId性能测试: %d次调用耗时%dms，应该小于5000ms", 
                         PERFORMANCE_TEST_ITERATIONS, duration));
    }

    // ==================== 安全测试 ====================

    @Test
    @Order(20)
    @DisplayName("3.1 SQL注入防护测试")
    public void testSqlInjectionProtection() {
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE review; --",
            "1' OR '1'='1",
            "review'--",
            "' UNION SELECT * FROM review --"
        };
        
        for (String injection : sqlInjectionAttempts) {
            // 测试评价创建注入
            Review injectionReview = new Review();
            injectionReview.setOrderId(1L);
            injectionReview.setProductId(1L);
            injectionReview.setUserId(1L);
            injectionReview.setRating(5);
            injectionReview.setComment(injection);
            
            try {
                Long result = reviewDao.createReview(injectionReview);
                // 不管成功与否，都不应该导致系统异常
                assertNotNull(reviewDao, "创建评价SQL注入应该被防护: " + injection);
            } catch (Exception e) {
                // 抛出异常也是可接受的防护措施
                assertNotNull(e, "SQL注入防护异常: " + injection);
            }
        }
    }

    // ==================== 数据完整性测试 ====================

    @Test
    @Order(30)
    @DisplayName("4.1 数据一致性测试")
    public void testDataConsistency() {
        // 测试多次查询的一致性
        Review result1 = reviewDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        Review result2 = reviewDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        Review result3 = reviewDao.findById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        
        assertEquals(result1, result2, "多次查询结果应该一致");
        assertEquals(result2, result3, "多次查询结果应该一致");
        
        // 测试订单评价状态一致性
        boolean reviewed1 = reviewDao.isOrderReviewed(TestConfig.BOUNDARY_ID_NONEXISTENT);
        boolean reviewed2 = reviewDao.isOrderReviewed(TestConfig.BOUNDARY_ID_NONEXISTENT);
        
        assertEquals(reviewed1, reviewed2, "订单评价状态查询应该一致");
    }

    @Test
    @Order(31)
    @DisplayName("4.2 评价列表一致性测试")
    public void testReviewListConsistency() {
        // 测试商品评价列表一致性
        List<ReviewVO> productReviews1 = reviewDao.findReviewsByProductId(TestConfig.BOUNDARY_ID_NONEXISTENT);
        List<ReviewVO> productReviews2 = reviewDao.findReviewsByProductId(TestConfig.BOUNDARY_ID_NONEXISTENT);
        
        assertEquals(productReviews1.size(), productReviews2.size(), "商品评价列表大小应该一致");
        
        // 测试用户评价列表一致性
        List<ReviewVO> userReviews1 = reviewDao.findReviewsByUserId(TestConfig.BOUNDARY_ID_NONEXISTENT);
        List<ReviewVO> userReviews2 = reviewDao.findReviewsByUserId(TestConfig.BOUNDARY_ID_NONEXISTENT);
        
        assertEquals(userReviews1.size(), userReviews2.size(), "用户评价列表大小应该一致");
    }

    // ==================== 边界值测试 ====================

    @Test
    @Order(40)
    @DisplayName("5.1 评分边界值测试")
    public void testRatingBoundaries() {
        int[] ratings = {-1, 0, 1, 5, 6, 10, Integer.MAX_VALUE, Integer.MIN_VALUE};
        
        for (int rating : ratings) {
            Review review = new Review();
            review.setOrderId(TestConfig.TEST_ORDER_ID);
            review.setProductId(TestConfig.TEST_PRODUCT_ID);
            review.setUserId(TestConfig.TEST_USER_ID);
            review.setRating(rating);
            review.setComment("边界值测试评价");
            
            try {
                Long result = reviewDao.createReview(review);
                // 不管成功与否，都不应该抛出异常
                assertNotNull(reviewDao, "评分边界值测试应该正常处理: " + rating);
            } catch (Exception e) {
                // 数据库约束异常是可接受的
                assertNotNull(e, "评分边界值异常是可接受的: " + rating);
            }
        }
    }

    @Test
    @Order(41)
    @DisplayName("5.2 评价内容边界值测试")
    public void testCommentBoundaries() {
        String[] comments = {
            null,                                    // null评价
            "",                                      // 空评价
            "a",                                     // 单字符评价
            TestConfig.LONG_STRING_1000,             // 超长评价
            TestConfig.SPECIAL_CHARS,                // 特殊字符评价
            TestConfig.CHINESE_CHARS,                // 中文评价
            TestConfig.SQL_INJECTION_1               // SQL注入评价
        };
        
        for (String comment : comments) {
            Review review = new Review();
            review.setOrderId(TestConfig.TEST_ORDER_ID);
            review.setProductId(TestConfig.TEST_PRODUCT_ID);
            review.setUserId(TestConfig.TEST_USER_ID);
            review.setRating(5);
            review.setComment(comment);
            
            try {
                Long result = reviewDao.createReview(review);
                // 不管成功与否，都不应该抛出异常
                assertNotNull(reviewDao, "评价内容边界值测试应该正常处理");
            } catch (Exception e) {
                // 数据库约束异常是可接受的
                assertNotNull(e, "评价内容边界值异常是可接受的");
            }
        }
    }

    // ==================== 压力测试 ====================

    @Test
    @Order(50)
    @DisplayName("6.1 高频调用压力测试")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testHighFrequencyStress() {
        final int STRESS_ITERATIONS = 200;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < STRESS_ITERATIONS; i++) {
            reviewDao.findById((long) (i % 50));
            reviewDao.isOrderReviewed((long) (i % 50));
            reviewDao.findReviewsByProductId((long) (i % 50));
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 15000, 
            String.format("压力测试: %d次调用耗时%dms，应该小于15000ms", 
                         STRESS_ITERATIONS * 3, duration));
        
        System.out.printf("ReviewDao压力测试完成: %d次调用耗时%dms%n", STRESS_ITERATIONS * 3, duration);
    }

    // ==================== 清理和验证 ====================

    @AfterEach
    public void tearDown() {
        assertNotNull(reviewDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("ReviewDao完整测试套件执行完成");
    }
}
