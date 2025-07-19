package com.shiwu.payment.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付请求数据传输对象
 * 用于接收前端发起支付的请求参数
 */
public class PaymentDTO {
    
    /**
     * 订单ID列表（支持批量支付）
     */
    private List<Long> orderIds;
    
    /**
     * 支付总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 支付方式（模拟）
     * 1-支付宝，2-微信支付，3-银行卡
     */
    private Integer paymentMethod;
    
    /**
     * 支付密码（模拟）
     */
    private String paymentPassword;
    
    // 构造函数
    public PaymentDTO() {}
    
    public PaymentDTO(List<Long> orderIds, BigDecimal totalAmount, Integer paymentMethod, String paymentPassword) {
        this.orderIds = orderIds;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentPassword = paymentPassword;
    }
    
    // Getter和Setter方法
    public List<Long> getOrderIds() {
        return orderIds;
    }
    
    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Integer getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentPassword() {
        return paymentPassword;
    }
    
    public void setPaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }
    
    @Override
    public String toString() {
        return "PaymentDTO{" +
                "orderIds=" + orderIds +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", paymentPassword='***'" + // 不显示密码
                '}';
    }
}
