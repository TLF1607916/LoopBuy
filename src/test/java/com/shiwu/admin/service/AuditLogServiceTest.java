package com.shiwu.admin.service;

import com.shiwu.admin.dao.AdminDao;
import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.dto.AuditLogQueryDTO;
import com.shiwu.admin.enums.AuditActionEnum;
import com.shiwu.admin.enums.AuditTargetTypeEnum;
import com.shiwu.admin.model.Administrator;
import com.shiwu.admin.model.AuditLog;
import com.shiwu.admin.service.impl.AuditLogServiceImpl;
import com.shiwu.admin.vo.AuditLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuditLogService单元测试
 * 测试NFR-SEC-03要求的审计日志服务功能
 */
public class AuditLogServiceTest {
    
    @Mock
    private AuditLogDao auditLogDao;
    
    @Mock
    private AdminDao adminDao;
    
    private AuditLogService auditLogService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditLogService = new AuditLogServiceImpl(auditLogDao, adminDao);
    }
    
    @Test
    void testLogAction_FullParameters_Success() {
        // Mock数据
        when(auditLogDao.createAuditLog(any(AuditLog.class))).thenReturn(1L);
        
        // 执行测试
        Long logId = auditLogService.logAction(1L, AuditActionEnum.USER_BAN, 
                                              AuditTargetTypeEnum.USER, 123L, 
                                              "封禁用户", "192.168.1.100", 
                                              "Mozilla/5.0", true);
        
        // 验证结果
        assertNotNull(logId);
        assertEquals(1L, logId);
        verify(auditLogDao).createAuditLog(any(AuditLog.class));
        System.out.println("✓ 完整参数记录审计日志成功");
    }
    
    @Test
    void testLogAction_SimplifiedParameters_Success() {
        // Mock数据
        when(auditLogDao.createAuditLog(any(AuditLog.class))).thenReturn(2L);
        
        // 执行测试
        Long logId = auditLogService.logAction(1L, AuditActionEnum.USER_MUTE, 
                                              "禁言用户", true);
        
        // 验证结果
        assertNotNull(logId);
        assertEquals(2L, logId);
        verify(auditLogDao).createAuditLog(any(AuditLog.class));
        System.out.println("✓ 简化参数记录审计日志成功");
    }
    
    @Test
    void testLogAction_NullAdminId() {
        // 执行测试
        Long logId = auditLogService.logAction(null, AuditActionEnum.USER_BAN, 
                                              "封禁用户", true);
        
        // 验证结果
        assertNull(logId);
        verify(auditLogDao, never()).createAuditLog(any(AuditLog.class));
        System.out.println("✓ 正确处理空管理员ID");
    }
    
    @Test
    void testLogAction_NullAction() {
        // 执行测试
        Long logId = auditLogService.logAction(1L, null, "操作", true);
        
        // 验证结果
        assertNull(logId);
        verify(auditLogDao, never()).createAuditLog(any(AuditLog.class));
        System.out.println("✓ 正确处理空操作类型");
    }
    
    @Test
    void testLogAdminLogin_Success() {
        // Mock数据
        when(auditLogDao.createAuditLog(any(AuditLog.class))).thenReturn(3L);
        
        // 执行测试
        Long logId = auditLogService.logAdminLogin(1L, "192.168.1.100", 
                                                  "Mozilla/5.0", true, "登录成功");
        
        // 验证结果
        assertNotNull(logId);
        assertEquals(3L, logId);
        verify(auditLogDao).createAuditLog(any(AuditLog.class));
        System.out.println("✓ 记录管理员登录日志成功");
    }
    
    @Test
    void testGetAuditLogs_Success() {
        // 准备Mock数据
        List<AuditLog> mockLogs = createMockAuditLogs();
        when(auditLogDao.findAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(mockLogs);
        when(auditLogDao.countAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(2L);
        
        Administrator mockAdmin = new Administrator();
        mockAdmin.setId(1L);
        mockAdmin.setUsername("admin");
        when(adminDao.findById(1L)).thenReturn(mockAdmin);
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        
        // 执行测试
        Map<String, Object> result = auditLogService.getAuditLogs(queryDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2L, result.get("totalCount"));
        assertEquals(1, result.get("page"));
        assertEquals(10, result.get("pageSize"));
        assertEquals(1L, result.get("totalPages"));
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> list = (List<AuditLogVO>) result.get("list");
        assertNotNull(list);
        assertEquals(2, list.size());
        
        // 验证第一条记录
        AuditLogVO firstLog = list.get(0);
        assertEquals("admin", firstLog.getAdminUsername());
        assertEquals("封禁用户", firstLog.getActionDescription());
        assertEquals("用户", firstLog.getTargetTypeDescription());
        assertEquals("成功", firstLog.getResultText());
        
        System.out.println("✓ 查询审计日志成功");
    }
    
    @Test
    void testGetAuditLogs_NullQuery() {
        // 执行测试
        Map<String, Object> result = auditLogService.getAuditLogs(null);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0L, result.get("totalCount"));
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> list = (List<AuditLogVO>) result.get("list");
        assertNotNull(list);
        assertEquals(0, list.size());
        
        System.out.println("✓ 正确处理空查询条件");
    }
    
    @Test
    void testGetAuditLogDetail_Success() {
        // 准备Mock数据
        AuditLog mockLog = createMockAuditLog();
        when(auditLogDao.findById(1L)).thenReturn(mockLog);
        
        Administrator mockAdmin = new Administrator();
        mockAdmin.setId(1L);
        mockAdmin.setUsername("admin");
        when(adminDao.findById(1L)).thenReturn(mockAdmin);
        
        // 执行测试
        AuditLogVO result = auditLogService.getAuditLogDetail(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin", result.getAdminUsername());
        assertEquals("封禁用户", result.getActionDescription());
        assertEquals("用户", result.getTargetTypeDescription());
        assertEquals("成功", result.getResultText());
        
        System.out.println("✓ 获取审计日志详情成功");
    }
    
    @Test
    void testGetAuditLogDetail_NotFound() {
        // Mock数据
        when(auditLogDao.findById(999L)).thenReturn(null);
        
        // 执行测试
        AuditLogVO result = auditLogService.getAuditLogDetail(999L);
        
        // 验证结果
        assertNull(result);
        System.out.println("✓ 正确处理不存在的审计日志");
    }
    
    @Test
    void testGetAuditLogDetail_NullId() {
        // 执行测试
        AuditLogVO result = auditLogService.getAuditLogDetail(null);
        
        // 验证结果
        assertNull(result);
        System.out.println("✓ 正确处理空ID");
    }
    
    @Test
    void testGetOperationStats_Success() {
        // 准备Mock数据
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalOperations", 100);
        mockStats.put("successOperations", 95);
        mockStats.put("failedOperations", 5);
        when(auditLogDao.getOperationStats(7)).thenReturn(mockStats);
        
        // 执行测试
        Map<String, Object> result = auditLogService.getOperationStats(7);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.get("totalOperations"));
        assertEquals(95, result.get("successOperations"));
        assertEquals(5, result.get("failedOperations"));
        
        System.out.println("✓ 获取操作统计数据成功");
    }
    
    @Test
    void testGetActivityTrend_Success() {
        // 准备Mock数据
        List<Map<String, Object>> mockTrend = new ArrayList<>();
        Map<String, Object> dayData = new HashMap<>();
        dayData.put("date", "2023-12-01");
        dayData.put("count", 10);
        mockTrend.add(dayData);
        when(auditLogDao.getActivityTrend(7)).thenReturn(mockTrend);
        
        // 执行测试
        List<Map<String, Object>> result = auditLogService.getActivityTrend(7);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2023-12-01", result.get(0).get("date"));
        assertEquals(10, result.get(0).get("count"));
        
        System.out.println("✓ 获取活动趋势数据成功");
    }
    
    @Test
    void testExportAuditLogs_Success() {
        // 准备Mock数据
        List<AuditLog> mockLogs = createMockAuditLogs();
        when(auditLogDao.findAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(mockLogs);
        
        Administrator mockAdmin = new Administrator();
        mockAdmin.setId(1L);
        mockAdmin.setUsername("admin");
        when(adminDao.findById(1L)).thenReturn(mockAdmin);
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        
        // 执行测试
        List<AuditLogVO> result = auditLogService.exportAuditLogs(queryDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("admin", result.get(0).getAdminUsername());
        
        System.out.println("✓ 导出审计日志成功");
    }
    
    @Test
    void testExportAuditLogs_NullQuery() {
        // 执行测试
        List<AuditLogVO> result = auditLogService.exportAuditLogs(null);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.size());
        
        System.out.println("✓ 正确处理空查询条件的导出");
    }
    
    @Test
    void testShouldLogAction() {
        // 测试敏感操作
        assertTrue(auditLogService.shouldLogAction(AuditActionEnum.USER_BAN));
        assertTrue(auditLogService.shouldLogAction(AuditActionEnum.ADMIN_DELETE));
        assertTrue(auditLogService.shouldLogAction(AuditActionEnum.PRODUCT_DELETE));
        
        // 测试空操作
        assertFalse(auditLogService.shouldLogAction(null));
        
        System.out.println("✓ 操作类型检查功能正常");
    }
    
    @Test
    void testGetAvailableActions() {
        // 执行测试
        List<Map<String, String>> actions = auditLogService.getAvailableActions();
        
        // 验证结果
        assertNotNull(actions);
        assertTrue(actions.size() > 0);
        
        // 验证包含预期的操作
        boolean foundUserBan = actions.stream()
                .anyMatch(action -> "USER_BAN".equals(action.get("code")));
        assertTrue(foundUserBan);
        
        System.out.println("✓ 获取可用操作类型成功: 共" + actions.size() + "种操作");
    }
    
    @Test
    void testGetAvailableTargetTypes() {
        // 执行测试
        List<Map<String, String>> targetTypes = auditLogService.getAvailableTargetTypes();
        
        // 验证结果
        assertNotNull(targetTypes);
        assertTrue(targetTypes.size() > 0);
        
        // 验证包含预期的目标类型
        boolean foundUser = targetTypes.stream()
                .anyMatch(type -> "USER".equals(type.get("code")));
        assertTrue(foundUser);
        
        System.out.println("✓ 获取可用目标类型成功: 共" + targetTypes.size() + "种类型");
    }
    
    /**
     * 创建Mock审计日志
     */
    private AuditLog createMockAuditLog() {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(1L);
        auditLog.setAdminId(1L);
        auditLog.setAction("USER_BAN");
        auditLog.setTargetType("USER");
        auditLog.setTargetId(123L);
        auditLog.setDetails("封禁用户测试");
        auditLog.setIpAddress("192.168.1.100");
        auditLog.setUserAgent("Mozilla/5.0");
        auditLog.setResult(1);
        auditLog.setCreateTime(LocalDateTime.now());
        return auditLog;
    }
    
    /**
     * 创建Mock审计日志列表
     */
    private List<AuditLog> createMockAuditLogs() {
        List<AuditLog> logs = new ArrayList<>();
        
        AuditLog log1 = createMockAuditLog();
        logs.add(log1);
        
        AuditLog log2 = new AuditLog();
        log2.setId(2L);
        log2.setAdminId(1L);
        log2.setAction("USER_MUTE");
        log2.setTargetType("USER");
        log2.setTargetId(124L);
        log2.setDetails("禁言用户测试");
        log2.setIpAddress("192.168.1.101");
        log2.setUserAgent("Mozilla/5.0");
        log2.setResult(1);
        log2.setCreateTime(LocalDateTime.now());
        logs.add(log2);
        
        return logs;
    }
}
