package com.shiwu.order.service;

import com.shiwu.order.dao.OrderDao;
import com.shiwu.order.model.Order;
import com.shiwu.order.model.OrderErrorCode;
import com.shiwu.order.model.OrderOperationResult;
import com.shiwu.order.service.impl.OrderServiceImpl;
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
 * 订单发货功能单元测试
 */
@ExtendWith(MockitoExtension.class)
public class OrderShipServiceTest {

    @Mock
    private OrderDao orderDao;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // 使用反射注入mock对象
        orderService = new OrderServiceImpl();
        
        try {
            java.lang.reflect.Field orderDaoField = OrderServiceImpl.class.getDeclaredField("orderDao");
            orderDaoField.setAccessible(true);
            orderDaoField.set(orderService, orderDao);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }
    }

    @Test
    void testShipOrder_Success() {
        // 准备测试数据
        Long orderId = 1L;
        Long sellerId = 100L;
        Long buyerId = 200L;
        Long productId = 300L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setProductId(productId);
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        order.setStatus(Order.STATUS_AWAITING_SHIPPING);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        when(orderDao.updateOrderStatus(orderId, Order.STATUS_SHIPPED)).thenReturn(true);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(orderId, data.get("orderId"));
        assertEquals(productId, data.get("productId"));
        assertEquals(new BigDecimal("99.99"), data.get("priceAtPurchase"));
        assertEquals(Order.STATUS_SHIPPED, data.get("status"));
        assertEquals("已发货", data.get("statusText"));
        assertEquals("发货成功", data.get("message"));
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, times(1)).updateOrderStatus(orderId, Order.STATUS_SHIPPED);
    }

    @Test
    void testShipOrder_InvalidParams() {
        // 测试空orderId
        OrderOperationResult result = orderService.shipOrder(null, 100L);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.INVALID_PARAMS, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_INVALID_PARAMS, result.getErrorMessage());
        
        // 测试空sellerId
        result = orderService.shipOrder(1L, null);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.INVALID_PARAMS, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_INVALID_PARAMS, result.getErrorMessage());
        
        // 验证没有调用DAO方法
        verify(orderDao, never()).findById(anyLong());
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
    }

    @Test
    void testShipOrder_OrderNotFound() {
        Long orderId = 999L;
        Long sellerId = 100L;
        
        // 设置mock行为 - 订单不存在
        when(orderDao.findById(orderId)).thenReturn(null);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_NOT_FOUND, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_NOT_FOUND, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
    }

    @Test
    void testShipOrder_PermissionDenied() {
        Long orderId = 1L;
        Long sellerId = 100L;
        Long actualSellerId = 999L; // 不同的卖家ID
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(200L);
        order.setSellerId(actualSellerId); // 实际的卖家ID与请求的不同
        order.setProductId(300L);
        order.setStatus(Order.STATUS_AWAITING_SHIPPING);
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.SHIP_PERMISSION_DENIED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_SHIP_PERMISSION_DENIED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
    }

    @Test
    void testShipOrder_InvalidOrderStatus() {
        Long orderId = 1L;
        Long sellerId = 100L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(200L);
        order.setSellerId(sellerId);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_AWAITING_PAYMENT); // 错误的状态，应该是待发货
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
    }

    @Test
    void testShipOrder_UpdateStatusFailed() {
        Long orderId = 1L;
        Long sellerId = 100L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(200L);
        order.setSellerId(sellerId);
        order.setProductId(300L);
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        order.setStatus(Order.STATUS_AWAITING_SHIPPING);
        
        // 设置mock行为 - 更新状态失败
        when(orderDao.findById(orderId)).thenReturn(order);
        when(orderDao.updateOrderStatus(orderId, Order.STATUS_SHIPPED)).thenReturn(false);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.SHIP_ORDER_FAILED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_SHIP_ORDER_FAILED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, times(1)).updateOrderStatus(orderId, Order.STATUS_SHIPPED);
    }

    @Test
    void testShipOrder_AlreadyShipped() {
        Long orderId = 1L;
        Long sellerId = 100L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(200L);
        order.setSellerId(sellerId);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_SHIPPED); // 已经是已发货状态
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
    }

    @Test
    void testShipOrder_OrderCompleted() {
        Long orderId = 1L;
        Long sellerId = 100L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(200L);
        order.setSellerId(sellerId);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_COMPLETED); // 已完成状态
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
    }

    @Test
    void testShipOrder_OrderCancelled() {
        Long orderId = 1L;
        Long sellerId = 100L;
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(200L);
        order.setSellerId(sellerId);
        order.setProductId(300L);
        order.setStatus(Order.STATUS_CANCELLED); // 已取消状态
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_ORDER_STATUS_NOT_AWAITING_SHIPPING, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(orderDao, never()).updateOrderStatus(anyLong(), anyInt());
    }
}
