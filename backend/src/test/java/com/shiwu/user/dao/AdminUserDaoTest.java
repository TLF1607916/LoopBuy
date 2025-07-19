package com.shiwu.user.dao;

import com.shiwu.admin.model.AdminUserQueryDTO;
import com.shiwu.common.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理员用户DAO测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminUserDaoTest {

    private static AdminUserDao adminUserDao;
    private static Long testUserId1;
    private static Long testUserId2;
    private static Long testAdminId = 1L;

    @BeforeAll
    static void setUpClass() throws SQLException {
        adminUserDao = new AdminUserDao();
        createTestUsers();
    }

    @AfterAll
    static void tearDownClass() throws SQLException {
        cleanupTestData();
    }

    @Test
    @Order(1)
    void testFindUsers_WithKeyword() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setKeyword("testuser");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行测试
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);

        // 验证结果
        assertNotNull(users);
        assertTrue(users.size() >= 2);
        
        // 验证返回的用户包含关键词
        boolean foundTestUser1 = false;
        boolean foundTestUser2 = false;
        for (Map<String, Object> user : users) {
            String username = (String) user.get("username");
            if (username.contains("testuser1")) {
                foundTestUser1 = true;
                assertEquals(0, user.get("status")); // 正常状态
                assertEquals("正常", user.get("statusText"));
                assertNotNull(user.get("id"));
                assertNotNull(user.get("createTime"));
            } else if (username.contains("testuser2")) {
                foundTestUser2 = true;
                assertEquals(1, user.get("status")); // 封禁状态
                assertEquals("已封禁", user.get("statusText"));
            }
        }
        assertTrue(foundTestUser1);
        assertTrue(foundTestUser2);
    }

    @Test
    @Order(2)
    void testFindUsers_WithStatus() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setStatus(0); // 查询正常状态用户
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行测试
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);

        // 验证结果
        assertNotNull(users);
        for (Map<String, Object> user : users) {
            assertEquals(0, user.get("status"));
            assertEquals("正常", user.get("statusText"));
        }
    }

    @Test
    @Order(3)
    void testFindUsers_WithPagination() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(1); // 每页只显示1条

        // 执行测试
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);

        // 验证结果
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    @Order(4)
    void testFindUsers_WithSorting() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setSortBy("username");
        queryDTO.setSortDirection("ASC");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行测试
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);

        // 验证结果
        assertNotNull(users);
        assertTrue(users.size() > 0);
        
        // 验证排序（检查前两个用户的用户名是否按升序排列）
        if (users.size() >= 2) {
            String username1 = (String) users.get(0).get("username");
            String username2 = (String) users.get(1).get("username");
            assertTrue(username1.compareTo(username2) <= 0);
        }
    }

    @Test
    @Order(5)
    void testFindUsers_EmptyKeyword() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setKeyword(""); // 空关键词
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行测试
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);

        // 验证结果
        assertNotNull(users);
        // 空关键词应该返回所有用户
    }

    @Test
    @Order(6)
    void testFindUsers_NoMatchKeyword() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setKeyword("nonexistent_user_12345");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 执行测试
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);

        // 验证结果
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    @Order(7)
    void testCountUsers_WithKeyword() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setKeyword("testuser");

        // 执行测试
        int count = adminUserDao.countUsers(queryDTO);

        // 验证结果
        assertTrue(count >= 2);
    }

    @Test
    @Order(8)
    void testCountUsers_WithStatus() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setStatus(1); // 封禁状态

        // 执行测试
        int count = adminUserDao.countUsers(queryDTO);

        // 验证结果
        assertTrue(count >= 1);
    }

    @Test
    @Order(9)
    void testCountUsers_NoMatch() {
        // 准备测试数据
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setKeyword("nonexistent_user_12345");

        // 执行测试
        int count = adminUserDao.countUsers(queryDTO);

        // 验证结果
        assertEquals(0, count);
    }

    @Test
    @Order(10)
    void testUpdateUserStatus_Success() {
        // 执行测试：将用户1状态改为禁言
        boolean result = adminUserDao.updateUserStatus(testUserId1, 2, testAdminId);

        // 验证结果
        assertTrue(result);
        
        // 验证状态确实被更新
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setStatus(2);
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);
        
        boolean found = false;
        for (Map<String, Object> user : users) {
            if (testUserId1.equals(user.get("id"))) {
                found = true;
                assertEquals(2, user.get("status"));
                assertEquals("已禁言", user.get("statusText"));
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    @Order(11)
    void testUpdateUserStatus_UserNotFound() {
        // 执行测试：更新不存在的用户
        boolean result = adminUserDao.updateUserStatus(99999L, 1, testAdminId);

        // 验证结果
        assertFalse(result);
    }

    @Test
    @Order(12)
    void testUpdateUserStatus_NullParams() {
        // 执行测试：空参数
        boolean result1 = adminUserDao.updateUserStatus(null, 1, testAdminId);
        boolean result2 = adminUserDao.updateUserStatus(testUserId1, null, testAdminId);
        boolean result3 = adminUserDao.updateUserStatus(testUserId1, 1, null);

        // 验证结果
        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
    }

    @Test
    @Order(13)
    void testUpdateUserStatus_RestoreNormal() {
        // 执行测试：将用户1状态恢复为正常
        boolean result = adminUserDao.updateUserStatus(testUserId1, 0, testAdminId);

        // 验证结果
        assertTrue(result);
        
        // 验证状态确实被更新
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setStatus(0);
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);
        
        boolean found = false;
        for (Map<String, Object> user : users) {
            if (testUserId1.equals(user.get("id"))) {
                found = true;
                assertEquals(0, user.get("status"));
                assertEquals("正常", user.get("statusText"));
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    @Order(14)
    void testFindUsers_ComplexQuery() {
        // 准备测试数据：组合查询条件
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        queryDTO.setKeyword("testuser");
        queryDTO.setStatus(0);
        queryDTO.setSortBy("create_time");
        queryDTO.setSortDirection("DESC");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(5);

        // 执行测试
        List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);

        // 验证结果
        assertNotNull(users);
        for (Map<String, Object> user : users) {
            String username = (String) user.get("username");
            assertTrue(username.contains("testuser"));
            assertEquals(0, user.get("status"));
        }
    }

    /**
     * 创建测试用户
     */
    private static void createTestUsers() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            
            // 创建测试用户1（正常状态）
            String sql = "INSERT INTO system_user (username, password, email, nickname, status, create_time, update_time, is_deleted) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, "testuser1_" + System.currentTimeMillis());
            pstmt.setString(2, "password123");
            pstmt.setString(3, "testuser1@test.com");
            pstmt.setString(4, "测试用户1");
            pstmt.setInt(5, 0); // 正常状态
            pstmt.setObject(6, LocalDateTime.now());
            pstmt.setObject(7, LocalDateTime.now());
            pstmt.setBoolean(8, false);
            
            pstmt.executeUpdate();
            java.sql.ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                testUserId1 = rs.getLong(1);
            }
            rs.close();
            pstmt.close();
            
            // 创建测试用户2（封禁状态）
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, "testuser2_" + System.currentTimeMillis());
            pstmt.setString(2, "password123");
            pstmt.setString(3, "testuser2@test.com");
            pstmt.setString(4, "测试用户2");
            pstmt.setInt(5, 1); // 封禁状态
            pstmt.setObject(6, LocalDateTime.now());
            pstmt.setObject(7, LocalDateTime.now());
            pstmt.setBoolean(8, false);
            
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                testUserId2 = rs.getLong(1);
            }
            rs.close();
            
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * 清理测试数据
     */
    private static void cleanupTestData() throws SQLException {
        if (testUserId1 == null && testUserId2 == null) {
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            
            String sql = "DELETE FROM system_user WHERE id IN (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, testUserId1 != null ? testUserId1 : 0);
            pstmt.setLong(2, testUserId2 != null ? testUserId2 : 0);
            
            pstmt.executeUpdate();
            
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }
}
