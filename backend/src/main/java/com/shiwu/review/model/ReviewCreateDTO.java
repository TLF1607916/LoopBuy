package com.shiwu.review.model;

/**
 * 创建评价请求DTO
 * 用于接收前端提交的评价数据
 */
public class ReviewCreateDTO {
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 评分（1-5星）
     */
    private Integer rating;
    
    /**
     * 评价内容（可选）
     */
    private String comment;
    
    // 构造函数
    public ReviewCreateDTO() {}
    
    public ReviewCreateDTO(Long orderId, Integer rating, String comment) {
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getter和Setter方法
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public String toString() {
        return "ReviewCreateDTO{" +
                "orderId=" + orderId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
