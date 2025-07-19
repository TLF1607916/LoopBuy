package com.shiwu.admin.dao;

import com.shiwu.admin.dto.AuditLogQueryDTO;
import com.shiwu.admin.model.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuditLogDao单元测试
 * 测试NFR-SEC-03要求的审计日志功能
 */
public class AuditLogDaoTest {
    
    private AuditLogDao auditLogDao;
    
    @BeforeEach
    void setUp() {
        auditLogDao = new AuditLogDao();
    }
    
    @Test
    void testCreateAuditLog_Success() {
        // 准备测试数据
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(1L);
        auditLog.setAction("USER_BAN");
        auditLog.setTargetType("USER");
        auditLog.setTargetId(123L);
        auditLog.setDetails("封禁用户测试");
        auditLog.setIpAddress("192.168.1.100");
        auditLog.setUserAgent("Mozilla/5.0 Test Browser");
        auditLog.setResult(1);
        
        // 执行测试
        Long logId = auditLogDao.createAuditLog(auditLog);
        
        // 验证结果
        assertNotNull(logId);
        assertTrue(logId > 0);
        System.out.println("✓ 创建审计日志成功: ID=" + logId);
    }
    
    @Test
    void testCreateAuditLog_NullAuditLog() {
        // 执行测试
        Long logId = auditLogDao.createAuditLog(null);
        
        // 验证结果
        assertNull(logId);
        System.out.println("✓ 正确处理空审计日志对象");
    }
    
    @Test
    void testCreateAuditLog_NullAdminId() {
        // 准备测试数据
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(null);
        auditLog.setAction("USER_BAN");
        auditLog.setResult(1);
        
        // 执行测试
        Long logId = auditLogDao.createAuditLog(auditLog);
        
        // 验证结果
        assertNull(logId);
        System.out.println("✓ 正确处理空管理员ID");
    }
    
    @Test
    void testLogAdminLogin_Success() {
        // 执行测试
        Long logId = auditLogDao.logAdminLogin(1L, "192.168.1.100", 
                                              "Mozilla/5.0 Test Browser", true, "登录成功");
        
        // 验证结果
        assertNotNull(logId);
        assertTrue(logId > 0);
        System.out.println("✓ 记录管理员登录日志成功: ID=" + logId);
    }
    
    @Test
    void testLogAdminLogin_Failed() {
        // 执行测试
        Long logId = auditLogDao.logAdminLogin(1L, "192.168.1.100", 
                                              "Mozilla/5.0 Test Browser", false, "密码错误");
        
        // 验证结果
        assertNotNull(logId);
        assertTrue(logId > 0);
        System.out.println("✓ 记录管理员登录失败日志成功: ID=" + logId);
    }
    
    @Test
    void testFindById_Success() {
        // 先创建一个审计日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(1L);
        auditLog.setAction("USER_MUTE");
        auditLog.setTargetType("USER");
        auditLog.setTargetId(456L);
        auditLog.setDetails("禁言用户测试");
        auditLog.setIpAddress("192.168.1.101");
        auditLog.setUserAgent("Mozilla/5.0 Test Browser");
        auditLog.setResult(1);
        
        Long logId = auditLogDao.createAuditLog(auditLog);
        assertNotNull(logId);
        
        // 执行测试
        AuditLog foundLog = auditLogDao.findById(logId);
        
        // 验证结果
        assertNotNull(foundLog);
        assertEquals(logId, foundLog.getId());
        assertEquals(1L, foundLog.getAdminId());
        assertEquals("USER_MUTE", foundLog.getAction());
        assertEquals("USER", foundLog.getTargetType());
        assertEquals(456L, foundLog.getTargetId());
        assertEquals("禁言用户测试", foundLog.getDetails());
        assertEquals("192.168.1.101", foundLog.getIpAddress());
        assertEquals("Mozilla/5.0 Test Browser", foundLog.getUserAgent());
        assertEquals(1, foundLog.getResult());
        assertNotNull(foundLog.getCreateTime());
        
        System.out.println("✓ 根据ID查找审计日志成功");
    }
    
    @Test
    void testFindById_NotFound() {
        // 执行测试
        AuditLog foundLog = auditLogDao.findById(999999L);
        
        // 验证结果
        assertNull(foundLog);
        System.out.println("✓ 正确处理不存在的审计日志ID");
    }
    
    @Test
    void testFindById_NullId() {
        // 执行测试
        AuditLog foundLog = auditLogDao.findById(null);
        
        // 验证结果
        assertNull(foundLog);
        System.out.println("✓ 正确处理空ID");
    }
    
    @Test
    void testFindAuditLogs_BasicQuery() {
        // 先创建一些测试数据
        createTestAuditLogs();
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        
        // 执行测试
        List<AuditLog> auditLogs = auditLogDao.findAuditLogs(queryDTO);
        
        // 验证结果
        assertNotNull(auditLogs);
        assertTrue(auditLogs.size() > 0);
        System.out.println("✓ 基本查询审计日志成功: 共" + auditLogs.size() + "条记录");
    }
    
