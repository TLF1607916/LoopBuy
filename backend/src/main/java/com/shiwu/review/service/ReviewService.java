package com.shiwu.review.service;

import com.shiwu.review.model.ReviewCreateDTO;
import com.shiwu.review.model.ReviewOperationResult;
import com.shiwu.review.model.ReviewVO;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {
    
    /**
     * 提交评价
     * @param reviewCreateDTO 评价创建请求
     * @param userId 当前用户ID（买家）
     * @return 评价操作结果
     */
    ReviewOperationResult submitReview(ReviewCreateDTO reviewCreateDTO, Long userId);
    
    /**
     * 根据商品ID获取评价列表
     * @param productId 商品ID
     * @return 评价列表
     */
    List<ReviewVO> getReviewsByProductId(Long productId);
    
    /**
     * 根据用户ID获取评价列表（用户发表的评价）
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ReviewVO> getReviewsByUserId(Long userId);
    
    /**
     * 检查订单是否可以评价
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 检查结果
     */
    ReviewOperationResult checkOrderCanReview(Long orderId, Long userId);
}
