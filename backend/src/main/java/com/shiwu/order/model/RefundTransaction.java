package com.shiwu.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款交易记录
 * 用于记录模拟退款操作的信息
 * 
 * 根据SRS文档UC-18要求：
 * "系统执行模拟退款操作。例如，可以将订单金额返还到买家的虚拟平台余额（如果有这样的设计），
 * 或者简单地记录一笔退款交易。"
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class RefundTransaction {
    
    /**
     * 退款交易ID
     */
    private String refundId;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 买家用户ID
     */
    private Long buyerId;
    
    /**
     * 卖家用户ID
     */
    private Long sellerId;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款状态：SUCCESS-成功，FAILED-失败，PENDING-处理中
     */
    private String status;
    
    /**
     * 退款原因
     */
    private String reason;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    // 退款状态常量
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PENDING = "PENDING";
    
    // 构造函数
    public RefundTransaction() {}
    
    public RefundTransaction(String refundId, Long orderId, Long buyerId, Long sellerId, 
                           BigDecimal refundAmount, String reason) {
        this.refundId = refundId;
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.refundAmount = refundAmount;
        this.reason = reason;
        this.status = STATUS_PENDING;
        this.createTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public String getRefundId() {
        return refundId;
    }
    
    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    /**
     * 标记退款成功
     */
    public void markAsSuccess() {
        this.status = STATUS_SUCCESS;
    }
    
    /**
     * 标记退款失败
     */
    public void markAsFailed() {
        this.status = STATUS_FAILED;
    }
    
    /**
     * 判断是否退款成功
     */
    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(status);
    }
    
    /**
     * 判断是否退款失败
     */
    public boolean isFailed() {
        return STATUS_FAILED.equals(status);
    }
    
    /**
     * 判断是否处理中
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }
    
    @Override
    public String toString() {
        return "RefundTransaction{" +
                "refundId='" + refundId + '\'' +
                ", orderId=" + orderId +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                ", refundAmount=" + refundAmount +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
