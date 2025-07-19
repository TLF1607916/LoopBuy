package com.shiwu.message.dto;

/**
 * 发送消息请求DTO
 * 
 * 用于接收前端发送消息的请求参数
 * 严格遵循项目规范：使用包装类型，不包含业务逻辑
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class MessageSendDTO {
    
    /**
     * 接收者用户ID
     */
    private Long receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 关联的商品ID（可选，用于商品咨询）
     */
    private Long productId;
    
    /**
     * 消息类型：TEXT-文本消息，IMAGE-图片消息
     * 默认为TEXT
     */
    private String messageType;
    
    // ==================== 构造函数 ====================
    
    public MessageSendDTO() {
    }
    
    public MessageSendDTO(Long receiverId, String content, Long productId) {
        this.receiverId = receiverId;
        this.content = content;
        this.productId = productId;
        this.messageType = "TEXT";
    }
    
    // ==================== Getter/Setter ====================
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    @Override
    public String toString() {
        return "MessageSendDTO{" +
                "receiverId=" + receiverId +
                ", content='" + content + '\'' +
                ", productId=" + productId +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
