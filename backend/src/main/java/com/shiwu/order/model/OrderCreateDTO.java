package com.shiwu.order.model;

import java.util.List;

/**
 * 创建订单数据传输对象
 * 用于接收前端创建订单的请求参数
 */
public class OrderCreateDTO {
    
    /**
     * 商品ID列表（从购物车中选择的商品）
     */
    private List<Long> productIds;
    
    // 构造函数
    public OrderCreateDTO() {}
    
    public OrderCreateDTO(List<Long> productIds) {
        this.productIds = productIds;
    }
    
    // Getter和Setter方法
    public List<Long> getProductIds() {
        return productIds;
    }
    
    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
    
    @Override
    public String toString() {
        return "OrderCreateDTO{" +
                "productIds=" + productIds +
                '}';
    }
}
