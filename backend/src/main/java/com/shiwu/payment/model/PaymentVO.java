package com.shiwu.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付结果视图对象
 * 用于返回给前端的支付信息
 */
public class PaymentVO {
    
    /**
     * 支付流水号
     */
    private String paymentId;
    
    /**
     * 订单ID列表
     */
    private List<Long> orderIds;
    
    /**
     * 支付状态
     */
    private Integer paymentStatus;
    
    /**
     * 支付状态描述
     */
    private String paymentStatusText;
    
    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;
    
    /**
     * 支付方式
     */
    private Integer paymentMethod;
    
    /**
     * 支付方式描述
     */
    private String paymentMethodText;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 失败原因
     */
    private String failureReason;
    
    /**
     * 第三方交易号
     */
    private String thirdPartyTransactionId;
    
    /**
     * 支付页面URL（用于跳转到模拟支付页面）
     */
    private String paymentUrl;
    
    /**
     * 支付超时时间（15分钟后）
     */
    private LocalDateTime expireTime;
    
    // 构造函数
    public PaymentVO() {}
    
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
    
    public String getPaymentStatusText() {
        return paymentStatusText;
    }
    
    public void setPaymentStatusText(String paymentStatusText) {
        this.paymentStatusText = paymentStatusText;
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
    
    public String getPaymentMethodText() {
        return paymentMethodText;
    }
    
    public void setPaymentMethodText(String paymentMethodText) {
        this.paymentMethodText = paymentMethodText;
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
    
    public String getPaymentUrl() {
        return paymentUrl;
    }
    
    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    @Override
    public String toString() {
        return "PaymentVO{" +
                "paymentId='" + paymentId + '\'' +
                ", orderIds=" + orderIds +
                ", paymentStatus=" + paymentStatus +
                ", paymentStatusText='" + paymentStatusText + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", paymentMethod=" + paymentMethod +
                ", paymentMethodText='" + paymentMethodText + '\'' +
                ", paymentTime=" + paymentTime +
                ", failureReason='" + failureReason + '\'' +
                ", thirdPartyTransactionId='" + thirdPartyTransactionId + '\'' +
                ", paymentUrl='" + paymentUrl + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
