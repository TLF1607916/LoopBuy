package com.shiwu.notification.model;

import java.time.LocalDateTime;

/**
 * 通知实体类
 * 
 * 用于Task4_2_1_2: 商品审核通过粉丝通知功能
 * 存储系统生成的各种通知信息
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class Notification {
    
    // 通知类型常量
    public static final String TYPE_PRODUCT_APPROVED = "PRODUCT_APPROVED";
    public static final String TYPE_ORDER_STATUS = "ORDER_STATUS";
    public static final String TYPE_MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
    public static final String TYPE_SYSTEM_NOTICE = "SYSTEM_NOTICE";
    
    // 来源类型常量
    public static final String SOURCE_PRODUCT = "PRODUCT";
    public static final String SOURCE_ORDER = "ORDER";
    public static final String SOURCE_MESSAGE = "MESSAGE";
    public static final String SOURCE_SYSTEM = "SYSTEM";
    
    // 优先级常量
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_IMPORTANT = 2;
    public static final int PRIORITY_URGENT = 3;
    
    // 主键字段
    private Long id;
    
    // 接收者信息
    private Long recipientId;
    
    // 通知内容
    private String title;
    private String content;
    
    // 通知类型和来源
    private String notificationType;
    private String sourceType;
    private Long sourceId;
    
    // 关联信息
    private Long relatedUserId;
    private String relatedUserName;
    
    // 跳转链接
    private String actionUrl;
    
    // 状态字段
    private Boolean isRead;
    private LocalDateTime readTime;
    
    // 优先级
    private Integer priority;
    
    // 过期时间
    private LocalDateTime expireTime;
    
    // 系统字段
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 构造函数
    public Notification() {
        this.isRead = false;
        this.priority = PRIORITY_NORMAL;
        this.isDeleted = false;
    }
    
    public Notification(Long recipientId, String title, String content, String notificationType) {
        this();
        this.recipientId = recipientId;
        this.title = title;
        this.content = content;
        this.notificationType = notificationType;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public Long getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
    
    public Long getRelatedUserId() {
        return relatedUserId;
    }
    
    public void setRelatedUserId(Long relatedUserId) {
        this.relatedUserId = relatedUserId;
    }
    
    public String getRelatedUserName() {
        return relatedUserName;
    }
    
    public void setRelatedUserName(String relatedUserName) {
        this.relatedUserName = relatedUserName;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public LocalDateTime getReadTime() {
        return readTime;
    }
    
    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
    
    // 便利方法
    
    /**
     * 检查通知是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }
    
    /**
     * 检查通知是否有效（未删除且未过期）
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(isDeleted) && !isExpired();
    }
    
    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.isRead = true;
        this.readTime = LocalDateTime.now();
    }
    
    /**
     * 设置过期时间（从现在开始的小时数）
     */
    public void setExpireAfterHours(int hours) {
        if (hours > 0) {
            this.expireTime = LocalDateTime.now().plusHours(hours);
        }
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", recipientId=" + recipientId +
                ", title='" + title + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", sourceId=" + sourceId +
                ", isRead=" + isRead +
                ", priority=" + priority +
                ", createTime=" + createTime +
                '}';
    }
}
