package com.shiwu.test;

import com.shiwu.common.util.DBUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 测试基类
 * 提供测试数据库初始化、清理等公共功能
 */
public abstract class TestBase {

    protected static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    // 测试用户数据
    protected static final Long TEST_USER_ID_1 = 1L;
    protected static final Long TEST_USER_ID_2 = 2L;
    protected static final Long TEST_USER_ID_3 = 3L;
    protected static final String TEST_USERNAME_1 = "alice";
    protected static final String TEST_USERNAME_2 = "bob";
    protected static final String TEST_USERNAME_3 = "charlie";
    protected static final String TEST_PASSWORD = "123456";
    // 使用BCrypt轮数10为密码"123456"生成的哈希值 - 这个哈希值是动态生成的，所以测试中会重新生成
    protected static final String TEST_PASSWORD_HASH = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVMLVZqpjn/6M.ltU6Td9e";

    // 测试管理员数据
    protected static final Long TEST_ADMIN_ID = 1L;
    protected static final String TEST_ADMIN_USERNAME = "admin";
    protected static final String TEST_ADMIN_PASSWORD = "admin123";

    // 测试商品数据
    protected static final Long TEST_PRODUCT_ID_1 = 1L;
    protected static final Long TEST_PRODUCT_ID_2 = 2L;
    protected static final String TEST_PRODUCT_TITLE_1 = "iPhone 13 Pro";
    protected static final String TEST_PRODUCT_TITLE_2 = "MacBook Pro";

    @BeforeAll
    public static void setUpClass() {
        logger.info("开始初始化测试环境");
        try {
            initializeTestDatabase();
            logger.info("测试环境初始化完成");
        } catch (Exception e) {
            logger.error("测试环境初始化失败", e);
            throw new RuntimeException("测试环境初始化失败", e);
        }
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("开始清理测试环境");
        try {
            cleanupTestDatabase();
            logger.info("测试环境清理完成");
        } catch (Exception e) {
            logger.error("测试环境清理失败", e);
        }
    }

    @BeforeEach
    public void setUp() {
        logger.debug("准备执行测试方法");
    }

    /**
     * 初始化测试数据库
     */
    private static void initializeTestDatabase() throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            // 清理现有数据
            cleanupTestData(conn);

            // 插入测试用户数据
            insertTestUsers(conn);

            // 插入测试管理员数据
            insertTestAdmins(conn);

            // 插入测试商品分类数据
            insertTestCategories(conn);

            // 插入测试商品数据
            insertTestProducts(conn);

            // 插入测试关注关系数据
            insertTestFollowRelations(conn);

