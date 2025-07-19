package com.shiwu.order.service;

import com.shiwu.order.model.OrderCreateDTO;
import com.shiwu.order.model.OrderOperationResult;
import com.shiwu.order.model.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     * @param dto 创建订单数据传输对象
     * @param buyerId 买家ID
     * @return 订单操作结果
     */
    OrderOperationResult createOrder(OrderCreateDTO dto, Long buyerId);
    
    /**
     * 获取用户的购买订单列表
     * @param buyerId 买家ID
     * @return 订单操作结果
     */
    OrderOperationResult getBuyerOrders(Long buyerId);
    
    /**
     * 获取用户的销售订单列表
     * @param sellerId 卖家ID
     * @return 订单操作结果
     */
    OrderOperationResult getSellerOrders(Long sellerId);
    
    /**
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @param userId 当前用户ID（用于权限验证）
     * @return 订单操作结果
     */
    OrderOperationResult getOrderById(Long orderId, Long userId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @param userId 当前用户ID（用于权限验证）
     * @return 订单操作结果
     */
    OrderOperationResult updateOrderStatus(Long orderId, Integer status, Long userId);

    /**
     * 支付成功后批量更新订单状态
     * @param orderIds 订单ID列表
     * @param paymentId 支付流水号（用于日志记录）
     * @return 订单操作结果
     */
    OrderOperationResult updateOrderStatusAfterPayment(List<Long> orderIds, String paymentId);

    /**
     * 支付超时或取消后批量处理订单
     * @param orderIds 订单ID列表
     * @param reason 取消原因
     * @return 订单操作结果
     */
    OrderOperationResult cancelOrdersAfterPaymentFailure(List<Long> orderIds, String reason);

    /**
     * 卖家发货
     * @param orderId 订单ID
     * @param sellerId 卖家用户ID（用于权限验证）
     * @return 订单操作结果
     */
    OrderOperationResult shipOrder(Long orderId, Long sellerId);

    /**
     * 买家确认收货
     * @param orderId 订单ID
     * @param buyerId 买家用户ID（用于权限验证）
     * @return 订单操作结果
     */
    OrderOperationResult confirmReceipt(Long orderId, Long buyerId);

    /**
     * 买家申请退货
     * @param orderId 订单ID
     * @param returnRequestDTO 退货申请数据
     * @param buyerId 买家用户ID（用于权限验证）
     * @return 订单操作结果
     */
    OrderOperationResult applyForReturn(Long orderId, com.shiwu.order.model.ReturnRequestDTO returnRequestDTO, Long buyerId);

    /**
     * 卖家处理退货申请
     * @param orderId 订单ID
     * @param processReturnRequestDTO 处理退货申请数据
     * @param sellerId 卖家用户ID（用于权限验证）
     * @return 订单操作结果
     */
    OrderOperationResult processReturnRequest(Long orderId, com.shiwu.order.model.ProcessReturnRequestDTO processReturnRequestDTO, Long sellerId);
}
