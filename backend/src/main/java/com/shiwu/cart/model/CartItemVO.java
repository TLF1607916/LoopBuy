package com.shiwu.cart.model;

import com.shiwu.user.model.ProductCardVO;

/**
 * 购物车项视图对象
 * 用于返回给前端的购物车项信息
 */
public class CartItemVO {
    
    /**
     * 购物车项ID
     */
    private Long id;
    
    /**
     * 商品信息
     */
    private ProductCardVO product;
    
    /**
     * 商品数量
     */
    private Integer quantity;
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 卖家昵称
     */
    private String sellerName;
    
    /**
     * 商品是否可用（是否还在售）
     */
    private Boolean available;
    
    // 构造函数
    public CartItemVO() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ProductCardVO getProduct() {
        return product;
    }
    
    public void setProduct(ProductCardVO product) {
        this.product = product;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
    
    public Boolean getAvailable() {
        return available;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return "CartItemVO{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", available=" + available +
                '}';
    }
}
