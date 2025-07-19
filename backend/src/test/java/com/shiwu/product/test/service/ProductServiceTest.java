package com.shiwu.product.test.service;

import com.shiwu.product.dao.CategoryDao;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Category;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCreateDTO;
import com.shiwu.product.model.ProductDetailVO;
import com.shiwu.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() throws Exception {
        // 初始化mock
        MockitoAnnotations.openMocks(this);
        
        // 通过反射注入mock对象
        productService = new ProductServiceImpl();
        Field productDaoField = ProductServiceImpl.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(productService, productDao);
        
        Field categoryDaoField = ProductServiceImpl.class.getDeclaredField("categoryDao");
        categoryDaoField.setAccessible(true);
        categoryDaoField.set(productService, categoryDao);
    }

    @Test
    public void testCreateProduct_Success() {
        // 准备
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle("测试商品");
        dto.setDescription("测试描述");
        dto.setPrice(new BigDecimal("99.99"));
        dto.setCategoryId(1);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);
        
        Long sellerId = 1L;
        Long expectedProductId = 123L;
        
        // 模拟CategoryDao行为 - 创建一个分类对象并返回
        Category mockCategory = new Category();
        mockCategory.setId(1);
        mockCategory.setName("电子产品");
        when(categoryDao.findById(eq(1))).thenReturn(mockCategory);
        
        // 模拟ProductDao行为
        when(productDao.createProduct(any(Product.class))).thenReturn(expectedProductId);
        
        // 执行
        Long productId = productService.createProduct(dto, sellerId);
        
        // 验证
        assertEquals(expectedProductId, productId);
        verify(categoryDao, times(1)).findById(eq(1));
        verify(productDao, times(1)).createProduct(any(Product.class));
    }

    @Test
    public void testCreateProduct_NullTitle() {
        // 准备
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(null); // 标题为空
        dto.setDescription("测试描述");
        dto.setPrice(new BigDecimal("99.99"));
        dto.setCategoryId(1);
        dto.setAction(ProductCreateDTO.ACTION_SUBMIT_REVIEW);
        
        Long sellerId = 1L;
        
        // 执行
        Long productId = productService.createProduct(dto, sellerId);
        
        // 验证
        assertNull(productId);
        verify(productDao, never()).createProduct(any(Product.class));
    }

    @Test
    public void testCreateProduct_SaveDraft_Success() {
        // 准备
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle("草稿商品");
        // 对于草稿，其他字段可以为空
        dto.setAction(ProductCreateDTO.ACTION_SAVE_DRAFT);
        
        Long sellerId = 1L;
        Long expectedProductId = 124L;
        
        // 模拟ProductDao行为
        when(productDao.createProduct(any(Product.class))).thenReturn(expectedProductId);
        
        // 执行
        Long productId = productService.createProduct(dto, sellerId);
        
        // 验证
        assertEquals(expectedProductId, productId);
        verify(productDao, times(1)).createProduct(any(Product.class));
        // 对于草稿，不应该调用categoryDao.findById
        verify(categoryDao, never()).findById(any());
    }

    @Test
    public void testGetProductDetailById_Success() {
        // 准备
        Long productId = 1L;
        Long currentUserId = 1L;
        
        ProductDetailVO mockProductDetail = new ProductDetailVO();
        mockProductDetail.setId(productId);
        mockProductDetail.setTitle("测试商品");
        mockProductDetail.setPrice(new BigDecimal("99.99"));
        mockProductDetail.setStatus(Product.STATUS_ONSALE);
        mockProductDetail.setSellerId(currentUserId);
        
        when(productDao.findProductDetailById(productId)).thenReturn(mockProductDetail);
        
        // 执行
        ProductDetailVO result = productService.getProductDetailById(productId, currentUserId);
        
        // 验证
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("测试商品", result.getTitle());
    }

    @Test
    public void testGetProductDetailById_NotExist() {
        // 准备
        Long productId = 999L;
        Long currentUserId = 1L;
        
        when(productDao.findProductDetailById(productId)).thenReturn(null);
        
        // 执行
        ProductDetailVO result = productService.getProductDetailById(productId, currentUserId);
        
        // 验证
        assertNull(result);
    }

    @Test
    public void testGetProductDetailById_NoPermission() {
        // 准备
        Long productId = 1L;
        Long currentUserId = 1L; // 当前用户
        Long sellerId = 2L;      // 卖家ID，与当前用户不同
        
        ProductDetailVO mockProductDetail = new ProductDetailVO();
        mockProductDetail.setId(productId);
        mockProductDetail.setStatus(Product.STATUS_DELISTED); // 已下架状态
        mockProductDetail.setSellerId(sellerId);  // 卖家ID与当前用户不同
        
        when(productDao.findProductDetailById(productId)).thenReturn(mockProductDetail);
        
        // 执行
        ProductDetailVO result = productService.getProductDetailById(productId, currentUserId);
        
        // 验证
        assertNull(result); // 无权查看非在售且非自己的商品
    }

    @Test
    public void testFindProducts_Success() {
        // 准备
        String keyword = "测试";
        Integer categoryId = 1;
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        String sortBy = "price";
        String sortDirection = "asc";
        int pageNum = 1;
        int pageSize = 10;
        
        // 模拟查询结果
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("total", 1);
        mockResult.put("list", new Object());
        
        when(productDao.findProducts(
            eq(keyword), eq(categoryId), eq(minPrice), eq(maxPrice),
            eq(sortBy), eq(sortDirection), eq(pageNum), eq(pageSize)
        )).thenReturn(mockResult);
        
        // 执行
        Map<String, Object> result = productService.findProducts(
            keyword, categoryId, minPrice, maxPrice,
            sortBy, sortDirection, pageNum, pageSize
        );
        
        // 验证
        assertNotNull(result);
        assertEquals(1, result.get("total"));
        verify(productDao, times(1)).findProducts(any(), any(), any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    public void testUpdateProductStatus_Success() {
        // 准备
        Long productId = 1L;
        Integer newStatus = Product.STATUS_ONSALE;
        Long sellerId = 1L;
        
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setSellerId(sellerId);
        mockProduct.setStatus(Product.STATUS_DELISTED);
        
        when(productDao.findById(productId)).thenReturn(mockProduct);
        when(productDao.updateProductStatus(productId, newStatus, sellerId)).thenReturn(true);
        
        // 执行
        boolean result = productService.updateProductStatus(productId, newStatus, sellerId);
        
        // 验证
        assertTrue(result);
    }
} 