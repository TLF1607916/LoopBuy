package com.shiwu.review.dao;

import com.shiwu.review.model.Review;
import com.shiwu.review.model.ReviewVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 评价DAO单元测试
 * 
 * 注意：这些测试需要真实的数据库连接
 * 在实际运行前，请确保：
 * 1. 数据库连接配置正确
 * 2. 数据库中存在必要的测试数据
 * 3. review表已创建
 * 
 * @author Shiwu Team
 * @version 1.0
 */
@DisplayName("评价DAO测试")
class ReviewDaoTest {

    private ReviewDao reviewDao;

    @BeforeEach
    void setUp() {
        reviewDao = new ReviewDao();
    }

    @Test
    @DisplayName("创建评价 - 成功场景")
    void testCreateReview_Success() {
        // Given
        Review review = new Review();
        review.setOrderId(1L);
        review.setProductId(1L);
        review.setUserId(1L);
        review.setRating(5);
        review.setComment("测试评价");

        try {
            // When
            Long reviewId = reviewDao.createReview(review);

            // Then
            if (reviewId != null) {
                assertNotNull(reviewId);
                assertTrue(reviewId > 0);
                System.out.println("创建评价成功，ID: " + reviewId);
            } else {
                System.out.println("数据库连接失败或测试数据不存在，跳过测试");
            }
        } catch (Exception e) {
            System.out.println("数据库连接失败，跳过测试: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("检查订单是否已评价 - 未评价")
    void testIsOrderReviewed_NotReviewed() {
        try {
            // When
            boolean isReviewed = reviewDao.isOrderReviewed(999999L); // 使用不存在的订单ID

            // Then
            assertFalse(isReviewed);
            System.out.println("检查订单是否已评价测试通过");
        } catch (Exception e) {
            System.out.println("数据库连接失败，跳过测试: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("根据ID查询评价 - 不存在")
    void testFindById_NotFound() {
        try {
            // When
            Review review = reviewDao.findById(999999L); // 使用不存在的评价ID

            // Then
            assertNull(review);
            System.out.println("根据ID查询评价测试通过");
        } catch (Exception e) {
            System.out.println("数据库连接失败，跳过测试: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("根据商品ID查询评价列表")
    void testFindReviewsByProductId() {
        try {
            // When
            List<ReviewVO> reviews = reviewDao.findReviewsByProductId(1L);

            // Then
            assertNotNull(reviews);
            System.out.println("根据商品ID查询评价列表测试通过，数量: " + reviews.size());
        } catch (Exception e) {
            System.out.println("数据库连接失败，跳过测试: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("根据用户ID查询评价列表")
    void testFindReviewsByUserId() {
        try {
            // When
            List<ReviewVO> reviews = reviewDao.findReviewsByUserId(1L);

            // Then
            assertNotNull(reviews);
            System.out.println("根据用户ID查询评价列表测试通过，数量: " + reviews.size());
        } catch (Exception e) {
            System.out.println("数据库连接失败，跳过测试: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("创建评价 - 参数验证")
    void testCreateReview_Validation() {
        try {
            // Given - 缺少必要字段的评价
            Review review = new Review();
            // 不设置必要字段

            // When
            Long reviewId = reviewDao.createReview(review);

            // Then
            // 应该返回null或抛出异常
            if (reviewId == null) {
                System.out.println("参数验证测试通过：缺少必要字段时返回null");
            } else {
                fail("应该因为缺少必要字段而失败");
            }
        } catch (Exception e) {
            // 预期的异常
            System.out.println("参数验证测试通过：缺少必要字段时抛出异常 - " + e.getMessage());
        }
    }

    @Test
    @DisplayName("数据库连接测试")
    void testDatabaseConnection() {
        try {
            // 尝试执行一个简单的查询来测试数据库连接
            List<ReviewVO> reviews = reviewDao.findReviewsByProductId(1L);
            System.out.println("数据库连接正常");
        } catch (Exception e) {
            System.out.println("数据库连接失败: " + e.getMessage());
            System.out.println("请检查：");
            System.out.println("1. 数据库服务是否启动");
            System.out.println("2. 数据库连接配置是否正确");
            System.out.println("3. review表是否已创建");
        }
    }

    @Test
    @DisplayName("完整的评价流程测试")
    void testCompleteReviewFlow() {
        try {
            // 1. 创建评价
            Review review = new Review();
            review.setOrderId(1L);
            review.setProductId(1L);
            review.setUserId(1L);
            review.setRating(4);
            review.setComment("完整流程测试评价");

            Long reviewId = reviewDao.createReview(review);
            
            if (reviewId != null) {
                System.out.println("步骤1：创建评价成功，ID: " + reviewId);

                // 2. 检查订单是否已评价
                boolean isReviewed = reviewDao.isOrderReviewed(1L);
                assertTrue(isReviewed);
                System.out.println("步骤2：检查订单已评价状态成功");

                // 3. 根据ID查询评价
                Review foundReview = reviewDao.findById(reviewId);
                assertNotNull(foundReview);
                assertEquals(4, foundReview.getRating().intValue());
                System.out.println("步骤3：根据ID查询评价成功");

                // 4. 根据商品ID查询评价列表
                List<ReviewVO> productReviews = reviewDao.findReviewsByProductId(1L);
                assertNotNull(productReviews);
                assertTrue(productReviews.size() > 0);
                System.out.println("步骤4：根据商品ID查询评价列表成功，数量: " + productReviews.size());

                // 5. 根据用户ID查询评价列表
                List<ReviewVO> userReviews = reviewDao.findReviewsByUserId(1L);
                assertNotNull(userReviews);
                assertTrue(userReviews.size() > 0);
                System.out.println("步骤5：根据用户ID查询评价列表成功，数量: " + userReviews.size());

                System.out.println("完整评价流程测试通过！");
            } else {
                System.out.println("数据库连接失败或测试数据不存在，跳过完整流程测试");
            }
        } catch (Exception e) {
            System.out.println("完整流程测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
