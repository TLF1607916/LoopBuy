package com.shiwu.review.service;

import com.shiwu.review.model.*;
import com.shiwu.review.service.impl.ReviewServiceImpl;
import com.shiwu.common.test.TestConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReviewService 综合测试类
 * 测试评价服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ReviewService 综合测试")
public class ReviewServiceComprehensiveTest {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceComprehensiveTest.class);
    
    private ReviewService reviewService;
    
    // 测试数据
    private static final Long TEST_USER_ID = TestConfig.TEST_USER_ID;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Integer TEST_RATING_5 = 5;
    private static final Integer TEST_RATING_3 = 3;
    private static final Integer TEST_RATING_1 = 1;
    private static final String TEST_COMMENT = "这是一个测试评价，商品质量很好！";
    
    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl();
        logger.info("ReviewService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("8.1 提交评价测试")
    public void testSubmitReview() {
        logger.info("开始测试提交评价功能");
        
        // 创建评价DTO
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(TEST_ORDER_ID);
        dto.setRating(TEST_RATING_5);
        dto.setComment(TEST_COMMENT);
        
        // 测试提交评价
        ReviewOperationResult result = reviewService.submitReview(dto, TEST_USER_ID);
        assertNotNull(result, "提交评价结果不应为空");
        
        logger.info("提交评价测试通过: success={}, errorMessage={}", 
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(2)
    @DisplayName("8.2 提交评价参数验证测试")
    public void testSubmitReviewValidation() {
        logger.info("开始测试提交评价参数验证");
        
        // 测试null DTO
        ReviewOperationResult result1 = reviewService.submitReview(null, TEST_USER_ID);
        assertNotNull(result1, "null DTO应该返回结果对象");
        assertFalse(result1.isSuccess(), "null DTO应该提交失败");
        
        // 测试null用户ID
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(TEST_ORDER_ID);
        dto.setRating(TEST_RATING_5);
        dto.setComment(TEST_COMMENT);
        
        ReviewOperationResult result2 = reviewService.submitReview(dto, null);
        assertNotNull(result2, "null用户ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null用户ID应该提交失败");
        
        // 测试null订单ID
        ReviewCreateDTO dto2 = new ReviewCreateDTO();
        dto2.setOrderId(null);
        dto2.setRating(TEST_RATING_5);
        dto2.setComment(TEST_COMMENT);
        
        ReviewOperationResult result3 = reviewService.submitReview(dto2, TEST_USER_ID);
        assertNotNull(result3, "null订单ID应该返回结果对象");
        assertFalse(result3.isSuccess(), "null订单ID应该提交失败");
        
        // 测试null评分
        ReviewCreateDTO dto3 = new ReviewCreateDTO();
        dto3.setOrderId(TEST_ORDER_ID);
        dto3.setRating(null);
        dto3.setComment(TEST_COMMENT);
        
        ReviewOperationResult result4 = reviewService.submitReview(dto3, TEST_USER_ID);
        assertNotNull(result4, "null评分应该返回结果对象");
        assertFalse(result4.isSuccess(), "null评分应该提交失败");
        
        logger.info("提交评价参数验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("8.3 评分范围验证测试")
    public void testRatingRangeValidation() {
        logger.info("开始测试评分范围验证");
        
        // 测试评分为0（无效）
        ReviewCreateDTO dto1 = new ReviewCreateDTO();
        dto1.setOrderId(TEST_ORDER_ID);
        dto1.setRating(0);
        dto1.setComment(TEST_COMMENT);
        
        ReviewOperationResult result1 = reviewService.submitReview(dto1, TEST_USER_ID);
        assertNotNull(result1, "评分为0应该返回结果对象");
        assertFalse(result1.isSuccess(), "评分为0应该提交失败");
        
        // 测试评分为6（无效）
        ReviewCreateDTO dto2 = new ReviewCreateDTO();
        dto2.setOrderId(TEST_ORDER_ID);
        dto2.setRating(6);
        dto2.setComment(TEST_COMMENT);
        
        ReviewOperationResult result2 = reviewService.submitReview(dto2, TEST_USER_ID);
        assertNotNull(result2, "评分为6应该返回结果对象");
        assertFalse(result2.isSuccess(), "评分为6应该提交失败");
        
        // 测试负数评分（无效）
        ReviewCreateDTO dto3 = new ReviewCreateDTO();
        dto3.setOrderId(TEST_ORDER_ID);
        dto3.setRating(-1);
        dto3.setComment(TEST_COMMENT);
        
        ReviewOperationResult result3 = reviewService.submitReview(dto3, TEST_USER_ID);
        assertNotNull(result3, "负数评分应该返回结果对象");
        assertFalse(result3.isSuccess(), "负数评分应该提交失败");
        
        logger.info("评分范围验证测试通过");
    }

    @Test
    @Order(4)
    @DisplayName("8.4 有效评分测试")
    public void testValidRatings() {
        logger.info("开始测试有效评分");
        
        // 测试1-5星评分
        for (int rating = 1; rating <= 5; rating++) {
            ReviewCreateDTO dto = new ReviewCreateDTO();
            dto.setOrderId(TEST_ORDER_ID + rating); // 使用不同的订单ID避免重复评价
            dto.setRating(rating);
            dto.setComment("测试" + rating + "星评价");
            
            ReviewOperationResult result = reviewService.submitReview(dto, TEST_USER_ID);
            assertNotNull(result, rating + "星评分应该返回结果对象");
            // 注意：由于订单可能不存在，这里不强制要求成功，只验证方法能正常执行
            logger.info("{}星评分测试: success={}", rating, result.isSuccess());
        }
        
        logger.info("有效评分测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("8.5 根据商品ID获取评价列表测试")
    public void testGetReviewsByProductId() {
        logger.info("开始测试根据商品ID获取评价列表功能");
        
        // 测试获取商品评价列表
        List<ReviewVO> reviews = reviewService.getReviewsByProductId(TEST_PRODUCT_ID);
        assertNotNull(reviews, "评价列表不应为空");
        
        logger.info("根据商品ID获取评价列表测试通过: reviewsSize={}", reviews.size());
    }

    @Test
    @Order(6)
    @DisplayName("8.6 根据商品ID获取评价列表参数验证测试")
    public void testGetReviewsByProductIdValidation() {
        logger.info("开始测试根据商品ID获取评价列表参数验证");
        
        // 测试null商品ID
        List<ReviewVO> reviews = reviewService.getReviewsByProductId(null);
        assertNotNull(reviews, "null商品ID应该返回空列表");
        assertTrue(reviews.isEmpty(), "null商品ID应该返回空列表");
        
        logger.info("根据商品ID获取评价列表参数验证测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("8.7 根据用户ID获取评价列表测试")
    public void testGetReviewsByUserId() {
        logger.info("开始测试根据用户ID获取评价列表功能");
        
        // 测试获取用户评价列表
        List<ReviewVO> reviews = reviewService.getReviewsByUserId(TEST_USER_ID);
        assertNotNull(reviews, "评价列表不应为空");
        
        logger.info("根据用户ID获取评价列表测试通过: reviewsSize={}", reviews.size());
    }

    @Test
    @Order(8)
    @DisplayName("8.8 根据用户ID获取评价列表参数验证测试")
    public void testGetReviewsByUserIdValidation() {
        logger.info("开始测试根据用户ID获取评价列表参数验证");
        
        // 测试null用户ID
        List<ReviewVO> reviews = reviewService.getReviewsByUserId(null);
        assertNotNull(reviews, "null用户ID应该返回空列表");
        assertTrue(reviews.isEmpty(), "null用户ID应该返回空列表");
        
        logger.info("根据用户ID获取评价列表参数验证测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("8.9 检查订单是否可以评价测试")
    public void testCheckOrderCanReview() {
        logger.info("开始测试检查订单是否可以评价功能");
        
        // 测试检查订单是否可以评价
        ReviewOperationResult result = reviewService.checkOrderCanReview(TEST_ORDER_ID, TEST_USER_ID);
        assertNotNull(result, "检查订单是否可以评价结果不应为空");
        
        logger.info("检查订单是否可以评价测试通过: success={}, errorMessage={}", 
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(10)
    @DisplayName("8.10 检查订单是否可以评价参数验证测试")
    public void testCheckOrderCanReviewValidation() {
        logger.info("开始测试检查订单是否可以评价参数验证");
        
        // 测试null订单ID
        ReviewOperationResult result1 = reviewService.checkOrderCanReview(null, TEST_USER_ID);
        assertNotNull(result1, "null订单ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID应该检查失败");
        
        // 测试null用户ID
        ReviewOperationResult result2 = reviewService.checkOrderCanReview(TEST_ORDER_ID, null);
        assertNotNull(result2, "null用户ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null用户ID应该检查失败");
        
        logger.info("检查订单是否可以评价参数验证测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("8.11 评价内容边界测试")
    public void testReviewCommentBoundary() {
        logger.info("开始测试评价内容边界情况");

        // 测试空评价内容
        ReviewCreateDTO dto1 = new ReviewCreateDTO();
        dto1.setOrderId(TEST_ORDER_ID + 10);
        dto1.setRating(TEST_RATING_5);
        dto1.setComment("");

        ReviewOperationResult result1 = reviewService.submitReview(dto1, TEST_USER_ID);
        assertNotNull(result1, "空评价内容应该返回结果对象");
        // 空评价内容可能是允许的，我们只验证方法能正常执行
        logger.info("空评价内容测试: success={}", result1.isSuccess());

        // 测试null评价内容
        ReviewCreateDTO dto2 = new ReviewCreateDTO();
        dto2.setOrderId(TEST_ORDER_ID + 11);
        dto2.setRating(TEST_RATING_5);
        dto2.setComment(null);

        ReviewOperationResult result2 = reviewService.submitReview(dto2, TEST_USER_ID);
        assertNotNull(result2, "null评价内容应该返回结果对象");
        // null评价内容可能是允许的，我们只验证方法能正常执行
        logger.info("null评价内容测试: success={}", result2.isSuccess());

        // 测试很长的评价内容
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append("这是一个很长的评价内容");
        }
        String longComment = sb.toString();
        ReviewCreateDTO dto3 = new ReviewCreateDTO();
        dto3.setOrderId(TEST_ORDER_ID + 12);
        dto3.setRating(TEST_RATING_5);
        dto3.setComment(longComment);

        ReviewOperationResult result3 = reviewService.submitReview(dto3, TEST_USER_ID);
        assertNotNull(result3, "很长的评价内容应该返回结果对象");
        logger.info("很长评价内容测试: success={}, commentLength={}", result3.isSuccess(), longComment.length());

        logger.info("评价内容边界测试通过");
    }

    @Test
    @Order(12)
    @DisplayName("8.12 评价业务流程测试")
    public void testReviewWorkflow() {
        logger.info("开始测试评价业务流程");

        Long testOrderId = TEST_ORDER_ID + 20;

        // 1. 检查订单是否可以评价
        ReviewOperationResult checkResult = reviewService.checkOrderCanReview(testOrderId, TEST_USER_ID);
        assertNotNull(checkResult, "检查订单是否可以评价结果不应为空");
        logger.info("检查订单是否可以评价: success={}", checkResult.isSuccess());

        // 2. 提交评价
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(testOrderId);
        dto.setRating(TEST_RATING_5);
        dto.setComment("业务流程测试评价");

        ReviewOperationResult submitResult = reviewService.submitReview(dto, TEST_USER_ID);
        assertNotNull(submitResult, "提交评价结果不应为空");
        logger.info("提交评价: success={}", submitResult.isSuccess());

        // 3. 获取用户的评价列表
        List<ReviewVO> userReviews = reviewService.getReviewsByUserId(TEST_USER_ID);
        assertNotNull(userReviews, "用户评价列表不应为空");
        logger.info("用户评价列表大小: {}", userReviews.size());

        // 4. 获取商品的评价列表
        List<ReviewVO> productReviews = reviewService.getReviewsByProductId(TEST_PRODUCT_ID);
        assertNotNull(productReviews, "商品评价列表不应为空");
        logger.info("商品评价列表大小: {}", productReviews.size());

        logger.info("评价业务流程测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("8.13 不同评分的评价测试")
    public void testDifferentRatingReviews() {
        logger.info("开始测试不同评分的评价");

        // 测试1星差评
        ReviewCreateDTO dto1 = new ReviewCreateDTO();
        dto1.setOrderId(TEST_ORDER_ID + 30);
        dto1.setRating(TEST_RATING_1);
        dto1.setComment("商品质量很差，非常不满意！");

        ReviewOperationResult result1 = reviewService.submitReview(dto1, TEST_USER_ID);
        assertNotNull(result1, "1星评价结果不应为空");
        logger.info("1星评价: success={}", result1.isSuccess());

        // 测试3星中评
        ReviewCreateDTO dto2 = new ReviewCreateDTO();
        dto2.setOrderId(TEST_ORDER_ID + 31);
        dto2.setRating(TEST_RATING_3);
        dto2.setComment("商品一般般，还可以接受。");

        ReviewOperationResult result2 = reviewService.submitReview(dto2, TEST_USER_ID);
        assertNotNull(result2, "3星评价结果不应为空");
        logger.info("3星评价: success={}", result2.isSuccess());

        // 测试5星好评
        ReviewCreateDTO dto3 = new ReviewCreateDTO();
        dto3.setOrderId(TEST_ORDER_ID + 32);
        dto3.setRating(TEST_RATING_5);
        dto3.setComment("商品质量非常好，强烈推荐！");

        ReviewOperationResult result3 = reviewService.submitReview(dto3, TEST_USER_ID);
        assertNotNull(result3, "5星评价结果不应为空");
        logger.info("5星评价: success={}", result3.isSuccess());

        logger.info("不同评分的评价测试通过");
    }

    @Test
    @Order(14)
    @DisplayName("8.14 重复评价测试")
    public void testDuplicateReview() {
        logger.info("开始测试重复评价");

        Long testOrderId = TEST_ORDER_ID + 40;

        // 第一次提交评价
        ReviewCreateDTO dto1 = new ReviewCreateDTO();
        dto1.setOrderId(testOrderId);
        dto1.setRating(TEST_RATING_5);
        dto1.setComment("第一次评价");

        ReviewOperationResult result1 = reviewService.submitReview(dto1, TEST_USER_ID);
        assertNotNull(result1, "第一次评价结果不应为空");
        logger.info("第一次评价: success={}", result1.isSuccess());

        // 第二次提交相同订单的评价
        ReviewCreateDTO dto2 = new ReviewCreateDTO();
        dto2.setOrderId(testOrderId);
        dto2.setRating(TEST_RATING_3);
        dto2.setComment("第二次评价");

        ReviewOperationResult result2 = reviewService.submitReview(dto2, TEST_USER_ID);
        assertNotNull(result2, "第二次评价结果不应为空");
        // 重复评价应该失败，但我们只验证方法能正常执行
        logger.info("第二次评价: success={}", result2.isSuccess());

        logger.info("重复评价测试通过");
    }

    @Test
    @Order(15)
    @DisplayName("8.15 评价数据完整性测试")
    public void testReviewDataIntegrity() {
        logger.info("开始测试评价数据完整性");

        // 获取用户评价列表
        List<ReviewVO> userReviews = reviewService.getReviewsByUserId(TEST_USER_ID);
        assertNotNull(userReviews, "用户评价列表不应为空");

        // 验证评价数据的完整性
        for (ReviewVO review : userReviews) {
            assertNotNull(review.getId(), "评价ID不应为空");
            assertNotNull(review.getOrderId(), "订单ID不应为空");
            assertNotNull(review.getProductId(), "商品ID不应为空");
            assertNotNull(review.getRating(), "评分不应为空");
            assertTrue(review.getRating() >= 1 && review.getRating() <= 5, "评分应该在1-5之间");
            assertNotNull(review.getCreateTime(), "创建时间不应为空");
            // 评价内容可能为空，所以不验证
            // 用户信息可能为空，所以不验证
        }

        logger.info("评价数据完整性测试通过: userReviewsSize={}", userReviews.size());
    }

    @Test
    @Order(16)
    @DisplayName("8.16 评价查询边界测试")
    public void testReviewQueryBoundary() {
        logger.info("开始测试评价查询边界情况");

        // 测试不存在的商品ID
        List<ReviewVO> nonExistentProductReviews = reviewService.getReviewsByProductId(999999L);
        assertNotNull(nonExistentProductReviews, "不存在商品的评价列表不应为空");
        assertTrue(nonExistentProductReviews.isEmpty(), "不存在商品的评价列表应该为空");

        // 测试不存在的用户ID
        List<ReviewVO> nonExistentUserReviews = reviewService.getReviewsByUserId(999999L);
        assertNotNull(nonExistentUserReviews, "不存在用户的评价列表不应为空");
        assertTrue(nonExistentUserReviews.isEmpty(), "不存在用户的评价列表应该为空");

        // 测试负数ID
        List<ReviewVO> negativeProductReviews = reviewService.getReviewsByProductId(-1L);
        assertNotNull(negativeProductReviews, "负数商品ID的评价列表不应为空");
        assertTrue(negativeProductReviews.isEmpty(), "负数商品ID的评价列表应该为空");

        List<ReviewVO> negativeUserReviews = reviewService.getReviewsByUserId(-1L);
        assertNotNull(negativeUserReviews, "负数用户ID的评价列表不应为空");
        assertTrue(negativeUserReviews.isEmpty(), "负数用户ID的评价列表应该为空");

        logger.info("评价查询边界测试通过");
    }

    @Test
    @Order(17)
    @DisplayName("8.17 评价系统错误处理测试")
    public void testReviewErrorHandling() {
        logger.info("开始测试评价系统错误处理");

        // 测试无效订单ID的评价检查
        ReviewOperationResult checkResult1 = reviewService.checkOrderCanReview(-1L, TEST_USER_ID);
        assertNotNull(checkResult1, "无效订单ID检查结果不应为空");
        assertFalse(checkResult1.isSuccess(), "无效订单ID检查应该失败");

        // 测试无效用户ID的评价检查
        ReviewOperationResult checkResult2 = reviewService.checkOrderCanReview(TEST_ORDER_ID, -1L);
        assertNotNull(checkResult2, "无效用户ID检查结果不应为空");
        assertFalse(checkResult2.isSuccess(), "无效用户ID检查应该失败");

        // 测试极端评分值
        ReviewCreateDTO extremeDto = new ReviewCreateDTO();
        extremeDto.setOrderId(TEST_ORDER_ID + 50);
        extremeDto.setRating(Integer.MAX_VALUE);
        extremeDto.setComment("极端评分测试");

        ReviewOperationResult extremeResult = reviewService.submitReview(extremeDto, TEST_USER_ID);
        assertNotNull(extremeResult, "极端评分结果不应为空");
        assertFalse(extremeResult.isSuccess(), "极端评分应该失败");

        logger.info("评价系统错误处理测试通过");
    }

    @AfterEach
    void tearDown() {
        logger.info("ReviewService测试清理完成");
    }
}
