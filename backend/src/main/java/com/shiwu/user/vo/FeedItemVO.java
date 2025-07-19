package com.shiwu.user.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 关注动态信息流项视图对象
 * 
 * 用于Task4_2_1_3: 获取关注动态信息流API
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class FeedItemVO {
    
    // 动态类型常量
    public static final String TYPE_PRODUCT_APPROVED = "PRODUCT_APPROVED";
    public static final String TYPE_PRODUCT_PUBLISHED = "PRODUCT_PUBLISHED";
    
    private Long id;
    private String type;
    private String title;
    private String content;
    
    // 卖家信息
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    
    // 商品信息
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal productPrice;
    
    // 操作信息
    private String actionUrl;
    private LocalDateTime createTime;
    
    public FeedItemVO() {
    }
    
    public FeedItemVO(Long id, String type, String title, String content) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
    
    public String getSellerAvatar() {
        return sellerAvatar;
    }
    
    public void setSellerAvatar(String sellerAvatar) {
        this.sellerAvatar = sellerAvatar;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductTitle() {
        return productTitle;
    }
    
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
    
    public String getProductImage() {
        return productImage;
    }
    
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    
    public BigDecimal getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return "FeedItemVO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", productId=" + productId +
                ", productTitle='" + productTitle + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
