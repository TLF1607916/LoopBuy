package com.shiwu.product.service;

import com.shiwu.admin.enums.AuditActionEnum;
import com.shiwu.admin.enums.AuditTargetTypeEnum;
import com.shiwu.admin.service.AuditLogService;
import com.shiwu.product.dao.AdminProductDao;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.product.service.impl.AdminProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminProductService单元测试
 * 测试管理员商品管理功能，包括审计日志记录
 */
@DisplayName("管理员商品服务测试")
public class AdminProductServiceTest {

    @Mock
    private AdminProductDao adminProductDao;
    
    @Mock
    private ProductDao productDao;
    
    @Mock
    private AuditLogService auditLogService;
    
    private AdminProductService adminProductService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用支持依赖注入的构造函数
        adminProductService = new AdminProductServiceImpl(adminProductDao, productDao, auditLogService);
    }
    
    /**
     * 测试审核通过商品成功场景
     */
    @Test
    @DisplayName("审核通过商品成功")
    public void testApproveProductSuccess() {
        // Given: 准备测试数据
        Long productId = 1L;
        Long adminId = 100L;
        String reason = "商品信息完整，符合规范";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setTitle("测试商品");
        mockProduct.setStatus(Product.STATUS_PENDING_REVIEW); // 待审核状态
        mockProduct.setPrice(new BigDecimal("99.99"));
        mockProduct.setCreateTime(LocalDateTime.now());
        
        // Mock DAO行为
        when(productDao.findById(productId)).thenReturn(mockProduct);
        when(adminProductDao.updateProductStatus(productId, Product.STATUS_ONSALE, adminId)).thenReturn(true);
        
        // Mock AuditLogService行为
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行审核通过
        boolean result = adminProductService.approveProduct(productId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertTrue(result, "审核通过商品应该成功");
        
        // 验证DAO方法被调用
        verify(productDao).findById(productId);
        verify(adminProductDao).updateProductStatus(productId, Product.STATUS_ONSALE, adminId);
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.PRODUCT_APPROVE), eq(AuditTargetTypeEnum.PRODUCT), 
                                         eq(productId), contains("审核通过商品"), eq(ipAddress), eq(userAgent), eq(true));
        
        System.out.println("✅ 审核通过商品成功测试通过");
    }

    /**
     * 测试审核通过不存在的商品
     */
    @Test
    @DisplayName("审核通过不存在的商品")
    public void testApproveProductNotFound() {
        // Given: 准备测试数据
        Long productId = 999L;
        Long adminId = 100L;
        String reason = "审核通过";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        when(productDao.findById(productId)).thenReturn(null);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行审核通过
        boolean result = adminProductService.approveProduct(productId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertFalse(result, "审核通过不存在的商品应该失败");
        
        // 验证DAO方法被调用
        verify(productDao).findById(productId);
        verify(adminProductDao, never()).updateProductStatus(any(), any(), any());
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.PRODUCT_APPROVE), eq(AuditTargetTypeEnum.PRODUCT), 
                                         eq(productId), contains("商品不存在"), eq(ipAddress), eq(userAgent), eq(false));
        
        System.out.println("✅ 审核通过不存在商品测试通过");
    }

    /**
     * 测试审核拒绝商品成功场景
     */
    @Test
    @DisplayName("审核拒绝商品成功")
    public void testRejectProductSuccess() {
        // Given: 准备测试数据
        Long productId = 1L;
        Long adminId = 100L;
        String reason = "商品描述不符合规范";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setTitle("测试商品");
        mockProduct.setStatus(Product.STATUS_PENDING_REVIEW); // 待审核状态
        
        // Mock DAO行为
        when(productDao.findById(productId)).thenReturn(mockProduct);
        when(adminProductDao.updateProductStatus(productId, Product.STATUS_DRAFT, adminId)).thenReturn(true);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行审核拒绝
        boolean result = adminProductService.rejectProduct(productId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertTrue(result, "审核拒绝商品应该成功");
        
        // 验证DAO方法被调用
        verify(productDao).findById(productId);
        verify(adminProductDao).updateProductStatus(productId, Product.STATUS_DRAFT, adminId);
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.PRODUCT_REJECT), eq(AuditTargetTypeEnum.PRODUCT), 
                                         eq(productId), contains("审核拒绝商品"), eq(ipAddress), eq(userAgent), eq(true));
        
        System.out.println("✅ 审核拒绝商品成功测试通过");
    }

    /**
     * 测试审核拒绝商品时原因为空
     */
    @Test
    @DisplayName("审核拒绝商品原因为空")
    public void testRejectProductEmptyReason() {
        // Given: 准备测试数据
        Long productId = 1L;
        Long adminId = 100L;
        String reason = ""; // 空原因
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        // When: 执行审核拒绝
        boolean result = adminProductService.rejectProduct(productId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertFalse(result, "审核拒绝商品时原因为空应该失败");
        
        // 验证DAO方法不被调用
        verify(productDao, never()).findById(any());
        verify(adminProductDao, never()).updateProductStatus(any(), any(), any());
        
        System.out.println("✅ 审核拒绝商品原因为空测试通过");
    }

    /**
     * 测试下架商品成功场景
     */
    @Test
    @DisplayName("下架商品成功")
    public void testDelistProductSuccess() {
        // Given: 准备测试数据
        Long productId = 1L;
        Long adminId = 100L;
        String reason = "商品存在质量问题";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setTitle("测试商品");
        mockProduct.setStatus(Product.STATUS_ONSALE); // 在售状态
        
        // Mock DAO行为
        when(productDao.findById(productId)).thenReturn(mockProduct);
        when(adminProductDao.updateProductStatus(productId, Product.STATUS_DELISTED, adminId)).thenReturn(true);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行下架
        boolean result = adminProductService.delistProduct(productId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertTrue(result, "下架商品应该成功");
        
        // 验证DAO方法被调用
        verify(productDao).findById(productId);
        verify(adminProductDao).updateProductStatus(productId, Product.STATUS_DELISTED, adminId);
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.PRODUCT_TAKEDOWN), eq(AuditTargetTypeEnum.PRODUCT), 
                                         eq(productId), contains("下架商品"), eq(ipAddress), eq(userAgent), eq(true));
        
        System.out.println("✅ 下架商品成功测试通过");
    }

    /**
     * 测试删除商品成功场景
     */
    @Test
    @DisplayName("删除商品成功")
    public void testDeleteProductSuccess() {
        // Given: 准备测试数据
        Long productId = 1L;
        Long adminId = 100L;
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setTitle("测试商品");
        mockProduct.setStatus(Product.STATUS_DRAFT);
        
        // Mock DAO行为
        when(productDao.findById(productId)).thenReturn(mockProduct);
        when(adminProductDao.deleteProduct(productId, adminId)).thenReturn(true);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行删除
        boolean result = adminProductService.deleteProduct(productId, adminId, ipAddress, userAgent);
        
        // Then: 验证结果
        assertTrue(result, "删除商品应该成功");
        
        // 验证DAO方法被调用
        verify(productDao).findById(productId);
        verify(adminProductDao).deleteProduct(productId, adminId);
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.PRODUCT_DELETE), eq(AuditTargetTypeEnum.PRODUCT), 
                                         eq(productId), contains("删除商品"), eq(ipAddress), eq(userAgent), eq(true));
        
        System.out.println("✅ 删除商品成功测试通过");
    }

    /**
     * 测试参数验证
     */
    @Test
    @DisplayName("参数验证测试")
    public void testParameterValidation() {
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        // 测试空商品ID
        assertFalse(adminProductService.approveProduct(null, 100L, "reason", ipAddress, userAgent), "空商品ID应该失败");
        
        // 测试空管理员ID
        assertFalse(adminProductService.approveProduct(1L, null, "reason", ipAddress, userAgent), "空管理员ID应该失败");
        
        // 测试审核拒绝时空原因
        assertFalse(adminProductService.rejectProduct(1L, 100L, null, ipAddress, userAgent), "审核拒绝时空原因应该失败");
        
        System.out.println("✅ 参数验证测试通过");
    }
}
