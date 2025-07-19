package com.shiwu.admin.dao;

import com.shiwu.common.util.DBUtil;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.user.dao.FollowDao;
import com.shiwu.user.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 仪表盘相关DAO测试类
 * 测试统计查询功能
 */
public class DashboardDaoTest {
    
    private UserDao userDao;
    private ProductDao productDao;
    private AuditLogDao auditLogDao;
    private FollowDao followDao;
    
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    
    @BeforeEach
    public void setUp() {
        userDao = new UserDao();
        productDao = new ProductDao();
        auditLogDao = new AuditLogDao();
        followDao = new FollowDao();
        
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }
    
    /**
     * 测试用户总数统计
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetTotalUserCount() throws SQLException {
        // Given: 模拟数据库返回用户总数
        Long expectedCount = 100L;
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong(1)).thenReturn(expectedCount);
            
            // When: 调用获取用户总数方法
            Long actualCount = userDao.getTotalUserCount();
            
            // Then: 验证结果
            assertEquals(expectedCount, actualCount, "用户总数应该匹配");
            
            // 验证SQL查询
            verify(mockConnection).prepareStatement("SELECT COUNT(*) FROM system_user WHERE is_deleted = 0");
            verify(mockPreparedStatement).executeQuery();
            verify(mockResultSet).getLong(1);
        }
    }
    
    /**
     * 测试活跃用户数统计
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetActiveUserCount() throws SQLException {
        // Given: 模拟数据库返回活跃用户数
        Long expectedCount = 50L;
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong(1)).thenReturn(expectedCount);
            
            // When: 调用获取活跃用户数方法
            Long actualCount = userDao.getActiveUserCount();
            
            // Then: 验证结果
            assertEquals(expectedCount, actualCount, "活跃用户数应该匹配");
            
            // 验证SQL查询包含30天条件
            verify(mockConnection).prepareStatement(contains("DATE_SUB(NOW(), INTERVAL 30 DAY)"));
        }
    }
    
    /**
     * 测试新增用户数统计
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetNewUserCount() throws SQLException {
        // Given: 模拟数据库返回新增用户数
        Long expectedCount = 10L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong(1)).thenReturn(expectedCount);
            
            // When: 调用获取新增用户数方法
            Long actualCount = userDao.getNewUserCount(startTime, endTime);
            
            // Then: 验证结果
            assertEquals(expectedCount, actualCount, "新增用户数应该匹配");
            
            // 验证参数设置
            verify(mockPreparedStatement).setObject(1, startTime);
            verify(mockPreparedStatement).setObject(2, endTime);
        }
    }
    
    /**
     * 测试商品总数统计
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetTotalProductCount() throws SQLException {
        // Given: 模拟数据库返回商品总数
        Long expectedCount = 200L;
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong(1)).thenReturn(expectedCount);
            
            // When: 调用获取商品总数方法
            Long actualCount = productDao.getTotalProductCount();
            
            // Then: 验证结果
            assertEquals(expectedCount, actualCount, "商品总数应该匹配");
            
            // 验证SQL查询
            verify(mockConnection).prepareStatement("SELECT COUNT(*) FROM product WHERE is_deleted = 0");
        }
    }
    
    /**
     * 测试指定状态商品数统计
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetProductCountByStatus() throws SQLException {
        // Given: 模拟数据库返回指定状态商品数
        Long expectedCount = 30L;
        Integer status = 1; // 在售状态
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong(1)).thenReturn(expectedCount);
            
            // When: 调用获取指定状态商品数方法
            Long actualCount = productDao.getProductCountByStatus(status);
            
            // Then: 验证结果
            assertEquals(expectedCount, actualCount, "指定状态商品数应该匹配");
            
            // 验证参数设置
            verify(mockPreparedStatement).setInt(1, status);
        }
    }
    
    /**
     * 测试审计日志数量统计
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetAuditLogCount() throws SQLException {
        // Given: 模拟数据库返回审计日志数量
        Long expectedCount = 15L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong(1)).thenReturn(expectedCount);
            
            // When: 调用获取审计日志数量方法
            Long actualCount = auditLogDao.getAuditLogCount(startTime, endTime);
            
            // Then: 验证结果
            assertEquals(expectedCount, actualCount, "审计日志数量应该匹配");
            
            // 验证参数设置
            verify(mockPreparedStatement).setObject(1, startTime);
            verify(mockPreparedStatement).setObject(2, endTime);
        }
    }
    
    /**
     * 测试关注关系总数统计
     * BCDE原则中的Correct：测试正确的业务逻辑
     */
    @Test
    public void testGetTotalFollowCount() throws SQLException {
        // Given: 模拟数据库返回关注关系总数
        Long expectedCount = 80L;
        
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong(1)).thenReturn(expectedCount);
            
            // When: 调用获取关注关系总数方法
            Long actualCount = followDao.getTotalFollowCount();
            
            // Then: 验证结果
            assertEquals(expectedCount, actualCount, "关注关系总数应该匹配");
            
            // 验证SQL查询
            verify(mockConnection).prepareStatement("SELECT COUNT(*) FROM user_follow WHERE is_deleted = 0");
        }
    }
    
    /**
     * 测试数据库连接异常处理
     * BCDE原则中的Error：测试错误条件
     */
    @Test
    public void testDatabaseConnectionError() {
        // Given: 模拟数据库连接失败
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(null);
            
            // When: 调用统计方法
            Long result = userDao.getTotalUserCount();
            
            // Then: 应该返回默认值0
            assertEquals(0L, result, "连接失败时应该返回0");
        }
    }
    
    /**
     * 测试SQL异常处理
     * BCDE原则中的Error：测试错误条件
     */
    @Test
    public void testSQLException() throws SQLException {
        // Given: 模拟SQL执行异常
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            // When: 调用统计方法
            Long result = userDao.getTotalUserCount();
            
            // Then: 应该返回默认值0
            assertEquals(0L, result, "SQL异常时应该返回0");
        }
    }
}
