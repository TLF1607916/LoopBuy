package com.shiwu.product.dao;

import com.shiwu.admin.model.AdminProductQueryDTO;
import com.shiwu.common.util.DBUtil;
import com.shiwu.product.model.Product;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理员商品DAO测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminProductDaoTest {

    private static AdminProductDao adminProductDao;
    private static Long testProductId1;
    private static Long testProductId2;
    private static Long testSellerId = 1L;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        adminProductDao = new AdminProductDao();
        
        // 创建测试商品
        createTestProducts();
    }

    @AfterAll
    public static void tearDownClass() throws SQLException {
        // 清理测试数据
        cleanupTestData();
    }

    private static void createTestProducts() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();

            // 首先创建一个测试分类
            String createCategorySql = "INSERT INTO category (name, parent_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name)";
            pstmt = conn.prepareStatement(createCategorySql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, "测试分类");
            pstmt.setInt(2, 0);
            pstmt.executeUpdate();

            java.sql.ResultSet categoryRs = pstmt.getGeneratedKeys();
            int categoryId = 1; // 默认值
            if (categoryRs.next()) {
                categoryId = categoryRs.getInt(1);
            } else {
                // 如果没有生成新ID，查询现有的
                categoryRs.close();
                pstmt.close();
                pstmt = conn.prepareStatement("SELECT id FROM category WHERE name = '测试分类' LIMIT 1");
                categoryRs = pstmt.executeQuery();
                if (categoryRs.next()) {
                    categoryId = categoryRs.getInt("id");
                }
            }
            categoryRs.close();
            pstmt.close();

            // 创建测试商品1
            String sql = "INSERT INTO product (title, description, price, status, seller_id, category_id, create_time, update_time, is_deleted) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, "测试商品1");
            pstmt.setString(2, "测试商品描述1");
            pstmt.setBigDecimal(3, new BigDecimal("99.99"));
            pstmt.setInt(4, Product.STATUS_PENDING_REVIEW);
            pstmt.setLong(5, testSellerId);
            pstmt.setInt(6, categoryId); // 使用查询到的分类ID
            pstmt.setObject(7, LocalDateTime.now());
            pstmt.setObject(8, LocalDateTime.now());
            pstmt.setBoolean(9, false);

            pstmt.executeUpdate();
            java.sql.ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                testProductId1 = rs.getLong(1);
            }

            // 创建测试商品2
            pstmt.setString(1, "测试商品2");
            pstmt.setString(2, "测试商品描述2");
            pstmt.setBigDecimal(3, new BigDecimal("199.99"));
            pstmt.setInt(4, Product.STATUS_ONSALE);
            pstmt.setLong(5, testSellerId);
            pstmt.setInt(6, categoryId); // 使用查询到的分类ID
            pstmt.setObject(7, LocalDateTime.now());
            pstmt.setObject(8, LocalDateTime.now());
            pstmt.setBoolean(9, false);

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                testProductId2 = rs.getLong(1);
            }
            
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private static void cleanupTestData() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM product WHERE id IN (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, testProductId1);
            pstmt.setLong(2, testProductId2);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    @Test
    @Order(1)
    public void testFindProducts_All() {
        // 准备查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行查询
        List<Map<String, Object>> products = adminProductDao.findProducts(queryDTO);

        // 验证结果
        assertNotNull(products);
        assertTrue(products.size() >= 2); // 至少包含我们创建的两个测试商品

        // 验证商品数据结构
        Map<String, Object> product = products.get(0);
        assertNotNull(product.get("id"));
        assertNotNull(product.get("title"));
        assertNotNull(product.get("price"));
        assertNotNull(product.get("status"));
        assertNotNull(product.get("statusText"));
        assertNotNull(product.get("createTime"));
        assertNotNull(product.get("updateTime"));
        assertNotNull(product.get("sellerId"));
    }

    @Test
    @Order(2)
    public void testFindProducts_WithKeyword() {
        // 准备查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setKeyword("测试商品1");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行查询
        List<Map<String, Object>> products = adminProductDao.findProducts(queryDTO);

        // 验证结果
        assertNotNull(products);
        assertTrue(products.size() >= 1);
        
        // 验证包含关键词的商品
        boolean found = products.stream()
                .anyMatch(p -> p.get("title").toString().contains("测试商品1"));
        assertTrue(found);
    }

    @Test
    @Order(3)
    public void testFindProducts_WithStatus() {
        // 准备查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setStatus(Product.STATUS_PENDING_REVIEW);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行查询
        List<Map<String, Object>> products = adminProductDao.findProducts(queryDTO);

        // 验证结果
        assertNotNull(products);
        
        // 验证所有商品都是待审核状态
        for (Map<String, Object> product : products) {
            assertEquals(Product.STATUS_PENDING_REVIEW, product.get("status"));
        }
    }

    @Test
    @Order(4)
    public void testFindProducts_WithSellerId() {
        // 准备查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setSellerId(testSellerId);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行查询
        List<Map<String, Object>> products = adminProductDao.findProducts(queryDTO);

        // 验证结果
        assertNotNull(products);
        assertTrue(products.size() >= 2); // 至少包含我们创建的两个测试商品
        
        // 验证所有商品都属于指定卖家
        for (Map<String, Object> product : products) {
            assertEquals(testSellerId, product.get("sellerId"));
        }
    }

    @Test
    @Order(5)
    public void testCountProducts() {
        // 准备查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();

        // 执行统计
        int count = adminProductDao.countProducts(queryDTO);

        // 验证结果
        assertTrue(count >= 2); // 至少包含我们创建的两个测试商品
    }

    @Test
    @Order(6)
    public void testCountProducts_WithConditions() {
        // 准备查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setKeyword("测试商品");
        queryDTO.setSellerId(testSellerId);

        // 执行统计
        int count = adminProductDao.countProducts(queryDTO);

        // 验证结果
        assertTrue(count >= 2); // 至少包含我们创建的两个测试商品
    }

    @Test
    @Order(7)
    public void testUpdateProductStatus_Success() {
        // 执行状态更新
        boolean result = adminProductDao.updateProductStatus(testProductId1, Product.STATUS_ONSALE, 1L);

        // 验证结果
        assertTrue(result);
    }

    @Test
    @Order(8)
    public void testUpdateProductStatus_NonExistentProduct() {
        // 执行状态更新 - 不存在的商品
        boolean result = adminProductDao.updateProductStatus(99999L, Product.STATUS_ONSALE, 1L);

        // 验证结果
        assertFalse(result);
    }

    @Test
    @Order(9)
    public void testDeleteProduct_Success() {
        // 执行软删除
        boolean result = adminProductDao.deleteProduct(testProductId2, 1L);

        // 验证结果
        assertTrue(result);
    }

    @Test
    @Order(10)
    public void testDeleteProduct_NonExistentProduct() {
        // 执行软删除 - 不存在的商品
        boolean result = adminProductDao.deleteProduct(99999L, 1L);

        // 验证结果
        assertFalse(result);
    }

    @Test
    @Order(11)
    public void testDeleteProduct_AlreadyDeleted() {
        // 再次删除已删除的商品
        boolean result = adminProductDao.deleteProduct(testProductId2, 1L);

        // 验证结果
        assertFalse(result); // 已删除的商品不能再次删除
    }

    @Test
    @Order(12)
    public void testFindProducts_ExcludeDeleted() {
        // 准备查询条件
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setSellerId(testSellerId);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行查询
        List<Map<String, Object>> products = adminProductDao.findProducts(queryDTO);

        // 验证结果 - 不应该包含已删除的商品
        boolean foundDeleted = products.stream()
                .anyMatch(p -> testProductId2.equals(p.get("id")));
        assertFalse(foundDeleted);
    }

    @Test
    @Order(13)
    public void testFindProducts_Pagination() {
        // 测试分页 - 第一页
        AdminProductQueryDTO queryDTO1 = new AdminProductQueryDTO();
        queryDTO1.setPageNum(1);
        queryDTO1.setPageSize(1);

        List<Map<String, Object>> page1 = adminProductDao.findProducts(queryDTO1);
        assertNotNull(page1);
        assertTrue(page1.size() <= 1);

        // 测试分页 - 第二页
        AdminProductQueryDTO queryDTO2 = new AdminProductQueryDTO();
        queryDTO2.setPageNum(2);
        queryDTO2.setPageSize(1);

        List<Map<String, Object>> page2 = adminProductDao.findProducts(queryDTO2);
        assertNotNull(page2);
        
        // 如果有足够的数据，两页的内容应该不同
        if (!page1.isEmpty() && !page2.isEmpty()) {
            assertNotEquals(page1.get(0).get("id"), page2.get(0).get("id"));
        }
    }

    @Test
    @Order(14)
    public void testFindProducts_Sorting() {
        // 测试按创建时间降序排序
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setSortBy("create_time");
        queryDTO.setSortDirection("DESC");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        List<Map<String, Object>> products = adminProductDao.findProducts(queryDTO);
        assertNotNull(products);
        
        // 验证排序（如果有多个商品）
        if (products.size() > 1) {
            LocalDateTime firstTime = (LocalDateTime) products.get(0).get("createTime");
            LocalDateTime secondTime = (LocalDateTime) products.get(1).get("createTime");
            assertTrue(firstTime.isAfter(secondTime) || firstTime.isEqual(secondTime));
        }
    }
}