    @Test
    void testFindAuditLogs_WithFilters() {
        // 先创建一些测试数据
        createTestAuditLogs();
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setAdminId(1L);
        queryDTO.setAction("USER_BAN");
        queryDTO.setResult(1);
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        
        // 执行测试
        List<AuditLog> auditLogs = auditLogDao.findAuditLogs(queryDTO);
        
        // 验证结果
        assertNotNull(auditLogs);
        for (AuditLog log : auditLogs) {
            assertEquals(1L, log.getAdminId());
            assertEquals("USER_BAN", log.getAction());
            assertEquals(1, log.getResult());
        }
        System.out.println("✓ 带过滤条件查询审计日志成功: 共" + auditLogs.size() + "条记录");
    }
    
    @Test
    void testFindAuditLogs_WithTimeRange() {
        // 先创建一些测试数据
        createTestAuditLogs();
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setStartTime(LocalDateTime.now().minusDays(1));
        queryDTO.setEndTime(LocalDateTime.now().plusDays(1));
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        
        // 执行测试
        List<AuditLog> auditLogs = auditLogDao.findAuditLogs(queryDTO);
        
        // 验证结果
        assertNotNull(auditLogs);
        System.out.println("✓ 时间范围查询审计日志成功: 共" + auditLogs.size() + "条记录");
    }
    
    @Test
    void testFindAuditLogs_WithKeyword() {
        // 先创建一些测试数据
        createTestAuditLogs();
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setKeyword("测试");
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        
        // 执行测试
        List<AuditLog> auditLogs = auditLogDao.findAuditLogs(queryDTO);
        
        // 验证结果
        assertNotNull(auditLogs);
        for (AuditLog log : auditLogs) {
            assertTrue(log.getDetails().contains("测试"));
        }
        System.out.println("✓ 关键词查询审计日志成功: 共" + auditLogs.size() + "条记录");
    }
    
    @Test
    void testFindAuditLogs_NullQuery() {
        // 执行测试
        List<AuditLog> auditLogs = auditLogDao.findAuditLogs(null);
        
        // 验证结果
        assertNotNull(auditLogs);
        assertEquals(0, auditLogs.size());
        System.out.println("✓ 正确处理空查询条件");
    }
    
    @Test
    void testCountAuditLogs_Success() {
        // 先创建一些测试数据
        createTestAuditLogs();
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        
        // 执行测试
        long count = auditLogDao.countAuditLogs(queryDTO);
        
        // 验证结果
        assertTrue(count > 0);
        System.out.println("✓ 统计审计日志数量成功: 共" + count + "条记录");
    }
    
    @Test
    void testCountAuditLogs_WithFilters() {
        // 先创建一些测试数据
        createTestAuditLogs();
        
        // 准备查询条件
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        queryDTO.setAdminId(1L);
        queryDTO.setResult(1);
        
        // 执行测试
        long count = auditLogDao.countAuditLogs(queryDTO);
        
        // 验证结果
        assertTrue(count >= 0);
        System.out.println("✓ 带过滤条件统计审计日志数量成功: 共" + count + "条记录");
    }
    
    @Test
    void testCountAuditLogs_NullQuery() {
        // 执行测试
        long count = auditLogDao.countAuditLogs(null);
        
        // 验证结果
        assertEquals(0, count);
        System.out.println("✓ 正确处理空查询条件的统计");
    }
    
    /**
     * 创建测试用的审计日志数据
     */
    private void createTestAuditLogs() {
        // 创建几条不同类型的审计日志
        AuditLog log1 = new AuditLog();
        log1.setAdminId(1L);
        log1.setAction("USER_BAN");
        log1.setTargetType("USER");
        log1.setTargetId(100L);
        log1.setDetails("封禁用户测试");
        log1.setIpAddress("192.168.1.100");
        log1.setUserAgent("Mozilla/5.0 Test Browser");
        log1.setResult(1);
        auditLogDao.createAuditLog(log1);
        
        AuditLog log2 = new AuditLog();
        log2.setAdminId(1L);
        log2.setAction("USER_MUTE");
        log2.setTargetType("USER");
        log2.setTargetId(101L);
        log2.setDetails("禁言用户测试");
        log2.setIpAddress("192.168.1.101");
        log2.setUserAgent("Mozilla/5.0 Test Browser");
        log2.setResult(1);
        auditLogDao.createAuditLog(log2);
        
        AuditLog log3 = new AuditLog();
        log3.setAdminId(2L);
        log3.setAction("PRODUCT_APPROVE");
        log3.setTargetType("PRODUCT");
        log3.setTargetId(200L);
        log3.setDetails("审核通过商品");
        log3.setIpAddress("192.168.1.102");
        log3.setUserAgent("Mozilla/5.0 Test Browser");
        log3.setResult(1);
        auditLogDao.createAuditLog(log3);
    }
}
