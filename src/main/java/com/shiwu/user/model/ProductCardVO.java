package com.shiwu.user.model;

import java.math.BigDecimal;

/**
 * 商品卡片视图对象
 * 用于在商品列表中显示商品的基本信息
 */
public class ProductCardVO {
    private Long productId;
    private String title;
    private String mainImageUrl;
    private BigDecimal price;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
