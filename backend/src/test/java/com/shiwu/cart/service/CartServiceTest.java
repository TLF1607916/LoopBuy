package com.shiwu.cart.service;

import com.shiwu.cart.dao.CartDao;
import com.shiwu.cart.model.*;
import com.shiwu.cart.service.impl.CartServiceImpl;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 购物车服务测试类
 */
public class CartServiceTest {
    
    @Mock
    private CartDao cartDao;
    
    @Mock
    private ProductDao productDao;
    
    private CartService cartService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartServiceImpl();
        
        // 使用反射设置mock对象
        try {
            java.lang.reflect.Field cartDaoField = CartServiceImpl.class.getDeclaredField("cartDao");
            cartDaoField.setAccessible(true);
            cartDaoField.set(cartService, cartDao);
            
            java.lang.reflect.Field productDaoField = CartServiceImpl.class.getDeclaredField("productDao");
            productDaoField.setAccessible(true);
            productDaoField.set(cartService, productDao);
        } catch (Exception e) {
            fail("Failed to set up mocks: " + e.getMessage());
        }
    }
    
    @Test
    void testAddToCart_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long productId = 2L;
        Long sellerId = 3L;
        CartAddDTO dto = new CartAddDTO(productId, 1);
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setStatus(Product.STATUS_ONSALE);
        
        // 设置mock行为
        when(productDao.findById(productId)).thenReturn(product);
        when(cartDao.existsInCart(userId, productId)).thenReturn(false);
        when(cartDao.addToCart(any(CartItem.class))).thenReturn(true);
        when(cartDao.getCartItemCount(userId)).thenReturn(3);
        
        // 执行测试
        CartOperationResult result = cartService.addToCart(dto, userId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(3, data.get("totalItems"));
        
        // 验证方法调用
        verify(productDao).findById(productId);
        verify(cartDao).existsInCart(userId, productId);
        verify(cartDao).addToCart(any(CartItem.class));
        verify(cartDao).getCartItemCount(userId);
    }
    
    @Test
    void testAddToCart_InvalidParams() {
        // 测试空DTO
        CartOperationResult result = cartService.addToCart(null, 1L);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_INVALID_PARAMS, result.getErrorMessage());
        
        // 测试空商品ID
        CartAddDTO dto = new CartAddDTO(null, 1);
        result = cartService.addToCart(dto, 1L);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());
        
        // 测试空用户ID
        dto = new CartAddDTO(1L, 1);
        result = cartService.addToCart(dto, null);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());
    }
    
    @Test
    void testAddToCart_InvalidQuantity() {
        CartAddDTO dto = new CartAddDTO(1L, 0);
        CartOperationResult result = cartService.addToCart(dto, 1L);
        
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_QUANTITY, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_INVALID_QUANTITY, result.getErrorMessage());
    }
    
    @Test
    void testAddToCart_ProductNotFound() {
        Long userId = 1L;
        Long productId = 2L;
        CartAddDTO dto = new CartAddDTO(productId, 1);
        
        when(productDao.findById(productId)).thenReturn(null);
        
        CartOperationResult result = cartService.addToCart(dto, userId);
        
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.PRODUCT_NOT_FOUND, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_PRODUCT_NOT_FOUND, result.getErrorMessage());
    }
    
    @Test
    void testAddToCart_ProductNotAvailable() {
        Long userId = 1L;
        Long productId = 2L;
        CartAddDTO dto = new CartAddDTO(productId, 1);
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(3L);
        product.setStatus(Product.STATUS_SOLD); // 已售出状态
        
        when(productDao.findById(productId)).thenReturn(product);
        
        CartOperationResult result = cartService.addToCart(dto, userId);
        
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.PRODUCT_NOT_AVAILABLE, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_PRODUCT_NOT_AVAILABLE, result.getErrorMessage());
    }
    
    @Test
    void testAddToCart_CantBuyOwnProduct() {
        Long userId = 1L;
        Long productId = 2L;
        CartAddDTO dto = new CartAddDTO(productId, 1);
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(userId); // 卖家就是当前用户
        product.setStatus(Product.STATUS_ONSALE);
        
        when(productDao.findById(productId)).thenReturn(product);
        
        CartOperationResult result = cartService.addToCart(dto, userId);
        
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.CANT_BUY_OWN_PRODUCT, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_CANT_BUY_OWN_PRODUCT, result.getErrorMessage());
    }
    
    @Test
    void testAddToCart_ProductAlreadyInCart() {
        Long userId = 1L;
        Long productId = 2L;
        Long sellerId = 3L;
        CartAddDTO dto = new CartAddDTO(productId, 1);
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setStatus(Product.STATUS_ONSALE);
        
        when(productDao.findById(productId)).thenReturn(product);
        when(cartDao.existsInCart(userId, productId)).thenReturn(true); // 已在购物车中
        
        CartOperationResult result = cartService.addToCart(dto, userId);
        
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.PRODUCT_ALREADY_IN_CART, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_PRODUCT_ALREADY_IN_CART, result.getErrorMessage());
    }
    
    @Test
    void testAddToCart_DatabaseError() {
        Long userId = 1L;
        Long productId = 2L;
        Long sellerId = 3L;
        CartAddDTO dto = new CartAddDTO(productId, 1);
        
        Product product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        product.setStatus(Product.STATUS_ONSALE);
        
        when(productDao.findById(productId)).thenReturn(product);
        when(cartDao.existsInCart(userId, productId)).thenReturn(false);
        when(cartDao.addToCart(any(CartItem.class))).thenReturn(false); // 数据库操作失败
        
        CartOperationResult result = cartService.addToCart(dto, userId);
        
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.ADD_TO_CART_FAILED, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_ADD_TO_CART_FAILED, result.getErrorMessage());
    }
    
    @Test
    void testGetCart_Success() {
        Long userId = 1L;
        List<CartItemVO> cartItems = new ArrayList<>();

        // 创建测试购物车项
        CartItemVO item1 = new CartItemVO();
        item1.setId(1L);
        item1.setQuantity(1);
        item1.setAvailable(true);

        com.shiwu.product.model.ProductCardVO product1 = new com.shiwu.product.model.ProductCardVO();
        product1.setId(1L);
        product1.setPrice(new BigDecimal("99.99"));
        item1.setProduct(product1);

        cartItems.add(item1);

        when(cartDao.findCartItemsByUserId(userId)).thenReturn(cartItems);

        CartOperationResult result = cartService.getCart(userId);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        CartVO cartVO = (CartVO) result.getData();
        assertEquals(1, cartVO.getTotalItems());
        assertEquals(new BigDecimal("99.99"), cartVO.getTotalPrice());
        assertEquals(1, cartVO.getItems().size());
    }

    @Test
    void testGetCart_EmptyCart() {
        Long userId = 1L;
        when(cartDao.findCartItemsByUserId(userId)).thenReturn(new ArrayList<>());

        CartOperationResult result = cartService.getCart(userId);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        CartVO cartVO = (CartVO) result.getData();
        assertEquals(0, cartVO.getTotalItems());
        assertEquals(BigDecimal.ZERO, cartVO.getTotalPrice());
        assertTrue(cartVO.getItems().isEmpty());
    }

    @Test
    void testGetCart_InvalidUserId() {
        CartOperationResult result = cartService.getCart(null);

        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());
    }

    @Test
    void testGetCart_WithUnavailableItems() {
        Long userId = 1L;
        List<CartItemVO> cartItems = new ArrayList<>();

        // 可用商品
        CartItemVO availableItem = new CartItemVO();
        availableItem.setId(1L);
        availableItem.setQuantity(1);
        availableItem.setAvailable(true);

        com.shiwu.product.model.ProductCardVO availableProduct = new com.shiwu.product.model.ProductCardVO();
        availableProduct.setId(1L);
        availableProduct.setPrice(new BigDecimal("50.00"));
        availableItem.setProduct(availableProduct);

        // 不可用商品
        CartItemVO unavailableItem = new CartItemVO();
        unavailableItem.setId(2L);
        unavailableItem.setQuantity(1);
        unavailableItem.setAvailable(false);

        com.shiwu.product.model.ProductCardVO unavailableProduct = new com.shiwu.product.model.ProductCardVO();
        unavailableProduct.setId(2L);
        unavailableProduct.setPrice(new BigDecimal("30.00"));
        unavailableItem.setProduct(unavailableProduct);

        cartItems.add(availableItem);
        cartItems.add(unavailableItem);

        when(cartDao.findCartItemsByUserId(userId)).thenReturn(cartItems);

        CartOperationResult result = cartService.getCart(userId);

        assertTrue(result.isSuccess());
        CartVO cartVO = (CartVO) result.getData();
        assertEquals(2, cartVO.getTotalItems()); // 总商品数包含不可用商品
        assertEquals(new BigDecimal("50.00"), cartVO.getTotalPrice()); // 总价只计算可用商品
    }

    @Test
    void testRemoveFromCart_Success() {
        Long userId = 1L;
        Long productId = 2L;

        when(cartDao.existsInCart(userId, productId)).thenReturn(true);
        when(cartDao.removeFromCart(userId, productId)).thenReturn(true);
        when(cartDao.getCartItemCount(userId)).thenReturn(2);

        CartOperationResult result = cartService.removeFromCart(productId, userId);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(2, data.get("totalItems"));

        verify(cartDao).existsInCart(userId, productId);
        verify(cartDao).removeFromCart(userId, productId);
        verify(cartDao).getCartItemCount(userId);
    }

    @Test
    void testRemoveFromCart_InvalidParams() {
        // 测试空商品ID
        CartOperationResult result = cartService.removeFromCart(null, 1L);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());

        // 测试空用户ID
        result = cartService.removeFromCart(1L, null);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());
    }

    @Test
    void testRemoveFromCart_ItemNotFound() {
        Long userId = 1L;
        Long productId = 2L;

        when(cartDao.existsInCart(userId, productId)).thenReturn(false);

        CartOperationResult result = cartService.removeFromCart(productId, userId);

        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.CART_ITEM_NOT_FOUND, result.getErrorCode());
        assertEquals(CartErrorCode.MSG_CART_ITEM_NOT_FOUND, result.getErrorMessage());
    }

    @Test
    void testRemoveFromCart_DatabaseError() {
        Long userId = 1L;
        Long productId = 2L;

        when(cartDao.existsInCart(userId, productId)).thenReturn(true);
        when(cartDao.removeFromCart(userId, productId)).thenReturn(false);

        CartOperationResult result = cartService.removeFromCart(productId, userId);

        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.REMOVE_FROM_CART_FAILED, result.getErrorCode());
    }

    @Test
    void testBatchRemoveFromCart_Success() {
        Long userId = 1L;
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);

        when(cartDao.batchRemoveFromCart(userId, productIds)).thenReturn(true);
        when(cartDao.getCartItemCount(userId)).thenReturn(2);

        CartOperationResult result = cartService.batchRemoveFromCart(productIds, userId);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(2, data.get("totalItems"));
        assertEquals(3, data.get("removedCount"));
    }

    @Test
    void testBatchRemoveFromCart_InvalidParams() {
        // 测试空列表
        CartOperationResult result = cartService.batchRemoveFromCart(null, 1L);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());

        // 测试空用户ID
        result = cartService.batchRemoveFromCart(Arrays.asList(1L, 2L), null);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());

        // 测试空列表
        result = cartService.batchRemoveFromCart(new ArrayList<>(), 1L);
        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());
    }

    @Test
    void testClearCart_Success() {
        Long userId = 1L;

        when(cartDao.clearCart(userId)).thenReturn(true);

        CartOperationResult result = cartService.clearCart(userId);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(0, data.get("totalItems"));
    }

    @Test
    void testClearCart_InvalidUserId() {
        CartOperationResult result = cartService.clearCart(null);

        assertFalse(result.isSuccess());
        assertEquals(CartErrorCode.INVALID_PARAMS, result.getErrorCode());
    }
}
