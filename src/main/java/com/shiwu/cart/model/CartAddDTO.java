package com.shiwu.cart.model;

/**
 * 添加商品到购物车的数据传输对象
 */
public class CartAddDTO {
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品数量（本项目固定为1）
     */
    private Integer quantity;
    
    // 构造函数
    public CartAddDTO() {}
    
    public CartAddDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getter和Setter方法
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "CartAddDTO{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
