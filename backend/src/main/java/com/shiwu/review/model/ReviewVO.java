package com.shiwu.review.model;

import com.shiwu.user.model.UserVO;
import java.time.LocalDateTime;

/**
 * 评价视图对象
 * 用于返回给前端的评价信息
 */
public class ReviewVO {
    
    /**
     * 评价ID
     */
    private Long id;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 关联商品ID
     */
    private Long productId;
    
    /**
     * 评价用户信息
     */
    private UserVO user;
    
    /**
     * 评分（1-5星）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    // 构造函数
    public ReviewVO() {}
    
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
    
    public UserVO getUser() {
        return user;
    }
    
    public void setUser(UserVO user) {
        this.user = user;
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
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return "ReviewVO{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", user=" + user +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