            logger.info("测试数据初始化完成");
        }
    }

    /**
     * 清理测试数据库
     */
    private static void cleanupTestDatabase() throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            cleanupTestData(conn);
        }
    }

    /**
     * 清理测试数据
     */
    private static void cleanupTestData(Connection conn) throws SQLException {
        String[] tables = {
            "notification", "message", "conversation", "audit_log",
            "payment", "trade_order", "shopping_cart", "product_image",
            "product", "user_follow", "system_user", "administrator", "category"
        };

        try (Statement stmt = conn.createStatement()) {
            // 禁用外键检查
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // 清理所有表数据
            for (String table : tables) {
                try {
                    stmt.execute("DELETE FROM " + table);
                    stmt.execute("ALTER TABLE " + table + " AUTO_INCREMENT = 1");
                } catch (SQLException e) {
                    // 忽略表不存在的错误
                    logger.debug("清理表 {} 时发生错误（可能表不存在）: {}", table, e.getMessage());
                }
            }

            // 启用外键检查
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    /**
     * 插入测试用户数据
     */
    private static void insertTestUsers(Connection conn) throws SQLException {
        String sql = "INSERT INTO system_user (id, username, password, email, nickname, avatar_url, status, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 用户1: alice
            pstmt.setLong(1, TEST_USER_ID_1);
            pstmt.setString(2, TEST_USERNAME_1);
            pstmt.setString(3, TEST_PASSWORD_HASH);
            pstmt.setString(4, "alice@test.com");
            pstmt.setString(5, "Alice");
            pstmt.setString(6, "/images/avatar/alice.jpg");
            pstmt.setInt(7, 1); // 正常状态
            pstmt.executeUpdate();

            // 用户2: bob
            pstmt.setLong(1, TEST_USER_ID_2);
            pstmt.setString(2, TEST_USERNAME_2);
            pstmt.setString(3, TEST_PASSWORD_HASH);
            pstmt.setString(4, "bob@test.com");
            pstmt.setString(5, "Bob");
            pstmt.setString(6, "/images/avatar/bob.jpg");
            pstmt.setInt(7, 1); // 正常状态
            pstmt.executeUpdate();

            // 用户3: charlie
            pstmt.setLong(1, TEST_USER_ID_3);
            pstmt.setString(2, TEST_USERNAME_3);
            pstmt.setString(3, TEST_PASSWORD_HASH);
            pstmt.setString(4, "charlie@test.com");
            pstmt.setString(5, "Charlie");
            pstmt.setString(6, "/images/avatar/charlie.jpg");
            pstmt.setInt(7, 1); // 正常状态
            pstmt.executeUpdate();
        }
    }

    /**
     * 插入测试管理员数据
     */
    private static void insertTestAdmins(Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO administrator (id, username, password, email, role, status, create_time) VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 管理员1: admin
            pstmt.setLong(1, TEST_ADMIN_ID);
            pstmt.setString(2, TEST_ADMIN_USERNAME);
            pstmt.setString(3, TEST_PASSWORD_HASH);
            pstmt.setString(4, "admin@test.com");
            pstmt.setString(5, "SUPER_ADMIN");
            pstmt.setInt(6, 1); // 正常状态
            pstmt.executeUpdate();

            // 管理员2: moderator (用于测试)
            pstmt.setLong(1, 2L);
            pstmt.setString(2, "moderator");
            pstmt.setString(3, TEST_PASSWORD_HASH);
            pstmt.setString(4, "moderator@test.com");
            pstmt.setString(5, "MODERATOR");
            pstmt.setInt(6, 1); // 正常状态
            pstmt.executeUpdate();
        }
    }

    /**
     * 插入测试订单数据
     */
    private static void insertTestOrders(Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO trade_order (id, buyer_id, seller_id, product_id, price_at_purchase, " +
                    "product_title_snapshot, product_description_snapshot, product_image_urls_snapshot, " +
                    "status, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 0)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 订单1: 待付款
            pstmt.setLong(1, 1001L);
            pstmt.setLong(2, TEST_USER_ID_1); // 买家
            pstmt.setLong(3, TEST_USER_ID_2); // 卖家
            pstmt.setLong(4, 101L); // 商品ID
            pstmt.setBigDecimal(5, new java.math.BigDecimal("199.99"));
            pstmt.setString(6, "测试商品标题");
            pstmt.setString(7, "测试商品描述");
            pstmt.setString(8, "[\"image1.jpg\",\"image2.jpg\"]");
            pstmt.setInt(9, 0); // 待付款
            pstmt.executeUpdate();

            // 订单2: 待发货
            pstmt.setLong(1, 1002L);
            pstmt.setLong(2, TEST_USER_ID_1); // 买家
            pstmt.setLong(3, TEST_USER_ID_3); // 卖家
            pstmt.setLong(4, 102L); // 商品ID
            pstmt.setBigDecimal(5, new java.math.BigDecimal("299.99"));
            pstmt.setString(6, "测试商品标题2");
            pstmt.setString(7, "测试商品描述2");
            pstmt.setString(8, "[\"image3.jpg\",\"image4.jpg\"]");
            pstmt.setInt(9, 1); // 待发货
            pstmt.executeUpdate();

            // 订单3: 已发货
            pstmt.setLong(1, 1003L);
            pstmt.setLong(2, TEST_USER_ID_2); // 买家
            pstmt.setLong(3, TEST_USER_ID_3); // 卖家
            pstmt.setLong(4, 103L); // 商品ID
            pstmt.setBigDecimal(5, new java.math.BigDecimal("399.99"));
            pstmt.setString(6, "测试商品标题3");
            pstmt.setString(7, "测试商品描述3");
            pstmt.setString(8, "[\"image5.jpg\",\"image6.jpg\"]");
            pstmt.setInt(9, 2); // 已发货
            pstmt.executeUpdate();
        }
    }

    /**
     * 插入测试商品分类数据
     */
    private static void insertTestCategories(Connection conn) throws SQLException {
        String sql = "INSERT INTO category (id, name, parent_id, create_time) VALUES (?, ?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            pstmt.setString(2, "电子产品");
            pstmt.setInt(3, 0); // 顶级分类
            pstmt.executeUpdate();

            pstmt.setInt(1, 2);
            pstmt.setString(2, "图书文具");
            pstmt.setInt(3, 0); // 顶级分类
            pstmt.executeUpdate();
        }
    }

    /**
     * 插入测试商品数据
     */
    private static void insertTestProducts(Connection conn) throws SQLException {
        String sql = "INSERT INTO product (id, title, description, price, category_id, seller_id, status, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 商品1
            pstmt.setLong(1, TEST_PRODUCT_ID_1);
            pstmt.setString(2, TEST_PRODUCT_TITLE_1);
            pstmt.setString(3, "九成新iPhone 13 Pro，256GB，深空灰色");
            pstmt.setBigDecimal(4, new java.math.BigDecimal("6999.00"));
            pstmt.setLong(5, 1L); // 电子产品分类
            pstmt.setLong(6, TEST_USER_ID_1); // alice发布
            pstmt.setInt(7, 1); // 在售状态
            pstmt.executeUpdate();

            // 商品2
            pstmt.setLong(1, TEST_PRODUCT_ID_2);
            pstmt.setString(2, TEST_PRODUCT_TITLE_2);
            pstmt.setString(3, "MacBook Pro 13寸，M1芯片，8GB内存");
            pstmt.setBigDecimal(4, new java.math.BigDecimal("8999.00"));
            pstmt.setLong(5, 1L); // 电子产品分类
            pstmt.setLong(6, TEST_USER_ID_2); // bob发布
            pstmt.setInt(7, 1); // 在售状态
            pstmt.executeUpdate();
        }
    }

    /**
     * 插入测试关注关系数据
     */
    private static void insertTestFollowRelations(Connection conn) throws SQLException {
        String sql = "INSERT INTO user_follow (follower_id, followed_id, create_time) VALUES (?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // alice关注bob
            pstmt.setLong(1, TEST_USER_ID_1);
            pstmt.setLong(2, TEST_USER_ID_2);
            pstmt.executeUpdate();

            // bob关注alice
            pstmt.setLong(1, TEST_USER_ID_2);
            pstmt.setLong(2, TEST_USER_ID_1);
            pstmt.executeUpdate();
        }
    }

    /**
     * 获取测试数据库连接
     */
    protected Connection getTestConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    /**
     * 执行SQL并忽略异常（用于清理操作）
     */
    protected void executeSqlIgnoreException(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.debug("执行SQL时发生异常（已忽略）: {}", e.getMessage());
        }
    }
}