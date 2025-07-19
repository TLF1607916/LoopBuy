package com.shiwu.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 对应数据库中的payment表
 */
public class Payment {
    
    /**
     * 支付ID
     */
    private Long id;
    
    /**
     * 支付流水号（唯一标识）
     */
    private String paymentId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单ID列表（JSON格式存储）
     */
    private String orderIds;
    
    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;
    
    /**
     * 支付方式
     * 1-支付宝，2-微信支付，3-银行卡
     */
    private Integer paymentMethod;
    
    /**
     * 支付状态
     * 0-待支付，1-支付成功，2-支付失败，3-支付取消，4-支付超时
     */
    private Integer paymentStatus;
    
    /**
     * 第三方交易号
     */
    private String thirdPartyTransactionId;
    
    /**
     * 失败原因
     */
    private String failureReason;
    
    /**
     * 支付完成时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 支付超时时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 是否删除
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
    
    // 支付状态常量
    public static final Integer STATUS_PENDING = 0;    // 待支付
    public static final Integer STATUS_SUCCESS = 1;    // 支付成功
    public static final Integer STATUS_FAILED = 2;     // 支付失败
    public static final Integer STATUS_CANCELLED = 3;  // 支付取消
    public static final Integer STATUS_TIMEOUT = 4;    // 支付超时
    
    // 支付方式常量
    public static final Integer METHOD_ALIPAY = 1;     // 支付宝
    public static final Integer METHOD_WECHAT = 2;     // 微信支付
    public static final Integer METHOD_BANK_CARD = 3;  // 银行卡
    
    // 构造函数
    public Payment() {}
    
    public Payment(String paymentId, Long userId, String orderIds, BigDecimal paymentAmount, 
                   Integer paymentMethod, LocalDateTime expireTime) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderIds = orderIds;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = STATUS_PENDING;
        this.expireTime = expireTime;
        this.deleted = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getOrderIds() {
        return orderIds;
    }
    
    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
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
    
    public Integer getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getThirdPartyTransactionId() {
        return thirdPartyTransactionId;
    }
    
    public void setThirdPartyTransactionId(String thirdPartyTransactionId) {
        this.thirdPartyTransactionId = thirdPartyTransactionId;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
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
        return "Payment{" +
                "id=" + id +
                ", paymentId='" + paymentId + '\'' +
                ", userId=" + userId +
                ", orderIds='" + orderIds + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                ", thirdPartyTransactionId='" + thirdPartyTransactionId + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", paymentTime=" + paymentTime +
                ", expireTime=" + expireTime +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
