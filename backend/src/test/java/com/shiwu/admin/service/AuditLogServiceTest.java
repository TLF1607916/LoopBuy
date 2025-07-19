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
 * 包含Task5_3_1_3：审计日志查询API，支持筛选和搜索功能
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

    // ==================== Task5_3_1_3 专门测试 ====================

    /**
     * Task5_3_1_3: 测试审计日志查询API - 复合筛选条件
     */
    @Test
    void testTask5_3_1_3_ComplexFiltering() {
        System.out.println("\n=== Task5_3_1_3: 测试复合筛选条件查询 ===");

        // Given: 准备复合查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setAdminId(1L);
        queryDTO.setAction("USER_BAN");
        queryDTO.setTargetType("USER");
        queryDTO.setResult(1);
        queryDTO.setKeyword("测试");
        queryDTO.setStartTime(LocalDateTime.now().minusDays(7));
        queryDTO.setEndTime(LocalDateTime.now());
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        queryDTO.setSortBy("create_time");
        queryDTO.setSortOrder("DESC");

        // Mock数据
        List<AuditLog> mockLogs = Arrays.asList(
            createTestAuditLog(1L, "USER_BAN", "封禁测试用户001"),
            createTestAuditLog(2L, "USER_BAN", "封禁测试用户002")
        );

        when(auditLogDao.findAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(mockLogs);
        when(auditLogDao.countAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(2L);

        // Mock管理员信息
        Administrator admin = new Administrator();
        admin.setId(1L);
        admin.setUsername("admin_test");
        when(adminDao.findById(1L)).thenReturn(admin);

        // When: 执行查询
        Map<String, Object> result = auditLogService.getAuditLogs(queryDTO);

        // Then: 验证结果
        assertNotNull(result, "查询结果不应为空");
        assertEquals(2L, result.get("totalCount"), "总数应该是2");
        assertEquals(1, result.get("page"), "页码应该是1");
        assertEquals(10, result.get("pageSize"), "页大小应该是10");

        @SuppressWarnings("unchecked")
        List<AuditLogVO> resultList = (List<AuditLogVO>) result.get("list");
        assertNotNull(resultList, "日志列表不应为空");
        assertEquals(2, resultList.size(), "日志列表大小应该是2");

        // 验证筛选结果
        for (AuditLogVO vo : resultList) {
            assertEquals("USER_BAN", vo.getAction(), "操作类型应该匹配筛选条件");
            assertEquals("USER", vo.getTargetType(), "目标类型应该匹配筛选条件");
            assertEquals(1, vo.getResult(), "操作结果应该匹配筛选条件");
            assertTrue(vo.getDetails().contains("测试"), "详情应该包含关键词");
            assertEquals("admin_test", vo.getAdminUsername(), "管理员用户名应该正确填充");
        }

        System.out.println("✅ 复合筛选条件查询测试通过 - 查询到" + resultList.size() + "条记录");
    }

    /**
     * Task5_3_1_3: 测试审计日志查询API - 关键词搜索功能
     */
    @Test
    void testTask5_3_1_3_KeywordSearch() {
        System.out.println("\n=== Task5_3_1_3: 测试关键词搜索功能 ===");

        // Given: 准备关键词搜索条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setKeyword("商品审核");
        queryDTO.setPage(1);
        queryDTO.setPageSize(20);

        // Mock数据 - 包含关键词的日志
        List<AuditLog> mockLogs = Arrays.asList(
            createTestAuditLog(1L, "PRODUCT_APPROVE", "商品审核通过：iPhone 15"),
            createTestAuditLog(2L, "PRODUCT_REJECT", "商品审核拒绝：违规商品"),
            createTestAuditLog(3L, "PRODUCT_APPROVE", "商品审核通过：MacBook Pro")
        );

        when(auditLogDao.findAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(mockLogs);
        when(auditLogDao.countAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(3L);

        // Mock管理员信息
        Administrator admin = new Administrator();
        admin.setId(1L);
        admin.setUsername("admin_reviewer");
        when(adminDao.findById(1L)).thenReturn(admin);

        // When: 执行搜索
        Map<String, Object> result = auditLogService.getAuditLogs(queryDTO);

        // Then: 验证结果
        assertNotNull(result, "搜索结果不应为空");
        assertEquals(3L, result.get("totalCount"), "总数应该是3");

        @SuppressWarnings("unchecked")
        List<AuditLogVO> resultList = (List<AuditLogVO>) result.get("list");
        assertNotNull(resultList, "日志列表不应为空");
        assertEquals(3, resultList.size(), "日志列表大小应该是3");

        // 验证搜索结果都包含关键词
        for (AuditLogVO vo : resultList) {
            assertTrue(vo.getDetails().contains("商品审核"), "所有结果都应该包含关键词'商品审核'");
        }

        System.out.println("✅ 关键词搜索功能测试通过 - 搜索到" + resultList.size() + "条记录");
    }

    /**
     * Task5_3_1_3: 测试审计日志查询API - 时间范围筛选
     */
    @Test
    void testTask5_3_1_3_TimeRangeFiltering() {
        System.out.println("\n=== Task5_3_1_3: 测试时间范围筛选功能 ===");

        // Given: 准备时间范围查询条件
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();

        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        queryDTO.setPage(1);
        queryDTO.setPageSize(15);

        // Mock数据 - 在时间范围内的日志
        List<AuditLog> mockLogs = Arrays.asList(
            createTestAuditLog(1L, "USER_LOGIN", "管理员登录"),
            createTestAuditLog(2L, "USER_LOGOUT", "管理员登出")
        );

        when(auditLogDao.findAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(mockLogs);
        when(auditLogDao.countAuditLogs(any(AuditLogQueryDTO.class))).thenReturn(2L);

        // Mock管理员信息
        Administrator admin = new Administrator();
        admin.setId(1L);
        admin.setUsername("admin_user");
        when(adminDao.findById(1L)).thenReturn(admin);

        // When: 执行查询
        Map<String, Object> result = auditLogService.getAuditLogs(queryDTO);

        // Then: 验证结果
        assertNotNull(result, "查询结果不应为空");
        assertEquals(2L, result.get("totalCount"), "总数应该是2");
        assertEquals(1, result.get("page"), "页码应该是1");
        assertEquals(15, result.get("pageSize"), "页大小应该是15");

        @SuppressWarnings("unchecked")
        List<AuditLogVO> resultList = (List<AuditLogVO>) result.get("list");
        assertNotNull(resultList, "日志列表不应为空");
        assertEquals(2, resultList.size(), "日志列表大小应该是2");

        // 验证时间范围
        for (AuditLogVO vo : resultList) {
            assertNotNull(vo.getCreateTime(), "创建时间不应为空");
            assertTrue(vo.getCreateTime().isAfter(startTime.minusMinutes(1)), "创建时间应该在开始时间之后");
            assertTrue(vo.getCreateTime().isBefore(endTime.plusMinutes(1)), "创建时间应该在结束时间之前");
        }

        System.out.println("✅ 时间范围筛选功能测试通过 - 查询到" + resultList.size() + "条记录");
    }

    /**
     * 创建测试用的AuditLog对象
     */
    private AuditLog createTestAuditLog(Long id, String action, String details) {
        AuditLog log = new AuditLog();
        log.setId(id);
        log.setAdminId(1L);
        log.setAction(action);
        log.setTargetType("USER");
        log.setTargetId(100L + id);
        log.setDetails(details);
        log.setIpAddress("127.0.0.1");
        log.setUserAgent("Mozilla/5.0 (Test)");
        log.setResult(1);
        log.setCreateTime(LocalDateTime.now().minusHours(id));
        return log;
    }
}
