package com.shiwu.user.service;

import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.model.AdminUserQueryDTO;
import com.shiwu.user.dao.AdminUserDao;
import com.shiwu.user.dao.UserDao;
import com.shiwu.user.model.User;
import com.shiwu.user.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 管理员用户服务测试类
 */
public class AdminUserServiceTest {

    @Mock
    private AdminUserDao adminUserDao;

    @Mock
    private UserDao userDao;

    @Mock
    private AuditLogDao auditLogDao;

    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminUserService = new AdminUserServiceImpl(adminUserDao, userDao, auditLogDao);
    }

    @Test
    void testFindUsers_Success() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setKeyword("test");
        queryDTO.setStatus(0);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(20);

        List<Map<String, Object>> mockUsers = Arrays.asList(
                createMockUserMap(1L, "user1", "user1@test.com", 0),
                createMockUserMap(2L, "user2", "user2@test.com", 1)
        );

        when(adminUserDao.findUsers(queryDTO)).thenReturn(mockUsers);
        when(adminUserDao.countUsers(queryDTO)).thenReturn(2);

        // 执行测试
        Map<String, Object> result = adminUserService.findUsers(queryDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.get("totalCount"));
        assertEquals(1, result.get("totalPages"));
        assertEquals(1, result.get("currentPage"));
        assertEquals(20, result.get("pageSize"));
        assertNotNull(result.get("users"));

        verify(adminUserDao).findUsers(queryDTO);
        verify(adminUserDao).countUsers(queryDTO);
    }

    @Test
    void testFindUsers_NullQuery() {
        // 执行测试
        Map<String, Object> result = adminUserService.findUsers(null);

        // 验证结果
        assertNull(result);
        verify(adminUserDao, never()).findUsers(any());
        verify(adminUserDao, never()).countUsers(any());
    }

    @Test
    void testGetUserDetail_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 0);
        when(userDao.findById(userId)).thenReturn(mockUser);

        // 执行测试
        Map<String, Object> result = adminUserService.getUserDetail(userId, adminId);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.get("user"));

        verify(userDao).findById(userId);
    }

    @Test
    void testGetUserDetail_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        Long adminId = 100L;

        when(userDao.findById(userId)).thenReturn(null);

        // 执行测试
        Map<String, Object> result = adminUserService.getUserDetail(userId, adminId);

        // 验证结果
        assertNull(result);

        verify(userDao).findById(userId);
    }

    @Test
    void testGetUserDetail_NullParams() {
        // 执行测试
        Map<String, Object> result1 = adminUserService.getUserDetail(null, 100L);
        Map<String, Object> result2 = adminUserService.getUserDetail(1L, null);

        // 验证结果
        assertNull(result1);
        assertNull(result2);

        verify(userDao, never()).findById(any());
    }

    @Test
    void testBanUser_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String reason = "违规行为";

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 0);
        when(userDao.findById(userId)).thenReturn(mockUser);
        when(adminUserDao.updateUserStatus(userId, 1, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminUserService.banUser(userId, adminId, reason);

        // 验证结果
        assertTrue(result);

        verify(userDao).findById(userId);
        verify(adminUserDao).updateUserStatus(userId, 1, adminId);
        verify(auditLogDao).createAuditLog(any());
    }

    @Test
    void testBanUser_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        Long adminId = 100L;
        String reason = "违规行为";

        when(userDao.findById(userId)).thenReturn(null);

        // 执行测试
        boolean result = adminUserService.banUser(userId, adminId, reason);

        // 验证结果
        assertFalse(result);

        verify(userDao).findById(userId);
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
        verify(auditLogDao, never()).createAuditLog(any());
    }

    @Test
    void testBanUser_AlreadyBanned() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String reason = "违规行为";

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 1); // 已封禁
        when(userDao.findById(userId)).thenReturn(mockUser);

        // 执行测试
        boolean result = adminUserService.banUser(userId, adminId, reason);

        // 验证结果
        assertFalse(result);

        verify(userDao).findById(userId);
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
        verify(auditLogDao, never()).createAuditLog(any());
    }

    @Test
    void testBanUser_NullParams() {
        // 执行测试
        boolean result1 = adminUserService.banUser(null, 100L, "reason");
        boolean result2 = adminUserService.banUser(1L, null, "reason");

        // 验证结果
        assertFalse(result1);
        assertFalse(result2);

        verify(userDao, never()).findById(any());
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
    }

    @Test
    void testMuteUser_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String reason = "发布不当言论";

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 0);
        when(userDao.findById(userId)).thenReturn(mockUser);
        when(adminUserDao.updateUserStatus(userId, 2, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminUserService.muteUser(userId, adminId, reason);

        // 验证结果
        assertTrue(result);

        verify(userDao).findById(userId);
        verify(adminUserDao).updateUserStatus(userId, 2, adminId);
        verify(auditLogDao).createAuditLog(any());
    }

    @Test
    void testMuteUser_UserBanned() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String reason = "发布不当言论";

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 1); // 已封禁
        when(userDao.findById(userId)).thenReturn(mockUser);

        // 执行测试
        boolean result = adminUserService.muteUser(userId, adminId, reason);

        // 验证结果
        assertFalse(result);

        verify(userDao).findById(userId);
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
        verify(auditLogDao, never()).createAuditLog(any());
    }

    @Test
    void testMuteUser_AlreadyMuted() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;
        String reason = "发布不当言论";

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 2); // 已禁言
        when(userDao.findById(userId)).thenReturn(mockUser);

        // 执行测试
        boolean result = adminUserService.muteUser(userId, adminId, reason);

        // 验证结果
        assertFalse(result);

        verify(userDao).findById(userId);
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
        verify(auditLogDao, never()).createAuditLog(any());
    }

    @Test
    void testUnbanUser_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 1); // 已封禁
        when(userDao.findById(userId)).thenReturn(mockUser);
        when(adminUserDao.updateUserStatus(userId, 0, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminUserService.unbanUser(userId, adminId);

        // 验证结果
        assertTrue(result);

        verify(userDao).findById(userId);
        verify(adminUserDao).updateUserStatus(userId, 0, adminId);
        verify(auditLogDao).createAuditLog(any());
    }

    @Test
    void testUnbanUser_NotBanned() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 0); // 正常状态
        when(userDao.findById(userId)).thenReturn(mockUser);

        // 执行测试
        boolean result = adminUserService.unbanUser(userId, adminId);

        // 验证结果
        assertFalse(result);

        verify(userDao).findById(userId);
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
        verify(auditLogDao, never()).createAuditLog(any());
    }

    @Test
    void testUnmuteUser_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 2); // 已禁言
        when(userDao.findById(userId)).thenReturn(mockUser);
        when(adminUserDao.updateUserStatus(userId, 0, adminId)).thenReturn(true);

        // 执行测试
        boolean result = adminUserService.unmuteUser(userId, adminId);

        // 验证结果
        assertTrue(result);

        verify(userDao).findById(userId);
        verify(adminUserDao).updateUserStatus(userId, 0, adminId);
        verify(auditLogDao).createAuditLog(any());
    }

    @Test
    void testUnmuteUser_NotMuted() {
        // 准备测试数据
        Long userId = 1L;
        Long adminId = 100L;

        User mockUser = createMockUser(userId, "testuser", "test@example.com", 0); // 正常状态
        when(userDao.findById(userId)).thenReturn(mockUser);

        // 执行测试
        boolean result = adminUserService.unmuteUser(userId, adminId);

        // 验证结果
        assertFalse(result);

        verify(userDao).findById(userId);
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
        verify(auditLogDao, never()).createAuditLog(any());
    }

    @Test
    void testBatchBanUsers_Success() {
        // 准备测试数据
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        Long adminId = 100L;
        String reason = "批量违规";

        // Mock单个封禁操作
        User user1 = createMockUser(1L, "user1", "user1@test.com", 0);
        User user2 = createMockUser(2L, "user2", "user2@test.com", 0);
        User user3 = createMockUser(3L, "user3", "user3@test.com", 1); // 已封禁

        when(userDao.findById(1L)).thenReturn(user1);
        when(userDao.findById(2L)).thenReturn(user2);
        when(userDao.findById(3L)).thenReturn(user3);

        when(adminUserDao.updateUserStatus(1L, 1, adminId)).thenReturn(true);
        when(adminUserDao.updateUserStatus(2L, 1, adminId)).thenReturn(true);

        // 执行测试
        Map<String, Object> result = adminUserService.batchBanUsers(userIds, adminId, reason);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.get("totalCount"));
        assertEquals(2, result.get("successCount"));
        assertEquals(1, result.get("failCount"));

        verify(userDao, times(3)).findById(any());
        verify(adminUserDao, times(2)).updateUserStatus(any(), eq(1), eq(adminId));
        verify(auditLogDao, times(2)).createAuditLog(any());
    }

    @Test
    void testBatchBanUsers_EmptyList() {
        // 执行测试
        Map<String, Object> result = adminUserService.batchBanUsers(Arrays.asList(), 100L, "reason");

        // 验证结果
        assertNull(result);

        verify(userDao, never()).findById(any());
        verify(adminUserDao, never()).updateUserStatus(any(), any(), any());
    }

    @Test
    void testBatchMuteUsers_Success() {
        // 准备测试数据
        List<Long> userIds = Arrays.asList(1L, 2L);
        Long adminId = 100L;
        String reason = "批量禁言";

        User user1 = createMockUser(1L, "user1", "user1@test.com", 0);
        User user2 = createMockUser(2L, "user2", "user2@test.com", 0);

        when(userDao.findById(1L)).thenReturn(user1);
        when(userDao.findById(2L)).thenReturn(user2);

        when(adminUserDao.updateUserStatus(1L, 2, adminId)).thenReturn(true);
        when(adminUserDao.updateUserStatus(2L, 2, adminId)).thenReturn(true);

        // 执行测试
        Map<String, Object> result = adminUserService.batchMuteUsers(userIds, adminId, reason);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.get("totalCount"));
        assertEquals(2, result.get("successCount"));
        assertEquals(0, result.get("failCount"));

        verify(userDao, times(2)).findById(any());
        verify(adminUserDao, times(2)).updateUserStatus(any(), eq(2), eq(adminId));
        verify(auditLogDao, times(2)).createAuditLog(any());
    }

    // 辅助方法：创建模拟用户对象
    private User createMockUser(Long id, String username, String email, Integer status) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setStatus(status);
        user.setNickname(username);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    // 辅助方法：创建模拟用户Map
    private Map<String, Object> createMockUserMap(Long id, String username, String email, Integer status) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", username);
        user.put("email", email);
        user.put("status", status);
        user.put("statusText", getStatusText(status));
        user.put("createTime", LocalDateTime.now());
        return user;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "正常";
            case 1: return "已封禁";
            case 2: return "已禁言";
            default: return "未知";
        }
    }
}
