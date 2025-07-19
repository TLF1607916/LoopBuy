package com.shiwu.admin.model;

/**
 * 管理员用户查询数据传输对象
 */
public class AdminUserQueryDTO {
    private String keyword;          // 搜索关键词（用户名、昵称、邮箱）
    private Integer status;          // 用户状态：0-正常，1-已封禁，2-已禁言
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
        return "AdminUserQueryDTO{" +
                "keyword='" + keyword + '\'' +
                ", status=" + status +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
