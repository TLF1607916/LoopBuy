package com.shiwu.review.demo;

import com.shiwu.review.model.ReviewCreateDTO;
import com.shiwu.review.model.ReviewOperationResult;
import com.shiwu.review.model.ReviewVO;
import com.shiwu.review.service.ReviewService;
import com.shiwu.review.service.impl.ReviewServiceImpl;

import java.util.List;

/**
 * 评价功能API演示程序
 * 
 * 演示评价功能的完整使用流程：
 * 1. 检查订单是否可以评价
 * 2. 提交评价
 * 3. 查询商品评价列表
 * 4. 查询用户评价列表
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class ReviewApiDemo {

    private final ReviewService reviewService;

    public ReviewApiDemo() {
        this.reviewService = new ReviewServiceImpl();
    }

    /**
     * 演示检查订单是否可以评价
     */
    public void demonstrateCheckOrderCanReview() {
        System.out.println("\n=== 演示检查订单是否可以评价 ===");
        
        Long orderId = 1L;
        Long userId = 1L;
        
        try {
            ReviewOperationResult result = reviewService.checkOrderCanReview(orderId, userId);
            
            if (result.isSuccess()) {
                System.out.println("✅ 订单可以评价");
                System.out.println("   订单ID: " + orderId);
                System.out.println("   用户ID: " + userId);
            } else {
                System.out.println("❌ 订单不能评价");
                System.out.println("   错误码: " + result.getErrorCode());
                System.out.println("   错误信息: " + result.getErrorMessage());
            }
        } catch (Exception e) {
            System.out.println("❌ 检查订单是否可评价时发生异常: " + e.getMessage());
        }
    }

    /**
     * 演示提交评价
     */
    public void demonstrateSubmitReview() {
        System.out.println("\n=== 演示提交评价 ===");
        
        // 创建评价请求
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(1L);
        dto.setRating(5);
        dto.setComment("商品质量很好，卖家服务态度也不错！推荐购买。");
        
        Long userId = 1L; // 买家用户ID
        
        try {
            ReviewOperationResult result = reviewService.submitReview(dto, userId);
            
            if (result.isSuccess()) {
                System.out.println("✅ 评价提交成功");
                System.out.println("   评价ID: " + result.getData());
                System.out.println("   订单ID: " + dto.getOrderId());
                System.out.println("   评分: " + dto.getRating() + "星");
                System.out.println("   评价内容: " + dto.getComment());
            } else {
                System.out.println("❌ 评价提交失败");
                System.out.println("   错误码: " + result.getErrorCode());
                System.out.println("   错误信息: " + result.getErrorMessage());
            }
        } catch (Exception e) {
            System.out.println("❌ 提交评价时发生异常: " + e.getMessage());
        }
    }

    /**
     * 演示获取商品评价列表
     */
    public void demonstrateGetProductReviews() {
        System.out.println("\n=== 演示获取商品评价列表 ===");
        
        Long productId = 1L;
        
        try {
            List<ReviewVO> reviews = reviewService.getReviewsByProductId(productId);
            
            System.out.println("📋 商品评价列表 (商品ID: " + productId + ")");
            System.out.println("   评价总数: " + reviews.size());
            
            if (!reviews.isEmpty()) {
                System.out.println("   评价详情:");
                for (int i = 0; i < reviews.size() && i < 5; i++) { // 最多显示5条
                    ReviewVO review = reviews.get(i);
                    System.out.println("   " + (i + 1) + ". 评价ID: " + review.getId());
                    System.out.println("      评分: " + review.getRating() + "星");
                    System.out.println("      评价者: " + (review.getUser() != null ? review.getUser().getNickname() : "匿名"));
                    System.out.println("      评价内容: " + (review.getComment() != null ? review.getComment() : "无评价内容"));
                    System.out.println("      评价时间: " + review.getCreateTime());
                    System.out.println();
                }
                
                if (reviews.size() > 5) {
                    System.out.println("   ... 还有 " + (reviews.size() - 5) + " 条评价");
                }
            } else {
                System.out.println("   暂无评价");
            }
        } catch (Exception e) {
            System.out.println("❌ 获取商品评价列表时发生异常: " + e.getMessage());
        }
    }

    /**
     * 演示获取用户评价列表
     */
    public void demonstrateGetUserReviews() {
        System.out.println("\n=== 演示获取用户评价列表 ===");
        
        Long userId = 1L;
        
        try {
            List<ReviewVO> reviews = reviewService.getReviewsByUserId(userId);
            
            System.out.println("📋 用户评价列表 (用户ID: " + userId + ")");
            System.out.println("   评价总数: " + reviews.size());
            
            if (!reviews.isEmpty()) {
                System.out.println("   评价详情:");
                for (int i = 0; i < reviews.size() && i < 3; i++) { // 最多显示3条
                    ReviewVO review = reviews.get(i);
                    System.out.println("   " + (i + 1) + ". 评价ID: " + review.getId());
                    System.out.println("      商品ID: " + review.getProductId());
                    System.out.println("      订单ID: " + review.getOrderId());
                    System.out.println("      评分: " + review.getRating() + "星");
                    System.out.println("      评价内容: " + (review.getComment() != null ? review.getComment() : "无评价内容"));
                    System.out.println("      评价时间: " + review.getCreateTime());
                    System.out.println();
                }
                
                if (reviews.size() > 3) {
                    System.out.println("   ... 还有 " + (reviews.size() - 3) + " 条评价");
                }
            } else {
                System.out.println("   暂无评价");
            }
        } catch (Exception e) {
            System.out.println("❌ 获取用户评价列表时发生异常: " + e.getMessage());
        }
    }

    /**
     * 演示错误场景
     */
    public void demonstrateErrorScenarios() {
        System.out.println("\n=== 演示错误场景 ===");
        
        // 1. 参数为空
        System.out.println("1. 测试参数为空的情况:");
        ReviewOperationResult result1 = reviewService.submitReview(null, 1L);
        System.out.println("   结果: " + result1.getErrorMessage());
        
        // 2. 评分无效
        System.out.println("\n2. 测试评分无效的情况:");
        ReviewCreateDTO dto2 = new ReviewCreateDTO();
        dto2.setOrderId(1L);
        dto2.setRating(0); // 无效评分
        ReviewOperationResult result2 = reviewService.submitReview(dto2, 1L);
        System.out.println("   结果: " + result2.getErrorMessage());
        
        // 3. 评价内容过长
        System.out.println("\n3. 测试评价内容过长的情况:");
        ReviewCreateDTO dto3 = new ReviewCreateDTO();
        dto3.setOrderId(1L);
        dto3.setRating(5);
        StringBuilder longComment = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            longComment.append("a");
        }
        dto3.setComment(longComment.toString());
        ReviewOperationResult result3 = reviewService.submitReview(dto3, 1L);
        System.out.println("   结果: " + result3.getErrorMessage());
    }

    /**
     * 运行完整演示
     */
    public void runFullDemo() {
        System.out.println("🌟 评价功能完整演示开始");
        System.out.println("=====================================");

        demonstrateCheckOrderCanReview();
        demonstrateSubmitReview();
        demonstrateGetProductReviews();
        demonstrateGetUserReviews();
        demonstrateErrorScenarios();

        System.out.println("\n=====================================");
        System.out.println("🎉 评价功能演示完成");
        System.out.println("\n💡 提示:");
        System.out.println("   - 如果看到数据库连接错误，请检查数据库配置");
        System.out.println("   - 如果看到订单不存在错误，请先创建测试订单数据");
        System.out.println("   - 评价功能需要订单状态为COMPLETED(3)才能评价");
    }

    /**
     * 主方法，用于直接运行演示
     */
    public static void main(String[] args) {
        ReviewApiDemo demo = new ReviewApiDemo();
        demo.runFullDemo();
    }
}
