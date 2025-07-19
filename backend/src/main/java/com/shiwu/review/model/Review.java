package com.shiwu.review.model;

import java.time.LocalDateTime;

/**
 * 评价实体类
 * 对应数据库中的review表
 */
public class Review {
    
    /**
     * 评价ID
     */
    private Long id;
    
    /**
     * 关联订单ID（唯一，一个订单只能评价一次）
     */
    private Long orderId;
    
    /**
     * 关联商品ID
     */
    private Long productId;
    
    /**
     * 评价用户ID（买家）
     */
    private Long userId;
    
    /**
     * 评分（1-5星）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 是否删除
     */
    private Boolean deleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 评分常量
    public static final Integer MIN_RATING = 1;  // 最低评分
    public static final Integer MAX_RATING = 5;  // 最高评分
    
    // 构造函数
    public Review() {}
    
    public Review(Long orderId, Long productId, Long userId, Integer rating, String comment) {
        this.orderId = orderId;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.deleted = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", userId=" + userId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
