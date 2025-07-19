package com.shiwu.order.service;

import com.shiwu.order.dao.OrderDao;
import com.shiwu.order.model.Order;
import com.shiwu.order.model.OrderErrorCode;
import com.shiwu.order.model.OrderOperationResult;
import com.shiwu.order.service.impl.OrderServiceImpl;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单确认收货功能单元测试
 */
@ExtendWith(MockitoExtension.class)
public class OrderConfirmReceiptServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private ProductDao productDao;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // 使用反射注入mock对象
        orderService = new OrderServiceImpl();
        
        try {
            java.lang.reflect.Field orderDaoField = OrderServiceImpl.class.getDeclaredField("orderDao");
            orderDaoField.setAccessible(true);
            orderDaoField.set(orderService, orderDao);

            java.lang.reflect.Field productDaoField = OrderServiceImpl.class.getDeclaredField("productDao");
            productDaoField.setAccessible(true);
            productDaoField.set(orderService, productDao);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }
    }

    @Test
    void testConfirmReceipt_Success() {
        // 准备测试数据
        Long orderId = 1L;
        Long buyerId = 200L;
        Long sellerId = 100L;
        Long productId = 300L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setProductId(productId);
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        order.setStatus(Order.STATUS_SHIPPED);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        when(orderDao.updateOrderStatus(orderId, Order.STATUS_COMPLETED)).thenReturn(true);
        when(productDao.updateProductStatusBySystem(productId, Product.STATUS_SOLD)).thenReturn(true);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(orderId, data.get("orderId"));
        assertEquals(productId, data.get("productId"));
        assertEquals(new BigDecimal("99.99"), data.get("priceAtPurchase"));
        assertEquals(Order.STATUS_COMPLETED, data.get("orderStatus"));
        assertEquals("已完成", data.get("orderStatusText"));
        assertEquals(Product.STATUS_SOLD, data.get("productStatus"));
        assertEquals("已售出", data.get("productStatusText"));
        assertEquals("确认收货成功", data.get("message"));
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, times(1)).updateOrderStatus(orderId, Order.STATUS_COMPLETED);
        verify(productDao, times(1)).updateProductStatusBySystem(productId, Product.STATUS_SOLD);
    }

    @Test
    void testConfirmReceipt_InvalidParams() {
        // 测试空orderId
        OrderOperationResult result = orderService.confirmReceipt(null, 200L);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.INVALID_PARAMS, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_INVALID_PARAMS, result.getErrorMessage());
        
        // 测试空buyerId
        result = orderService.confirmReceipt(1L, null);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.INVALID_PARAMS, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_INVALID_PARAMS, result.getErrorMessage());
        
        // 验证没有调用DAO方法
        verify(orderDao, never()).findById(anyLong());
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }

    @Test
    void testConfirmReceipt_OrderNotFound() {
        Long orderId = 999L;
        Long buyerId = 200L;
        
        // 设置mock行为 - 订单不存在
        when(orderDao.findById(orderId)).thenReturn(null);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_NOT_FOUND, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_NOT_FOUND, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }

    @Test
    void testConfirmReceipt_PermissionDenied() {
        Long orderId = 1L;
        Long buyerId = 200L;
        Long actualBuyerId = 999L; // 不同的买家ID
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(actualBuyerId); // 实际的买家ID与请求的不同
        order.setSellerId(100L);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_SHIPPED);
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.CONFIRM_RECEIPT_PERMISSION_DENIED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_CONFIRM_RECEIPT_PERMISSION_DENIED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }

    @Test
    void testConfirmReceipt_InvalidOrderStatus() {
        Long orderId = 1L;
        Long buyerId = 200L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(100L);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_AWAITING_SHIPPING); // 错误的状态，应该是已发货
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_SHIPPED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_SHIPPED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }

    @Test
    void testConfirmReceipt_UpdateOrderStatusFailed() {
        Long orderId = 1L;
        Long buyerId = 200L;
        Long productId = 300L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(100L);
        order.setProductId(productId);
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        order.setStatus(Order.STATUS_SHIPPED);
        
        // 设置mock行为 - 更新订单状态失败
        when(orderDao.findById(orderId)).thenReturn(order);
        when(orderDao.updateOrderStatus(orderId, Order.STATUS_COMPLETED)).thenReturn(false);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.CONFIRM_RECEIPT_FAILED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_CONFIRM_RECEIPT_FAILED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, times(1)).updateOrderStatus(orderId, Order.STATUS_COMPLETED);
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }

    @Test
    void testConfirmReceipt_UpdateProductStatusFailed() {
        Long orderId = 1L;
        Long buyerId = 200L;
        Long productId = 300L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(100L);
        order.setProductId(productId);
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        order.setStatus(Order.STATUS_SHIPPED);
        
        // 设置mock行为 - 更新商品状态失败
        when(orderDao.findById(orderId)).thenReturn(order);
        when(orderDao.updateOrderStatus(orderId, Order.STATUS_COMPLETED)).thenReturn(true);
        when(productDao.updateProductStatusBySystem(productId, Product.STATUS_SOLD)).thenReturn(false);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.UPDATE_PRODUCT_TO_SOLD_FAILED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_UPDATE_PRODUCT_TO_SOLD_FAILED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, times(1)).updateOrderStatus(orderId, Order.STATUS_COMPLETED);
        verify(productDao, times(1)).updateProductStatusBySystem(productId, Product.STATUS_SOLD);
    }

    @Test
    void testConfirmReceipt_OrderAlreadyCompleted() {
        Long orderId = 1L;
        Long buyerId = 200L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(100L);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_COMPLETED); // 已经是已完成状态
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_SHIPPED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_SHIPPED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }

    @Test
    void testConfirmReceipt_OrderCancelled() {
        Long orderId = 1L;
        Long buyerId = 200L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(100L);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_CANCELLED); // 已取消状态
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_SHIPPED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_SHIPPED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }

    @Test
    void testConfirmReceipt_OrderAwaitingPayment() {
        Long orderId = 1L;
        Long buyerId = 200L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(100L);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_AWAITING_PAYMENT); // 待付款状态
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_SHIPPED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_SHIPPED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
        verify(productDao, never()).updateProductStatusBySystem(anyLong(), anyInt());
    }
}
