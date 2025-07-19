package com.shiwu.order.service;

import com.shiwu.cart.dao.CartDao;
import com.shiwu.order.dao.OrderDao;
import com.shiwu.order.model.*;
import com.shiwu.order.service.impl.OrderServiceImpl;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private ProductDao productDao;

    @Mock
    private CartDao cartDao;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // 使用反射或者创建一个可以注入依赖的构造函数
        orderService = new OrderServiceImpl();
        
        // 由于OrderServiceImpl没有提供依赖注入的构造函数，
        // 这里我们需要使用反射来注入mock对象
        try {
            java.lang.reflect.Field orderDaoField = OrderServiceImpl.class.getDeclaredField("orderDao");
            orderDaoField.setAccessible(true);
            orderDaoField.set(orderService, orderDao);

            java.lang.reflect.Field productDaoField = OrderServiceImpl.class.getDeclaredField("productDao");
            productDaoField.setAccessible(true);
            productDaoField.set(orderService, productDao);

            java.lang.reflect.Field cartDaoField = OrderServiceImpl.class.getDeclaredField("cartDao");
            cartDaoField.setAccessible(true);
            cartDaoField.set(orderService, cartDao);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }
    }

    @Test
    void testCreateOrder_Success() {
        // 准备测试数据
        Long buyerId = 1L;
        Long sellerId = 2L;
        Long productId = 3L;
        
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(productId));
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setTitle("测试商品");
        product.setDescription("测试描述");
        product.setPrice(new BigDecimal("99.99"));
        product.setStatus(Product.STATUS_ONSALE);
        
        List<ProductImage> productImages = new ArrayList<>();
        ProductImage image = new ProductImage();
        image.setImageUrl("http://example.com/image1.jpg");
        productImages.add(image);
        
        // 设置mock行为
        when(productDao.findById(productId)).thenReturn(product);
        when(productDao.updateProductStatusBySystem(productId, Product.STATUS_LOCKED)).thenReturn(true);
        when(productDao.findImagesByProductId(productId)).thenReturn(productImages);
        when(orderDao.createOrder(any(Order.class))).thenReturn(100L);
        when(cartDao.removeFromCart(buyerId, productId)).thenReturn(true);
        
        // 执行测试
        OrderOperationResult result = orderService.createOrder(dto, buyerId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(1, data.get("orderCount"));
        
        // 验证方法调用
        verify(productDao, times(1)).findById(productId);
        verify(productDao, times(1)).updateProductStatusBySystem(productId, Product.STATUS_LOCKED);
        verify(orderDao, times(1)).createOrder(any(Order.class));
        verify(cartDao, times(1)).removeFromCart(buyerId, productId);
    }

    @Test
    void testCreateOrder_InvalidParams() {
        // 测试空DTO
        OrderOperationResult result = orderService.createOrder(null, 1L);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空buyerId
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(1L));
        result = orderService.createOrder(dto, null);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空商品列表
        dto.setProductIds(null);
        result = orderService.createOrder(dto, 1L);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.EMPTY_PRODUCT_LIST, result.getErrorCode());
        
        // 测试空商品列表
        dto.setProductIds(new ArrayList<>());
        result = orderService.createOrder(dto, 1L);
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.EMPTY_PRODUCT_LIST, result.getErrorCode());
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        Long buyerId = 1L;
        Long productId = 999L;
        
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(productId));
        
        // 设置mock行为 - 商品不存在
        when(productDao.findById(productId)).thenReturn(null);
        
        // 执行测试
        OrderOperationResult result = orderService.createOrder(dto, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.PRODUCT_NOT_FOUND, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_PRODUCT_NOT_FOUND, result.getErrorMessage());
        
        // 验证方法调用
        verify(productDao, times(1)).findById(productId);
        verify(orderDao, never()).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_ProductNotAvailable() {
        Long buyerId = 1L;
        Long sellerId = 2L;
        Long productId = 3L;
        
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(productId));
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setStatus(Product.STATUS_SOLD); // 商品已售出
        
        // 设置mock行为
        when(productDao.findById(productId)).thenReturn(product);
        
        // 执行测试
        OrderOperationResult result = orderService.createOrder(dto, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.PRODUCT_NOT_AVAILABLE, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_PRODUCT_NOT_AVAILABLE, result.getErrorMessage());
        
        // 验证方法调用
        verify(productDao, times(1)).findById(productId);
        verify(orderDao, never()).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_CantBuyOwnProduct() {
        Long userId = 1L; // 同时作为买家和卖家
        Long productId = 3L;
        
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(productId));
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(userId); // 卖家ID与买家ID相同
        product.setStatus(Product.STATUS_ONSALE);
        
        // 设置mock行为
        when(productDao.findById(productId)).thenReturn(product);
        
        // 执行测试
        OrderOperationResult result = orderService.createOrder(dto, userId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.CANT_BUY_OWN_PRODUCT, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_CANT_BUY_OWN_PRODUCT, result.getErrorMessage());
        
        // 验证方法调用
        verify(productDao, times(1)).findById(productId);
        verify(orderDao, never()).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_LockProductFailed() {
        Long buyerId = 1L;
        Long sellerId = 2L;
        Long productId = 3L;
        
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(productId));
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setStatus(Product.STATUS_ONSALE);
        
        // 设置mock行为 - 锁定商品失败
        when(productDao.findById(productId)).thenReturn(product);
        when(productDao.updateProductStatusBySystem(productId, Product.STATUS_LOCKED)).thenReturn(false);
        
        // 执行测试
        OrderOperationResult result = orderService.createOrder(dto, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.UPDATE_PRODUCT_STATUS_FAILED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_UPDATE_PRODUCT_STATUS_FAILED, result.getErrorMessage());
        
        // 验证方法调用
        verify(productDao, times(1)).findById(productId);
        verify(productDao, times(1)).updateProductStatusBySystem(productId, Product.STATUS_LOCKED);
        verify(orderDao, never()).createOrder(any(Order.class));
    }

    @Test
    void testCreateOrder_CreateOrderFailed() {
        Long buyerId = 1L;
        Long sellerId = 2L;
        Long productId = 3L;
        
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(productId));
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setTitle("测试商品");
        product.setDescription("测试描述");
        product.setPrice(new BigDecimal("99.99"));
        product.setStatus(Product.STATUS_ONSALE);
        
        List<ProductImage> productImages = new ArrayList<>();
        
        // 设置mock行为 - 创建订单失败
        when(productDao.findById(productId)).thenReturn(product);
        when(productDao.updateProductStatusBySystem(productId, Product.STATUS_LOCKED)).thenReturn(true);
        when(productDao.findImagesByProductId(productId)).thenReturn(productImages);
        when(orderDao.createOrder(any(Order.class))).thenReturn(null); // 创建失败
        
        // 执行测试
        OrderOperationResult result = orderService.createOrder(dto, buyerId);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.CREATE_ORDER_FAILED, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_CREATE_ORDER_FAILED, result.getErrorMessage());
        
        // 验证方法调用
        verify(productDao, times(1)).findById(productId);
        verify(productDao, times(1)).updateProductStatusBySystem(productId, Product.STATUS_LOCKED);
        verify(orderDao, times(1)).createOrder(any(Order.class));
        // 应该回滚商品状态
        verify(productDao, times(1)).updateProductStatusBySystem(productId, Product.STATUS_ONSALE);
    }

    @Test
    void testGetBuyerOrders_Success() {
        Long buyerId = 1L;
        List<OrderVO> orders = new ArrayList<>();
        
        OrderVO order = new OrderVO();
        order.setId(1L);
        order.setStatus(Order.STATUS_AWAITING_PAYMENT);
        order.setProductImageUrlsSnapshot(new ArrayList<>());
        orders.add(order);
        
        // 设置mock行为
        when(orderDao.findOrdersByBuyerId(buyerId)).thenReturn(orders);
        
        // 执行测试
        OrderOperationResult result = orderService.getBuyerOrders(buyerId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(1, data.get("total"));
        
        // 验证方法调用
        verify(orderDao, times(1)).findOrdersByBuyerId(buyerId);
    }

    @Test
    void testGetBuyerOrders_InvalidParams() {
        // 测试空buyerId
        OrderOperationResult result = orderService.getBuyerOrders(null);
        
        assertFalse(result.isSuccess());
        assertEquals(OrderErrorCode.INVALID_PARAMS, result.getErrorCode());
        assertEquals(OrderErrorCode.MSG_INVALID_PARAMS, result.getErrorMessage());
        
        // 验证方法调用
        verify(orderDao, never()).findOrdersByBuyerId(any());
    }

    @Test
    void testGetSellerOrders_Success() {
        Long sellerId = 1L;
        List<OrderVO> orders = new ArrayList<>();
        
        OrderVO order = new OrderVO();
        order.setId(1L);
        order.setStatus(Order.STATUS_AWAITING_SHIPPING);
        order.setProductImageUrlsSnapshot(new ArrayList<>());
        orders.add(order);
        
        // 设置mock行为
        when(orderDao.findOrdersBySellerId(sellerId)).thenReturn(orders);
        
        // 执行测试
        OrderOperationResult result = orderService.getSellerOrders(sellerId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(1, data.get("total"));
        
        // 验证方法调用
        verify(orderDao, times(1)).findOrdersBySellerId(sellerId);
    }
}
