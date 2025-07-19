package com.shiwu.order.service;

import com.shiwu.order.model.Order;
import com.shiwu.order.model.RefundTransaction;

/**
 * 退款服务接口
 * 
 * 根据SRS文档UC-18要求实现模拟退款操作
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public interface RefundService {
    
    /**
     * 执行模拟退款操作
     * @param order 订单信息
     * @param reason 退款原因
     * @return 退款交易记录
     */
    RefundTransaction processRefund(Order order, String reason);
    
    /**
     * 查询退款交易记录
     * @param refundId 退款交易ID
     * @return 退款交易记录
     */
    RefundTransaction getRefundTransaction(String refundId);
    
    /**
     * 根据订单ID查询退款记录
     * @param orderId 订单ID
     * @return 退款交易记录
     */
    RefundTransaction getRefundByOrderId(Long orderId);
}
