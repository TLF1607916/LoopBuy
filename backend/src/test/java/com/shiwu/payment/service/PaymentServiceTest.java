package com.shiwu.payment.service;

import com.shiwu.order.dao.OrderDao;
import com.shiwu.order.model.Order;
import com.shiwu.order.service.OrderService;
import com.shiwu.payment.dao.PaymentDao;
import com.shiwu.payment.model.*;
import com.shiwu.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 支付服务单元测试
 */
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentDao paymentDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderService orderService;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        // 使用反射注入mock对象
        paymentService = new PaymentServiceImpl();
        
        try {
            java.lang.reflect.Field paymentDaoField = PaymentServiceImpl.class.getDeclaredField("paymentDao");
            paymentDaoField.setAccessible(true);
            paymentDaoField.set(paymentService, paymentDao);

            java.lang.reflect.Field orderDaoField = PaymentServiceImpl.class.getDeclaredField("orderDao");
            orderDaoField.setAccessible(true);
            orderDaoField.set(paymentService, orderDao);

            java.lang.reflect.Field orderServiceField = PaymentServiceImpl.class.getDeclaredField("orderService");
            orderServiceField.setAccessible(true);
            orderServiceField.set(paymentService, orderService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }
    }

    @Test
    void testCreatePayment_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 100L;
        List<Long> orderIds = Arrays.asList(orderId);
        BigDecimal amount = new BigDecimal("99.99");
        
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(orderIds);
        dto.setTotalAmount(amount);
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        dto.setPaymentPassword("123456");
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(userId);
        order.setStatus(Order.STATUS_AWAITING_PAYMENT);
        order.setPriceAtPurchase(amount);
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        when(paymentDao.createPayment(any(Payment.class))).thenReturn(1L);
        
        // 执行测试
        PaymentOperationResult result = paymentService.createPayment(dto, userId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        PaymentVO paymentVO = (PaymentVO) result.getData();
        assertNotNull(paymentVO.getPaymentId());
        assertEquals(orderIds, paymentVO.getOrderIds());
        assertEquals(amount, paymentVO.getPaymentAmount());
        assertEquals(Payment.METHOD_ALIPAY, paymentVO.getPaymentMethod());
        assertEquals(Payment.STATUS_PENDING, paymentVO.getPaymentStatus());
        assertNotNull(paymentVO.getPaymentUrl());
        assertNotNull(paymentVO.getExpireTime());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(paymentDao, times(1)).createPayment(any(Payment.class));
    }

    @Test
    void testCreatePayment_InvalidParams() {
        // 测试空DTO
        PaymentOperationResult result = paymentService.createPayment(null, 1L);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空userId
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(1L));
        dto.setTotalAmount(new BigDecimal("99.99"));
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        result = paymentService.createPayment(dto, null);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空订单列表
        dto.setOrderIds(null);
        result = paymentService.createPayment(dto, 1L);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.EMPTY_ORDER_LIST, result.getErrorCode());
        
        // 测试无效金额
        dto.setOrderIds(Arrays.asList(1L));
        dto.setTotalAmount(BigDecimal.ZERO);
        result = paymentService.createPayment(dto, 1L);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_AMOUNT, result.getErrorCode());
        
        // 测试无效支付方式
        dto.setTotalAmount(new BigDecimal("99.99"));
        dto.setPaymentMethod(null);
        result = paymentService.createPayment(dto, 1L);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_PAYMENT_METHOD, result.getErrorCode());
    }

    @Test
    void testCreatePayment_OrderNotFound() {
        Long userId = 1L;
        Long orderId = 999L;
        
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(orderId));
        dto.setTotalAmount(new BigDecimal("99.99"));
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        
        // 设置mock行为 - 订单不存在
        when(orderDao.findById(orderId)).thenReturn(null);
        
        // 执行测试
        PaymentOperationResult result = paymentService.createPayment(dto, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.ORDER_NOT_FOUND, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_ORDER_NOT_FOUND, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(paymentDao, never()).createPayment(any(Payment.class));
    }

    @Test
    void testCreatePayment_OrderPermissionDenied() {
        Long userId = 1L;
        Long orderId = 100L;
        Long otherUserId = 2L;
        
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(orderId));
        dto.setTotalAmount(new BigDecimal("99.99"));
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(otherUserId); // 不同的用户ID
        order.setStatus(Order.STATUS_AWAITING_PAYMENT);
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        PaymentOperationResult result = paymentService.createPayment(dto, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.ORDER_PERMISSION_DENIED, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_ORDER_PERMISSION_DENIED, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(paymentDao, never()).createPayment(any(Payment.class));
    }

    @Test
    void testCreatePayment_OrderStatusInvalid() {
        Long userId = 1L;
        Long orderId = 100L;
        
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(orderId));
        dto.setTotalAmount(new BigDecimal("99.99"));
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(userId);
        order.setStatus(Order.STATUS_AWAITING_SHIPPING); // 错误的状态
        order.setPriceAtPurchase(new BigDecimal("99.99"));
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        PaymentOperationResult result = paymentService.createPayment(dto, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.ORDER_STATUS_INVALID, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_ORDER_STATUS_INVALID, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(paymentDao, never()).createPayment(any(Payment.class));
    }

    @Test
    void testCreatePayment_AmountMismatch() {
        Long userId = 1L;
        Long orderId = 100L;
        
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(Arrays.asList(orderId));
        dto.setTotalAmount(new BigDecimal("199.99")); // 不匹配的金额
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        
        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(userId);
        order.setStatus(Order.STATUS_AWAITING_PAYMENT);
        order.setPriceAtPurchase(new BigDecimal("99.99")); // 实际金额
        
        // 设置mock行为
        when(orderDao.findById(orderId)).thenReturn(order);
        
        // 执行测试
        PaymentOperationResult result = paymentService.createPayment(dto, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.ORDER_AMOUNT_MISMATCH, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_ORDER_AMOUNT_MISMATCH, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, times(1)).findById(orderId);
        verify(paymentDao, never()).createPayment(any(Payment.class));
    }

    @Test
    void testProcessPayment_Success() {
        String paymentId = "PAY123456";
        String paymentPassword = "123456";
        Long userId = 1L;
        
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserId(userId);
        payment.setOrderIds("[100]");
        payment.setPaymentAmount(new BigDecimal("99.99"));
        payment.setPaymentMethod(Payment.METHOD_ALIPAY);
        payment.setPaymentStatus(Payment.STATUS_PENDING);
        payment.setExpireTime(LocalDateTime.now().plusMinutes(10)); // 未过期
        
        // 设置mock行为
        when(paymentDao.findByPaymentId(paymentId)).thenReturn(payment);
        when(paymentDao.updatePaymentStatus(eq(paymentId), eq(Payment.STATUS_SUCCESS), anyString(), isNull())).thenReturn(true);
        when(orderService.updateOrderStatusAfterPayment(anyList(), eq(paymentId))).thenReturn(
            com.shiwu.order.model.OrderOperationResult.success(null)
        );
        
        // 执行测试
        PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        PaymentVO paymentVO = (PaymentVO) result.getData();
        assertEquals(paymentId, paymentVO.getPaymentId());
        assertEquals(Payment.STATUS_SUCCESS, paymentVO.getPaymentStatus());
        
        // 验证方法调用
        verify(paymentDao, times(1)).findByPaymentId(paymentId);
        verify(paymentDao, times(1)).updatePaymentStatus(eq(paymentId), eq(Payment.STATUS_SUCCESS), anyString(), isNull());
        verify(orderService, times(1)).updateOrderStatusAfterPayment(anyList(), eq(paymentId));
    }

    @Test
    void testProcessPayment_InvalidParams() {
        // 测试空paymentId
        PaymentOperationResult result = paymentService.processPayment(null, "123456", 1L);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空paymentPassword
        result = paymentService.processPayment("PAY123", null, 1L);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空userId
        result = paymentService.processPayment("PAY123", "123456", null);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空密码
        result = paymentService.processPayment("PAY123", "", 1L);
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.INVALID_PAYMENT_PASSWORD, result.getErrorCode());
    }

    @Test
    void testProcessPayment_PaymentNotFound() {
        String paymentId = "PAY999999";
        String paymentPassword = "123456";
        Long userId = 1L;
        
        // 设置mock行为 - 支付记录不存在
        when(paymentDao.findByPaymentId(paymentId)).thenReturn(null);
        
        // 执行测试
        PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.PAYMENT_NOT_FOUND, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_PAYMENT_NOT_FOUND, result.getErrorMessage());
        
        // 验证方法调用
        verify(paymentDao, times(1)).findByPaymentId(paymentId);
        verify(paymentDao, never()).updatePaymentStatus(anyString(), anyInt(), anyString(), anyString());
    }

    @Test
    void testProcessPayment_PermissionDenied() {
        String paymentId = "PAY123456";
        String paymentPassword = "123456";
        Long userId = 1L;
        Long otherUserId = 2L;
        
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserId(otherUserId); // 不同的用户ID
        payment.setPaymentStatus(Payment.STATUS_PENDING);
        
        // 设置mock行为
        when(paymentDao.findByPaymentId(paymentId)).thenReturn(payment);
        
        // 执行测试
        PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.PAYMENT_NOT_FOUND, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_PAYMENT_NOT_FOUND, result.getErrorMessage());
        
        // 验证方法调用
        verify(paymentDao, times(1)).findByPaymentId(paymentId);
        verify(paymentDao, never()).updatePaymentStatus(anyString(), anyInt(), anyString(), anyString());
    }

    @Test
    void testProcessPayment_AlreadyProcessed() {
        String paymentId = "PAY123456";
        String paymentPassword = "123456";
        Long userId = 1L;
        
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserId(userId);
        payment.setPaymentStatus(Payment.STATUS_SUCCESS); // 已处理
        
        // 设置mock行为
        when(paymentDao.findByPaymentId(paymentId)).thenReturn(payment);
        
        // 执行测试
        PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_PAYMENT_ALREADY_PROCESSED, result.getErrorMessage());
        
        // 验证方法调用
        verify(paymentDao, times(1)).findByPaymentId(paymentId);
        verify(paymentDao, never()).updatePaymentStatus(anyString(), anyInt(), anyString(), anyString());
    }

    @Test
    void testProcessPayment_Expired() {
        String paymentId = "PAY123456";
        String paymentPassword = "123456";
        Long userId = 1L;
        
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserId(userId);
        payment.setPaymentStatus(Payment.STATUS_PENDING);
        payment.setExpireTime(LocalDateTime.now().minusMinutes(1)); // 已过期
        
        // 设置mock行为
        when(paymentDao.findByPaymentId(paymentId)).thenReturn(payment);
        
        // 执行测试
        PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.PAYMENT_TIMEOUT, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_PAYMENT_TIMEOUT, result.getErrorMessage());
        
        // 验证方法调用 - 由于过期时会调用handlePaymentTimeout，所以findByPaymentId会被调用2次
        verify(paymentDao, times(2)).findByPaymentId(paymentId);
    }

    @Test
    void testProcessPayment_WrongPassword() {
        String paymentId = "PAY123456";
        String paymentPassword = "wrong_password";
        Long userId = 1L;
        
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserId(userId);
        payment.setPaymentStatus(Payment.STATUS_PENDING);
        payment.setExpireTime(LocalDateTime.now().plusMinutes(10)); // 未过期
        
        // 设置mock行为
        when(paymentDao.findByPaymentId(paymentId)).thenReturn(payment);
        
        // 执行测试
        PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(PaymentErrorCode.PAYMENT_PASSWORD_ERROR, result.getErrorCode());
        assertEquals(PaymentErrorCode.MSG_PAYMENT_PASSWORD_ERROR, result.getErrorMessage());
        
        // 验证方法调用
        verify(paymentDao, times(1)).findByPaymentId(paymentId);
        verify(paymentDao, never()).updatePaymentStatus(anyString(), anyInt(), anyString(), anyString());
    }
}
