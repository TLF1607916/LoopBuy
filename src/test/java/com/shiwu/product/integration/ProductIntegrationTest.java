package com.shiwu.integration;

import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCreateDTO;
import com.shiwu.product.model.ProductDetailVO;
import com.shiwu.product.service.ProductService;
import com.shiwu.product.service.impl.ProductServiceImpl;
import com.shiwu.test.util.ProductTestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商品服务集成测试类
 * 注意：此测试需要连接到真实的数据库，请确保测试环境已正确配置
 * 暂时禁用此测试，直到数据库环境准备就绪
 */
@Disabled("暂时禁用集成测试，直到数据库环境准备就绪")
public class ProductIntegrationTest {

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductServiceImpl();
    }

    @Test
    public void testCreateAndRetrieveProduct() {
        // 创建测试商品
        Long sellerId = 1L;
        ProductCreateDTO dto = ProductTestUtil.createTestProductCreateDTO(
            "集成测试商品 " + System.currentTimeMillis(),
            ProductCreateDTO.ACTION_SUBMIT_REVIEW
        );
        
        // 执行创建操作
        Long productId = productService.createProduct(dto, sellerId);
        
        // 验证创建是否成功
        assertNotNull(productId, "商品应该被成功创建");
        
        // 查询并验证商品
        ProductDetailVO productDetail = productService.getProductDetailById(productId, sellerId);
        assertNotNull(productDetail, "应该能够找到创建的商品");
        assertEquals(dto.getTitle(), productDetail.getTitle(), "商品标题应该匹配");
        assertEquals(sellerId, productDetail.getSellerId(), "卖家ID应该匹配");
    }

    @Test
    public void testFindProducts() {
        // 查询所有在售商品
        Map<String, Object> result = productService.findProducts(
            null, null, null, null,
            "create_time", "desc", 1, 10
        );
        
        assertNotNull(result, "查询结果不应为空");
        assertNotNull(result.get("list"), "商品列表不应为空");
        assertTrue(result.containsKey("total"), "结果应包含总数");
        assertTrue(result.containsKey("pageNum"), "结果应包含页码");
        assertTrue(result.containsKey("pageSize"), "结果应包含每页大小");
    }

    @Test
    public void testUpdateProductStatus() {
        // 先创建一个商品
        Long sellerId = 1L;
        ProductCreateDTO dto = ProductTestUtil.createTestProductCreateDTO(
            "测试状态更新商品 " + System.currentTimeMillis(),
            ProductCreateDTO.ACTION_SUBMIT_REVIEW
        );
        
        Long productId = productService.createProduct(dto, sellerId);
        assertNotNull(productId, "商品应该被成功创建");
        
        // 尝试更改状态为下架
        boolean updateResult = productService.updateProductStatus(productId, Product.STATUS_DELISTED, sellerId);
        assertTrue(updateResult, "状态更新应该成功");
        
        // 验证状态是否更新成功
        ProductDetailVO productDetail = productService.getProductDetailById(productId, sellerId);
        assertNotNull(productDetail, "应该能够找到创建的商品");
        assertEquals(Product.STATUS_DELISTED, productDetail.getStatus(), "商品状态应该已更新为下架");
    }
} 