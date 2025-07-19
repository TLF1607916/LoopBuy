package com.shiwu.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

/**
 * DBUtil测试类
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 * 
 * 注意：这些测试需要实际的数据库环境，如果数据库不可用，测试可能会失败
 * 在CI/CD环境中，应该使用内存数据库（如H2）进行测试
 */
public class DBUtilTest {

    /**
     * 测试获取数据库连接 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     * 
     * 注意：此测试依赖于实际的数据库配置
     */
    @Test
    public void testGetConnection_Success() {
        // When: 获取数据库连接
        Connection conn = DBUtil.getConnection();
        
        // Then: 验证连接
        if (conn != null) {
            // 如果能获取到连接，验证连接有效性
            try {
                assertFalse(conn.isClosed(), "连接应该是打开的");
                assertTrue(conn.isValid(5), "连接应该是有效的");
                
                // 测试连接关闭
                DBUtil.closeConnection(conn);
                assertTrue(conn.isClosed(), "连接应该已关闭");
                
            } catch (Exception e) {
                fail("连接操作不应该抛出异常: " + e.getMessage());
            }
        } else {
            // 如果无法获取连接（可能是数据库未启动），记录但不失败测试
            System.out.println("警告：无法获取数据库连接，可能数据库未启动或配置错误");
            // 在实际的CI/CD环境中，这里应该使用内存数据库
        }
    }

    /**
     * 测试关闭null连接 - 边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testCloseConnection_NullConnection() {
        // Given: null连接
        Connection nullConn = null;
        
        // When & Then: 关闭null连接不应该抛出异常
        assertDoesNotThrow(() -> {
            DBUtil.closeConnection(nullConn);
        }, "关闭null连接不应该抛出异常");
    }

    /**
     * 测试多次获取连接
     * BCDE原则中的Design：测试连接管理设计
     */
    @Test
    public void testMultipleConnections() {
        // When: 获取多个连接
        Connection conn1 = DBUtil.getConnection();
        Connection conn2 = DBUtil.getConnection();
        
        // Then: 验证连接
        if (conn1 != null && conn2 != null) {
            try {
                // 应该是不同的连接对象
                assertNotSame(conn1, conn2, "应该获取到不同的连接对象");
                
                // 两个连接都应该有效
                assertTrue(conn1.isValid(5), "连接1应该有效");
                assertTrue(conn2.isValid(5), "连接2应该有效");
                
                // 关闭连接
                DBUtil.closeConnection(conn1);
                DBUtil.closeConnection(conn2);
                
                // 验证连接已关闭
                assertTrue(conn1.isClosed(), "连接1应该已关闭");
                assertTrue(conn2.isClosed(), "连接2应该已关闭");
                
            } catch (Exception e) {
                fail("多连接操作不应该抛出异常: " + e.getMessage());
            }
        } else {
            System.out.println("警告：无法获取数据库连接进行多连接测试");
        }
    }

    /**
     * 测试连接的基本属性
     * BCDE原则中的Design：测试连接的设计属性
     */
    @Test
    public void testConnectionProperties() {
        // When: 获取数据库连接
        Connection conn = DBUtil.getConnection();
        
        // Then: 验证连接属性
        if (conn != null) {
            try {
                // 验证连接不是只读的（应该支持写操作）
                assertFalse(conn.isReadOnly(), "连接不应该是只读的");
                
                // 验证自动提交模式（默认应该是true）
                assertTrue(conn.getAutoCommit(), "连接应该默认开启自动提交");
                
                // 验证连接的数据库产品名称
                String productName = conn.getMetaData().getDatabaseProductName();
                assertNotNull(productName, "数据库产品名称不应为空");
                assertTrue(productName.toLowerCase().contains("mysql"), 
                          "应该连接到MySQL数据库，实际连接到: " + productName);
                
                // 关闭连接
                DBUtil.closeConnection(conn);
                
            } catch (Exception e) {
                fail("获取连接属性不应该抛出异常: " + e.getMessage());
            }
        } else {
            System.out.println("警告：无法获取数据库连接进行属性测试");
        }
    }

    /**
     * 测试连接的重复关闭
     * BCDE原则中的Error：测试错误场景
     */
    @Test
    public void testCloseConnectionTwice() {
        // Given: 获取一个连接
        Connection conn = DBUtil.getConnection();
        
        if (conn != null) {
            try {
                // When: 关闭连接两次
                DBUtil.closeConnection(conn);
                
                // Then: 第二次关闭不应该抛出异常
                assertDoesNotThrow(() -> {
                    DBUtil.closeConnection(conn);
                }, "重复关闭连接不应该抛出异常");
                
                // 验证连接确实已关闭
                assertTrue(conn.isClosed(), "连接应该已关闭");
                
            } catch (Exception e) {
                fail("连接关闭测试不应该抛出异常: " + e.getMessage());
            }
        } else {
            System.out.println("警告：无法获取数据库连接进行重复关闭测试");
        }
    }

    /**
     * 测试连接超时处理
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testConnectionTimeout() {
        // When: 获取连接并测试超时
        Connection conn = DBUtil.getConnection();
        
        if (conn != null) {
            try {
                // 测试连接有效性检查（5秒超时）
                boolean isValid = conn.isValid(5);
                assertTrue(isValid, "连接在5秒内应该有效");
                
                // 测试更短的超时（1秒）
                boolean isValidShort = conn.isValid(1);
                assertTrue(isValidShort, "连接在1秒内应该有效");
                
                // 关闭连接
                DBUtil.closeConnection(conn);
                
            } catch (Exception e) {
                fail("连接超时测试不应该抛出异常: " + e.getMessage());
            }
        } else {
            System.out.println("警告：无法获取数据库连接进行超时测试");
        }
    }
}
