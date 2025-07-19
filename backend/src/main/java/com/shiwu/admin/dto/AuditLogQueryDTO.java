package com.shiwu.admin.dto;

import java.time.LocalDateTime;

/**
 * 审计日志查询参数DTO
 * 支持NFR-SEC-03要求的过滤和搜索功能
 */
public class AuditLogQueryDTO {
    
    private Long adminId;           // 管理员ID
    private String action;          // 操作类型
    private String targetType;      // 目标类型
    private Long targetId;          // 目标ID
    private String ipAddress;       // IP地址
    private Integer result;         // 操作结果：0-失败，1-成功
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime;   // 结束时间
    private String keyword;         // 关键词搜索（在details中搜索）
    
    // 分页参数
    private Integer page = 1;       // 页码，默认第1页
    private Integer pageSize = 20;  // 每页大小，默认20条
    private String sortBy = "create_time"; // 排序字段，默认按创建时间
    private String sortOrder = "DESC";     // 排序方向，默认降序
    
    public Long getAdminId() {
        return adminId;
    }
    
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Integer getResult() {
        return result;
    }
    
    public void setResult(Integer result) {
        this.result = result;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        if (page != null && page > 0) {
            this.page = page;
        }
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        if (pageSize != null && pageSize > 0 && pageSize <= 100) {
            this.pageSize = pageSize;
        }
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        // 只允许特定字段排序
        if (sortBy != null && isValidSortField(sortBy)) {
            this.sortBy = sortBy;
        }
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
            this.sortOrder = sortOrder.toUpperCase();
        }
    }
    
    /**
     * 获取分页偏移量
     * @return 偏移量
     */
    public int getOffset() {
        return (page - 1) * pageSize;
    }
    
    /**
     * 检查是否为有效的排序字段
     * @param field 字段名
     * @return true如果是有效字段
     */
    private boolean isValidSortField(String field) {
        switch (field) {
            case "id":
            case "admin_id":
            case "action":
            case "target_type":
            case "target_id":
            case "result":
            case "create_time":
            case "ip_address":
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return "AuditLogQueryDTO{" +
                "adminId=" + adminId +
                ", action='" + action + '\'' +
                ", targetType='" + targetType + '\'' +
                ", targetId=" + targetId +
                ", ipAddress='" + ipAddress + '\'' +
                ", result=" + result +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", keyword='" + keyword + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}
