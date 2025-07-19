package com.shiwu.admin.integration;

import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.dto.AuditLogQueryDTO;
import com.shiwu.admin.enums.AuditActionEnum;
import com.shiwu.admin.enums.AuditTargetTypeEnum;
import com.shiwu.admin.model.AuditLog;
import com.shiwu.admin.service.AuditLogService;
import com.shiwu.admin.service.impl.AuditLogServiceImpl;
import com.shiwu.admin.vo.AuditLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 审计日志集成测试
 * 测试NFR-SEC-03要求的完整审计日志功能
 */
public class AuditLogIntegrationTest {
    
    private AuditLogService auditLogService;
    private AuditLogDao auditLogDao;
    
    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogServiceImpl();
        auditLogDao = new AuditLogDao();
    }
    
    @Test
    void testCompleteAuditLogWorkflow() {
        System.out.println("=== 开始完整审计日志工作流测试 ===");
        
        // 1. 记录审计日志
        Long logId = auditLogService.logAction(
            1L, 
            AuditActionEnum.USER_BAN, 
            AuditTargetTypeEnum.USER, 
            123L, 
            "集成测试：封禁用户", 
            "192.168.1.100", 
            "Mozilla/5.0 Integration Test", 
            true
        );
        
        assertNotNull(logId);
        assertTrue(logId > 0);
        System.out.println("✓ 步骤1：记录审计日志成功，ID=" + logId);
        
        // 2. 查询审计日志详情
        AuditLogVO logDetail = auditLogService.getAuditLogDetail(logId);
        assertNotNull(logDetail);
        assertEquals(logId, logDetail.getId());
        assertEquals(1L, logDetail.getAdminId());
        assertEquals("USER_BAN", logDetail.getAction());
        assertEquals("USER", logDetail.getTargetType());
        assertEquals(123L, logDetail.getTargetId());
        assertEquals("集成测试：封禁用户", logDetail.getDetails());
        assertEquals("192.168.1.100", logDetail.getIpAddress());
        assertEquals("Mozilla/5.0 Integration Test", logDetail.getUserAgent());
        assertEquals(1, logDetail.getResult());
        assertEquals("成功", logDetail.getResultText());
        assertNotNull(logDetail.getCreateTime());
        assertNotNull(logDetail.getCreateTimeText());
        System.out.println("✓ 步骤2：查询审计日志详情成功");
        
        // 3. 分页查询审计日志
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setAdminId(1L);
        queryDTO.setAction("USER_BAN");
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        
        Map<String, Object> queryResult = auditLogService.getAuditLogs(queryDTO);
        assertNotNull(queryResult);
        assertTrue((Long) queryResult.get("totalCount") > 0);
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> logList = (List<AuditLogVO>) queryResult.get("list");
        assertNotNull(logList);
        assertTrue(logList.size() > 0);
        
        // 验证查询结果中包含我们刚创建的日志
        boolean foundOurLog = logList.stream()
            .anyMatch(log -> logId.equals(log.getId()));
        assertTrue(foundOurLog);
        System.out.println("✓ 步骤3：分页查询审计日志成功，共" + queryResult.get("totalCount") + "条记录");
        
        System.out.println("=== 完整审计日志工作流测试通过 ===");
    }
    
    @Test
    void testMultipleAuditLogTypes() {
        System.out.println("=== 开始多种审计日志类型测试 ===");
        
        // 记录不同类型的审计日志
        Long loginLogId = auditLogService.logAdminLogin(
            1L, "192.168.1.100", "Mozilla/5.0", true, "管理员登录成功"
        );
        assertNotNull(loginLogId);
        System.out.println("✓ 记录管理员登录日志成功");
        
        Long userBanLogId = auditLogService.logAction(
            1L, AuditActionEnum.USER_BAN, AuditTargetTypeEnum.USER, 
            456L, "违规用户封禁", "192.168.1.100", "Mozilla/5.0", true
        );
        assertNotNull(userBanLogId);
        System.out.println("✓ 记录用户封禁日志成功");
        
        Long productApproveLogId = auditLogService.logAction(
            1L, AuditActionEnum.PRODUCT_APPROVE, AuditTargetTypeEnum.PRODUCT, 
            789L, "商品审核通过", "192.168.1.100", "Mozilla/5.0", true
        );
        assertNotNull(productApproveLogId);
        System.out.println("✓ 记录商品审核日志成功");
        
        Long systemConfigLogId = auditLogService.logAction(
            1L, AuditActionEnum.SYSTEM_CONFIG_UPDATE, AuditTargetTypeEnum.SYSTEM, 
            null, "更新系统配置", "192.168.1.100", "Mozilla/5.0", true
        );
        assertNotNull(systemConfigLogId);
        System.out.println("✓ 记录系统配置日志成功");
        
        // 验证所有日志都能正确查询
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setAdminId(1L);
        queryDTO.setStartTime(LocalDateTime.now().minusMinutes(5));
        queryDTO.setEndTime(LocalDateTime.now().plusMinutes(5));
        
        Map<String, Object> result = auditLogService.getAuditLogs(queryDTO);
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> logs = (List<AuditLogVO>) result.get("list");
        assertTrue(logs.size() >= 4); // 至少包含我们刚创建的4条日志
        
        System.out.println("=== 多种审计日志类型测试通过 ===");
    }
    
    @Test
    void testAuditLogFiltering() {
        System.out.println("=== 开始审计日志过滤测试 ===");
        
        // 创建测试数据
        createTestAuditLogs();
        
        // 测试按操作类型过滤
        AuditLogQueryDTO actionQuery = new AuditLogQueryDTO();
        actionQuery.setAction("USER_MUTE");
        Map<String, Object> actionResult = auditLogService.getAuditLogs(actionQuery);
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> actionLogs = (List<AuditLogVO>) actionResult.get("list");
        for (AuditLogVO log : actionLogs) {
            assertEquals("USER_MUTE", log.getAction());
        }
        System.out.println("✓ 按操作类型过滤测试通过");
        
        // 测试按目标类型过滤
        AuditLogQueryDTO targetQuery = new AuditLogQueryDTO();
        targetQuery.setTargetType("PRODUCT");
        Map<String, Object> targetResult = auditLogService.getAuditLogs(targetQuery);
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> targetLogs = (List<AuditLogVO>) targetResult.get("list");
        for (AuditLogVO log : targetLogs) {
            assertEquals("PRODUCT", log.getTargetType());
        }
        System.out.println("✓ 按目标类型过滤测试通过");
        
        // 测试按结果过滤
        AuditLogQueryDTO resultQuery = new AuditLogQueryDTO();
        resultQuery.setResult(1); // 成功
        Map<String, Object> resultResult = auditLogService.getAuditLogs(resultQuery);
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> resultLogs = (List<AuditLogVO>) resultResult.get("list");
        for (AuditLogVO log : resultLogs) {
            assertEquals(1, log.getResult());
            assertEquals("成功", log.getResultText());
        }
        System.out.println("✓ 按结果过滤测试通过");
        
        // 测试关键词搜索
        AuditLogQueryDTO keywordQuery = new AuditLogQueryDTO();
        keywordQuery.setKeyword("集成测试");
        Map<String, Object> keywordResult = auditLogService.getAuditLogs(keywordQuery);
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> keywordLogs = (List<AuditLogVO>) keywordResult.get("list");
        for (AuditLogVO log : keywordLogs) {
            assertTrue(log.getDetails().contains("集成测试"));
        }
        System.out.println("✓ 关键词搜索测试通过");
        
        System.out.println("=== 审计日志过滤测试通过 ===");
    }
    
    @Test
    void testAuditLogPagination() {
        System.out.println("=== 开始审计日志分页测试 ===");
        
        // 创建足够的测试数据
        createTestAuditLogs();
        
        // 测试第一页
        AuditLogQueryDTO page1Query = new AuditLogQueryDTO();
        page1Query.setPage(1);
        page1Query.setPageSize(5);
        page1Query.setSortBy("create_time");
        page1Query.setSortOrder("DESC");
        
        Map<String, Object> page1Result = auditLogService.getAuditLogs(page1Query);
        assertNotNull(page1Result);
        assertEquals(1, page1Result.get("page"));
        assertEquals(5, page1Result.get("pageSize"));
        
        @SuppressWarnings("unchecked")
        List<AuditLogVO> page1Logs = (List<AuditLogVO>) page1Result.get("list");
        assertTrue(page1Logs.size() <= 5);
        System.out.println("✓ 第一页查询测试通过，返回" + page1Logs.size() + "条记录");
        
        // 如果有足够的数据，测试第二页
        long totalCount = (Long) page1Result.get("totalCount");
        if (totalCount > 5) {
            AuditLogQueryDTO page2Query = new AuditLogQueryDTO();
            page2Query.setPage(2);
            page2Query.setPageSize(5);
            page2Query.setSortBy("create_time");
            page2Query.setSortOrder("DESC");
            
            Map<String, Object> page2Result = auditLogService.getAuditLogs(page2Query);
            assertNotNull(page2Result);
            assertEquals(2, page2Result.get("page"));
            
            @SuppressWarnings("unchecked")
            List<AuditLogVO> page2Logs = (List<AuditLogVO>) page2Result.get("list");
            assertTrue(page2Logs.size() <= 5);
            System.out.println("✓ 第二页查询测试通过，返回" + page2Logs.size() + "条记录");
        }
        
        System.out.println("=== 审计日志分页测试通过 ===");
    }
    
    @Test
    void testAuditLogExport() {
        System.out.println("=== 开始审计日志导出测试 ===");
        
        // 创建测试数据
        createTestAuditLogs();
        
        // 测试导出
        AuditLogQueryDTO exportQuery = new AuditLogQueryDTO();
        exportQuery.setStartTime(LocalDateTime.now().minusHours(1));
        exportQuery.setEndTime(LocalDateTime.now().plusHours(1));
        
        List<AuditLogVO> exportData = auditLogService.exportAuditLogs(exportQuery);
        assertNotNull(exportData);
        assertTrue(exportData.size() > 0);
        
        // 验证导出数据的完整性
        for (AuditLogVO log : exportData) {
            assertNotNull(log.getId());
            assertNotNull(log.getAction());
            assertNotNull(log.getCreateTime());
            assertNotNull(log.getCreateTimeText());
            assertNotNull(log.getResultText());
        }
        
        System.out.println("✓ 导出审计日志测试通过，导出" + exportData.size() + "条记录");
        System.out.println("=== 审计日志导出测试通过 ===");
    }
    
    @Test
    void testAuditLogEnumIntegration() {
        System.out.println("=== 开始审计日志枚举集成测试 ===");
        
        // 测试获取可用操作类型
        List<Map<String, String>> actions = auditLogService.getAvailableActions();
        assertNotNull(actions);
        assertTrue(actions.size() > 0);
        
        boolean foundUserBan = actions.stream()
            .anyMatch(action -> "USER_BAN".equals(action.get("code")) && 
                               "封禁用户".equals(action.get("description")));
        assertTrue(foundUserBan);
        System.out.println("✓ 获取可用操作类型测试通过，共" + actions.size() + "种操作");
        
        // 测试获取可用目标类型
        List<Map<String, String>> targetTypes = auditLogService.getAvailableTargetTypes();
        assertNotNull(targetTypes);
        assertTrue(targetTypes.size() > 0);
        
        boolean foundUser = targetTypes.stream()
            .anyMatch(type -> "USER".equals(type.get("code")) && 
                             "用户".equals(type.get("description")));
        assertTrue(foundUser);
        System.out.println("✓ 获取可用目标类型测试通过，共" + targetTypes.size() + "种类型");
        
        // 测试操作检查
        assertTrue(auditLogService.shouldLogAction(AuditActionEnum.USER_BAN));
        assertTrue(auditLogService.shouldLogAction(AuditActionEnum.ADMIN_DELETE));
        assertFalse(auditLogService.shouldLogAction(null));
        System.out.println("✓ 操作检查测试通过");
        
        System.out.println("=== 审计日志枚举集成测试通过 ===");
    }
    
    /**
     * 创建测试用的审计日志数据
     */
    private void createTestAuditLogs() {
        // 创建多种类型的审计日志用于测试
        auditLogService.logAction(1L, AuditActionEnum.USER_BAN, AuditTargetTypeEnum.USER, 
                                 100L, "集成测试：封禁用户1", "192.168.1.100", "Mozilla/5.0", true);
        
        auditLogService.logAction(1L, AuditActionEnum.USER_MUTE, AuditTargetTypeEnum.USER, 
                                 101L, "集成测试：禁言用户1", "192.168.1.101", "Mozilla/5.0", true);
        
        auditLogService.logAction(2L, AuditActionEnum.PRODUCT_APPROVE, AuditTargetTypeEnum.PRODUCT, 
                                 200L, "集成测试：审核商品1", "192.168.1.102", "Mozilla/5.0", true);
        
        auditLogService.logAction(2L, AuditActionEnum.PRODUCT_REJECT, AuditTargetTypeEnum.PRODUCT, 
                                 201L, "集成测试：拒绝商品1", "192.168.1.103", "Mozilla/5.0", false);
        
        auditLogService.logAction(1L, AuditActionEnum.SYSTEM_CONFIG_UPDATE, AuditTargetTypeEnum.SYSTEM, 
                                 null, "集成测试：更新系统配置", "192.168.1.104", "Mozilla/5.0", true);
    }
}
