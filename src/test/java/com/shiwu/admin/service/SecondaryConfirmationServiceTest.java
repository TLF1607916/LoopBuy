package com.shiwu.admin.service;

import com.shiwu.admin.dao.AdminDao;
import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.model.*;
import com.shiwu.admin.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 二次确认Service单元测试
 * 使用Mockito模拟依赖，遵循BCDE原则
 */
@DisplayName("二次确认服务测试")
public class SecondaryConfirmationServiceTest {
    
    @Mock
    private AdminDao adminDao;
    
    @Mock
    private AuditLogDao auditLogDao;
    
    private AdminService adminService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminServiceImpl(adminDao, auditLogDao);
    }
    
    /**
     * 测试二次确认 - 成功情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("二次确认 - 成功")
    public void testVerifySecondaryConfirmation_Success() {
        // Given: 准备测试数据
        Long adminId = 1L;
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String operationCode = "DELETE_USER_PERMANENTLY";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        
        Administrator mockAdmin = createMockAdmin(adminId, "admin", hashedPassword, "SUPER_ADMIN", 1);
        
        // Mock DAO行为
        when(adminDao.findById(adminId)).thenReturn(mockAdmin);
        when(auditLogDao.logAdminLogin(eq(adminId), eq(ipAddress), eq(userAgent), eq(true), anyString()))
            .thenReturn(1L);
        
        // When: 执行二次确认
        SecondaryConfirmationResult result = adminService.verifySecondaryConfirmation(
                adminId, password, operationCode, ipAddress, userAgent);
        
        // Then: 验证结果
        assertNotNull(result, "确认结果不应为空");
        assertTrue(result.getSuccess(), "二次确认应该成功");
        assertNotNull(result.getMessage(), "应该有成功消息");
        assertNull(result.getError(), "成功时不应该有错误");
        assertEquals("永久删除用户账户", result.getData(), "应该返回操作描述");
        
        // 验证DAO方法被调用
        verify(adminDao).findById(adminId);
        verify(auditLogDao).logAdminLogin(eq(adminId), eq(ipAddress), eq(userAgent), eq(true), anyString());
        
        System.out.println("✅ 二次确认成功测试通过");
    }
    
    /**
     * 测试二次确认 - 管理员ID为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("二次确认 - 管理员ID为空")
    public void testVerifySecondaryConfirmation_NullAdminId() {
        // When & Then: 空管理员ID应该返回参数错误
        SecondaryConfirmationResult result = adminService.verifySecondaryConfirmation(
                null, "password", "DELETE_USER_PERMANENTLY", "127.0.0.1", "test");
        
        assertNotNull(result, "确认结果不应为空");
        assertFalse(result.getSuccess(), "确认应该失败");
        assertEquals(SecondaryConfirmationErrorEnum.PARAMETER_ERROR, result.getError(), "应该返回参数错误");
        
        // 验证DAO方法没有被调用
        verify(adminDao, never()).findById(anyLong());
        
        System.out.println("✅ 空管理员ID测试通过");
    }
    
    /**
     * 测试二次确认 - 密码为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("二次确认 - 密码为空")
    public void testVerifySecondaryConfirmation_NullPassword() {
        // When & Then: 空密码应该返回密码为空错误
        SecondaryConfirmationResult result = adminService.verifySecondaryConfirmation(
                1L, null, "DELETE_USER_PERMANENTLY", "127.0.0.1", "test");
        
        assertNotNull(result, "确认结果不应为空");
        assertFalse(result.getSuccess(), "确认应该失败");
        assertEquals(SecondaryConfirmationErrorEnum.PASSWORD_EMPTY, result.getError(), "应该返回密码为空错误");
        
        // 验证DAO方法没有被调用
        verify(adminDao, never()).findById(anyLong());
        
        System.out.println("✅ 空密码测试通过");
    }
    
    /**
     * 测试二次确认 - 操作代码为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("二次确认 - 操作代码为空")
    public void testVerifySecondaryConfirmation_NullOperationCode() {
        // When & Then: 空操作代码应该返回操作代码为空错误
        SecondaryConfirmationResult result = adminService.verifySecondaryConfirmation(
                1L, "password", null, "127.0.0.1", "test");
        
        assertNotNull(result, "确认结果不应为空");
        assertFalse(result.getSuccess(), "确认应该失败");
        assertEquals(SecondaryConfirmationErrorEnum.OPERATION_CODE_EMPTY, result.getError(), "应该返回操作代码为空错误");
        
        // 验证DAO方法没有被调用
        verify(adminDao, never()).findById(anyLong());
        
        System.out.println("✅ 空操作代码测试通过");
    }
    
    /**
     * 测试二次确认 - 管理员不存在
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("二次确认 - 管理员不存在")
    public void testVerifySecondaryConfirmation_AdminNotFound() {
        // Given: 不存在的管理员
        Long adminId = 999L;
        
        when(adminDao.findById(adminId)).thenReturn(null);
        when(auditLogDao.logAdminLogin(eq(adminId), anyString(), anyString(), eq(false), anyString()))
            .thenReturn(1L);
        
        // When: 执行二次确认
        SecondaryConfirmationResult result = adminService.verifySecondaryConfirmation(
                adminId, "password", "DELETE_USER_PERMANENTLY", "127.0.0.1", "test");
        
        // Then: 验证结果
        assertNotNull(result, "确认结果不应为空");
        assertFalse(result.getSuccess(), "确认应该失败");
        assertEquals(SecondaryConfirmationErrorEnum.ADMIN_NOT_FOUND, result.getError(), "应该返回管理员不存在错误");
        
        // 验证DAO方法被调用
        verify(adminDao).findById(adminId);
        verify(auditLogDao).logAdminLogin(eq(adminId), anyString(), anyString(), eq(false), anyString());
        
        System.out.println("✅ 管理员不存在测试通过");
    }
    
    /**
     * 测试二次确认 - 密码错误
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("二次确认 - 密码错误")
    public void testVerifySecondaryConfirmation_WrongPassword() {
        // Given: 存在的管理员但密码错误
        Long adminId = 1L;
        String correctPassword = "admin123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt());
        
        Administrator mockAdmin = createMockAdmin(adminId, "admin", hashedPassword, "SUPER_ADMIN", 1);
        
        when(adminDao.findById(adminId)).thenReturn(mockAdmin);
        when(auditLogDao.logAdminLogin(eq(adminId), anyString(), anyString(), eq(false), anyString()))
            .thenReturn(1L);
        
        // When: 执行二次确认
        SecondaryConfirmationResult result = adminService.verifySecondaryConfirmation(
                adminId, wrongPassword, "DELETE_USER_PERMANENTLY", "127.0.0.1", "test");
        
        // Then: 验证结果
        assertNotNull(result, "确认结果不应为空");
        assertFalse(result.getSuccess(), "确认应该失败");
        assertEquals(SecondaryConfirmationErrorEnum.WRONG_PASSWORD, result.getError(), "应该返回密码错误");
        
        // 验证DAO方法被调用
        verify(adminDao).findById(adminId);
        verify(auditLogDao).logAdminLogin(eq(adminId), anyString(), anyString(), eq(false), anyString());
        
        System.out.println("✅ 密码错误测试通过");
    }
    
    /**
     * 测试二次确认 - 权限不足
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("二次确认 - 权限不足")
    public void testVerifySecondaryConfirmation_InsufficientPermission() {
        // Given: 普通管理员尝试执行超级管理员操作
        Long adminId = 2L;
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String operationCode = "CREATE_ADMIN_ACCOUNT"; // 需要超级管理员权限
        
        Administrator mockAdmin = createMockAdmin(adminId, "moderator", hashedPassword, "ADMIN", 1);
        
        when(adminDao.findById(adminId)).thenReturn(mockAdmin);
        when(auditLogDao.logAdminLogin(eq(adminId), anyString(), anyString(), eq(false), anyString()))
            .thenReturn(1L);
        
        // When: 执行二次确认
        SecondaryConfirmationResult result = adminService.verifySecondaryConfirmation(
                adminId, password, operationCode, "127.0.0.1", "test");
        
        // Then: 验证结果
        assertNotNull(result, "确认结果不应为空");
        assertFalse(result.getSuccess(), "确认应该失败");
        assertEquals(SecondaryConfirmationErrorEnum.INSUFFICIENT_PERMISSION, result.getError(), "应该返回权限不足错误");
        
        // 验证DAO方法被调用
        verify(adminDao).findById(adminId);
        verify(auditLogDao).logAdminLogin(eq(adminId), anyString(), anyString(), eq(false), anyString());
        
        System.out.println("✅ 权限不足测试通过");
    }
    
    /**
     * 测试操作上下文创建
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("操作上下文创建 - 成功")
    public void testCreateOperationContext_Success() {
        // Given: 准备测试数据
        Long adminId = 1L;
        String operationCode = "DELETE_USER_PERMANENTLY";
        Map<String, Object> operationData = new HashMap<>();
        operationData.put("userId", 123L);
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        
        // When: 创建操作上下文
        String operationId = adminService.createOperationContext(
                adminId, operationCode, operationData, ipAddress, userAgent);
        
        // Then: 验证结果
        assertNotNull(operationId, "操作ID不应为空");
        assertFalse(operationId.trim().isEmpty(), "操作ID不应为空字符串");
        
        // 验证可以获取操作上下文
        OperationContext context = adminService.getOperationContext(operationId);
        assertNotNull(context, "操作上下文不应为空");
        assertEquals(adminId, context.getAdminId(), "管理员ID应该匹配");
        assertEquals(operationCode, context.getOperationCode(), "操作代码应该匹配");
        assertEquals(operationData, context.getOperationData(), "操作数据应该匹配");
        assertEquals(ipAddress, context.getIpAddress(), "IP地址应该匹配");
        assertEquals(userAgent, context.getUserAgent(), "用户代理应该匹配");
        assertFalse(context.getConfirmed(), "初始状态应该是未确认");
        assertFalse(context.isExpired(), "新创建的上下文不应该过期");
        
        System.out.println("✅ 操作上下文创建成功测试通过");
    }
    
    /**
     * 测试高风险操作检查
     * BCDE原则中的Design：测试设计逻辑
     */
    @Test
    @DisplayName("高风险操作检查")
    public void testRequiresSecondaryConfirmation() {
        // 测试高风险操作
        assertTrue(adminService.requiresSecondaryConfirmation("DELETE_USER_PERMANENTLY", "SUPER_ADMIN"), 
                  "删除用户操作应该需要二次确认");
        assertTrue(adminService.requiresSecondaryConfirmation("CREATE_ADMIN_ACCOUNT", "SUPER_ADMIN"), 
                  "创建管理员操作应该需要二次确认");
        
        // 测试权限不足的情况
        assertFalse(adminService.requiresSecondaryConfirmation("CREATE_ADMIN_ACCOUNT", "ADMIN"), 
                   "普通管理员不应该能执行创建管理员操作");
        
        // 测试不存在的操作
        assertFalse(adminService.requiresSecondaryConfirmation("NON_EXISTENT_OPERATION", "SUPER_ADMIN"), 
                   "不存在的操作不应该需要二次确认");
        
        // 测试空参数
        assertFalse(adminService.requiresSecondaryConfirmation(null, "SUPER_ADMIN"), 
                   "空操作代码不应该需要二次确认");
        assertFalse(adminService.requiresSecondaryConfirmation("DELETE_USER_PERMANENTLY", null), 
                   "空角色不应该需要二次确认");
        
        System.out.println("✅ 高风险操作检查测试通过");
    }
    
    /**
     * 创建模拟管理员对象
     */
    private Administrator createMockAdmin(Long id, String username, String password, String role, Integer status) {
        Administrator admin = new Administrator();
        admin.setId(id);
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setRole(role);
        admin.setStatus(status);
        admin.setDeleted(false);
        admin.setCreateTime(LocalDateTime.now());
        admin.setLoginCount(0);
        return admin;
    }
}
