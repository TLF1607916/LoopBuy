package com.shiwu.message.vo;

import java.util.List;

/**
 * 消息轮询响应VO
 * 
 * 用于返回轮询获取的新消息和相关信息
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
public class MessagePollVO {
    
    /**
     * 新消息列表
     */
    private List<MessageVO> newMessages;
    
    /**
     * 当前时间戳（毫秒）
     */
    private Long currentTime;
    
    /**
     * 总未读消息数量
     */
    private Integer totalUnreadCount;
    
    /**
     * 是否有新消息
     */
    private Boolean hasNewMessages;
    
    public MessagePollVO() {
        this.currentTime = System.currentTimeMillis();
        this.hasNewMessages = false;
        this.totalUnreadCount = 0;
    }
    
    public List<MessageVO> getNewMessages() {
        return newMessages;
    }
    
    public void setNewMessages(List<MessageVO> newMessages) {
        this.newMessages = newMessages;
        this.hasNewMessages = newMessages != null && !newMessages.isEmpty();
    }
    
    public Long getCurrentTime() {
        return currentTime;
    }
    
    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }
    
    public Integer getTotalUnreadCount() {
        return totalUnreadCount;
    }
    
    public void setTotalUnreadCount(Integer totalUnreadCount) {
        this.totalUnreadCount = totalUnreadCount;
    }
    
    public Boolean getHasNewMessages() {
        return hasNewMessages;
    }
    
    public void setHasNewMessages(Boolean hasNewMessages) {
        this.hasNewMessages = hasNewMessages;
    }
    
    @Override
    public String toString() {
        return "MessagePollVO{" +
                "newMessages=" + (newMessages != null ? newMessages.size() : 0) + " messages" +
                ", currentTime=" + currentTime +
                ", totalUnreadCount=" + totalUnreadCount +
                ", hasNewMessages=" + hasNewMessages +
                '}';
    }
}
