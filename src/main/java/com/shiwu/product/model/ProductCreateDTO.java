package com.shiwu.product.model;

import java.math.BigDecimal;

/**
 * 商品创建数据传输对象
 */
public class ProductCreateDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private Integer categoryId;
    private String action; // 操作类型: SUBMIT_REVIEW-提交审核, SAVE_DRAFT-保存草稿
    
    // 操作常量
    public static final String ACTION_SUBMIT_REVIEW = "SUBMIT_REVIEW";
    public static final String ACTION_SAVE_DRAFT = "SAVE_DRAFT";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
} 