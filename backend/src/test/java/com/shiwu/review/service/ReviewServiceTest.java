package com.shiwu.review.service;

import com.shiwu.order.dao.OrderDao;
import com.shiwu.order.model.Order;
import com.shiwu.review.dao.ReviewDao;
import com.shiwu.review.model.*;
import com.shiwu.review.service.impl.ReviewServiceImpl;
import com.shiwu.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 评价服务单元测试
 * 
 * @author Shiwu Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("评价服务测试")
class ReviewServiceTest {

    @Mock
    private ReviewDao reviewDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private UserService userService;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewDao, orderDao, userService);
    }

    @Test
    @DisplayName("提交评价 - 成功场景")
    void testSubmitReview_Success() {
        // Given
        Long userId = 1L;
        Long orderId = 100L;
        Long sellerId = 2L;
        Long productId = 50L;
        
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(orderId);
        dto.setRating(5);
        dto.setComment("很好的商品");

        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(userId);
        order.setSellerId(sellerId);
        order.setProductId(productId);
        order.setStatus(Order.STATUS_COMPLETED);

        when(orderDao.findById(orderId)).thenReturn(order);
        when(reviewDao.isOrderReviewed(orderId)).thenReturn(false);
        when(reviewDao.createReview(any(Review.class))).thenReturn(1L);
        when(userService.updateUserAverageRating(sellerId)).thenReturn(true);

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, userId);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(1L, result.getData());
        
        verify(reviewDao).createReview(any(Review.class));
        verify(userService).updateUserAverageRating(sellerId);
    }

    @Test
    @DisplayName("提交评价 - 参数为空")
    void testSubmitReview_NullParams() {
        // When & Then
        ReviewOperationResult result1 = reviewService.submitReview(null, 1L);
        assertFalse(result1.isSuccess());
        assertEquals(ReviewErrorCode.INVALID_PARAMS, result1.getErrorCode());

        ReviewOperationResult result2 = reviewService.submitReview(new ReviewCreateDTO(), null);
        assertFalse(result2.isSuccess());
        assertEquals(ReviewErrorCode.INVALID_PARAMS, result2.getErrorCode());
    }

    @Test
    @DisplayName("提交评价 - 订单ID为空")
    void testSubmitReview_NullOrderId() {
        // Given
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setRating(5);

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, 1L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ReviewErrorCode.INVALID_ORDER_ID, result.getErrorCode());
    }

    @Test
    @DisplayName("提交评价 - 评分无效")
    void testSubmitReview_InvalidRating() {
        // Given
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(100L);
        dto.setRating(0); // 无效评分

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, 1L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ReviewErrorCode.INVALID_RATING, result.getErrorCode());
    }

    @Test
    @DisplayName("提交评价 - 评价内容过长")
    void testSubmitReview_CommentTooLong() {
        // Given
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(100L);
        dto.setRating(5);
        // 创建超过500字符的字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            sb.append("a");
        }
        dto.setComment(sb.toString());

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, 1L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ReviewErrorCode.COMMENT_TOO_LONG, result.getErrorCode());
    }

    @Test
    @DisplayName("提交评价 - 订单不存在")
    void testSubmitReview_OrderNotFound() {
        // Given
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(100L);
        dto.setRating(5);

        when(orderDao.findById(100L)).thenReturn(null);

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, 1L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ReviewErrorCode.ORDER_NOT_FOUND, result.getErrorCode());
    }

    @Test
    @DisplayName("提交评价 - 不是订单买家")
    void testSubmitReview_NotOrderBuyer() {
        // Given
        Long userId = 1L;
        Long orderId = 100L;
        
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(orderId);
        dto.setRating(5);

        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(2L); // 不同的买家ID
        order.setStatus(Order.STATUS_COMPLETED);

        when(orderDao.findById(orderId)).thenReturn(order);

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, userId);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ReviewErrorCode.NOT_ORDER_BUYER, result.getErrorCode());
    }

    @Test
    @DisplayName("提交评价 - 订单状态不是已完成")
    void testSubmitReview_OrderNotCompleted() {
        // Given
        Long userId = 1L;
        Long orderId = 100L;
        
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(orderId);
        dto.setRating(5);

        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(userId);
        order.setStatus(Order.STATUS_SHIPPED); // 不是已完成状态

        when(orderDao.findById(orderId)).thenReturn(order);

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, userId);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ReviewErrorCode.ORDER_NOT_COMPLETED, result.getErrorCode());
    }

    @Test
    @DisplayName("提交评价 - 订单已评价")
    void testSubmitReview_OrderAlreadyReviewed() {
        // Given
        Long userId = 1L;
        Long orderId = 100L;
        
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(orderId);
        dto.setRating(5);

        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(userId);
        order.setStatus(Order.STATUS_COMPLETED);

        when(orderDao.findById(orderId)).thenReturn(order);
        when(reviewDao.isOrderReviewed(orderId)).thenReturn(true);

        // When
        ReviewOperationResult result = reviewService.submitReview(dto, userId);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ReviewErrorCode.ORDER_ALREADY_REVIEWED, result.getErrorCode());
    }

    @Test
    @DisplayName("获取商品评价列表 - 成功")
    void testGetReviewsByProductId_Success() {
        // Given
        Long productId = 50L;
        ReviewVO review1 = new ReviewVO();
        review1.setId(1L);
        review1.setRating(5);
        
        ReviewVO review2 = new ReviewVO();
        review2.setId(2L);
        review2.setRating(4);
        
        List<ReviewVO> expectedReviews = Arrays.asList(review1, review2);
        when(reviewDao.findReviewsByProductId(productId)).thenReturn(expectedReviews);

        // When
        List<ReviewVO> result = reviewService.getReviewsByProductId(productId);

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedReviews, result);
    }

    @Test
    @DisplayName("获取商品评价列表 - 商品ID为空")
    void testGetReviewsByProductId_NullProductId() {
        // When
        List<ReviewVO> result = reviewService.getReviewsByProductId(null);

        // Then
        assertTrue(result.isEmpty());
        verify(reviewDao, never()).findReviewsByProductId(any());
    }

    @Test
    @DisplayName("检查订单是否可评价 - 可以评价")
    void testCheckOrderCanReview_CanReview() {
        // Given
        Long userId = 1L;
        Long orderId = 100L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(userId);
        order.setStatus(Order.STATUS_COMPLETED);

        when(orderDao.findById(orderId)).thenReturn(order);
        when(reviewDao.isOrderReviewed(orderId)).thenReturn(false);

        // When
        ReviewOperationResult result = reviewService.checkOrderCanReview(orderId, userId);

        // Then
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("检查订单是否可评价 - 参数为空")
    void testCheckOrderCanReview_NullParams() {
        // When & Then
        ReviewOperationResult result1 = reviewService.checkOrderCanReview(null, 1L);
        assertFalse(result1.isSuccess());
        assertEquals(ReviewErrorCode.INVALID_PARAMS, result1.getErrorCode());

        ReviewOperationResult result2 = reviewService.checkOrderCanReview(100L, null);
        assertFalse(result2.isSuccess());
        assertEquals(ReviewErrorCode.INVALID_PARAMS, result2.getErrorCode());
    }
}
