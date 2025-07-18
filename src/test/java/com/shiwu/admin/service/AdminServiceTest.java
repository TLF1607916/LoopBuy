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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminService单元测试
 * 使用Mockito模拟依赖，遵循BCDE原则
 */
@DisplayName("管理员服务测试")
public class AdminServiceTest {
    
    @Mock
    private AdminDao adminDao;
    
    @Mock
    private AuditLogDao auditLogDao;
    
    private AdminService adminService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用支持依赖注入的构造函数
        adminService = new AdminServiceImpl(adminDao, auditLogDao);
    }
    
    /**
     * 测试管理员登录 - 成功情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("管理员登录 - 成功")
    public void testLogin_Success() {
        // Given: 准备测试数据
        String username = "admin";
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        
        Administrator mockAdmin = createMockAdmin(1L, username, hashedPassword, "SUPER_ADMIN", 1);
        
        // Mock DAO行为
        when(adminDao.findByUsername(username)).thenReturn(mockAdmin);
        when(adminDao.updateLastLoginInfo(1L)).thenReturn(true);
        when(auditLogDao.logAdminLogin(eq(1L), eq(ipAddress), eq(userAgent), eq(true), anyString()))
            .thenReturn(1L);
        
        // When: 执行登录
        AdminLoginResult result = adminService.login(username, password, ipAddress, userAgent);
        
        // Then: 验证结果
        assertNotNull(result, "登录结果不应为空");
        assertTrue(result.getSuccess(), "登录应该成功");
        assertNotNull(result.getData(), "登录成功应该返回管理员数据");
        assertNull(result.getError(), "登录成功不应该有错误");
        
        AdminVO adminVO = result.getData();
        assertEquals(1L, adminVO.getId(), "管理员ID应该匹配");
        assertEquals(username, adminVO.getUsername(), "用户名应该匹配");
        assertEquals("SUPER_ADMIN", adminVO.getRole(), "角色应该匹配");
        assertEquals("超级管理员", adminVO.getRoleDescription(), "角色描述应该匹配");
        assertNotNull(adminVO.getToken(), "应该生成JWT令牌");
        
        // 验证DAO方法被调用
        verify(adminDao).findByUsername(username);
        verify(adminDao).updateLastLoginInfo(1L);
        verify(auditLogDao).logAdminLogin(eq(1L), eq(ipAddress), eq(userAgent), eq(true), anyString());
        
        System.out.println("✅ 管理员登录成功测试通过");
    }
    
    /**
     * 测试管理员登录 - 用户名为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("管理员登录 - 用户名为空")
    public void testLogin_NullUsername() {
        // When & Then: 空用户名应该返回参数错误
        AdminLoginResult result = adminService.login(null, "password", "127.0.0.1", "test");
        
        assertNotNull(result, "登录结果不应为空");
        assertFalse(result.getSuccess(), "登录应该失败");
        assertNull(result.getData(), "登录失败不应该返回数据");
        assertEquals(AdminLoginErrorEnum.PARAMETER_ERROR, result.getError(), "应该返回参数错误");
        
        // 验证DAO方法没有被调用
        verify(adminDao, never()).findByUsername(anyString());
        
        System.out.println("✅ 空用户名登录测试通过");
    }
    
    /**
     * 测试管理员登录 - 密码为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("管理员登录 - 密码为空")
    public void testLogin_NullPassword() {
        // When & Then: 空密码应该返回参数错误
        AdminLoginResult result = adminService.login("admin", null, "127.0.0.1", "test");
        
        assertNotNull(result, "登录结果不应为空");
        assertFalse(result.getSuccess(), "登录应该失败");
        assertNull(result.getData(), "登录失败不应该返回数据");
        assertEquals(AdminLoginErrorEnum.PARAMETER_ERROR, result.getError(), "应该返回参数错误");
        
        // 验证DAO方法没有被调用
        verify(adminDao, never()).findByUsername(anyString());
        
        System.out.println("✅ 空密码登录测试通过");
    }
    
    /**
     * 测试管理员登录 - 管理员不存在
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("管理员登录 - 管理员不存在")
    public void testLogin_AdminNotFound() {
        // Given: 不存在的管理员
        String username = "nonexistent";
        String password = "password";
        
        when(adminDao.findByUsername(username)).thenReturn(null);
        when(auditLogDao.logAdminLogin(isNull(), anyString(), anyString(), eq(false), anyString()))
            .thenReturn(1L);
        
        // When: 执行登录
        AdminLoginResult result = adminService.login(username, password, "127.0.0.1", "test");
        
        // Then: 验证结果
        assertNotNull(result, "登录结果不应为空");
        assertFalse(result.getSuccess(), "登录应该失败");
        assertNull(result.getData(), "登录失败不应该返回数据");
        assertEquals(AdminLoginErrorEnum.ADMIN_NOT_FOUND, result.getError(), "应该返回管理员不存在错误");
        
        // 验证DAO方法被调用
        verify(adminDao).findByUsername(username);
        verify(auditLogDao).logAdminLogin(isNull(), anyString(), anyString(), eq(false), anyString());
        
        System.out.println("✅ 管理员不存在登录测试通过");
    }
    
    /**
     * 测试管理员登录 - 密码错误
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("管理员登录 - 密码错误")
    public void testLogin_WrongPassword() {
        // Given: 存在的管理员但密码错误
        String username = "admin";
        String correctPassword = "admin123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt());
        
        Administrator mockAdmin = createMockAdmin(1L, username, hashedPassword, "ADMIN", 1);
        
        when(adminDao.findByUsername(username)).thenReturn(mockAdmin);
        when(auditLogDao.logAdminLogin(eq(1L), anyString(), anyString(), eq(false), anyString()))
            .thenReturn(1L);
        
        // When: 执行登录
        AdminLoginResult result = adminService.login(username, wrongPassword, "127.0.0.1", "test");
        
        // Then: 验证结果
        assertNotNull(result, "登录结果不应为空");
        assertFalse(result.getSuccess(), "登录应该失败");
        assertNull(result.getData(), "登录失败不应该返回数据");
        assertEquals(AdminLoginErrorEnum.WRONG_PASSWORD, result.getError(), "应该返回密码错误");
        
        // 验证DAO方法被调用
        verify(adminDao).findByUsername(username);
        verify(adminDao, never()).updateLastLoginInfo(anyLong());
        verify(auditLogDao).logAdminLogin(eq(1L), anyString(), anyString(), eq(false), anyString());
        
        System.out.println("✅ 密码错误登录测试通过");
    }
    
    /**
     * 测试管理员登录 - 账户被禁用
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("管理员登录 - 账户被禁用")
    public void testLogin_AdminDisabled() {
        // Given: 被禁用的管理员
        String username = "admin";
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        Administrator mockAdmin = createMockAdmin(1L, username, hashedPassword, "ADMIN", 0); // 状态为0（禁用）
        
        when(adminDao.findByUsername(username)).thenReturn(mockAdmin);
        when(auditLogDao.logAdminLogin(eq(1L), anyString(), anyString(), eq(false), anyString()))
            .thenReturn(1L);
        
        // When: 执行登录
        AdminLoginResult result = adminService.login(username, password, "127.0.0.1", "test");
        
        // Then: 验证结果
        assertNotNull(result, "登录结果不应为空");
        assertFalse(result.getSuccess(), "登录应该失败");
        assertNull(result.getData(), "登录失败不应该返回数据");
        assertEquals(AdminLoginErrorEnum.ADMIN_DISABLED, result.getError(), "应该返回账户被禁用错误");
        
        // 验证DAO方法被调用
        verify(adminDao).findByUsername(username);
        verify(adminDao, never()).updateLastLoginInfo(anyLong());
        verify(auditLogDao).logAdminLogin(eq(1L), anyString(), anyString(), eq(false), anyString());
        
        System.out.println("✅ 账户被禁用登录测试通过");
    }
    
    /**
     * 测试权限检查 - 超级管理员
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("权限检查 - 超级管理员")
    public void testHasPermission_SuperAdmin() {
        // Given: 超级管理员
        Long adminId = 1L;
        Administrator mockAdmin = createMockAdmin(adminId, "admin", "hash", "SUPER_ADMIN", 1);
        
        when(adminDao.findById(adminId)).thenReturn(mockAdmin);
        
        // When & Then: 超级管理员应该有所有权限
        assertTrue(adminService.hasPermission(adminId, "ADMIN"), "超级管理员应该有ADMIN权限");
        assertTrue(adminService.hasPermission(adminId, "SUPER_ADMIN"), "超级管理员应该有SUPER_ADMIN权限");
        assertTrue(adminService.hasPermission(adminId, null), "超级管理员应该有任何权限");
        
        System.out.println("✅ 超级管理员权限检查测试通过");
    }
    
    /**
     * 测试权限检查 - 普通管理员
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("权限检查 - 普通管理员")
    public void testHasPermission_RegularAdmin() {
        // Given: 普通管理员
        Long adminId = 2L;
        Administrator mockAdmin = createMockAdmin(adminId, "moderator", "hash", "ADMIN", 1);
        
        when(adminDao.findById(adminId)).thenReturn(mockAdmin);
        
        // When & Then: 普通管理员只有对应权限
        assertTrue(adminService.hasPermission(adminId, "ADMIN"), "普通管理员应该有ADMIN权限");
        assertFalse(adminService.hasPermission(adminId, "SUPER_ADMIN"), "普通管理员不应该有SUPER_ADMIN权限");
        assertTrue(adminService.hasPermission(adminId, null), "普通管理员应该有基本权限");
        
        System.out.println("✅ 普通管理员权限检查测试通过");
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
