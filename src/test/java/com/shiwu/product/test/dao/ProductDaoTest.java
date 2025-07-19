package com.shiwu.product.test.dao;

import com.shiwu.common.util.DBUtil;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCardVO;
import com.shiwu.product.model.ProductDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProductDaoTest {

    @InjectMocks
    private ProductDao productDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        // 初始化Mockito
        productDao = new ProductDao();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        
        // 模拟数据库连接和操作
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        }
    }

    @Test
    public void testFindById_WhenProductExists() throws SQLException {
        // 准备
        Long productId = 1L;
        
        // 模拟ResultSet返回数据
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getLong("id")).thenReturn(productId);
        when(mockResultSet.getLong("seller_id")).thenReturn(2L);
        when(mockResultSet.getInt("category_id")).thenReturn(1);
        when(mockResultSet.getString("title")).thenReturn("测试商品");
        when(mockResultSet.getString("description")).thenReturn("测试描述");
        when(mockResultSet.getBigDecimal("price")).thenReturn(new BigDecimal("99.99"));
        when(mockResultSet.getInt("status")).thenReturn(1);
        when(mockResultSet.getObject(eq("create_time"), eq(LocalDateTime.class)))
                .thenReturn(LocalDateTime.now());
        when(mockResultSet.getObject(eq("update_time"), eq(LocalDateTime.class)))
                .thenReturn(LocalDateTime.now());
        when(mockResultSet.getBoolean("is_deleted")).thenReturn(false);
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            // 执行
            Product product = productDao.findById(productId);
            
            // 验证
            assertNotNull(product);
            assertEquals(productId, product.getId());
            assertEquals("测试商品", product.getTitle());
            assertEquals(new BigDecimal("99.99"), product.getPrice());
            assertEquals(1, product.getStatus());
        }
    }

    @Test
    public void testFindById_WhenProductNotExists() throws SQLException {
        // 准备
        Long productId = 999L;
        when(mockResultSet.next()).thenReturn(false);
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            // 执行
            Product product = productDao.findById(productId);
            
            // 验证
            assertNull(product);
        }
    }

    @Test
    public void testFindProductDetailById_WhenProductExists() throws SQLException {
        // 准备
        Long productId = 1L;
        
        // 模拟第一次查询（商品基本信息）
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getLong("id")).thenReturn(productId);
        when(mockResultSet.getString("title")).thenReturn("测试商品");
        when(mockResultSet.getString("description")).thenReturn("测试描述");
        when(mockResultSet.getBigDecimal("price")).thenReturn(new BigDecimal("99.99"));
        when(mockResultSet.getInt("status")).thenReturn(1);
        when(mockResultSet.getObject(eq("create_time"), eq(LocalDateTime.class)))
                .thenReturn(LocalDateTime.now());
        when(mockResultSet.getInt("category_id")).thenReturn(1);
        when(mockResultSet.getString("category_name")).thenReturn("电子产品");
        when(mockResultSet.getLong("seller_id")).thenReturn(2L);
        when(mockResultSet.getString("seller_name")).thenReturn("测试卖家");
        when(mockResultSet.getString("seller_avatar")).thenReturn("avatar.jpg");
        
        // 模拟第二次查询（商品图片）
        when(mockResultSet.getString("image_url")).thenReturn("image1.jpg", "image2.jpg");
        when(mockResultSet.getBoolean("is_main")).thenReturn(true, false);
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            // 执行
            ProductDetailVO productDetail = productDao.findProductDetailById(productId);
            
            // 验证
            assertNotNull(productDetail);
            assertEquals(productId, productDetail.getId());
            assertEquals("测试商品", productDetail.getTitle());
            assertEquals(new BigDecimal("99.99"), productDetail.getPrice());
            assertEquals("电子产品", productDetail.getCategoryName());
            assertEquals("测试卖家", productDetail.getSellerName());
        }
    }
} 