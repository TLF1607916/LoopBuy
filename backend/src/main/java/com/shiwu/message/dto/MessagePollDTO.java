package com.shiwu.message.dto;

/**
 * 消息轮询DTO
 * 
 * 用于客户端轮询获取新消息的请求参数
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
public class MessagePollDTO {
    
    /**
     * 上次获取消息的时间戳（毫秒）
     */
    private Long lastMessageTime;
    
    /**
     * 是否只获取未读消息
     */
    private Boolean unreadOnly;
    
    /**
     * 限制返回的消息数量
     */
    private Integer limit;
    
    public MessagePollDTO() {
        this.unreadOnly = false;
        this.limit = 50;
    }
    
    public Long getLastMessageTime() {
        return lastMessageTime;
    }
    
    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    
    public Boolean getUnreadOnly() {
        return unreadOnly;
    }
    
    public void setUnreadOnly(Boolean unreadOnly) {
        this.unreadOnly = unreadOnly;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    @Override
    public String toString() {
        return "MessagePollDTO{" +
                "lastMessageTime=" + lastMessageTime +
                ", unreadOnly=" + unreadOnly +
                ", limit=" + limit +
                '}';
    }
}
