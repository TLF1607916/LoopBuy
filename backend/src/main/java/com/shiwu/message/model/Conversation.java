package com.shiwu.message.model;

import java.time.LocalDateTime;

/**
 * 会话实体类 (DO - Data Object)
 * 
 * 用于管理用户间的对话会话，每个会话可能关联一个商品
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class Conversation {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 会话唯一标识符
     * 格式: smaller_user_id + "_" + larger_user_id + "_" + product_id
     * 例如: "1_2_100" 表示用户1和用户2关于商品100的对话
     */
    private String conversationId;
    
    /**
     * 参与者1的用户ID（较小的用户ID）
     */
    private Long participant1Id;
    
    /**
     * 参与者2的用户ID（较大的用户ID）
     */
    private Long participant2Id;
    
    /**
     * 关联的商品ID（可选）
     */
    private Long productId;
    
    /**
     * 最后一条消息内容
     */
    private String lastMessage;
    
    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 参与者1的未读消息数量
     */
    private Integer unreadCount1;
    
    /**
     * 参与者2的未读消息数量
     */
    private Integer unreadCount2;
    
    /**
     * 会话状态：ACTIVE-活跃，ARCHIVED-已归档，BLOCKED-已屏蔽
     */
    private String status;
    
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
    
    public Conversation() {
    }
    
    public Conversation(String conversationId, Long participant1Id, Long participant2Id, Long productId) {
        this.conversationId = conversationId;
        this.participant1Id = participant1Id;
        this.participant2Id = participant2Id;
        this.productId = productId;
        this.unreadCount1 = 0;
        this.unreadCount2 = 0;
        this.status = "ACTIVE";
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
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
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
    
    public Integer getUnreadCount1() {
        return unreadCount1;
    }
    
    public void setUnreadCount1(Integer unreadCount1) {
        this.unreadCount1 = unreadCount1;
    }
    
    public Integer getUnreadCount2() {
        return unreadCount2;
    }
    
    public void setUnreadCount2(Integer unreadCount2) {
        this.unreadCount2 = unreadCount2;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
        return "Conversation{" +
                "id=" + id +
                ", conversationId='" + conversationId + '\'' +
                ", participant1Id=" + participant1Id +
                ", participant2Id=" + participant2Id +
                ", productId=" + productId +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                ", unreadCount1=" + unreadCount1 +
                ", unreadCount2=" + unreadCount2 +
                ", status='" + status + '\'' +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
