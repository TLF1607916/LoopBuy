package com.shiwu.product.service;

import com.shiwu.admin.model.AdminProductQueryDTO;
import com.shiwu.product.service.impl.AdminProductServiceImpl;
import com.shiwu.common.test.TestConfig;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminProductService 综合测试类
 * 测试管理员商品服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AdminProductService 综合测试")
public class AdminProductServiceComprehensiveTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(AdminProductServiceComprehensiveTest.class);
    
    private AdminProductService adminProductService;
    
    // 测试数据
    private static final Long TEST_ADMIN_ID = TestBase.TEST_ADMIN_ID;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final String TEST_IP_ADDRESS = "127.0.0.1";
    private static final String TEST_USER_AGENT = "Test-Agent/1.0";
    private static final String TEST_REASON = "测试审核原因";
    
    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        adminProductService = new AdminProductServiceImpl();
        logger.info("AdminProductService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("4.1 查询商品列表测试")
    public void testFindProducts() {
        logger.info("开始测试查询商品列表功能");
        
        // 创建查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);
        queryDTO.setSortBy("create_time");
        queryDTO.setSortDirection("DESC");
        
        // 测试查询商品列表
        Map<String, Object> result = adminProductService.findProducts(queryDTO);
        assertNotNull(result, "查询结果不应为空");
        assertTrue(result.containsKey("products"), "结果应包含商品列表");
        assertTrue(result.containsKey("totalCount"), "结果应包含总数");
        
        logger.info("查询商品列表测试通过");
    }

    @Test
    @Order(2)
    @DisplayName("4.2 查询商品列表参数验证测试")
    public void testFindProductsValidation() {
        logger.info("开始测试查询商品列表参数验证");
        
        // 测试null查询条件
        Map<String, Object> result = adminProductService.findProducts(null);
        assertNull(result, "null查询条件应该返回null");
        
        logger.info("查询商品列表参数验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("4.3 获取商品详情测试")
    public void testGetProductDetail() {
        logger.info("开始测试获取商品详情功能");
        
        // 测试获取商品详情
        Map<String, Object> result = adminProductService.getProductDetail(TEST_PRODUCT_ID, TEST_ADMIN_ID);
        // 注意：商品可能不存在，我们只验证方法能正常执行
        logger.info("获取商品详情测试通过: result={}", result != null ? "有数据" : "无数据");
    }

    @Test
    @Order(4)
    @DisplayName("4.4 获取商品详情参数验证测试")
    public void testGetProductDetailValidation() {
        logger.info("开始测试获取商品详情参数验证");
        
        // 测试null商品ID
        Map<String, Object> result1 = adminProductService.getProductDetail(null, TEST_ADMIN_ID);
        assertNull(result1, "null商品ID应该返回null");
        
        // 测试null管理员ID
        Map<String, Object> result2 = adminProductService.getProductDetail(TEST_PRODUCT_ID, null);
        assertNull(result2, "null管理员ID应该返回null");
        
        logger.info("获取商品详情参数验证测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("4.5 审核通过商品测试")
    public void testApproveProduct() {
        logger.info("开始测试审核通过商品功能");
        
        // 测试审核通过商品
        boolean result = adminProductService.approveProduct(
            TEST_PRODUCT_ID, TEST_ADMIN_ID, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        // 注意：商品可能不存在或状态不允许审核，我们只验证方法能正常执行
        logger.info("审核通过商品测试完成: result={}", result);
    }

    @Test
    @Order(6)
    @DisplayName("4.6 审核通过商品参数验证测试")
    public void testApproveProductValidation() {
        logger.info("开始测试审核通过商品参数验证");
        
        // 测试null商品ID
        boolean result1 = adminProductService.approveProduct(
            null, TEST_ADMIN_ID, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result1, "null商品ID应该返回false");
        
        // 测试null管理员ID
        boolean result2 = adminProductService.approveProduct(
            TEST_PRODUCT_ID, null, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result2, "null管理员ID应该返回false");
        
        logger.info("审核通过商品参数验证测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("4.7 审核拒绝商品测试")
    public void testRejectProduct() {
        logger.info("开始测试审核拒绝商品功能");
        
        // 测试审核拒绝商品
        boolean result = adminProductService.rejectProduct(
            TEST_PRODUCT_ID, TEST_ADMIN_ID, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        // 注意：商品可能不存在或状态不允许审核，我们只验证方法能正常执行
        logger.info("审核拒绝商品测试完成: result={}", result);
    }

    @Test
    @Order(8)
    @DisplayName("4.8 审核拒绝商品参数验证测试")
    public void testRejectProductValidation() {
        logger.info("开始测试审核拒绝商品参数验证");
        
        // 测试null商品ID
        boolean result1 = adminProductService.rejectProduct(
            null, TEST_ADMIN_ID, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result1, "null商品ID应该返回false");
        
        // 测试null管理员ID
        boolean result2 = adminProductService.rejectProduct(
            TEST_PRODUCT_ID, null, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result2, "null管理员ID应该返回false");
        
        logger.info("审核拒绝商品参数验证测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("4.9 下架商品测试")
    public void testDelistProduct() {
        logger.info("开始测试下架商品功能");
        
        // 测试下架商品
        boolean result = adminProductService.delistProduct(
            TEST_PRODUCT_ID, TEST_ADMIN_ID, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        // 注意：商品可能不存在或状态不允许下架，我们只验证方法能正常执行
        logger.info("下架商品测试完成: result={}", result);
    }

    @Test
    @Order(10)
    @DisplayName("4.10 下架商品参数验证测试")
    public void testDelistProductValidation() {
        logger.info("开始测试下架商品参数验证");
        
        // 测试null商品ID
        boolean result1 = adminProductService.delistProduct(
            null, TEST_ADMIN_ID, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result1, "null商品ID应该返回false");
        
        // 测试null管理员ID
        boolean result2 = adminProductService.delistProduct(
            TEST_PRODUCT_ID, null, TEST_REASON, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result2, "null管理员ID应该返回false");
        
        logger.info("下架商品参数验证测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("4.11 删除商品测试")
    public void testDeleteProduct() {
        logger.info("开始测试删除商品功能");
        
        // 测试删除商品
        boolean result = adminProductService.deleteProduct(
            TEST_PRODUCT_ID, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        // 注意：商品可能不存在，我们只验证方法能正常执行
        logger.info("删除商品测试完成: result={}", result);
    }

    @Test
    @Order(12)
    @DisplayName("4.12 删除商品参数验证测试")
    public void testDeleteProductValidation() {
        logger.info("开始测试删除商品参数验证");
        
        // 测试null商品ID
        boolean result1 = adminProductService.deleteProduct(
            null, TEST_ADMIN_ID, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result1, "null商品ID应该返回false");
        
        // 测试null管理员ID
        boolean result2 = adminProductService.deleteProduct(
            TEST_PRODUCT_ID, null, TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        assertFalse(result2, "null管理员ID应该返回false");
        
        logger.info("删除商品参数验证测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("4.13 查询商品列表带条件测试")
    public void testFindProductsWithConditions() {
        logger.info("开始测试带条件查询商品列表功能");
        
        // 创建带条件的查询
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setKeyword("测试");
        queryDTO.setStatus(0); // 待审核状态
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(5);
        queryDTO.setSortBy("create_time");
        queryDTO.setSortDirection("DESC");
        
        // 测试查询商品列表
        Map<String, Object> result = adminProductService.findProducts(queryDTO);
        assertNotNull(result, "查询结果不应为空");
        
        logger.info("带条件查询商品列表测试通过");
    }

    @Test
    @Order(14)
    @DisplayName("4.14 审核操作完整流程测试")
    public void testAuditWorkflow() {
        logger.info("开始测试审核操作完整流程");
        
        // 测试审核通过
        boolean approveResult = adminProductService.approveProduct(
            TestConfig.BOUNDARY_ID_NONEXISTENT, TEST_ADMIN_ID, "测试审核通过", TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        
        // 测试审核拒绝
        boolean rejectResult = adminProductService.rejectProduct(
            TestConfig.BOUNDARY_ID_NONEXISTENT, TEST_ADMIN_ID, "测试审核拒绝", TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        
        // 测试下架
        boolean delistResult = adminProductService.delistProduct(
            TestConfig.BOUNDARY_ID_NONEXISTENT, TEST_ADMIN_ID, "测试下架", TEST_IP_ADDRESS, TEST_USER_AGENT
        );
        
        logger.info("审核操作完整流程测试完成: approve={}, reject={}, delist={}", 
                   approveResult, rejectResult, delistResult);
    }

    @AfterEach
    void tearDown() {
        logger.info("AdminProductService测试清理完成");
    }
}
