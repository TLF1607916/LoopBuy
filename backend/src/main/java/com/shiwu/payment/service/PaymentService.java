package com.shiwu.payment.service;

import com.shiwu.payment.model.PaymentDTO;
import com.shiwu.payment.model.PaymentOperationResult;

import java.util.List;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付订单（跳转到模拟支付页面）
     * @param dto 支付请求数据
     * @param userId 当前用户ID
     * @return 支付操作结果，包含支付页面URL
     */
    PaymentOperationResult createPayment(PaymentDTO dto, Long userId);
    
    /**
     * 模拟支付（用户在支付页面点击确认支付）
     * @param paymentId 支付流水号
     * @param paymentPassword 支付密码
     * @param userId 当前用户ID
     * @return 支付操作结果
     */
    PaymentOperationResult processPayment(String paymentId, String paymentPassword, Long userId);
    

    
    /**
     * 查询支付状态
     * @param paymentId 支付流水号
     * @param userId 当前用户ID
     * @return 支付操作结果
     */
    PaymentOperationResult getPaymentStatus(String paymentId, Long userId);
    
    /**
     * 取消支付
     * @param paymentId 支付流水号
     * @param userId 当前用户ID
     * @return 支付操作结果
     */
    PaymentOperationResult cancelPayment(String paymentId, Long userId);
    
    /**
     * 处理支付超时（15分钟后自动调用）
     * @param paymentId 支付流水号
     * @return 支付操作结果
     */
    PaymentOperationResult handlePaymentTimeout(String paymentId);
    
    /**
     * 获取用户的支付记录
     * @param userId 用户ID
     * @return 支付操作结果
     */
    PaymentOperationResult getUserPayments(Long userId);
    
    /**
     * 根据订单ID获取支付信息
     * @param orderIds 订单ID列表
     * @param userId 当前用户ID
     * @return 支付操作结果
     */
    PaymentOperationResult getPaymentByOrderIds(List<Long> orderIds, Long userId);
}
