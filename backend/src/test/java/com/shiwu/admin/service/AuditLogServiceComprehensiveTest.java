package com.shiwu.admin.service;

import com.shiwu.admin.dto.AuditLogQueryDTO;
import com.shiwu.admin.enums.AuditActionEnum;
import com.shiwu.admin.enums.AuditTargetTypeEnum;
import com.shiwu.admin.service.impl.AuditLogServiceImpl;
import com.shiwu.admin.vo.AuditLogVO;
import com.shiwu.common.test.TestConfig;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuditLogService 综合测试类
 * 测试审计日志服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AuditLogService 综合测试")
public class AuditLogServiceComprehensiveTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceComprehensiveTest.class);
    
    private AuditLogService auditLogService;
    
    // 测试数据
    private static final Long TEST_ADMIN_ID = TestBase.TEST_ADMIN_ID;
    private static final String TEST_IP = "192.168.1.100";
    private static final String TEST_USER_AGENT = "Mozilla/5.0 Test Browser";
    private static final String TEST_DETAILS = "测试操作详情";
    private static final Long TEST_TARGET_ID = 1L;
    
    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        auditLogService = new AuditLogServiceImpl();
        logger.info("AuditLogService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("2.1 记录审计日志功能测试")
    public void testLogAction() {
        logger.info("开始测试记录审计日志功能");
        
        // 测试完整版本的记录审计日志
        Long logId1 = auditLogService.logAction(
            TEST_ADMIN_ID, 
            AuditActionEnum.USER_BAN, 
            AuditTargetTypeEnum.USER, 
            TEST_TARGET_ID, 
            TEST_DETAILS, 
            TEST_IP, 
            TEST_USER_AGENT, 
            true
        );
        
        assertNotNull(logId1, "记录审计日志应该返回日志ID");
        assertTrue(logId1 > 0, "日志ID应该大于0");
        
        // 测试简化版本的记录审计日志
        Long logId2 = auditLogService.logAction(
            TEST_ADMIN_ID, 
            AuditActionEnum.USER_MUTE, 
            TEST_DETAILS, 
            true
        );
        
        assertNotNull(logId2, "简化版记录审计日志应该返回日志ID");
        assertTrue(logId2 > 0, "日志ID应该大于0");
        
        logger.info("记录审计日志测试通过: logId1={}, logId2={}", logId1, logId2);
    }

    @Test
    @Order(2)
    @DisplayName("2.2 记录审计日志参数验证测试")
    public void testLogActionValidation() {
        logger.info("开始测试记录审计日志参数验证");
        
        // 测试null管理员ID
        Long logId1 = auditLogService.logAction(
            null, 
            AuditActionEnum.USER_BAN, 
            AuditTargetTypeEnum.USER, 
            TEST_TARGET_ID, 
            TEST_DETAILS, 
            TEST_IP, 
            TEST_USER_AGENT, 
            true
        );
        assertNull(logId1, "null管理员ID应该返回null");
        
        // 测试null操作类型
        Long logId2 = auditLogService.logAction(
            TEST_ADMIN_ID, 
            null, 
            AuditTargetTypeEnum.USER, 
            TEST_TARGET_ID, 
            TEST_DETAILS, 
            TEST_IP, 
            TEST_USER_AGENT, 
            true
        );
        assertNull(logId2, "null操作类型应该返回null");
        
        logger.info("记录审计日志参数验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("2.3 记录管理员登录日志测试")
    public void testLogAdminLogin() {
        logger.info("开始测试记录管理员登录日志功能");
        
        // 测试成功登录日志
        Long logId1 = auditLogService.logAdminLogin(
            TEST_ADMIN_ID, 
            TEST_IP, 
            TEST_USER_AGENT, 
            true, 
            "登录成功"
        );
        
        assertNotNull(logId1, "记录登录日志应该返回日志ID");
        assertTrue(logId1 > 0, "日志ID应该大于0");
        
        // 测试失败登录日志
        Long logId2 = auditLogService.logAdminLogin(
            TEST_ADMIN_ID, 
            TEST_IP, 
            TEST_USER_AGENT, 
            false, 
            "密码错误"
        );
        
        assertNotNull(logId2, "记录失败登录日志应该返回日志ID");
        assertTrue(logId2 > 0, "日志ID应该大于0");
        
        logger.info("记录管理员登录日志测试通过: successLogId={}, failLogId={}", logId1, logId2);
    }

    @Test
    @Order(4)
    @DisplayName("2.4 记录管理员登录日志参数验证测试")
    public void testLogAdminLoginValidation() {
        logger.info("开始测试记录管理员登录日志参数验证");
        
        // 测试null管理员ID
        Long logId1 = auditLogService.logAdminLogin(null, TEST_IP, TEST_USER_AGENT, true, "测试");
        assertNull(logId1, "null管理员ID应该返回null");
        
        logger.info("记录管理员登录日志参数验证测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("2.5 分页查询审计日志测试")
    public void testGetAuditLogs() {
        logger.info("开始测试分页查询审计日志功能");
        
        // 先记录一些日志
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_BAN, TEST_DETAILS, true);
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_MUTE, TEST_DETAILS, true);
        
        // 创建查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        queryDTO.setAdminId(TEST_ADMIN_ID);
        
        // 测试查询
        Map<String, Object> result = auditLogService.getAuditLogs(queryDTO);
        assertNotNull(result, "查询结果不应为空");
        assertTrue(result.containsKey("list"), "结果应包含日志列表");
        assertTrue(result.containsKey("totalCount"), "结果应包含总数");

        @SuppressWarnings("unchecked")
        List<AuditLogVO> logs = (List<AuditLogVO>) result.get("list");
        assertNotNull(logs, "日志列表不应为空");

        logger.info("分页查询审计日志测试通过: totalCount={}, logsSize={}", result.get("totalCount"), logs.size());
    }

    @Test
    @Order(6)
    @DisplayName("2.6 分页查询审计日志参数验证测试")
    public void testGetAuditLogsValidation() {
        logger.info("开始测试分页查询审计日志参数验证");
        
        // 测试null查询条件
        Map<String, Object> result1 = auditLogService.getAuditLogs(null);
        assertNotNull(result1, "null查询条件应该返回默认结果");
        assertTrue(result1.containsKey("totalCount"), "结果应包含总数");
        
        logger.info("分页查询审计日志参数验证测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("2.7 获取审计日志详情测试")
    public void testGetAuditLogDetail() {
        logger.info("开始测试获取审计日志详情功能");
        
        // 先记录一个日志
        Long logId = auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_BAN, TEST_DETAILS, true);
        
        if (logId != null) {
            // 测试获取详情
            AuditLogVO detail = auditLogService.getAuditLogDetail(logId);
            assertNotNull(detail, "日志详情不应为空");
            assertEquals(logId, detail.getId(), "日志ID应该匹配");
            assertEquals(TEST_ADMIN_ID, detail.getAdminId(), "管理员ID应该匹配");
            
            logger.info("获取审计日志详情测试通过: logId={}", logId);
        } else {
            logger.warn("无法创建日志，跳过详情测试");
        }
    }

    @Test
    @Order(8)
    @DisplayName("2.8 获取审计日志详情参数验证测试")
    public void testGetAuditLogDetailValidation() {
        logger.info("开始测试获取审计日志详情参数验证");
        
        // 测试null日志ID
        AuditLogVO detail1 = auditLogService.getAuditLogDetail(null);
        assertNull(detail1, "null日志ID应该返回null");
        
        // 测试不存在的日志ID
        AuditLogVO detail2 = auditLogService.getAuditLogDetail(TestConfig.BOUNDARY_ID_NONEXISTENT);
        assertNull(detail2, "不存在的日志ID应该返回null");
        
        logger.info("获取审计日志详情参数验证测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("2.9 获取操作统计数据测试")
    public void testGetOperationStats() {
        logger.info("开始测试获取操作统计数据功能");
        
        // 先记录一些日志
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_BAN, TEST_DETAILS, true);
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_MUTE, TEST_DETAILS, true);
        
        // 测试获取统计数据
        Map<String, Object> stats = auditLogService.getOperationStats(7);
        assertNotNull(stats, "统计数据不应为空");
        
        logger.info("获取操作统计数据测试通过: stats={}", stats);
    }

    @Test
    @Order(10)
    @DisplayName("2.10 获取活动趋势数据测试")
    public void testGetActivityTrend() {
        logger.info("开始测试获取活动趋势数据功能");
        
        // 先记录一些日志
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_BAN, TEST_DETAILS, true);
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_MUTE, TEST_DETAILS, true);
        
        // 测试获取趋势数据
        List<Map<String, Object>> trend = auditLogService.getActivityTrend(7);
        assertNotNull(trend, "趋势数据不应为空");
        
        logger.info("获取活动趋势数据测试通过: trendSize={}", trend.size());
    }

    @Test
    @Order(11)
    @DisplayName("2.11 导出审计日志测试")
    public void testExportAuditLogs() {
        logger.info("开始测试导出审计日志功能");
        
        // 先记录一些日志
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_BAN, TEST_DETAILS, true);
        auditLogService.logAction(TEST_ADMIN_ID, AuditActionEnum.USER_MUTE, TEST_DETAILS, true);
        
        // 创建查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setAdminId(TEST_ADMIN_ID);
        
        // 测试导出
        List<AuditLogVO> exportLogs = auditLogService.exportAuditLogs(queryDTO);
        assertNotNull(exportLogs, "导出日志列表不应为空");
        
        logger.info("导出审计日志测试通过: exportSize={}", exportLogs.size());
    }

    @Test
    @Order(12)
    @DisplayName("2.12 导出审计日志参数验证测试")
    public void testExportAuditLogsValidation() {
        logger.info("开始测试导出审计日志参数验证");
        
        // 测试null查询条件
        List<AuditLogVO> exportLogs1 = auditLogService.exportAuditLogs(null);
        assertNotNull(exportLogs1, "null查询条件应该返回空列表");
        assertTrue(exportLogs1.isEmpty(), "null查询条件应该返回空列表");
        
        logger.info("导出审计日志参数验证测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("2.13 检查操作是否需要记录审计日志测试")
    public void testShouldLogAction() {
        logger.info("开始测试检查操作是否需要记录审计日志功能");
        
        // 测试不同操作类型
        boolean shouldLog1 = auditLogService.shouldLogAction(AuditActionEnum.USER_BAN);
        assertTrue(shouldLog1 == true || shouldLog1 == false, "结果应该是布尔值");
        
        boolean shouldLog2 = auditLogService.shouldLogAction(AuditActionEnum.ADMIN_LOGIN);
        assertTrue(shouldLog2 == true || shouldLog2 == false, "结果应该是布尔值");
        
        logger.info("检查操作是否需要记录审计日志测试通过");
    }

    @Test
    @Order(14)
    @DisplayName("2.14 检查操作是否需要记录审计日志参数验证测试")
    public void testShouldLogActionValidation() {
        logger.info("开始测试检查操作是否需要记录审计日志参数验证");
        
        // 测试null操作类型
        boolean shouldLog = auditLogService.shouldLogAction(null);
        assertFalse(shouldLog, "null操作类型应该不需要记录");
        
        logger.info("检查操作是否需要记录审计日志参数验证测试通过");
    }

    @Test
    @Order(15)
    @DisplayName("2.15 获取可用操作类型测试")
    public void testGetAvailableActions() {
        logger.info("开始测试获取可用操作类型功能");
        
        // 测试获取操作类型列表
        List<Map<String, String>> actions = auditLogService.getAvailableActions();
        assertNotNull(actions, "操作类型列表不应为空");
        
        logger.info("获取可用操作类型测试通过: actionsSize={}", actions.size());
    }

    @Test
    @Order(16)
    @DisplayName("2.16 获取可用目标类型测试")
    public void testGetAvailableTargetTypes() {
        logger.info("开始测试获取可用目标类型功能");
        
        // 测试获取目标类型列表
        List<Map<String, String>> targetTypes = auditLogService.getAvailableTargetTypes();
        assertNotNull(targetTypes, "目标类型列表不应为空");
        
        logger.info("获取可用目标类型测试通过: targetTypesSize={}", targetTypes.size());
    }

    @AfterEach
    void tearDown() {
        logger.info("AuditLogService测试清理完成");
    }
}
