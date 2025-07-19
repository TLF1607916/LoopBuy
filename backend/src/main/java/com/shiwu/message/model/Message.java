package com.shiwu.message.model;

import java.time.LocalDateTime;

/**
 * 消息实体类 (DO - Data Object)
 * 
 * 严格遵循项目规范：
 * 1. 使用包装类型而非基本类型
 * 2. Boolean属性不使用is前缀
 * 3. 不包含业务逻辑
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class Message {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 会话ID (格式: smaller_user_id + "_" + larger_user_id + "_" + product_id)
     * 例如: "1_2_100" 表示用户1和用户2关于商品100的对话
     */
    private String conversationId;
    
    /**
     * 发送者用户ID
     */
    private Long senderId;
    
    /**
     * 接收者用户ID
     */
    private Long receiverId;
    
    /**
     * 关联的商品ID（可选，用于商品咨询）
     */
    private Long productId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：TEXT-文本消息，IMAGE-图片消息，SYSTEM-系统消息
     */
    private String messageType;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Boolean read;
    
    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    private Boolean deleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // ==================== 构造函数 ====================
    
    public Message() {
    }
    
    public Message(String conversationId, Long senderId, Long receiverId, 
                   Long productId, String content, String messageType) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.productId = productId;
        this.content = content;
        this.messageType = messageType;
        this.read = false;
        this.deleted = false;
    }
    
    // ==================== Getter/Setter ====================
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public Boolean getRead() {
        return read;
    }
    
    public void setRead(Boolean read) {
        this.read = read;
    }

    // 兼容性方法
    public Boolean getIsRead() {
        return read;
    }

    public void setIsRead(Boolean isRead) {
        this.read = isRead;
    }

    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId='" + conversationId + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", productId=" + productId +
                ", content='" + content + '\'' +
                ", messageType='" + messageType + '\'' +
                ", read=" + read +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
