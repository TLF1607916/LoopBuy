package com.shiwu.integration;

import com.shiwu.common.util.DBUtil;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库连接池集成测试
 * 测试数据库连接、连接池性能和并发访问
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("数据库连接池集成测试")
public class DatabaseIntegrationTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseIntegrationTest.class);
    
    @BeforeAll
    public static void setUpClass() {
        logger.info("开始数据库连接池集成测试");
    }
    
    @Test
    @Order(1)
    @DisplayName("1.1 数据库连接基础测试")
    public void testBasicDatabaseConnection() {
        logger.info("测试基础数据库连接");
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            assertNotNull(conn, "数据库连接不应为null");
            assertFalse(conn.isClosed(), "数据库连接应该是打开的");
            
            // 测试基本查询
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 as test_value");
            assertTrue(rs.next(), "查询应该返回结果");
            assertEquals(1, rs.getInt("test_value"), "查询结果应该正确");
            
            rs.close();
            stmt.close();
            
            logger.info("基础数据库连接测试通过");
        } catch (SQLException e) {
            fail("数据库连接失败: " + e.getMessage());
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("1.2 数据库连接池配置测试")
    public void testConnectionPoolConfiguration() {
        logger.info("测试数据库连接池配置");
        
        // 测试多个连接的获取和释放
        List<Connection> connections = new ArrayList<>();
        
        try {
            // 获取多个连接
            for (int i = 0; i < 5; i++) {
                Connection conn = DBUtil.getConnection();
                assertNotNull(conn, "第" + (i + 1) + "个连接不应为null");
                assertFalse(conn.isClosed(), "第" + (i + 1) + "个连接应该是打开的");
                connections.add(conn);
            }
            
            logger.info("成功获取了 " + connections.size() + " 个数据库连接");
            
            // 测试连接是否可用
            for (int i = 0; i < connections.size(); i++) {
                Connection conn = connections.get(i);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT " + (i + 1) + " as conn_test");
                assertTrue(rs.next(), "连接" + (i + 1) + "应该可以执行查询");
                assertEquals(i + 1, rs.getInt("conn_test"), "查询结果应该正确");
                rs.close();
                stmt.close();
            }
            
            logger.info("连接池配置测试通过");
        } catch (SQLException e) {
            fail("连接池配置测试失败: " + e.getMessage());
        } finally {
            // 释放所有连接
            for (Connection conn : connections) {
                DBUtil.closeConnection(conn);
            }
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("1.3 数据库事务测试")
    public void testDatabaseTransaction() {
        logger.info("测试数据库事务");
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务
            
            // 创建测试表（如果不存在）
            Statement stmt = conn.createStatement();
            try {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS test_transaction (" +
                                 "id INT PRIMARY KEY AUTO_INCREMENT, " +
                                 "test_value VARCHAR(50))");
            } catch (SQLException e) {
                // 表可能已存在，忽略错误
                logger.debug("测试表创建警告: " + e.getMessage());
            }
            
            // 插入测试数据
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO test_transaction (test_value) VALUES (?)");
            pstmt.setString(1, "transaction_test_" + System.currentTimeMillis());
            int result = pstmt.executeUpdate();
            assertEquals(1, result, "应该插入一条记录");
            
            // 提交事务
            conn.commit();
            
            // 验证数据已提交
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) as count FROM test_transaction WHERE test_value LIKE 'transaction_test_%'");
            assertTrue(rs.next(), "查询应该返回结果");
            assertTrue(rs.getInt("count") > 0, "应该有测试数据");
            
            rs.close();
            pstmt.close();
            stmt.close();
            
            logger.info("数据库事务测试通过");
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.error("事务回滚失败", rollbackEx);
                }
            }
            fail("数据库事务测试失败: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 恢复自动提交
                } catch (SQLException e) {
                    logger.error("恢复自动提交失败", e);
                }
            }
            DBUtil.closeConnection(conn);
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("1.4 数据库并发访问测试")
    public void testConcurrentDatabaseAccess() {
        logger.info("测试数据库并发访问");
        
        int threadCount = 10;
        int operationsPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<Boolean>> futures = new ArrayList<>();
        
        // 创建并发任务
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Future<Boolean> future = executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        Connection conn = DBUtil.getConnection();
                        assertNotNull(conn, "线程" + threadId + "获取连接失败");
                        
                        // 执行简单查询
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT " + threadId + " as thread_id, " + j + " as operation_id");
                        assertTrue(rs.next(), "线程" + threadId + "查询失败");
                        assertEquals(threadId, rs.getInt("thread_id"), "线程ID应该正确");
                        assertEquals(j, rs.getInt("operation_id"), "操作ID应该正确");
                        
                        rs.close();
                        stmt.close();
                        DBUtil.closeConnection(conn);
                        
                        // 短暂休眠模拟实际操作
                        Thread.sleep(10);
                    }
                    return true;
                } catch (Exception e) {
                    logger.error("线程" + threadId + "执行失败", e);
                    return false;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }
        
        try {
            // 等待所有任务完成
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertTrue(completed, "所有并发任务应该在30秒内完成");
            
            // 检查所有任务的结果
            for (int i = 0; i < futures.size(); i++) {
                Future<Boolean> future = futures.get(i);
                assertTrue(future.get(), "线程" + i + "应该执行成功");
            }
            
            logger.info("数据库并发访问测试通过");
        } catch (InterruptedException | ExecutionException e) {
            fail("并发访问测试失败: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("1.5 数据库连接泄漏测试")
    public void testConnectionLeakPrevention() {
        logger.info("测试数据库连接泄漏预防");
        
        // 模拟连接泄漏场景
        List<Connection> connections = new ArrayList<>();
        
        try {
            // 获取大量连接但不立即释放
            for (int i = 0; i < 20; i++) {
                try {
                    Connection conn = DBUtil.getConnection();
                    if (conn != null) {
                        connections.add(conn);
                        
                        // 执行简单查询确保连接可用
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT 1");
                        assertTrue(rs.next(), "连接" + i + "应该可用");
                        rs.close();
                        stmt.close();
                    }
                } catch (SQLException e) {
                    // 连接池可能已满，这是正常的
                    logger.info("连接" + i + "获取失败（连接池可能已满）: " + e.getMessage());
                    break;
                }
            }
            
            logger.info("获取了 " + connections.size() + " 个连接");
            
            // 逐个释放连接
            int releasedCount = 0;
            for (Connection conn : connections) {
                if (conn != null && !conn.isClosed()) {
                    DBUtil.closeConnection(conn);
                    releasedCount++;
                }
            }
            
            logger.info("释放了 " + releasedCount + " 个连接");
            
            // 验证连接池恢复正常
            Connection testConn = DBUtil.getConnection();
            assertNotNull(testConn, "释放连接后应该能够获取新连接");
            DBUtil.closeConnection(testConn);
            
            logger.info("连接泄漏预防测试通过");
        } catch (SQLException e) {
            fail("连接泄漏测试失败: " + e.getMessage());
        } finally {
            // 确保所有连接都被释放
            for (Connection conn : connections) {
                DBUtil.closeConnection(conn);
            }
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("1.6 数据库性能基准测试")
    public void testDatabasePerformanceBenchmark() {
        logger.info("测试数据库性能基准");
        
        int iterations = 100;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            Connection conn = null;
            try {
                conn = DBUtil.getConnection();
                assertNotNull(conn, "连接" + i + "不应为null");
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT " + i + " as iteration");
                assertTrue(rs.next(), "查询" + i + "应该返回结果");
                assertEquals(i, rs.getInt("iteration"), "查询结果应该正确");
                
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                fail("性能测试第" + i + "次迭代失败: " + e.getMessage());
            } finally {
                DBUtil.closeConnection(conn);
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / iterations;
        
        logger.info("完成 " + iterations + " 次数据库操作");
        logger.info("总耗时: " + totalTime + "ms");
        logger.info("平均耗时: " + String.format("%.2f", avgTime) + "ms/操作");
        
        // 性能断言（平均每次操作不应超过100ms）
        assertTrue(avgTime < 100, "平均操作时间应该小于100ms，实际: " + avgTime + "ms");
        
        logger.info("数据库性能基准测试通过");
    }
    
    @Test
    @Order(7)
    @DisplayName("1.7 数据库连接超时测试")
    public void testConnectionTimeout() {
        logger.info("测试数据库连接超时");
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            assertNotNull(conn, "连接不应为null");
            
            // 设置查询超时
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(5); // 5秒超时
            
            // 执行快速查询
            ResultSet rs = stmt.executeQuery("SELECT 1 as quick_test");
            assertTrue(rs.next(), "快速查询应该成功");
            assertEquals(1, rs.getInt("quick_test"), "查询结果应该正确");
            
            rs.close();
            stmt.close();
            
            logger.info("连接超时测试通过");
        } catch (SQLException e) {
            fail("连接超时测试失败: " + e.getMessage());
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
    
    @Test
    @Order(8)
    @DisplayName("1.8 数据库连接验证测试")
    public void testConnectionValidation() {
        logger.info("测试数据库连接验证");
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            assertNotNull(conn, "连接不应为null");
            
            // 测试连接有效性
            assertTrue(conn.isValid(5), "连接应该在5秒内有效");
            assertFalse(conn.isClosed(), "连接不应该是关闭的");
            
            // 测试连接元数据
            DatabaseMetaData metaData = conn.getMetaData();
            assertNotNull(metaData, "数据库元数据不应为null");
            
            String databaseProductName = metaData.getDatabaseProductName();
            assertNotNull(databaseProductName, "数据库产品名称不应为null");
            logger.info("数据库产品: " + databaseProductName);
            
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            assertNotNull(databaseProductVersion, "数据库版本不应为null");
            logger.info("数据库版本: " + databaseProductVersion);
            
            logger.info("连接验证测试通过");
        } catch (SQLException e) {
            fail("连接验证测试失败: " + e.getMessage());
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
    
    @AfterAll
    public static void tearDownClass() {
        logger.info("数据库连接池集成测试完成");
    }
}
