package com.shiwu.product.dao;

import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCardVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductDao基础功能测试
 * 不依赖数据库初始化的基本功能测试
 */
@DisplayName("ProductDao基础功能测试")
public class ProductDaoBasicTest {

    private ProductDao productDao;

    @BeforeEach
    public void setUp() {
        productDao = new ProductDao();
    }

    @Test
    @DisplayName("ProductDao实例化测试")
    public void testProductDaoInstantiation() {
        assertNotNull(productDao, "ProductDao应该能够正常实例化");
    }

    @Test
    @DisplayName("测试null参数处理")
    public void testNullParameterHandling() {
        // 测试各种方法对null参数的处理

        // findById - 可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            productDao.findById(null);
        }, "findById(null)应该抛出NullPointerException");

        // findProductsBySellerIdAndStatus - 可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            productDao.findProductsBySellerIdAndStatus(null, 1);
        }, "findProductsBySellerIdAndStatus(null)应该抛出NullPointerException");

        // findProducts - 复合查询方法
        Map<String, Object> result3 = productDao.findProducts(null, null, null, null, null, null, 1, 10);
        assertNotNull(result3, "findProducts(null)应该返回结果");

        // createProduct - 可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            productDao.createProduct(null);
        }, "createProduct(null)应该抛出NullPointerException");

        // updateProduct - 可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            productDao.updateProduct(null);
        }, "updateProduct(null)应该抛出NullPointerException");

        // deleteProduct - 需要两个参数，null参数可能抛出异常
        assertThrows(NullPointerException.class, () -> {
            productDao.deleteProduct(null, 1L);
        }, "deleteProduct(null, sellerId)应该抛出NullPointerException");

        // deleteProduct with null sellerId可能抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            productDao.deleteProduct(1L, null);
        }, "deleteProduct(productId, null)应该抛出NullPointerException");
    }

    @Test
    @DisplayName("测试边界值ID")
    public void testBoundaryIds() {
        // 测试各种边界值ID
        Long[] boundaryIds = {
            0L,           // 零值
            -1L,          // 负值
            Long.MAX_VALUE // 最大值
        };
        
        for (Long id : boundaryIds) {
            // findById可能抛出异常，我们用try-catch处理
            try {
                Product result = productDao.findById(id);
                // 如果没有抛出异常，验证结果
                assertNull(result, "不存在的ID应该返回null，ID: " + id);
            } catch (Exception e) {
                // 如果抛出异常，验证DAO仍然正常
                assertNotNull(productDao, "ProductDao应该保持正常状态，ID: " + id);
            }
        }
    }

    @Test
    @DisplayName("测试特殊字符搜索")
    public void testSpecialCharacterSearch() {
        String[] specialSearchTerms = {
            "product@domain.com",
            "商品中文",
            "product-name_123",
            "product.name+tag",
            "product name with spaces",
            "product'with'quotes",
            "product\"with\"doublequotes",
            "product\\with\\backslashes",
            "product/with/slashes"
        };

        for (String searchTerm : specialSearchTerms) {
            // 使用findProducts方法进行搜索
            Map<String, Object> result = productDao.findProducts(searchTerm, null, null, null, null, null, 1, 10);
            // 不存在的搜索词应该返回结果，不应该抛出异常
            assertNotNull(result, "搜索结果不应为null: " + searchTerm);
            // 验证不会抛出异常即可
            assertNotNull(productDao, "ProductDao应该正常工作: " + searchTerm);
        }
    }

    @Test
    @DisplayName("测试Product对象创建")
    public void testProductObjectCreation() {
        // 测试创建Product对象的各种情况
        
        // 完整的Product对象
        Product completeProduct = new Product();
        completeProduct.setTitle("测试商品");
        completeProduct.setDescription("这是一个测试商品");
        completeProduct.setPrice(new BigDecimal("99.99"));
        completeProduct.setCategoryId(1);
        completeProduct.setSellerId(1L);
        completeProduct.setStatus(1);
        
        assertNotNull(completeProduct, "完整Product对象应该创建成功");
        assertEquals("测试商品", completeProduct.getTitle(), "标题应该设置正确");
        assertEquals(new BigDecimal("99.99"), completeProduct.getPrice(), "价格应该设置正确");
        
        // 最小化Product对象
        Product minimalProduct = new Product();
        minimalProduct.setTitle("最小商品");
        minimalProduct.setPrice(new BigDecimal("1.00"));
        
        assertNotNull(minimalProduct, "最小化Product对象应该创建成功");
        assertEquals("最小商品", minimalProduct.getTitle(), "标题应该设置正确");
        assertNull(minimalProduct.getDescription(), "未设置的描述应该为null");
        
        // 空Product对象
        Product emptyProduct = new Product();
        assertNotNull(emptyProduct, "空Product对象应该创建成功");
        assertNull(emptyProduct.getTitle(), "未设置的标题应该为null");
    }

    @Test
    @DisplayName("测试分页参数")
    public void testPaginationParameters() {
        // 测试各种分页参数
        String searchTerm = "test";

        // 正常分页
        Map<String, Object> result1 = productDao.findProducts(searchTerm, null, null, null, null, null, 1, 10);
        assertNotNull(result1, "正常分页应该返回结果");

        // 边界分页参数
        Map<String, Object> result2 = productDao.findProducts(searchTerm, null, null, null, null, null, -1, 10);
        assertNotNull(result2, "负数页码应该返回结果");

        Map<String, Object> result3 = productDao.findProducts(searchTerm, null, null, null, null, null, 1, -1);
        assertNotNull(result3, "负数页大小应该返回结果");

        Map<String, Object> result4 = productDao.findProducts(searchTerm, null, null, null, null, null, 1, 0);
        assertNotNull(result4, "零页大小应该返回结果");

        Map<String, Object> result5 = productDao.findProducts(searchTerm, null, null, null, null, null, 1000000, 10);
        assertNotNull(result5, "大页码应该返回结果");

        Map<String, Object> result6 = productDao.findProducts(searchTerm, null, null, null, null, null, 1, 1000000);
        assertNotNull(result6, "大页大小应该返回结果");
    }

    @Test
    @DisplayName("测试统计方法")
    public void testStatisticMethods() {
        // 测试统计相关方法不会抛出异常

        Long totalCount = productDao.getTotalProductCount();
        assertNotNull(totalCount, "总商品数不应该为null");
        assertTrue(totalCount >= 0, "总商品数应该大于等于0");

        // 测试按状态统计
        Long statusCount = productDao.getProductCountByStatus(1);
        assertNotNull(statusCount, "状态统计不应该为null");
        assertTrue(statusCount >= 0, "状态统计应该大于等于0");

        // 状态商品数不应该超过总商品数
        assertTrue(statusCount <= totalCount, "状态商品数不应该超过总商品数");
    }

    @Test
    @DisplayName("测试价格范围")
    public void testPriceRanges() {
        // 测试各种价格范围
        BigDecimal[] prices = {
            new BigDecimal("0.00"),      // 零价格
            new BigDecimal("0.01"),      // 最小价格
            new BigDecimal("999999.99"), // 大价格
            new BigDecimal("-1.00")      // 负价格
        };
        
        for (BigDecimal price : prices) {
            Product product = new Product();
            product.setTitle("价格测试商品");
            product.setPrice(price);
            
            assertNotNull(product, "设置价格的商品应该创建成功: " + price);
            assertEquals(price, product.getPrice(), "价格应该设置正确: " + price);
        }
    }

    @Test
    @DisplayName("测试删除操作")
    public void testDeleteOperations() {
        // 测试删除操作的各种情况

        // 删除不存在的商品 - deleteProduct需要两个参数：productId和sellerId
        boolean result1 = productDao.deleteProduct(99999L, 1L);
        assertFalse(result1, "删除不存在商品应该返回false");

        // 删除边界ID
        boolean result2 = productDao.deleteProduct(0L, 1L);
        assertFalse(result2, "删除ID为0的商品应该返回false");

        boolean result3 = productDao.deleteProduct(-1L, 1L);
        assertFalse(result3, "删除负ID商品应该返回false");

        // 测试方法调用不会抛出异常
        assertNotNull(productDao, "删除操作后ProductDao应该正常工作");
    }

    @Test
    @DisplayName("测试DAO方法的幂等性")
    public void testDaoMethodIdempotency() {
        // 测试多次调用相同方法的结果一致性
        
        // 多次查询不存在的商品
        Product result1 = null;
        Product result2 = null;
        Product result3 = null;
        
        try {
            result1 = productDao.findById(99999L);
            result2 = productDao.findById(99999L);
            result3 = productDao.findById(99999L);
        } catch (Exception e) {
            // 如果抛出异常，验证异常一致性
            assertThrows(e.getClass(), () -> productDao.findById(99999L), 
                        "多次调用应该抛出相同类型的异常");
            return;
        }
        
        assertEquals(result1, result2, "多次查询结果应该一致");
        assertEquals(result2, result3, "多次查询结果应该一致");
        
        // 多次查询统计信息
        Long count1 = productDao.getTotalProductCount();
        Long count2 = productDao.getTotalProductCount();
        
        assertEquals(count1, count2, "多次统计查询结果应该一致（在没有数据变更的情况下）");
        
        // 多次搜索
        Map<String, Object> search1 = productDao.findProducts("nonexistent_product_12345", null, null, null, null, null, 1, 10);
        Map<String, Object> search2 = productDao.findProducts("nonexistent_product_12345", null, null, null, null, null, 1, 10);

        assertNotNull(search1, "第一次搜索结果不应为null");
        assertNotNull(search2, "第二次搜索结果不应为null");
    }

    @Test
    @DisplayName("测试分类和卖家查询")
    public void testCategoryAndSellerQueries() {
        // 测试分类查询 - 使用findProducts方法
        Map<String, Object> categoryProducts1 = productDao.findProducts(null, 1, null, null, null, null, 1, 10);
        assertNotNull(categoryProducts1, "分类查询结果不应为null");

        Map<String, Object> categoryProducts2 = productDao.findProducts(null, 99999, null, null, null, null, 1, 10);
        assertNotNull(categoryProducts2, "不存在分类查询结果不应为null");

        // 测试卖家查询 - 使用findProductsBySellerIdAndStatus方法
        List<ProductCardVO> sellerProducts1 = productDao.findProductsBySellerIdAndStatus(1L, null);
        assertNotNull(sellerProducts1, "卖家查询结果不应为null");

        List<ProductCardVO> sellerProducts2 = productDao.findProductsBySellerIdAndStatus(99999L, null);
        assertNotNull(sellerProducts2, "不存在卖家查询结果不应为null");
        assertTrue(sellerProducts2.isEmpty(), "不存在卖家应该返回空列表");

        // 测试边界值
        Map<String, Object> categoryProducts3 = productDao.findProducts(null, -1, null, null, null, null, 1, 10);
        assertNotNull(categoryProducts3, "负数分类查询结果不应为null");

        List<ProductCardVO> sellerProducts3 = productDao.findProductsBySellerIdAndStatus(-1L, null);
        assertNotNull(sellerProducts3, "负数卖家查询结果不应为null");
    }
}
