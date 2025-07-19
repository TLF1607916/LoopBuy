package com.shiwu.cart.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车视图对象
 * 用于返回给前端的完整购物车信息
 */
public class CartVO {
    
    /**
     * 购物车项列表
     */
    private List<CartItemVO> items;
    
    /**
     * 购物车中商品总数
     */
    private Integer totalItems;
    
    /**
     * 购物车总价格（所有可用商品的价格总和）
     */
    private BigDecimal totalPrice;
    
    // 构造函数
    public CartVO() {}
    
    public CartVO(List<CartItemVO> items, Integer totalItems, BigDecimal totalPrice) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
    }
    
    // Getter和Setter方法
    public List<CartItemVO> getItems() {
        return items;
    }
    
    public void setItems(List<CartItemVO> items) {
        this.items = items;
    }
    
    public Integer getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    @Override
    public String toString() {
        return "CartVO{" +
                "items=" + items +
                ", totalItems=" + totalItems +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
