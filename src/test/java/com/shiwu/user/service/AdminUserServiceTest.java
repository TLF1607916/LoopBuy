package com.shiwu.user.service;

import com.shiwu.admin.enums.AuditActionEnum;
import com.shiwu.admin.enums.AuditTargetTypeEnum;
import com.shiwu.admin.service.AuditLogService;
import com.shiwu.user.dao.AdminUserDao;
import com.shiwu.user.dao.UserDao;
import com.shiwu.user.model.User;
import com.shiwu.user.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminUserService单元测试
 * 测试管理员用户管理功能，包括审计日志记录
 */
@DisplayName("管理员用户服务测试")
public class AdminUserServiceTest {

    @Mock
    private AdminUserDao adminUserDao;
    
    @Mock
    private UserDao userDao;
    
    @Mock
    private AuditLogService auditLogService;
    
    private AdminUserService adminUserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用支持依赖注入的构造函数
        adminUserService = new AdminUserServiceImpl(adminUserDao, userDao, auditLogService);
    }
    
    /**
     * 测试封禁用户成功场景
     */
    @Test
    @DisplayName("封禁用户成功")
    public void testBanUserSuccess() {
        // Given: 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String reason = "违规行为";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setStatus(0); // 正常状态
        mockUser.setCreateTime(LocalDateTime.now());
        
        // Mock DAO行为
        when(userDao.findById(userId)).thenReturn(mockUser);
        when(adminUserDao.updateUserStatus(userId, 1, adminId)).thenReturn(true);
        
        // Mock AuditLogService行为
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行封禁
        boolean result = adminUserService.banUser(userId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertTrue(result, "封禁用户应该成功");
        
        // 验证DAO方法被调用
        verify(userDao).findById(userId);
        verify(adminUserDao).updateUserStatus(userId, 1, adminId);
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.USER_BAN), eq(AuditTargetTypeEnum.USER), 
                                         eq(userId), contains("封禁用户"), eq(ipAddress), eq(userAgent), eq(true));
        
        System.out.println("✅ 封禁用户成功测试通过");
    }

    /**
     * 测试封禁不存在的用户
     */
    @Test
    @DisplayName("封禁不存在的用户")
    public void testBanUserNotFound() {
        // Given: 准备测试数据
        Long userId = 999L;
        Long adminId = 100L;
        String reason = "违规行为";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        when(userDao.findById(userId)).thenReturn(null);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行封禁
        boolean result = adminUserService.banUser(userId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertFalse(result, "封禁不存在的用户应该失败");
        
        // 验证DAO方法被调用
        verify(userDao).findById(userId);
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.USER_BAN), eq(AuditTargetTypeEnum.USER), 
                                         eq(userId), contains("用户不存在"), eq(ipAddress), eq(userAgent), eq(false));
        
        System.out.println("✅ 封禁不存在用户测试通过");
    }

    /**
     * 测试禁言用户成功场景
     */
    @Test
    @DisplayName("禁言用户成功")
    public void testMuteUserSuccess() {
        // Given: 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String reason = "发布不当言论";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setStatus(0); // 正常状态
        
        // Mock DAO行为
        when(userDao.findById(userId)).thenReturn(mockUser);
        when(adminUserDao.updateUserStatus(userId, 2, adminId)).thenReturn(true);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行禁言
        boolean result = adminUserService.muteUser(userId, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertTrue(result, "禁言用户应该成功");
        
        // 验证DAO方法被调用
        verify(userDao).findById(userId);
        verify(adminUserDao).updateUserStatus(userId, 2, adminId);
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.USER_MUTE), eq(AuditTargetTypeEnum.USER), 
                                         eq(userId), contains("禁言用户"), eq(ipAddress), eq(userAgent), eq(true));
        
        System.out.println("✅ 禁言用户成功测试通过");
    }

    /**
     * 测试解封用户成功场景
     */
    @Test
    @DisplayName("解封用户成功")
    public void testUnbanUserSuccess() {
        // Given: 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setStatus(1); // 被封禁状态
        
        // Mock DAO行为
        when(userDao.findById(userId)).thenReturn(mockUser);
        when(adminUserDao.updateUserStatus(userId, 0, adminId)).thenReturn(true);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行解封
        boolean result = adminUserService.unbanUser(userId, adminId, ipAddress, userAgent);
        
        // Then: 验证结果
        assertTrue(result, "解封用户应该成功");
        
        // 验证DAO方法被调用
        verify(userDao).findById(userId);
        verify(adminUserDao).updateUserStatus(userId, 0, adminId);
        verify(auditLogService).logAction(eq(adminId), eq(AuditActionEnum.USER_UNBAN), eq(AuditTargetTypeEnum.USER), 
                                         eq(userId), contains("解封用户"), eq(ipAddress), eq(userAgent), eq(true));
        
        System.out.println("✅ 解封用户成功测试通过");
    }

    /**
     * 测试批量封禁用户
     */
    @Test
    @DisplayName("批量封禁用户")
    public void testBatchBanUsers() {
        // Given: 准备测试数据
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        Long adminId = 100L;
        String reason = "批量违规";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        // Mock用户数据
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setStatus(0);
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setStatus(0);
        
        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("user3");
        user3.setStatus(0);
        
        when(userDao.findById(1L)).thenReturn(user1);
        when(userDao.findById(2L)).thenReturn(user2);
        when(userDao.findById(3L)).thenReturn(user3);
        when(adminUserDao.updateUserStatus(anyLong(), eq(1), eq(adminId))).thenReturn(true);
        when(auditLogService.logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(1L);
        
        // When: 执行批量封禁
        Map<String, Object> result = adminUserService.batchBanUsers(userIds, adminId, reason, ipAddress, userAgent);
        
        // Then: 验证结果
        assertNotNull(result, "批量封禁结果不应为空");
        assertEquals(3, result.get("totalCount"), "总数应该是3");
        assertEquals(3, result.get("successCount"), "成功数应该是3");
        assertEquals(0, result.get("failCount"), "失败数应该是0");
        
        // 验证DAO方法被调用
        verify(userDao, times(3)).findById(anyLong());
        verify(adminUserDao, times(3)).updateUserStatus(anyLong(), eq(1), eq(adminId));
        verify(auditLogService, times(4)).logAction(any(), any(), any(), any(), anyString(), anyString(), anyString(), anyBoolean());
        
        System.out.println("✅ 批量封禁用户测试通过");
    }

    /**
     * 测试参数验证
     */
    @Test
    @DisplayName("参数验证测试")
    public void testParameterValidation() {
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        
        // 测试空用户ID
        assertFalse(adminUserService.banUser(null, 100L, "reason", ipAddress, userAgent), "空用户ID应该失败");
        
        // 测试空管理员ID
        assertFalse(adminUserService.banUser(1L, null, "reason", ipAddress, userAgent), "空管理员ID应该失败");
        
        // 测试空用户ID列表
        assertNull(adminUserService.batchBanUsers(null, 100L, "reason", ipAddress, userAgent), "空用户ID列表应该返回null");
        
        System.out.println("✅ 参数验证测试通过");
    }
}
