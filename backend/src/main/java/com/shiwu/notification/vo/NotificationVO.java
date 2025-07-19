package com.shiwu.notification.vo;

import java.time.LocalDateTime;

/**
 * 通知视图对象
 * 
 * 用于Task4_2_1_2: 商品审核通过粉丝通知功能
 * 返回给前端的通知信息
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class NotificationVO {
    
    private Long id;
    private String title;
    private String content;
    private String notificationType;
    private String sourceType;
    private Long sourceId;
    private Long relatedUserId;
    private String relatedUserName;
    private String actionUrl;
    private Boolean isRead;
    private LocalDateTime readTime;
    private Integer priority;
    private LocalDateTime createTime;
    
    // 扩展字段
    private String priorityText; // 优先级文本描述
    private String typeText; // 类型文本描述
    private String timeAgo; // 相对时间描述
    
    public NotificationVO() {
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
        // 自动设置类型文本描述
        this.typeText = getTypeTextByType(notificationType);
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
        // 自动设置优先级文本描述
        this.priorityText = getPriorityTextByLevel(priority);
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        // 自动设置相对时间描述
        this.timeAgo = getTimeAgoText(createTime);
    }
    
    public String getPriorityText() {
        return priorityText;
    }
    
    public void setPriorityText(String priorityText) {
        this.priorityText = priorityText;
    }
    
    public String getTypeText() {
        return typeText;
    }
    
    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }
    
    public String getTimeAgo() {
        return timeAgo;
    }
    
    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
    
    // 便利方法
    
    /**
     * 根据优先级数值获取文本描述
     */
    private String getPriorityTextByLevel(Integer priority) {
        if (priority == null) {
            return "普通";
        }
        
        switch (priority) {
            case 1:
                return "普通";
            case 2:
                return "重要";
            case 3:
                return "紧急";
            default:
                return "普通";
        }
    }
    
    /**
     * 根据通知类型获取文本描述
     */
    private String getTypeTextByType(String notificationType) {
        if (notificationType == null) {
            return "未知";
        }
        
        switch (notificationType) {
            case "PRODUCT_APPROVED":
                return "商品上架";
            case "ORDER_STATUS":
                return "订单状态";
            case "MESSAGE_RECEIVED":
                return "新消息";
            case "SYSTEM_NOTICE":
                return "系统公告";
            default:
                return "其他";
        }
    }
    
    /**
     * 获取相对时间描述
     */
    private String getTimeAgoText(LocalDateTime createTime) {
        if (createTime == null) {
            return "";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createTime, now).toMinutes();
        
        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) { // 24小时
            long hours = minutes / 60;
            return hours + "小时前";
        } else if (minutes < 10080) { // 7天
            long days = minutes / 1440;
            return days + "天前";
        } else {
            // 超过7天显示具体日期
            return createTime.toLocalDate().toString();
        }
    }
    
    /**
     * 检查是否为重要通知
     */
    public boolean isImportant() {
        return priority != null && priority >= 2;
    }
    
    /**
     * 检查是否为紧急通知
     */
    public boolean isUrgent() {
        return priority != null && priority >= 3;
    }
    
    @Override
    public String toString() {
        return "NotificationVO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", isRead=" + isRead +
                ", priority=" + priority +
                ", createTime=" + createTime +
                '}';
    }
}
