package com.shiwu.order.model;

import com.shiwu.user.model.UserVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象
 * 用于返回给前端的订单信息
 */
public class OrderVO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 买家信息
     */
    private UserVO buyer;
    
    /**
     * 卖家信息
     */
    private UserVO seller;
    
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
     * 商品图片URL列表快照
     */
    private List<String> productImageUrlsSnapshot;
    
    /**
     * 订单状态
     */
    private Integer status;
    
    /**
     * 订单状态描述
     */
    private String statusText;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 构造函数
    public OrderVO() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserVO getBuyer() {
        return buyer;
    }
    
    public void setBuyer(UserVO buyer) {
        this.buyer = buyer;
    }
    
    public UserVO getSeller() {
        return seller;
    }
    
    public void setSeller(UserVO seller) {
        this.seller = seller;
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
    
    public List<String> getProductImageUrlsSnapshot() {
        return productImageUrlsSnapshot;
    }
    
    public void setProductImageUrlsSnapshot(List<String> productImageUrlsSnapshot) {
        this.productImageUrlsSnapshot = productImageUrlsSnapshot;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
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
        return "OrderVO{" +
                "id=" + id +
                ", buyer=" + buyer +
                ", seller=" + seller +
                ", productId=" + productId +
                ", priceAtPurchase=" + priceAtPurchase +
                ", productTitleSnapshot='" + productTitleSnapshot + '\'' +
                ", productDescriptionSnapshot='" + productDescriptionSnapshot + '\'' +
                ", productImageUrlsSnapshot=" + productImageUrlsSnapshot +
                ", status=" + status +
                ", statusText='" + statusText + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
