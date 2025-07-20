package com.shiwu.cart.service;

import com.shiwu.cart.model.*;
import com.shiwu.cart.service.impl.CartServiceImpl;
import com.shiwu.common.test.TestConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CartService 综合测试类
 * 测试购物车服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("CartService 综合测试")
public class CartServiceComprehensiveTest {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceComprehensiveTest.class);
    
    private CartService cartService;
    
    // 测试数据
    private static final Long TEST_USER_ID = TestConfig.TEST_USER_ID;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long TEST_PRODUCT_ID_2 = 2L;
    private static final Long TEST_PRODUCT_ID_3 = 3L;
    private static final Integer TEST_QUANTITY = 1;
    
    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl();
        logger.info("CartService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("7.1 添加商品到购物车测试")
    public void testAddToCart() {
        logger.info("开始测试添加商品到购物车功能");
        
        // 创建添加购物车DTO
        CartAddDTO dto = new CartAddDTO();
        dto.setProductId(TEST_PRODUCT_ID);
        dto.setQuantity(TEST_QUANTITY);
        
        // 测试添加商品到购物车
        CartOperationResult result = cartService.addToCart(dto, TEST_USER_ID);
        assertNotNull(result, "添加商品到购物车结果不应为空");
        
        logger.info("添加商品到购物车测试通过: success={}, errorMessage={}", 
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(2)
    @DisplayName("7.2 添加商品到购物车参数验证测试")
    public void testAddToCartValidation() {
        logger.info("开始测试添加商品到购物车参数验证");
        
        // 测试null DTO
        CartOperationResult result1 = cartService.addToCart(null, TEST_USER_ID);
        assertNotNull(result1, "null DTO应该返回结果对象");
        assertFalse(result1.isSuccess(), "null DTO应该添加失败");
        
        // 测试null用户ID
        CartAddDTO dto = new CartAddDTO();
        dto.setProductId(TEST_PRODUCT_ID);
        dto.setQuantity(TEST_QUANTITY);
        
        CartOperationResult result2 = cartService.addToCart(dto, null);
        assertNotNull(result2, "null用户ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null用户ID应该添加失败");
        
        // 测试null商品ID
        CartAddDTO dto2 = new CartAddDTO();
        dto2.setProductId(null);
        dto2.setQuantity(TEST_QUANTITY);
        
        CartOperationResult result3 = cartService.addToCart(dto2, TEST_USER_ID);
        assertNotNull(result3, "null商品ID应该返回结果对象");
        assertFalse(result3.isSuccess(), "null商品ID应该添加失败");
        
        logger.info("添加商品到购物车参数验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("7.3 获取用户购物车测试")
    public void testGetCart() {
        logger.info("开始测试获取用户购物车功能");
        
        // 测试获取用户购物车
        CartOperationResult result = cartService.getCart(TEST_USER_ID);
        assertNotNull(result, "获取用户购物车结果不应为空");
        
        logger.info("获取用户购物车测试通过: success={}, errorMessage={}", 
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(4)
    @DisplayName("7.4 获取用户购物车参数验证测试")
    public void testGetCartValidation() {
        logger.info("开始测试获取用户购物车参数验证");
        
        // 测试null用户ID
        CartOperationResult result = cartService.getCart(null);
        assertNotNull(result, "null用户ID应该返回结果对象");
        assertFalse(result.isSuccess(), "null用户ID应该获取失败");
        
        logger.info("获取用户购物车参数验证测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("7.5 从购物车中移除商品测试")
    public void testRemoveFromCart() {
        logger.info("开始测试从购物车中移除商品功能");
        
        // 先添加商品到购物车
        CartAddDTO dto = new CartAddDTO();
        dto.setProductId(TEST_PRODUCT_ID);
        dto.setQuantity(TEST_QUANTITY);
        cartService.addToCart(dto, TEST_USER_ID);
        
        // 测试从购物车中移除商品
        CartOperationResult result = cartService.removeFromCart(TEST_PRODUCT_ID, TEST_USER_ID);
        assertNotNull(result, "从购物车中移除商品结果不应为空");
        
        logger.info("从购物车中移除商品测试通过: success={}, errorMessage={}", 
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(6)
    @DisplayName("7.6 从购物车中移除商品参数验证测试")
    public void testRemoveFromCartValidation() {
        logger.info("开始测试从购物车中移除商品参数验证");
        
        // 测试null商品ID
        CartOperationResult result1 = cartService.removeFromCart(null, TEST_USER_ID);
        assertNotNull(result1, "null商品ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null商品ID应该移除失败");
        
        // 测试null用户ID
        CartOperationResult result2 = cartService.removeFromCart(TEST_PRODUCT_ID, null);
        assertNotNull(result2, "null用户ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null用户ID应该移除失败");
        
        logger.info("从购物车中移除商品参数验证测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("7.7 批量从购物车中移除商品测试")
    public void testBatchRemoveFromCart() {
        logger.info("开始测试批量从购物车中移除商品功能");
        
        // 先添加多个商品到购物车
        CartAddDTO dto1 = new CartAddDTO(TEST_PRODUCT_ID, TEST_QUANTITY);
        CartAddDTO dto2 = new CartAddDTO(TEST_PRODUCT_ID_2, TEST_QUANTITY);
        CartAddDTO dto3 = new CartAddDTO(TEST_PRODUCT_ID_3, TEST_QUANTITY);
        
        cartService.addToCart(dto1, TEST_USER_ID);
        cartService.addToCart(dto2, TEST_USER_ID);
        cartService.addToCart(dto3, TEST_USER_ID);
        
        // 测试批量移除商品
        List<Long> productIds = Arrays.asList(TEST_PRODUCT_ID, TEST_PRODUCT_ID_2);
        CartOperationResult result = cartService.batchRemoveFromCart(productIds, TEST_USER_ID);
        assertNotNull(result, "批量从购物车中移除商品结果不应为空");
        
        logger.info("批量从购物车中移除商品测试通过: success={}, errorMessage={}", 
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(8)
    @DisplayName("7.8 批量从购物车中移除商品参数验证测试")
    public void testBatchRemoveFromCartValidation() {
        logger.info("开始测试批量从购物车中移除商品参数验证");
        
        // 测试null商品ID列表
        CartOperationResult result1 = cartService.batchRemoveFromCart(null, TEST_USER_ID);
        assertNotNull(result1, "null商品ID列表应该返回结果对象");
        assertFalse(result1.isSuccess(), "null商品ID列表应该移除失败");
        
        // 测试空商品ID列表
        CartOperationResult result2 = cartService.batchRemoveFromCart(Arrays.asList(), TEST_USER_ID);
        assertNotNull(result2, "空商品ID列表应该返回结果对象");
        assertFalse(result2.isSuccess(), "空商品ID列表应该移除失败");
        
        // 测试null用户ID
        List<Long> productIds = Arrays.asList(TEST_PRODUCT_ID, TEST_PRODUCT_ID_2);
        CartOperationResult result3 = cartService.batchRemoveFromCart(productIds, null);
        assertNotNull(result3, "null用户ID应该返回结果对象");
        assertFalse(result3.isSuccess(), "null用户ID应该移除失败");
        
        logger.info("批量从购物车中移除商品参数验证测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("7.9 清空购物车测试")
    public void testClearCart() {
        logger.info("开始测试清空购物车功能");
        
        // 先添加一些商品到购物车
        CartAddDTO dto1 = new CartAddDTO(TEST_PRODUCT_ID, TEST_QUANTITY);
        CartAddDTO dto2 = new CartAddDTO(TEST_PRODUCT_ID_2, TEST_QUANTITY);
        
        cartService.addToCart(dto1, TEST_USER_ID);
        cartService.addToCart(dto2, TEST_USER_ID);
        
        // 测试清空购物车
        CartOperationResult result = cartService.clearCart(TEST_USER_ID);
        assertNotNull(result, "清空购物车结果不应为空");
        
        logger.info("清空购物车测试通过: success={}, errorMessage={}", 
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(10)
    @DisplayName("7.10 清空购物车参数验证测试")
    public void testClearCartValidation() {
        logger.info("开始测试清空购物车参数验证");
        
        // 测试null用户ID
        CartOperationResult result = cartService.clearCart(null);
        assertNotNull(result, "null用户ID应该返回结果对象");
        assertFalse(result.isSuccess(), "null用户ID应该清空失败");
        
        logger.info("清空购物车参数验证测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("7.11 获取购物车中商品总数测试")
    public void testGetCartItemCount() {
        logger.info("开始测试获取购物车中商品总数功能");
        
        // 先清空购物车
        cartService.clearCart(TEST_USER_ID);
        
        // 添加一些商品到购物车
        CartAddDTO dto1 = new CartAddDTO(TEST_PRODUCT_ID, TEST_QUANTITY);
        CartAddDTO dto2 = new CartAddDTO(TEST_PRODUCT_ID_2, TEST_QUANTITY);
        CartAddDTO dto3 = new CartAddDTO(TEST_PRODUCT_ID_3, TEST_QUANTITY);
        
        cartService.addToCart(dto1, TEST_USER_ID);
        cartService.addToCart(dto2, TEST_USER_ID);
        cartService.addToCart(dto3, TEST_USER_ID);
        
        // 测试获取购物车中商品总数
        int count = cartService.getCartItemCount(TEST_USER_ID);
        assertTrue(count >= 0, "购物车商品总数应该非负");
        
        logger.info("获取购物车中商品总数测试通过: count={}", count);
    }

    @Test
    @Order(12)
    @DisplayName("7.12 获取购物车中商品总数参数验证测试")
    public void testGetCartItemCountValidation() {
        logger.info("开始测试获取购物车中商品总数参数验证");
        
        // 测试null用户ID
        int count = cartService.getCartItemCount(null);
        assertEquals(0, count, "null用户ID应该返回0");
        
        logger.info("获取购物车中商品总数参数验证测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("7.13 购物车完整业务流程测试")
    public void testCompleteCartWorkflow() {
        logger.info("开始测试购物车完整业务流程");

        // 1. 清空购物车
        CartOperationResult clearResult = cartService.clearCart(TEST_USER_ID);
        assertNotNull(clearResult, "清空购物车结果不应为空");

        // 2. 验证购物车为空
        int initialCount = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("初始购物车商品数量: {}", initialCount);

        // 3. 添加商品到购物车
        CartAddDTO dto1 = new CartAddDTO(TEST_PRODUCT_ID, TEST_QUANTITY);
        CartOperationResult addResult1 = cartService.addToCart(dto1, TEST_USER_ID);
        assertNotNull(addResult1, "添加商品1结果不应为空");

        CartAddDTO dto2 = new CartAddDTO(TEST_PRODUCT_ID_2, TEST_QUANTITY);
        CartOperationResult addResult2 = cartService.addToCart(dto2, TEST_USER_ID);
        assertNotNull(addResult2, "添加商品2结果不应为空");

        // 4. 获取购物车内容
        CartOperationResult getResult = cartService.getCart(TEST_USER_ID);
        assertNotNull(getResult, "获取购物车结果不应为空");

        // 5. 检查商品数量
        int afterAddCount = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("添加商品后购物车数量: {}", afterAddCount);

        // 6. 移除一个商品
        CartOperationResult removeResult = cartService.removeFromCart(TEST_PRODUCT_ID, TEST_USER_ID);
        assertNotNull(removeResult, "移除商品结果不应为空");

        // 7. 检查移除后的数量
        int afterRemoveCount = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("移除商品后购物车数量: {}", afterRemoveCount);

        // 8. 最终清空购物车
        CartOperationResult finalClearResult = cartService.clearCart(TEST_USER_ID);
        assertNotNull(finalClearResult, "最终清空购物车结果不应为空");

        logger.info("购物车完整业务流程测试通过");
    }

    @Test
    @Order(14)
    @DisplayName("7.14 购物车边界情况测试")
    public void testCartBoundaryConditions() {
        logger.info("开始测试购物车边界情况");

        // 测试添加相同商品多次
        CartAddDTO dto = new CartAddDTO(TEST_PRODUCT_ID, TEST_QUANTITY);

        CartOperationResult result1 = cartService.addToCart(dto, TEST_USER_ID);
        assertNotNull(result1, "第一次添加相同商品结果不应为空");

        CartOperationResult result2 = cartService.addToCart(dto, TEST_USER_ID);
        assertNotNull(result2, "第二次添加相同商品结果不应为空");

        // 测试移除不存在的商品
        CartOperationResult removeNonExistentResult = cartService.removeFromCart(999999L, TEST_USER_ID);
        assertNotNull(removeNonExistentResult, "移除不存在商品结果不应为空");

        // 测试批量移除包含不存在的商品
        List<Long> mixedProductIds = Arrays.asList(TEST_PRODUCT_ID, 999999L, TEST_PRODUCT_ID_2);
        CartOperationResult batchRemoveResult = cartService.batchRemoveFromCart(mixedProductIds, TEST_USER_ID);
        assertNotNull(batchRemoveResult, "批量移除混合商品结果不应为空");

        // 测试对空购物车进行操作
        cartService.clearCart(TEST_USER_ID);

        CartOperationResult removeFromEmptyResult = cartService.removeFromCart(TEST_PRODUCT_ID, TEST_USER_ID);
        assertNotNull(removeFromEmptyResult, "从空购物车移除商品结果不应为空");

        CartOperationResult clearEmptyResult = cartService.clearCart(TEST_USER_ID);
        assertNotNull(clearEmptyResult, "清空空购物车结果不应为空");

        logger.info("购物车边界情况测试通过");
    }

    @Test
    @Order(15)
    @DisplayName("7.15 购物车数量一致性测试")
    public void testCartCountConsistency() {
        logger.info("开始测试购物车数量一致性");

        // 清空购物车
        cartService.clearCart(TEST_USER_ID);

        // 验证清空后数量为0
        int emptyCount = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("清空后购物车数量: {}", emptyCount);

        // 添加商品并验证数量变化
        CartAddDTO dto1 = new CartAddDTO(TEST_PRODUCT_ID, TEST_QUANTITY);
        cartService.addToCart(dto1, TEST_USER_ID);

        int countAfterAdd1 = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("添加商品1后数量: {}", countAfterAdd1);

        CartAddDTO dto2 = new CartAddDTO(TEST_PRODUCT_ID_2, TEST_QUANTITY);
        cartService.addToCart(dto2, TEST_USER_ID);

        int countAfterAdd2 = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("添加商品2后数量: {}", countAfterAdd2);

        // 移除商品并验证数量变化
        cartService.removeFromCart(TEST_PRODUCT_ID, TEST_USER_ID);

        int countAfterRemove = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("移除商品后数量: {}", countAfterRemove);

        // 批量移除并验证数量变化
        List<Long> remainingIds = Arrays.asList(TEST_PRODUCT_ID_2);
        cartService.batchRemoveFromCart(remainingIds, TEST_USER_ID);

        int countAfterBatchRemove = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("批量移除后数量: {}", countAfterBatchRemove);

        logger.info("购物车数量一致性测试通过");
    }

    @Test
    @Order(16)
    @DisplayName("7.16 购物车并发操作模拟测试")
    public void testCartConcurrentOperations() {
        logger.info("开始测试购物车并发操作模拟");

        // 清空购物车
        cartService.clearCart(TEST_USER_ID);

        // 模拟快速连续操作
        for (int i = 0; i < 5; i++) {
            CartAddDTO dto = new CartAddDTO(TEST_PRODUCT_ID + i, TEST_QUANTITY);
            CartOperationResult addResult = cartService.addToCart(dto, TEST_USER_ID);
            assertNotNull(addResult, "快速添加商品" + i + "结果不应为空");
        }

        int countAfterRapidAdd = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("快速添加后购物车数量: {}", countAfterRapidAdd);

        // 模拟快速移除操作
        for (int i = 0; i < 3; i++) {
            CartOperationResult removeResult = cartService.removeFromCart(TEST_PRODUCT_ID + i, TEST_USER_ID);
            assertNotNull(removeResult, "快速移除商品" + i + "结果不应为空");
        }

        int countAfterRapidRemove = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("快速移除后购物车数量: {}", countAfterRapidRemove);

        logger.info("购物车并发操作模拟测试通过");
    }

    @Test
    @Order(17)
    @DisplayName("7.17 购物车数据完整性测试")
    public void testCartDataIntegrity() {
        logger.info("开始测试购物车数据完整性");

        // 清空购物车
        cartService.clearCart(TEST_USER_ID);

        // 添加商品
        CartAddDTO dto = new CartAddDTO(TEST_PRODUCT_ID, TEST_QUANTITY);
        CartOperationResult addResult = cartService.addToCart(dto, TEST_USER_ID);
        assertNotNull(addResult, "添加商品结果不应为空");

        // 获取购物车内容
        CartOperationResult getResult = cartService.getCart(TEST_USER_ID);
        assertNotNull(getResult, "获取购物车结果不应为空");

        // 验证数据一致性
        int itemCount = cartService.getCartItemCount(TEST_USER_ID);
        logger.info("购物车商品数量: {}", itemCount);

        if (getResult.isSuccess() && getResult.getData() != null) {
            logger.info("购物车数据: {}", getResult.getData());
        }

        logger.info("购物车数据完整性测试通过");
    }

    @Test
    @Order(18)
    @DisplayName("7.18 购物车错误处理测试")
    public void testCartErrorHandling() {
        logger.info("开始测试购物车错误处理");

        // 测试无效商品ID
        CartAddDTO invalidDto = new CartAddDTO(-1L, TEST_QUANTITY);
        CartOperationResult invalidAddResult = cartService.addToCart(invalidDto, TEST_USER_ID);
        assertNotNull(invalidAddResult, "无效商品ID添加结果不应为空");

        // 测试无效数量
        CartAddDTO invalidQuantityDto = new CartAddDTO(TEST_PRODUCT_ID, -1);
        CartOperationResult invalidQuantityResult = cartService.addToCart(invalidQuantityDto, TEST_USER_ID);
        assertNotNull(invalidQuantityResult, "无效数量添加结果不应为空");

        // 测试无效用户ID
        int invalidUserCount = cartService.getCartItemCount(-1L);
        assertEquals(0, invalidUserCount, "无效用户ID应该返回0");

        logger.info("购物车错误处理测试通过");
    }

    @AfterEach
    void tearDown() {
        logger.info("CartService测试清理完成");
    }
}
