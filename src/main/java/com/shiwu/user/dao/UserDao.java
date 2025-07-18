package com.shiwu.user.dao;

import com.shiwu.common.util.DBUtil;
import com.shiwu.user.model.User;
import com.shiwu.user.model.ProductCardVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "SELECT id, username, password, email, phone, status, avatar_url, nickname, gender, bio, school, last_login_time, create_time, update_time, is_deleted FROM system_user WHERE username = ? AND is_deleted = 0";
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
                user.setStatus(rs.getInt("status"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setNickname(rs.getString("nickname"));
                user.setGender(rs.getInt("gender"));
                user.setBio(rs.getString("bio"));
                user.setSchool(rs.getString("school"));
                user.setLastLoginTime(rs.getObject("last_login_time", LocalDateTime.class));
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
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    public User findById(Long userId) {
        String sql = "SELECT id, username, password, email, phone, status, avatar_url, nickname, gender, bio, school, follower_count, average_rating, last_login_time, create_time, update_time, is_deleted FROM system_user WHERE id = ? AND is_deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getInt("status"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setNickname(rs.getString("nickname"));
                user.setGender(rs.getInt("gender"));
                user.setBio(rs.getString("bio"));
                user.setSchool(rs.getString("school"));
                user.setFollowerCount(rs.getInt("follower_count"));
                user.setAverageRating(rs.getBigDecimal("average_rating"));
                user.setLastLoginTime(rs.getObject("last_login_time", LocalDateTime.class));
                user.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
                user.setUpdateTime(rs.getObject("update_time", LocalDateTime.class));
                user.setDeleted(rs.getBoolean("is_deleted"));
            }
        } catch (SQLException e) {
            logger.error("根据ID查询用户失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return user;
    }
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回null
     */
    public User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        
        String sql = "SELECT id FROM system_user WHERE email = ? AND is_deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            logger.error("根据邮箱查询用户失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return user;
    }
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户对象，如果不存在则返回null
     */
    public User findByPhone(String phone) {
        if (phone == null) {
            return null;
        }
        
        String sql = "SELECT id FROM system_user WHERE phone = ? AND is_deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            logger.error("根据手机号查询用户失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return user;
    }
    
    /**
     * 创建新用户
     * @param user 用户对象
     * @return 创建成功返回用户ID，失败返回null
     */
    public Long createUser(User user) {
        String sql = "INSERT INTO system_user (username, password, email, phone, status, nickname, school) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Long userId = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.setInt(5, user.getStatus() != null ? user.getStatus() : 0);
            pstmt.setString(6, user.getNickname());
            pstmt.setString(7, user.getSchool());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getLong(1);
                    logger.info("创建用户成功, ID: {}", userId);
                }
            }
        } catch (SQLException e) {
            logger.error("创建用户失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return userId;
    }
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码（已哈希）
     * @return 是否更新成功
     */
    public boolean updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE system_user SET password = ? WHERE id = ? AND is_deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setLong(2, userId);
            int rows = pstmt.executeUpdate();
            success = rows > 0;
            
            if (success) {
                logger.info("更新用户 {} 密码成功", userId);
            } else {
                logger.warn("更新用户 {} 密码失败: 用户不存在或已删除", userId);
            }
        } catch (SQLException e) {
            logger.error("更新用户密码失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return success;
    }
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 是否更新成功
     */
    public boolean updateLastLoginTime(Long userId) {
        String sql = "UPDATE system_user SET last_login_time = ? WHERE id = ? AND is_deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, LocalDateTime.now());
            pstmt.setLong(2, userId);
            int rows = pstmt.executeUpdate();
            success = rows > 0;
            
            if (success) {
                logger.info("更新用户 {} 最后登录时间成功", userId);
            } else {
                logger.warn("更新用户 {} 最后登录时间失败: 用户不存在或已删除", userId);
            }
        } catch (SQLException e) {
            logger.error("更新用户最后登录时间失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return success;
    }

    /**
     * 根据用户ID获取用户公开信息（不包含敏感信息）
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    public User findPublicInfoById(Long userId) {
        String sql = "SELECT id, username, nickname, avatar_url, status, follower_count, average_rating, create_time FROM system_user WHERE id = ? AND is_deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setNickname(rs.getString("nickname"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setStatus(rs.getInt("status"));
                user.setFollowerCount(rs.getInt("follower_count"));
                user.setAverageRating(rs.getBigDecimal("average_rating"));
                user.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
            }
        } catch (SQLException e) {
            logger.error("根据ID查询用户公开信息失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return user;
    }

    /**
     * 获取用户的在售商品列表
     * @param userId 用户ID
     * @return 在售商品列表
     */
    public List<ProductCardVO> findOnSaleProductsByUserId(Long userId) {
        // 注意：这里暂时返回空列表，因为产品模块还未实现
        // 当产品模块实现后，应该调用ProductDao的相应方法
        // 根据模块解耦原则，UserDao不应该直接查询product表
        logger.warn("获取用户在售商品列表功能暂未实现，需要产品模块支持");
        return new ArrayList<>();
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