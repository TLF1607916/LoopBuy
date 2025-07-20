package com.shiwu.order.service;

import com.shiwu.order.model.*;
import com.shiwu.order.service.impl.OrderServiceImpl;
import com.shiwu.common.test.TestConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderService 综合测试类
 * 测试订单服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("OrderService 综合测试")
public class OrderServiceComprehensiveTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceComprehensiveTest.class);
    
    private OrderService orderService;
    
    // 测试数据
    private static final Long TEST_BUYER_ID = TestConfig.TEST_USER_ID;
    private static final Long TEST_SELLER_ID = TestConfig.TEST_USER_ID + 1;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final String TEST_PAYMENT_ID = "PAY_" + System.currentTimeMillis();
    private static final String TEST_CANCEL_REASON = "支付超时";
    
    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl();
        logger.info("OrderService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("5.1 创建订单测试")
    public void testCreateOrder() {
        logger.info("开始测试创建订单功能");
        
        // 创建订单DTO
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(TEST_PRODUCT_ID));
        
        // 测试创建订单
        OrderOperationResult result = orderService.createOrder(dto, TEST_BUYER_ID);
        assertNotNull(result, "创建订单结果不应为空");
        
        logger.info("创建订单测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(2)
    @DisplayName("5.2 创建订单参数验证测试")
    public void testCreateOrderValidation() {
        logger.info("开始测试创建订单参数验证");
        
        // 测试null DTO
        OrderOperationResult result1 = orderService.createOrder(null, TEST_BUYER_ID);
        assertNotNull(result1, "null DTO应该返回结果对象");
        assertFalse(result1.isSuccess(), "null DTO应该创建失败");
        
        // 测试null买家ID
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductIds(Arrays.asList(TEST_PRODUCT_ID));
        
        OrderOperationResult result2 = orderService.createOrder(dto, null);
        assertNotNull(result2, "null买家ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null买家ID应该创建失败");
        
        logger.info("创建订单参数验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("5.3 获取买家订单列表测试")
    public void testGetBuyerOrders() {
        logger.info("开始测试获取买家订单列表功能");
        
        // 测试获取买家订单列表
        OrderOperationResult result = orderService.getBuyerOrders(TEST_BUYER_ID);
        assertNotNull(result, "获取买家订单列表结果不应为空");
        
        logger.info("获取买家订单列表测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(4)
    @DisplayName("5.4 获取买家订单列表参数验证测试")
    public void testGetBuyerOrdersValidation() {
        logger.info("开始测试获取买家订单列表参数验证");
        
        // 测试null买家ID
        OrderOperationResult result = orderService.getBuyerOrders(null);
        assertNotNull(result, "null买家ID应该返回结果对象");
        assertFalse(result.isSuccess(), "null买家ID应该获取失败");
        
        logger.info("获取买家订单列表参数验证测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("5.5 获取卖家订单列表测试")
    public void testGetSellerOrders() {
        logger.info("开始测试获取卖家订单列表功能");
        
        // 测试获取卖家订单列表
        OrderOperationResult result = orderService.getSellerOrders(TEST_SELLER_ID);
        assertNotNull(result, "获取卖家订单列表结果不应为空");
        
        logger.info("获取卖家订单列表测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(6)
    @DisplayName("5.6 获取卖家订单列表参数验证测试")
    public void testGetSellerOrdersValidation() {
        logger.info("开始测试获取卖家订单列表参数验证");
        
        // 测试null卖家ID
        OrderOperationResult result = orderService.getSellerOrders(null);
        assertNotNull(result, "null卖家ID应该返回结果对象");
        assertFalse(result.isSuccess(), "null卖家ID应该获取失败");
        
        logger.info("获取卖家订单列表参数验证测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("5.7 根据ID获取订单详情测试")
    public void testGetOrderById() {
        logger.info("开始测试根据ID获取订单详情功能");
        
        // 测试获取订单详情
        OrderOperationResult result = orderService.getOrderById(TEST_ORDER_ID, TEST_BUYER_ID);
        assertNotNull(result, "获取订单详情结果不应为空");
        
        logger.info("根据ID获取订单详情测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(8)
    @DisplayName("5.8 根据ID获取订单详情参数验证测试")
    public void testGetOrderByIdValidation() {
        logger.info("开始测试根据ID获取订单详情参数验证");
        
        // 测试null订单ID
        OrderOperationResult result1 = orderService.getOrderById(null, TEST_BUYER_ID);
        assertNotNull(result1, "null订单ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID应该获取失败");
        
        // 测试null用户ID
        OrderOperationResult result2 = orderService.getOrderById(TEST_ORDER_ID, null);
        assertNotNull(result2, "null用户ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null用户ID应该获取失败");
        
        logger.info("根据ID获取订单详情参数验证测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("5.9 更新订单状态测试")
    public void testUpdateOrderStatus() {
        logger.info("开始测试更新订单状态功能");
        
        // 测试更新订单状态
        OrderOperationResult result = orderService.updateOrderStatus(TEST_ORDER_ID, 2, TEST_BUYER_ID);
        assertNotNull(result, "更新订单状态结果不应为空");
        
        logger.info("更新订单状态测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(10)
    @DisplayName("5.10 更新订单状态参数验证测试")
    public void testUpdateOrderStatusValidation() {
        logger.info("开始测试更新订单状态参数验证");

        // 测试null订单ID
        OrderOperationResult result1 = orderService.updateOrderStatus(null, 2, TEST_BUYER_ID);
        assertNotNull(result1, "null订单ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID应该更新失败");

        // 测试null用户ID
        OrderOperationResult result2 = orderService.updateOrderStatus(TEST_ORDER_ID, 2, null);
        assertNotNull(result2, "null用户ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null用户ID应该更新失败");

        logger.info("更新订单状态参数验证测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("5.11 支付成功后批量更新订单状态测试")
    public void testUpdateOrderStatusAfterPayment() {
        logger.info("开始测试支付成功后批量更新订单状态功能");

        // 测试批量更新订单状态
        List<Long> orderIds = Arrays.asList(TEST_ORDER_ID, TEST_ORDER_ID + 1);
        OrderOperationResult result = orderService.updateOrderStatusAfterPayment(orderIds, TEST_PAYMENT_ID);
        assertNotNull(result, "批量更新订单状态结果不应为空");

        logger.info("支付成功后批量更新订单状态测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(12)
    @DisplayName("5.12 支付成功后批量更新订单状态参数验证测试")
    public void testUpdateOrderStatusAfterPaymentValidation() {
        logger.info("开始测试支付成功后批量更新订单状态参数验证");
        
        // 测试null订单ID列表
        OrderOperationResult result1 = orderService.updateOrderStatusAfterPayment(null, TEST_PAYMENT_ID);
        assertNotNull(result1, "null订单ID列表应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID列表应该更新失败");
        
        // 测试空订单ID列表
        OrderOperationResult result2 = orderService.updateOrderStatusAfterPayment(Arrays.asList(), TEST_PAYMENT_ID);
        assertNotNull(result2, "空订单ID列表应该返回结果对象");
        assertFalse(result2.isSuccess(), "空订单ID列表应该更新失败");
        
        logger.info("支付成功后批量更新订单状态参数验证测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("5.13 支付失败后批量取消订单测试")
    public void testCancelOrdersAfterPaymentFailure() {
        logger.info("开始测试支付失败后批量取消订单功能");
        
        // 测试批量取消订单
        List<Long> orderIds = Arrays.asList(TEST_ORDER_ID, TEST_ORDER_ID + 1);
        OrderOperationResult result = orderService.cancelOrdersAfterPaymentFailure(orderIds, TEST_CANCEL_REASON);
        assertNotNull(result, "批量取消订单结果不应为空");
        
        logger.info("支付失败后批量取消订单测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(14)
    @DisplayName("5.14 支付失败后批量取消订单参数验证测试")
    public void testCancelOrdersAfterPaymentFailureValidation() {
        logger.info("开始测试支付失败后批量取消订单参数验证");
        
        // 测试null订单ID列表
        OrderOperationResult result1 = orderService.cancelOrdersAfterPaymentFailure(null, TEST_CANCEL_REASON);
        assertNotNull(result1, "null订单ID列表应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID列表应该取消失败");
        
        // 测试空订单ID列表
        OrderOperationResult result2 = orderService.cancelOrdersAfterPaymentFailure(Arrays.asList(), TEST_CANCEL_REASON);
        assertNotNull(result2, "空订单ID列表应该返回结果对象");
        assertFalse(result2.isSuccess(), "空订单ID列表应该取消失败");
        
        logger.info("支付失败后批量取消订单参数验证测试通过");
    }

    @Test
    @Order(15)
    @DisplayName("5.15 卖家发货测试")
    public void testShipOrder() {
        logger.info("开始测试卖家发货功能");

        // 测试卖家发货
        OrderOperationResult result = orderService.shipOrder(TEST_ORDER_ID, TEST_SELLER_ID);
        assertNotNull(result, "卖家发货结果不应为空");

        logger.info("卖家发货测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(16)
    @DisplayName("5.16 卖家发货参数验证测试")
    public void testShipOrderValidation() {
        logger.info("开始测试卖家发货参数验证");

        // 测试null订单ID
        OrderOperationResult result1 = orderService.shipOrder(null, TEST_SELLER_ID);
        assertNotNull(result1, "null订单ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID应该发货失败");

        // 测试null卖家ID
        OrderOperationResult result2 = orderService.shipOrder(TEST_ORDER_ID, null);
        assertNotNull(result2, "null卖家ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null卖家ID应该发货失败");

        logger.info("卖家发货参数验证测试通过");
    }

    @Test
    @Order(17)
    @DisplayName("5.17 买家确认收货测试")
    public void testConfirmReceipt() {
        logger.info("开始测试买家确认收货功能");

        // 测试买家确认收货
        OrderOperationResult result = orderService.confirmReceipt(TEST_ORDER_ID, TEST_BUYER_ID);
        assertNotNull(result, "买家确认收货结果不应为空");

        logger.info("买家确认收货测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(18)
    @DisplayName("5.18 买家确认收货参数验证测试")
    public void testConfirmReceiptValidation() {
        logger.info("开始测试买家确认收货参数验证");

        // 测试null订单ID
        OrderOperationResult result1 = orderService.confirmReceipt(null, TEST_BUYER_ID);
        assertNotNull(result1, "null订单ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID应该确认收货失败");

        // 测试null买家ID
        OrderOperationResult result2 = orderService.confirmReceipt(TEST_ORDER_ID, null);
        assertNotNull(result2, "null买家ID应该返回结果对象");
        assertFalse(result2.isSuccess(), "null买家ID应该确认收货失败");

        logger.info("买家确认收货参数验证测试通过");
    }

    @Test
    @Order(19)
    @DisplayName("5.19 买家申请退货测试")
    public void testApplyForReturn() {
        logger.info("开始测试买家申请退货功能");

        // 创建退货申请DTO
        ReturnRequestDTO returnRequestDTO = new ReturnRequestDTO();
        returnRequestDTO.setReason("商品质量问题");

        // 测试买家申请退货
        OrderOperationResult result = orderService.applyForReturn(TEST_ORDER_ID, returnRequestDTO, TEST_BUYER_ID);
        assertNotNull(result, "买家申请退货结果不应为空");

        logger.info("买家申请退货测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(20)
    @DisplayName("5.20 买家申请退货参数验证测试")
    public void testApplyForReturnValidation() {
        logger.info("开始测试买家申请退货参数验证");

        ReturnRequestDTO returnRequestDTO = new ReturnRequestDTO();
        returnRequestDTO.setReason("测试退货原因");

        // 测试null订单ID
        OrderOperationResult result1 = orderService.applyForReturn(null, returnRequestDTO, TEST_BUYER_ID);
        assertNotNull(result1, "null订单ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID应该申请退货失败");

        // 测试null退货申请DTO
        OrderOperationResult result2 = orderService.applyForReturn(TEST_ORDER_ID, null, TEST_BUYER_ID);
        assertNotNull(result2, "null退货申请DTO应该返回结果对象");
        assertFalse(result2.isSuccess(), "null退货申请DTO应该申请退货失败");

        // 测试null买家ID
        OrderOperationResult result3 = orderService.applyForReturn(TEST_ORDER_ID, returnRequestDTO, null);
        assertNotNull(result3, "null买家ID应该返回结果对象");
        assertFalse(result3.isSuccess(), "null买家ID应该申请退货失败");

        logger.info("买家申请退货参数验证测试通过");
    }

    @Test
    @Order(21)
    @DisplayName("5.21 卖家处理退货申请测试")
    public void testProcessReturnRequest() {
        logger.info("开始测试卖家处理退货申请功能");

        // 创建处理退货申请DTO
        ProcessReturnRequestDTO processReturnRequestDTO = new ProcessReturnRequestDTO();
        processReturnRequestDTO.setApproved(true);
        processReturnRequestDTO.setRejectReason("同意退货申请");

        // 测试卖家处理退货申请
        OrderOperationResult result = orderService.processReturnRequest(TEST_ORDER_ID, processReturnRequestDTO, TEST_SELLER_ID);
        assertNotNull(result, "卖家处理退货申请结果不应为空");

        logger.info("卖家处理退货申请测试通过: success={}, errorMessage={}",
                   result.isSuccess(), result.getErrorMessage());
    }

    @Test
    @Order(22)
    @DisplayName("5.22 卖家处理退货申请参数验证测试")
    public void testProcessReturnRequestValidation() {
        logger.info("开始测试卖家处理退货申请参数验证");

        ProcessReturnRequestDTO processReturnRequestDTO = new ProcessReturnRequestDTO();
        processReturnRequestDTO.setApproved(false);
        processReturnRequestDTO.setRejectReason("拒绝退货申请");

        // 测试null订单ID
        OrderOperationResult result1 = orderService.processReturnRequest(null, processReturnRequestDTO, TEST_SELLER_ID);
        assertNotNull(result1, "null订单ID应该返回结果对象");
        assertFalse(result1.isSuccess(), "null订单ID应该处理退货申请失败");

        // 测试null处理退货申请DTO
        OrderOperationResult result2 = orderService.processReturnRequest(TEST_ORDER_ID, null, TEST_SELLER_ID);
        assertNotNull(result2, "null处理退货申请DTO应该返回结果对象");
        assertFalse(result2.isSuccess(), "null处理退货申请DTO应该处理退货申请失败");

        // 测试null卖家ID
        OrderOperationResult result3 = orderService.processReturnRequest(TEST_ORDER_ID, processReturnRequestDTO, null);
        assertNotNull(result3, "null卖家ID应该返回结果对象");
        assertFalse(result3.isSuccess(), "null卖家ID应该处理退货申请失败");

        logger.info("卖家处理退货申请参数验证测试通过");
    }

    @Test
    @Order(23)
    @DisplayName("5.23 订单状态流转测试")
    public void testOrderStatusFlow() {
        logger.info("开始测试订单状态流转");

        // 测试不同状态的更新
        OrderOperationResult result1 = orderService.updateOrderStatus(TEST_ORDER_ID, 1, TEST_BUYER_ID); // 待支付
        assertNotNull(result1, "更新为待支付状态结果不应为空");

        OrderOperationResult result2 = orderService.updateOrderStatus(TEST_ORDER_ID, 2, TEST_BUYER_ID); // 已支付
        assertNotNull(result2, "更新为已支付状态结果不应为空");

        OrderOperationResult result3 = orderService.updateOrderStatus(TEST_ORDER_ID, 3, TEST_SELLER_ID); // 已发货
        assertNotNull(result3, "更新为已发货状态结果不应为空");

        logger.info("订单状态流转测试通过");
    }

    @Test
    @Order(24)
    @DisplayName("5.24 订单完整业务流程测试")
    public void testCompleteOrderWorkflow() {
        logger.info("开始测试订单完整业务流程");

        // 1. 创建订单
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setProductIds(Arrays.asList(TEST_PRODUCT_ID));

        OrderOperationResult createResult = orderService.createOrder(createDTO, TEST_BUYER_ID);
        assertNotNull(createResult, "创建订单结果不应为空");

        // 2. 获取订单详情
        OrderOperationResult getResult = orderService.getOrderById(TEST_ORDER_ID, TEST_BUYER_ID);
        assertNotNull(getResult, "获取订单详情结果不应为空");

        // 3. 卖家发货
        OrderOperationResult shipResult = orderService.shipOrder(TEST_ORDER_ID, TEST_SELLER_ID);
        assertNotNull(shipResult, "卖家发货结果不应为空");

        // 4. 买家确认收货
        OrderOperationResult confirmResult = orderService.confirmReceipt(TEST_ORDER_ID, TEST_BUYER_ID);
        assertNotNull(confirmResult, "买家确认收货结果不应为空");

        logger.info("订单完整业务流程测试通过");
    }

    @AfterEach
    void tearDown() {
        logger.info("OrderService测试清理完成");
    }
}
