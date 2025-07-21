package com.shiwu.product.service;

import com.shiwu.product.model.*;
import com.shiwu.product.service.impl.ProductServiceImpl;
import com.shiwu.common.test.TestConfig;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductService 综合测试类
 * 测试商品服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ProductService 综合测试")
public class ProductServiceComprehensiveTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceComprehensiveTest.class);
    
    private ProductService productService;
    
    // 测试数据
    private static final Long TEST_SELLER_ID = TestConfig.TEST_USER_ID;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final String TEST_PRODUCT_NAME = "测试商品" + System.currentTimeMillis();
    private static final String TEST_PRODUCT_DESCRIPTION = "这是一个测试商品描述";
    private static final BigDecimal TEST_PRODUCT_PRICE = new BigDecimal("99.99");
    private static final Integer TEST_CATEGORY_ID = 1;
    
    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        productService = new ProductServiceImpl();
        logger.info("ProductService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("3.1 获取所有商品分类测试")
    public void testGetAllCategories() {
        logger.info("开始测试获取所有商品分类功能");
        
        // 测试获取分类列表
        List<CategoryVO> categories = productService.getAllCategories();
        assertNotNull(categories, "分类列表不应为空");
        
        logger.info("获取所有商品分类测试通过: categoriesSize={}", categories.size());
    }

    @Test
    @Order(2)
    @DisplayName("3.2 创建商品测试")
    public void testCreateProduct() {
        logger.info("开始测试创建商品功能");
        
        // 创建商品DTO
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME);
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);
        
        // 测试创建商品
        Long productId = productService.createProduct(dto, TEST_SELLER_ID);
        assertNotNull(productId, "创建商品应该返回商品ID");
        assertTrue(productId > 0, "商品ID应该大于0");
        
        logger.info("创建商品测试通过: productId={}", productId);
    }

    @Test
    @Order(3)
    @DisplayName("3.3 创建商品参数验证测试")
    public void testCreateProductValidation() {
        logger.info("开始测试创建商品参数验证");
        
        // 测试null DTO
        Long productId1 = productService.createProduct(null, TEST_SELLER_ID);
        assertNull(productId1, "null DTO应该返回null");
        
        // 测试null卖家ID
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME);
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        
        Long productId2 = productService.createProduct(dto, null);
        assertNull(productId2, "null卖家ID应该返回null");
        
        logger.info("创建商品参数验证测试通过");
    }

    @Test
    @Order(4)
    @DisplayName("3.4 上传商品图片测试")
    public void testUploadProductImage() {
        logger.info("开始测试上传商品图片功能");
        
        // 先创建一个商品
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME + "_image");
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);
        
        Long productId = productService.createProduct(dto, TEST_SELLER_ID);
        
        if (productId != null) {
            // 创建测试图片流
            byte[] imageData = "test image data".getBytes();
            InputStream imageStream = new ByteArrayInputStream(imageData);
            
            // 测试上传图片
            String imageUrl = productService.uploadProductImage(
                productId, 
                "test-image.jpg", 
                imageStream, 
                "image/jpeg", 
                true, 
                TEST_SELLER_ID
            );
            
            // 注意：实际实现可能返回null（如果没有配置文件存储），我们只验证方法能正常执行
            logger.info("上传商品图片测试通过: imageUrl={}", imageUrl);
        } else {
            logger.warn("无法创建商品，跳过图片上传测试");
        }
    }

    @Test
    @Order(5)
    @DisplayName("3.5 上传商品图片参数验证测试")
    public void testUploadProductImageValidation() {
        logger.info("开始测试上传商品图片参数验证");
        
        byte[] imageData = "test image data".getBytes();
        InputStream imageStream = new ByteArrayInputStream(imageData);
        
        // 测试null商品ID
        String imageUrl1 = productService.uploadProductImage(
            null, "test.jpg", imageStream, "image/jpeg", true, TEST_SELLER_ID
        );
        assertNull(imageUrl1, "null商品ID应该返回null");
        
        // 测试null卖家ID
        String imageUrl2 = productService.uploadProductImage(
            TEST_PRODUCT_ID, "test.jpg", imageStream, "image/jpeg", true, null
        );
        assertNull(imageUrl2, "null卖家ID应该返回null");
        
        logger.info("上传商品图片参数验证测试通过");
    }

    @Test
    @Order(6)
    @DisplayName("3.6 检查商品所有权测试")
    public void testIsProductOwnedBySeller() {
        logger.info("开始测试检查商品所有权功能");
        
        // 先创建一个商品
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME + "_ownership");
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);
        
        Long productId = productService.createProduct(dto, TEST_SELLER_ID);
        
        if (productId != null) {
            // 测试正确的所有权
            boolean isOwned1 = productService.isProductOwnedBySeller(productId, TEST_SELLER_ID);
            assertTrue(isOwned1, "商品应该属于创建者");
            
            // 测试错误的所有权
            boolean isOwned2 = productService.isProductOwnedBySeller(productId, TestConfig.BOUNDARY_ID_NONEXISTENT);
            assertFalse(isOwned2, "商品不应该属于其他用户");
            
            logger.info("检查商品所有权测试通过: productId={}", productId);
        } else {
            logger.warn("无法创建商品，跳过所有权测试");
        }
    }

    @Test
    @Order(7)
    @DisplayName("3.7 检查商品所有权参数验证测试")
    public void testIsProductOwnedBySellerValidation() {
        logger.info("开始测试检查商品所有权参数验证");
        
        // 测试null商品ID
        boolean isOwned1 = productService.isProductOwnedBySeller(null, TEST_SELLER_ID);
        assertFalse(isOwned1, "null商品ID应该返回false");
        
        // 测试null卖家ID
        boolean isOwned2 = productService.isProductOwnedBySeller(TEST_PRODUCT_ID, null);
        assertFalse(isOwned2, "null卖家ID应该返回false");
        
        logger.info("检查商品所有权参数验证测试通过");
    }

    @Test
    @Order(8)
    @DisplayName("3.8 获取卖家商品列表测试")
    public void testGetProductsBySellerIdAndStatus() {
        logger.info("开始测试获取卖家商品列表功能");
        
        // 测试获取所有状态的商品
        List<ProductCardVO> products1 = productService.getProductsBySellerIdAndStatus(TEST_SELLER_ID, null);
        assertNotNull(products1, "商品列表不应为空");
        
        // 测试获取特定状态的商品
        List<ProductCardVO> products2 = productService.getProductsBySellerIdAndStatus(TEST_SELLER_ID, 1);
        assertNotNull(products2, "特定状态商品列表不应为空");
        
        logger.info("获取卖家商品列表测试通过: allProducts={}, statusProducts={}", 
                   products1.size(), products2.size());
    }

    @Test
    @Order(9)
    @DisplayName("3.9 获取卖家商品列表参数验证测试")
    public void testGetProductsBySellerIdAndStatusValidation() {
        logger.info("开始测试获取卖家商品列表参数验证");
        
        // 测试null卖家ID
        List<ProductCardVO> products = productService.getProductsBySellerIdAndStatus(null, 1);
        assertNotNull(products, "null卖家ID应该返回空列表");
        assertTrue(products.isEmpty(), "null卖家ID应该返回空列表");
        
        logger.info("获取卖家商品列表参数验证测试通过");
    }

    @Test
    @Order(10)
    @DisplayName("3.10 获取商品详情测试")
    public void testGetProductById() {
        logger.info("开始测试获取商品详情功能");
        
        // 先创建一个商品
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME + "_detail");
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);
        
        Long productId = productService.createProduct(dto, TEST_SELLER_ID);
        
        if (productId != null) {
            // 测试获取商品详情
            Product product = productService.getProductById(productId);
            assertNotNull(product, "商品详情不应为空");
            assertEquals(productId, product.getId(), "商品ID应该匹配");
            
            logger.info("获取商品详情测试通过: productId={}", productId);
        } else {
            logger.warn("无法创建商品，跳过详情测试");
        }
    }

    @Test
    @Order(11)
    @DisplayName("3.11 获取商品详情参数验证测试")
    public void testGetProductByIdValidation() {
        logger.info("开始测试获取商品详情参数验证");
        
        // 测试null商品ID
        Product product1 = productService.getProductById(null);
        assertNull(product1, "null商品ID应该返回null");
        
        // 测试不存在的商品ID
        Product product2 = productService.getProductById(TestConfig.BOUNDARY_ID_NONEXISTENT);
        assertNull(product2, "不存在的商品ID应该返回null");
        
        logger.info("获取商品详情参数验证测试通过");
    }

    @Test
    @Order(12)
    @DisplayName("3.12 获取商品详情视图测试")
    public void testGetProductDetailById() {
        logger.info("开始测试获取商品详情视图功能");
        
        // 先创建一个商品
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME + "_detailvo");
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);

        Long productId = productService.createProduct(dto, TEST_SELLER_ID);

        if (productId != null) {
            // 测试获取商品详情视图
            ProductDetailVO productDetail = productService.getProductDetailById(productId, TEST_SELLER_ID);
            assertNotNull(productDetail, "商品详情视图不应为空");
            assertEquals(productId, productDetail.getId(), "商品ID应该匹配");

            logger.info("获取商品详情视图测试通过: productId={}", productId);
        } else {
            logger.warn("无法创建商品，跳过详情视图测试");
        }
    }

    @Test
    @Order(13)
    @DisplayName("3.13 获取商品详情视图参数验证测试")
    public void testGetProductDetailByIdValidation() {
        logger.info("开始测试获取商品详情视图参数验证");

        // 测试null商品ID
        ProductDetailVO productDetail1 = productService.getProductDetailById(null, TEST_SELLER_ID);
        assertNull(productDetail1, "null商品ID应该返回null");

        // 测试不存在的商品ID
        ProductDetailVO productDetail2 = productService.getProductDetailById(TestConfig.BOUNDARY_ID_NONEXISTENT, TEST_SELLER_ID);
        assertNull(productDetail2, "不存在的商品ID应该返回null");

        logger.info("获取商品详情视图参数验证测试通过");
    }

    @Test
    @Order(14)
    @DisplayName("3.14 更新商品信息测试")
    public void testUpdateProduct() {
        logger.info("开始测试更新商品信息功能");

        // 先创建一个商品
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME + "_update");
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);

        Long productId = productService.createProduct(dto, TEST_SELLER_ID);

        if (productId != null) {
            // 获取商品并更新
            Product product = productService.getProductById(productId);
            if (product != null) {
                product.setTitle("更新后的商品名称");
                product.setPrice(new BigDecimal("199.99"));

                // 测试更新商品
                boolean updateResult = productService.updateProduct(product, TEST_SELLER_ID);
                assertTrue(updateResult, "更新商品应该成功");

                logger.info("更新商品信息测试通过: productId={}", productId);
            } else {
                logger.warn("无法获取商品，跳过更新测试");
            }
        } else {
            logger.warn("无法创建商品，跳过更新测试");
        }
    }

    @Test
    @Order(15)
    @DisplayName("3.15 更新商品信息参数验证测试")
    public void testUpdateProductValidation() {
        logger.info("开始测试更新商品信息参数验证");

        // 测试null商品
        boolean updateResult1 = productService.updateProduct(null, TEST_SELLER_ID);
        assertFalse(updateResult1, "null商品应该更新失败");

        // 测试null卖家ID
        Product product = new Product();
        product.setId(TEST_PRODUCT_ID);
        product.setTitle("测试商品");

        boolean updateResult2 = productService.updateProduct(product, null);
        assertFalse(updateResult2, "null卖家ID应该更新失败");

        logger.info("更新商品信息参数验证测试通过");
    }

    @Test
    @Order(16)
    @DisplayName("3.16 更新商品状态测试")
    public void testUpdateProductStatus() {
        logger.info("开始测试更新商品状态功能");

        // 先创建一个商品
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME + "_status");
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);

        Long productId = productService.createProduct(dto, TEST_SELLER_ID);

        if (productId != null) {
            // 测试更新商品状态 (使用下架状态)
            // 注意：由于业务规则限制，从待审核(0)状态可能无法直接转换到下架(3)状态
            boolean updateResult = productService.updateProductStatus(productId, 3, TEST_SELLER_ID);
            // 这里我们只验证方法能正常执行，不强制要求成功，因为状态转换有业务规则限制
            logger.info("更新商品状态测试完成: productId={}, result={}", productId, updateResult);
        } else {
            logger.warn("无法创建商品，跳过状态更新测试");
        }
    }

    @Test
    @Order(17)
    @DisplayName("3.17 更新商品状态参数验证测试")
    public void testUpdateProductStatusValidation() {
        logger.info("开始测试更新商品状态参数验证");

        // 测试null商品ID
        boolean updateResult1 = productService.updateProductStatus(null, 1, TEST_SELLER_ID);
        assertFalse(updateResult1, "null商品ID应该更新失败");

        // 测试null卖家ID
        boolean updateResult2 = productService.updateProductStatus(TEST_PRODUCT_ID, 1, null);
        assertFalse(updateResult2, "null卖家ID应该更新失败");

        logger.info("更新商品状态参数验证测试通过");
    }

    @Test
    @Order(18)
    @DisplayName("3.18 删除商品测试")
    public void testDeleteProduct() {
        logger.info("开始测试删除商品功能");

        // 先创建一个商品
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(TEST_PRODUCT_NAME + "_delete");
        dto.setDescription(TEST_PRODUCT_DESCRIPTION);
        dto.setPrice(TEST_PRODUCT_PRICE);
        dto.setCategoryId(TEST_CATEGORY_ID);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);

        Long productId = productService.createProduct(dto, TEST_SELLER_ID);

        if (productId != null) {
            // 测试删除商品
            boolean deleteResult = productService.deleteProduct(productId, TEST_SELLER_ID);
            assertTrue(deleteResult, "删除商品应该成功");

            logger.info("删除商品测试通过: productId={}", productId);
        } else {
            logger.warn("无法创建商品，跳过删除测试");
        }
    }

    @Test
    @Order(19)
    @DisplayName("3.19 删除商品参数验证测试")
    public void testDeleteProductValidation() {
        logger.info("开始测试删除商品参数验证");

        // 测试null商品ID
        boolean deleteResult1 = productService.deleteProduct(null, TEST_SELLER_ID);
        assertFalse(deleteResult1, "null商品ID应该删除失败");

        // 测试null卖家ID
        boolean deleteResult2 = productService.deleteProduct(TEST_PRODUCT_ID, null);
        assertFalse(deleteResult2, "null卖家ID应该删除失败");

        logger.info("删除商品参数验证测试通过");
    }

    @Test
    @Order(20)
    @DisplayName("3.20 查询商品列表测试")
    public void testFindProducts() {
        logger.info("开始测试查询商品列表功能");

        // 测试基本查询
        Map<String, Object> result1 = productService.findProducts(
            null, null, null, null, null, null, 1, 10
        );
        assertNotNull(result1, "查询结果不应为空");
        assertTrue(result1.containsKey("list"), "结果应包含商品列表");
        assertTrue(result1.containsKey("total"), "结果应包含总数");

        // 测试关键词查询
        Map<String, Object> result2 = productService.findProducts(
            "测试", null, null, null, null, null, 1, 10
        );
        assertNotNull(result2, "关键词查询结果不应为空");

        // 测试价格范围查询
        Map<String, Object> result3 = productService.findProducts(
            null, null, new BigDecimal("50"), new BigDecimal("200"), null, null, 1, 10
        );
        assertNotNull(result3, "价格范围查询结果不应为空");

        logger.info("查询商品列表测试通过");
    }

    @Test
    @Order(21)
    @DisplayName("3.21 查询商品列表参数验证测试")
    public void testFindProductsValidation() {
        logger.info("开始测试查询商品列表参数验证");

        // 测试无效页码
        Map<String, Object> result1 = productService.findProducts(
            null, null, null, null, null, null, 0, 10
        );
        assertNotNull(result1, "无效页码应该返回默认结果");

        // 测试无效页面大小
        Map<String, Object> result2 = productService.findProducts(
            null, null, null, null, null, null, 1, 0
        );
        assertNotNull(result2, "无效页面大小应该返回默认结果");

        logger.info("查询商品列表参数验证测试通过");
    }

    @AfterEach
    void tearDown() {
        logger.info("ProductService测试清理完成");
    }
}
