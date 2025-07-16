package com.shiwu.user.dao;

import com.shiwu.common.util.DBUtil;
import com.shiwu.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 用户数据访问对象
 */
public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, email, phone, create_time, update_time, is_deleted FROM system_user WHERE username = ? AND is_deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
                user.setUpdateTime(rs.getObject("update_time", LocalDateTime.class));
                user.setDeleted(rs.getBoolean("is_deleted"));
            }
        } catch (SQLException e) {
            logger.error("查询用户失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return user;
    }

    /**
     * 关闭数据库资源
     */
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("关闭ResultSet失败: {}", e.getMessage(), e);
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                logger.error("关闭PreparedStatement失败: {}", e.getMessage(), e);
            }
        }
        DBUtil.closeConnection(conn);
    }
}