package com.shiwu.message.vo;

import java.time.LocalDateTime;

/**
 * 消息视图对象VO
 * 
 * 用于返回给前端的消息数据
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class MessageVO {
    
    /**
     * 消息ID
     */
    private Long messageId;
    
    /**
     * 会话ID
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
     * 发送者用户名
     */
    private String senderUsername;
    
    /**
     * 发送者昵称
     */
    private String senderNickname;
    
    /**
     * 发送者头像URL
     */
    private String senderAvatarUrl;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 是否已读
     */
    private Boolean read;
    
    /**
     * 关联的商品ID
     */
    private Long productId;
    
    /**
     * 关联的商品标题
     */
    private String productTitle;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 发送时间（别名，用于兼容）
     */
    private LocalDateTime sendTime;
    
    // ==================== 构造函数 ====================
    
    public MessageVO() {
    }
    
    public MessageVO(Long messageId, String conversationId, Long senderId, 
                     String content, String messageType, Boolean read, LocalDateTime createTime) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
        this.read = read;
        this.createTime = createTime;
    }
    
    // ==================== Getter/Setter ====================
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
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

    public String getSenderUsername() {
        return senderUsername;
    }
    
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
    
    public String getSenderNickname() {
        return senderNickname;
    }
    
    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }
    
    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }
    
    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;
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
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductTitle() {
        return productTitle;
    }
    
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getSendTime() {
        return sendTime != null ? sendTime : createTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    // 兼容性方法
    public Boolean getIsRead() {
        return read;
    }

    public void setIsRead(Boolean isRead) {
        this.read = isRead;
    }

    @Override
    public String toString() {
        return "MessageVO{" +
                "messageId=" + messageId +
                ", conversationId='" + conversationId + '\'' +
                ", senderId=" + senderId +
                ", senderUsername='" + senderUsername + '\'' +
                ", senderNickname='" + senderNickname + '\'' +
                ", content='" + content + '\'' +
                ", messageType='" + messageType + '\'' +
                ", read=" + read +
                ", productId=" + productId +
                ", productTitle='" + productTitle + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
