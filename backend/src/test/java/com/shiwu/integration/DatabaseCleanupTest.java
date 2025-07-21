package com.shiwu.integration;

import com.shiwu.common.util.DBUtil;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库清理测试
 * 清空所有表数据，为集成测试提供干净的环境
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("数据库清理测试")
public class DatabaseCleanupTest {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCleanupTest.class);
    
    // 需要清理的表，按照依赖关系排序（从子表到父表）
    private static final String[] TABLES_TO_CLEAN = {
        "notification",
        "message", 
        "conversation",
        "audit_log",
        "payment",
        "trade_order",
        "shopping_cart",
        "product_image",
        "product",
        "user_follow",
        "system_user",
        "administrator",
        "category"
    };
    
    @BeforeAll
    public static void setUpClass() {
        logger.info("开始数据库清理测试");
    }
    
    @Test
    @Order(1)
    @DisplayName("1.1 数据库连接测试")
    public void testDatabaseConnection() {
        logger.info("测试数据库连接");
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            assertNotNull(conn, "数据库连接不应为null");
            assertFalse(conn.isClosed(), "数据库连接应该是打开的");
            
            logger.info("数据库连接测试通过");
        } catch (SQLException e) {
            fail("数据库连接失败: " + e.getMessage());
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("1.2 清理所有表数据")
    public void testCleanAllTables() {
        logger.info("开始清理所有表数据");
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            
            // 禁用外键检查
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            logger.info("已禁用外键检查");
            
            // 清理所有表数据
            int totalCleaned = 0;
            for (String table : TABLES_TO_CLEAN) {
                try {
                    // 删除数据
                    int deletedRows = stmt.executeUpdate("DELETE FROM " + table);
                    logger.info("清理表 {} - 删除了 {} 行数据", table, deletedRows);
                    totalCleaned += deletedRows;
                    
                    // 重置自增ID
                    stmt.execute("ALTER TABLE " + table + " AUTO_INCREMENT = 1");
                    logger.debug("重置表 {} 的自增ID", table);
                    
                } catch (SQLException e) {
                    // 记录错误但继续清理其他表
                    logger.warn("清理表 {} 时发生错误: {}", table, e.getMessage());
                }
            }
            
            // 启用外键检查
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            logger.info("已启用外键检查");
            
            logger.info("数据库清理完成 - 总共清理了 {} 行数据", totalCleaned);
            
        } catch (SQLException e) {
            fail("数据库清理失败: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error("关闭Statement失败", e);
                }
            }
            DBUtil.closeConnection(conn);
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("1.3 验证表数据已清空")
    public void testVerifyTablesEmpty() {
        logger.info("验证表数据已清空");
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            
            boolean allTablesEmpty = true;
            
            for (String table : TABLES_TO_CLEAN) {
                try {
                    java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + table);
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        logger.info("表 {} 当前记录数: {}", table, count);
                        
                        if (count > 0) {
                            allTablesEmpty = false;
                            logger.warn("表 {} 仍有 {} 条记录未清理", table, count);
                        }
                    }
                    rs.close();
                } catch (SQLException e) {
                    logger.warn("检查表 {} 时发生错误: {}", table, e.getMessage());
                }
            }
            
            assertTrue(allTablesEmpty, "所有表应该都已清空");
            logger.info("验证完成 - 所有表数据已清空");
            
        } catch (SQLException e) {
            fail("验证表清空状态失败: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error("关闭Statement失败", e);
                }
            }
            DBUtil.closeConnection(conn);
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("1.4 插入基础测试数据")
    public void testInsertBasicTestData() {
        logger.info("插入基础测试数据");
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            
            // 插入基础分类数据
            String insertCategories = "INSERT INTO category (id, name, parent_id) VALUES " +
                "(1, '数码产品', 0), " +
                "(2, '图书教材', 0), " +
                "(3, '服装鞋帽', 0), " +
                "(4, '生活用品', 0), " +
                "(5, '运动健身', 0)";
            
            int categoryRows = stmt.executeUpdate(insertCategories);
            logger.info("插入了 {} 个商品分类", categoryRows);
            
            // 插入管理员数据
            String insertAdmin = "INSERT INTO administrator (username, password, email, real_name, role, status) VALUES " +
                "('admin', '$2a$10$zET/DZxiY3ZIElkyQth62u6rmqttBv62/bK0C1.vqw41zH.F9bfA6', 'admin@test.com', '测试管理员', 'SUPER_ADMIN', 1), " +
                "('moderator', '$2a$10$zET/DZxiY3ZIElkyQth62u6rmqttBv62/bK0C1.vqw41zH.F9bfA6', 'mod@test.com', '测试审核员', 'ADMIN', 1)";
            
            int adminRows = stmt.executeUpdate(insertAdmin);
            logger.info("插入了 {} 个管理员账户", adminRows);
            
            logger.info("基础测试数据插入完成");
            
        } catch (SQLException e) {
            fail("插入基础测试数据失败: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error("关闭Statement失败", e);
                }
            }
            DBUtil.closeConnection(conn);
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("1.5 验证基础数据插入成功")
    public void testVerifyBasicDataInserted() {
        logger.info("验证基础数据插入成功");
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            
            // 验证分类数据
            java.sql.ResultSet categoryRs = stmt.executeQuery("SELECT COUNT(*) as count FROM category");
            if (categoryRs.next()) {
                int categoryCount = categoryRs.getInt("count");
                assertTrue(categoryCount >= 5, "应该至少有5个商品分类");
                logger.info("商品分类数量: {}", categoryCount);
            }
            categoryRs.close();
            
            // 验证管理员数据
            java.sql.ResultSet adminRs = stmt.executeQuery("SELECT COUNT(*) as count FROM administrator");
            if (adminRs.next()) {
                int adminCount = adminRs.getInt("count");
                assertTrue(adminCount >= 2, "应该至少有2个管理员账户");
                logger.info("管理员账户数量: {}", adminCount);
            }
            adminRs.close();
            
            logger.info("基础数据验证完成");
            
        } catch (SQLException e) {
            fail("验证基础数据失败: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error("关闭Statement失败", e);
                }
            }
            DBUtil.closeConnection(conn);
        }
    }
    
    @AfterAll
    public static void tearDownClass() {
        logger.info("数据库清理测试完成");
        logger.info("数据库已准备好进行集成测试");
    }
}
