package com.shiwu.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
public class Product {
    private Long id;
    private Long sellerId;
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer status; // 商品状态：0-待审核，1-在售，2-已售出，3-已下架，4-草稿
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

    // 商品状态常量
    public static final Integer STATUS_PENDING_REVIEW = 0; // 待审核
    public static final Integer STATUS_ONSALE = 1;        // 在售
    public static final Integer STATUS_SOLD = 2;          // 已售出
    public static final Integer STATUS_DELISTED = 3;      // 已下架
    public static final Integer STATUS_DRAFT = 4;         // 草稿

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
} 