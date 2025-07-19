package com.shiwu.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付回调数据传输对象
 * 用于模拟第三方支付平台的回调通知
 */
public class PaymentCallbackDTO {
    
    /**
     * 支付流水号（模拟第三方支付平台生成）
     */
    private String paymentId;
    
    /**
     * 订单ID列表
     */
    private List<Long> orderIds;
    
    /**
     * 支付状态
     * 1-支付成功，2-支付失败，3-支付取消
     */
    private Integer paymentStatus;
    
    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;
    
    /**
     * 支付方式
     */
    private Integer paymentMethod;
    
    /**
     * 支付完成时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 失败原因（支付失败时）
     */
    private String failureReason;
    
    /**
     * 第三方交易号（模拟）
     */
    private String thirdPartyTransactionId;
    
    // 支付状态常量
    public static final Integer STATUS_SUCCESS = 1;  // 支付成功
    public static final Integer STATUS_FAILED = 2;   // 支付失败
    public static final Integer STATUS_CANCELLED = 3; // 支付取消
    
    // 构造函数
    public PaymentCallbackDTO() {}
    
    public PaymentCallbackDTO(String paymentId, List<Long> orderIds, Integer paymentStatus, 
                             BigDecimal paymentAmount, Integer paymentMethod) {
        this.paymentId = paymentId;
        this.orderIds = orderIds;
        this.paymentStatus = paymentStatus;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public List<Long> getOrderIds() {
        return orderIds;
    }
    
    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }
    
    public Integer getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }
    
    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    
    public Integer getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    public String getThirdPartyTransactionId() {
        return thirdPartyTransactionId;
    }
    
    public void setThirdPartyTransactionId(String thirdPartyTransactionId) {
        this.thirdPartyTransactionId = thirdPartyTransactionId;
    }
    
    @Override
    public String toString() {
        return "PaymentCallbackDTO{" +
                "paymentId='" + paymentId + '\'' +
                ", orderIds=" + orderIds +
                ", paymentStatus=" + paymentStatus +
                ", paymentAmount=" + paymentAmount +
                ", paymentMethod=" + paymentMethod +
                ", paymentTime=" + paymentTime +
                ", failureReason='" + failureReason + '\'' +
                ", thirdPartyTransactionId='" + thirdPartyTransactionId + '\'' +
                '}';
    }
}
