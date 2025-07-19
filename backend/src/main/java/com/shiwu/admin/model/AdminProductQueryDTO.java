package com.shiwu.admin.model;

/**
 * 管理员商品查询数据传输对象
 */
public class AdminProductQueryDTO {
    private String keyword;          // 搜索关键词
    private Integer status;          // 商品状态
    private Long sellerId;           // 卖家ID
    private Integer categoryId;      // 分类ID
    private int pageNum = 1;         // 页码
    private int pageSize = 20;       // 每页大小
    private String sortBy = "create_time";        // 排序字段
    private String sortDirection = "DESC";        // 排序方向

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "AdminProductQueryDTO{" +
                "keyword='" + keyword + '\'' +
                ", status=" + status +
                ", sellerId=" + sellerId +
                ", categoryId=" + categoryId +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
