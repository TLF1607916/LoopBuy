package com.shiwu.cart.dao;

import com.shiwu.cart.model.CartItem;
import com.shiwu.cart.model.CartItemVO;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CartDao空指针修复验证测试
 */
@DisplayName("CartDao空指针修复验证测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartDaoNullFixTest {

    private CartDao cartDao;

    @BeforeEach
    public void setUp() {
        cartDao = new CartDao();
    }

    @Test
    @Order(1)
    @DisplayName("1.1 addToCart null参数测试")
    public void testAddToCartNullParameters() {
        // 测试null CartItem
        boolean result1 = cartDao.addToCart(null);
        assertFalse(result1, "addToCart(null)应该返回false");

        // 测试null userId
        CartItem item1 = new CartItem();
        item1.setUserId(null);
        item1.setProductId(1L);
        item1.setQuantity(1);
        boolean result2 = cartDao.addToCart(item1);
        assertFalse(result2, "userId为null应该返回false");

        // 测试null productId
        CartItem item2 = new CartItem();
        item2.setUserId(1L);
        item2.setProductId(null);
        item2.setQuantity(1);
        boolean result3 = cartDao.addToCart(item2);
        assertFalse(result3, "productId为null应该返回false");

        // 测试null quantity
        CartItem item3 = new CartItem();
        item3.setUserId(1L);
        item3.setProductId(1L);
        item3.setQuantity(null);
        boolean result4 = cartDao.addToCart(item3);
        assertFalse(result4, "quantity为null应该返回false");

        // 测试无效quantity
        CartItem item4 = new CartItem();
        item4.setUserId(1L);
        item4.setProductId(1L);
        item4.setQuantity(0);
        boolean result5 = cartDao.addToCart(item4);
        assertFalse(result5, "quantity为0应该返回false");

        System.out.println("✅ addToCart null参数测试通过");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 existsInCart null参数测试")
    public void testExistsInCartNullParameters() {
        // 测试null userId
        boolean result1 = cartDao.existsInCart(null, 1L);
        assertFalse(result1, "userId为null应该返回false");

        // 测试null productId
        boolean result2 = cartDao.existsInCart(1L, null);
        assertFalse(result2, "productId为null应该返回false");

        // 测试都为null
        boolean result3 = cartDao.existsInCart(null, null);
        assertFalse(result3, "userId和productId都为null应该返回false");

        System.out.println("✅ existsInCart null参数测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("1.3 findCartItemsByUserId null参数测试")
    public void testFindCartItemsByUserIdNullParameters() {
        // 测试null userId
        List<CartItemVO> result = cartDao.findCartItemsByUserId(null);
        assertNotNull(result, "null userId应该返回空列表而不是null");
        assertTrue(result.isEmpty(), "null userId应该返回空列表");

        System.out.println("✅ findCartItemsByUserId null参数测试通过");
    }

    @Test
    @Order(4)
    @DisplayName("1.4 removeFromCart null参数测试")
    public void testRemoveFromCartNullParameters() {
        // 测试null userId
        boolean result1 = cartDao.removeFromCart(null, 1L);
        assertFalse(result1, "userId为null应该返回false");

        // 测试null productId
        boolean result2 = cartDao.removeFromCart(1L, null);
        assertFalse(result2, "productId为null应该返回false");

        // 测试都为null
        boolean result3 = cartDao.removeFromCart(null, null);
        assertFalse(result3, "userId和productId都为null应该返回false");

        System.out.println("✅ removeFromCart null参数测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("1.5 batchRemoveFromCart null参数测试")
    public void testBatchRemoveFromCartNullParameters() {
        // 测试null userId
        boolean result1 = cartDao.batchRemoveFromCart(null, Arrays.asList(1L, 2L));
        assertFalse(result1, "userId为null应该返回false");

        // 测试null productIds
        boolean result2 = cartDao.batchRemoveFromCart(1L, null);
        assertTrue(result2, "productIds为null应该返回true");

        // 测试空productIds
        boolean result3 = cartDao.batchRemoveFromCart(1L, Arrays.asList());
        assertTrue(result3, "productIds为空列表应该返回true");

        System.out.println("✅ batchRemoveFromCart null参数测试通过");
    }

    @Test
    @Order(6)
    @DisplayName("1.6 clearCart null参数测试")
    public void testClearCartNullParameters() {
        // 测试null userId
        boolean result = cartDao.clearCart(null);
        assertFalse(result, "userId为null应该返回false");

        System.out.println("✅ clearCart null参数测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("1.7 getCartItemCount null参数测试")
    public void testGetCartItemCountNullParameters() {
        // 测试null userId
        int result = cartDao.getCartItemCount(null);
        assertEquals(0, result, "userId为null应该返回0");

        System.out.println("✅ getCartItemCount null参数测试通过");
    }

    @AfterEach
    public void tearDown() {
        assertNotNull(cartDao, "测试后DAO应该保持正常状态");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("🎉 CartDao空指针修复验证测试全部通过！");
    }
}
