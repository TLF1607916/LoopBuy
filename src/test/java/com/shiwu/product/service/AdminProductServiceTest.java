package com.shiwu.product.service;

import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.model.AdminProductQueryDTO;
import com.shiwu.product.dao.AdminProductDao;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductDetailVO;
import com.shiwu.product.service.impl.AdminProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 管理员商品服务测试
 */
public class AdminProductServiceTest {

    @Mock
    private AdminProductDao mockAdminProductDao;

    @Mock
    private ProductDao mockProductDao;

    @Mock
    private AuditLogDao mockAuditLogDao;

    private AdminProductService adminProductService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adminProductService = new AdminProductServiceImpl(mockAdminProductDao, mockProductDao, mockAuditLogDao);
    }

    @Test
    public void testFindProducts_Success() {
        // 准备测试数据
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        queryDTO.setKeyword("test");
        queryDTO.setStatus(1);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(20);

        List<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> product = new HashMap<>();
        product.put("id", 1L);
        product.put("title", "Test Product");
        products.add(product);

        // Mock DAO调用
        when(mockAdminProductDao.findProducts(queryDTO)).thenReturn(products);
        when(mockAdminProductDao.countProducts(queryDTO)).thenReturn(1);

        // 执行测试
        Map<String, Object> result = adminProductService.findProducts(queryDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(products, result.get("products"));
        assertEquals(1, result.get("totalCount"));
        assertEquals(1, result.get("totalPages"));
        assertEquals(1, result.get("currentPage"));
        assertEquals(20, result.get("pageSize"));

        // 验证Mock调用
        verify(mockAdminProductDao).findProducts(queryDTO);
        verify(mockAdminProductDao).countProducts(queryDTO);
    }

    @Test
    public void testFindProducts_NullQuery() {
        // 执行测试
        Map<String, Object> result = adminProductService.findProducts(null);

        // 验证结果
        assertNull(result);

        // 验证Mock调用
        verify(mockAdminProductDao, never()).findProducts(any());
        verify(mockAdminProductDao, never()).countProducts(any());
    }

    @Test
    public void testGetProductDetail_Success() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        ProductDetailVO productDetail = new ProductDetailVO();
        productDetail.setId(productId);
        productDetail.setTitle("Test Product");

        // Mock DAO调用
        when(mockProductDao.findProductDetailById(productId)).thenReturn(productDetail);

        // 执行测试
        Map<String, Object> result = adminProductService.getProductDetail(productId, adminId);

        // 验证结果
        assertNotNull(result);
        assertEquals(productDetail, result.get("product"));

        // 验证Mock调用
        verify(mockProductDao).findProductDetailById(productId);
    }

    @Test
    public void testGetProductDetail_ProductNotFound() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;

        // Mock DAO调用
        when(mockProductDao.findProductDetailById(productId)).thenReturn(null);

        // 执行测试
        Map<String, Object> result = adminProductService.getProductDetail(productId, adminId);

        // 验证结果
        assertNull(result);

        // 验证Mock调用
        verify(mockProductDao).findProductDetailById(productId);
    }

    @Test
    public void testApproveProduct_Success() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        String reason = "审核通过";

        Product product = new Product();
        product.setId(productId);
        product.setStatus(Product.STATUS_PENDING_REVIEW);

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(product);
        when(mockAdminProductDao.updateProductStatus(productId, Product.STATUS_ONSALE, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminProductService.approveProduct(productId, adminId, reason);

        // 验证结果
        assertTrue(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao).updateProductStatus(productId, Product.STATUS_ONSALE, adminId);
        verify(mockAuditLogDao).createAuditLog(any());
    }

    @Test
    public void testApproveProduct_ProductNotFound() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        String reason = "审核通过";

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(null);

        // 执行测试
        boolean result = adminProductService.approveProduct(productId, adminId, reason);

        // 验证结果
        assertFalse(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao, never()).updateProductStatus(any(), any(), any());
        verify(mockAuditLogDao, never()).createAuditLog(any());
    }

    @Test
    public void testApproveProduct_WrongStatus() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        String reason = "审核通过";

        Product product = new Product();
        product.setId(productId);
        product.setStatus(Product.STATUS_ONSALE); // 已经是在售状态

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(product);

        // 执行测试
        boolean result = adminProductService.approveProduct(productId, adminId, reason);

        // 验证结果
        assertFalse(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao, never()).updateProductStatus(any(), any(), any());
        verify(mockAuditLogDao, never()).createAuditLog(any());
    }

    @Test
    public void testRejectProduct_Success() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        String reason = "不符合规范";

        Product product = new Product();
        product.setId(productId);
        product.setStatus(Product.STATUS_PENDING_REVIEW);

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(product);
        when(mockAdminProductDao.updateProductStatus(productId, Product.STATUS_DRAFT, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminProductService.rejectProduct(productId, adminId, reason);

        // 验证结果
        assertTrue(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao).updateProductStatus(productId, Product.STATUS_DRAFT, adminId);
        verify(mockAuditLogDao).createAuditLog(any());
    }

    @Test
    public void testRejectProduct_EmptyReason() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        String reason = ""; // 空原因

        // 执行测试
        boolean result = adminProductService.rejectProduct(productId, adminId, reason);

        // 验证结果
        assertFalse(result);

        // 验证Mock调用
        verify(mockProductDao, never()).findById(any());
        verify(mockAdminProductDao, never()).updateProductStatus(any(), any(), any());
        verify(mockAuditLogDao, never()).createAuditLog(any());
    }

    @Test
    public void testDelistProduct_Success() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        String reason = "违规内容";

        Product product = new Product();
        product.setId(productId);
        product.setStatus(Product.STATUS_ONSALE);

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(product);
        when(mockAdminProductDao.updateProductStatus(productId, Product.STATUS_DELISTED, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminProductService.delistProduct(productId, adminId, reason);

        // 验证结果
        assertTrue(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao).updateProductStatus(productId, Product.STATUS_DELISTED, adminId);
        verify(mockAuditLogDao).createAuditLog(any());
    }

    @Test
    public void testDelistProduct_WrongStatus() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;
        String reason = "违规内容";

        Product product = new Product();
        product.setId(productId);
        product.setStatus(Product.STATUS_DRAFT); // 草稿状态，不能下架

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(product);

        // 执行测试
        boolean result = adminProductService.delistProduct(productId, adminId, reason);

        // 验证结果
        assertFalse(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao, never()).updateProductStatus(any(), any(), any());
        verify(mockAuditLogDao, never()).createAuditLog(any());
    }

    @Test
    public void testDeleteProduct_Success() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;

        Product product = new Product();
        product.setId(productId);
        product.setStatus(Product.STATUS_ONSALE);

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(product);
        when(mockAdminProductDao.deleteProduct(productId, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminProductService.deleteProduct(productId, adminId);

        // 验证结果
        assertTrue(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao).deleteProduct(productId, adminId);
        verify(mockAuditLogDao).createAuditLog(any());
    }

    @Test
    public void testDeleteProduct_ProductNotFound() {
        // 准备测试数据
        Long productId = 1L;
        Long adminId = 1L;

        // Mock DAO调用
        when(mockProductDao.findById(productId)).thenReturn(null);

        // 执行测试
        boolean result = adminProductService.deleteProduct(productId, adminId);

        // 验证结果
        assertFalse(result);

        // 验证Mock调用
        verify(mockProductDao).findById(productId);
        verify(mockAdminProductDao, never()).deleteProduct(any(), any());
        verify(mockAuditLogDao, never()).createAuditLog(any());
    }

    @Test
    public void testNullParameters() {
        // 测试空参数
        assertNull(adminProductService.getProductDetail(null, 1L));
        assertNull(adminProductService.getProductDetail(1L, null));
        assertFalse(adminProductService.approveProduct(null, 1L, "reason"));
        assertFalse(adminProductService.approveProduct(1L, null, "reason"));
        assertFalse(adminProductService.rejectProduct(null, 1L, "reason"));
        assertFalse(adminProductService.rejectProduct(1L, null, "reason"));
        assertFalse(adminProductService.delistProduct(null, 1L, "reason"));
        assertFalse(adminProductService.delistProduct(1L, null, "reason"));
        assertFalse(adminProductService.deleteProduct(null, 1L));
        assertFalse(adminProductService.deleteProduct(1L, null));
    }
}
