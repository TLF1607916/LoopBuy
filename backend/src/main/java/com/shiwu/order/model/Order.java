package com.shiwu.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库中的trade_order表
 */
public class Order {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 买家用户ID
     */
    private Long buyerId;
    
    /**
     * 卖家用户ID
     */
    private Long sellerId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 购买时的商品价格（快照）
     */
    private BigDecimal priceAtPurchase;
    
    /**
     * 商品标题快照
     */
    private String productTitleSnapshot;
    
    /**
     * 商品描述快照
     */
    private String productDescriptionSnapshot;
    
    /**
     * 商品图片URL列表快照（JSON格式）
     */
    private String productImageUrlsSnapshot;
    
    /**
     * 订单状态
     */
    private Integer status;
    
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
    
    // 订单状态常量
    public static final Integer STATUS_AWAITING_PAYMENT = 0;  // 待付款
    public static final Integer STATUS_AWAITING_SHIPPING = 1; // 待发货
    public static final Integer STATUS_SHIPPED = 2;           // 已发货
    public static final Integer STATUS_COMPLETED = 3;         // 已完成
    public static final Integer STATUS_CANCELLED = 4;         // 已取消
    public static final Integer STATUS_RETURN_REQUESTED = 5;  // 申请退货
    public static final Integer STATUS_RETURNED = 6;          // 已退货
    
    // 构造函数
    public Order() {}
    
    public Order(Long buyerId, Long sellerId, Long productId, BigDecimal priceAtPurchase,
                 String productTitleSnapshot, String productDescriptionSnapshot, 
                 String productImageUrlsSnapshot) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.priceAtPurchase = priceAtPurchase;
        this.productTitleSnapshot = productTitleSnapshot;
        this.productDescriptionSnapshot = productDescriptionSnapshot;
        this.productImageUrlsSnapshot = productImageUrlsSnapshot;
        this.status = STATUS_AWAITING_PAYMENT;
        this.deleted = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }
    
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }
    
    public String getProductTitleSnapshot() {
        return productTitleSnapshot;
    }
    
    public void setProductTitleSnapshot(String productTitleSnapshot) {
        this.productTitleSnapshot = productTitleSnapshot;
    }
    
    public String getProductDescriptionSnapshot() {
        return productDescriptionSnapshot;
    }
    
    public void setProductDescriptionSnapshot(String productDescriptionSnapshot) {
        this.productDescriptionSnapshot = productDescriptionSnapshot;
    }
    
    public String getProductImageUrlsSnapshot() {
        return productImageUrlsSnapshot;
    }
    
    public void setProductImageUrlsSnapshot(String productImageUrlsSnapshot) {
        this.productImageUrlsSnapshot = productImageUrlsSnapshot;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
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
        return "Order{" +
                "id=" + id +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                ", productId=" + productId +
                ", priceAtPurchase=" + priceAtPurchase +
                ", productTitleSnapshot='" + productTitleSnapshot + '\'' +
                ", productDescriptionSnapshot='" + productDescriptionSnapshot + '\'' +
                ", productImageUrlsSnapshot='" + productImageUrlsSnapshot + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
