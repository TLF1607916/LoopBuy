package com.shiwu.message.dto;

/**
 * 会话查询请求DTO
 * 
 * 用于查询用户的会话列表
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class ConversationQueryDTO {
    
    /**
     * 页码（从1开始）
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 会话状态过滤：ACTIVE-活跃，ARCHIVED-已归档
     */
    private String status;
    
    /**
     * 是否只显示有未读消息的会话
     */
    private Boolean onlyUnread;
    
    // ==================== 构造函数 ====================
    
    public ConversationQueryDTO() {
        this.page = 1;
        this.pageSize = 20;
        this.status = "ACTIVE";
        this.onlyUnread = false;
    }
    
    public ConversationQueryDTO(Integer page, Integer pageSize) {
        this.page = page != null && page > 0 ? page : 1;
        this.pageSize = pageSize != null && pageSize > 0 ? pageSize : 20;
        this.status = "ACTIVE";
        this.onlyUnread = false;
    }
    
    // ==================== Getter/Setter ====================
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getOnlyUnread() {
        return onlyUnread;
    }
    
    public void setOnlyUnread(Boolean onlyUnread) {
        this.onlyUnread = onlyUnread;
    }
    
    /**
     * 获取查询偏移量
     */
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
    
    @Override
    public String toString() {
        return "ConversationQueryDTO{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", status='" + status + '\'' +
                ", onlyUnread=" + onlyUnread +
                '}';
    }
}
