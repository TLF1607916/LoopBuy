package com.shiwu.message.vo;

import java.time.LocalDateTime;

/**
 * 会话视图对象VO
 * 
 * 用于返回给前端的会话列表数据
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class ConversationVO {
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 参与者1 ID
     */
    private Long participant1Id;

    /**
     * 参与者2 ID
     */
    private Long participant2Id;

    /**
     * 对方用户ID
     */
    private Long otherPartyId;
    
    /**
     * 对方用户名
     */
    private String otherPartyUsername;
    
    /**
     * 对方昵称
     */
    private String otherPartyNickname;
    
    /**
     * 对方头像URL
     */
    private String otherPartyAvatarUrl;
    
    /**
     * 关联的商品ID
     */
    private Long productId;
    
    /**
     * 关联的商品标题
     */
    private String productTitle;
    
    /**
     * 关联的商品图片URL
     */
    private String productImageUrl;
    
    /**
     * 最后一条消息内容
     */
    private String lastMessage;
    
    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 未读消息数量
     */
    private Integer unreadCount;
    
    /**
     * 会话状态
     */
    private String status;
    
    /**
     * 会话创建时间
     */
    private LocalDateTime createTime;
    
    // ==================== 构造函数 ====================
    
    public ConversationVO() {
    }
    
    public ConversationVO(String conversationId, Long otherPartyId, String otherPartyUsername, 
                          String otherPartyNickname, String lastMessage, LocalDateTime lastMessageTime, 
                          Integer unreadCount) {
        this.conversationId = conversationId;
        this.otherPartyId = otherPartyId;
        this.otherPartyUsername = otherPartyUsername;
        this.otherPartyNickname = otherPartyNickname;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }
    
    // ==================== Getter/Setter ====================
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public Long getOtherPartyId() {
        return otherPartyId;
    }
    
    public void setOtherPartyId(Long otherPartyId) {
        this.otherPartyId = otherPartyId;
    }

    public Long getParticipant1Id() {
        return participant1Id;
    }

    public void setParticipant1Id(Long participant1Id) {
        this.participant1Id = participant1Id;
    }

    public Long getParticipant2Id() {
        return participant2Id;
    }

    public void setParticipant2Id(Long participant2Id) {
        this.participant2Id = participant2Id;
    }

    public String getOtherPartyUsername() {
        return otherPartyUsername;
    }
    
    public void setOtherPartyUsername(String otherPartyUsername) {
        this.otherPartyUsername = otherPartyUsername;
    }
    
    public String getOtherPartyNickname() {
        return otherPartyNickname;
    }
    
    public void setOtherPartyNickname(String otherPartyNickname) {
        this.otherPartyNickname = otherPartyNickname;
    }
    
    public String getOtherPartyAvatarUrl() {
        return otherPartyAvatarUrl;
    }
    
    public void setOtherPartyAvatarUrl(String otherPartyAvatarUrl) {
        this.otherPartyAvatarUrl = otherPartyAvatarUrl;
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
    
    public String getProductImageUrl() {
        return productImageUrl;
    }
    
    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }
    
    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    
    public Integer getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return "ConversationVO{" +
                "conversationId='" + conversationId + '\'' +
                ", otherPartyId=" + otherPartyId +
                ", otherPartyUsername='" + otherPartyUsername + '\'' +
                ", otherPartyNickname='" + otherPartyNickname + '\'' +
                ", productId=" + productId +
                ", productTitle='" + productTitle + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                ", unreadCount=" + unreadCount +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
