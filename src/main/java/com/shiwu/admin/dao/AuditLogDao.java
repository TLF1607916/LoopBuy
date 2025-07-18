package com.shiwu.admin.dao;

import com.shiwu.admin.model.AuditLog;
import com.shiwu.common.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * 审计日志数据访问对象
 */
public class AuditLogDao {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogDao.class);

    /**
     * 创建审计日志
     * @param auditLog 审计日志对象
     * @return 创建的日志ID，如果失败则返回null
     */
    public Long createAuditLog(AuditLog auditLog) {
        if (auditLog == null) {
            logger.warn("创建审计日志失败: 日志对象为空");
            return null;
        }

        if (auditLog.getAdminId() == null) {
            logger.warn("创建审计日志失败: 管理员ID为空");
            return null;
        }

        String sql = "INSERT INTO audit_log (admin_id, action, target_type, target_id, details, " +
                    "ip_address, user_agent, result, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn == null) {
                logger.error("获取数据库连接失败");
                return null;
            }

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, auditLog.getAdminId());
            pstmt.setString(2, auditLog.getAction());
            pstmt.setString(3, auditLog.getTargetType());
            
            if (auditLog.getTargetId() != null) {
                pstmt.setLong(4, auditLog.getTargetId());
            } else {
                pstmt.setNull(4, Types.BIGINT);
            }
            
            pstmt.setString(5, auditLog.getDetails());
            pstmt.setString(6, auditLog.getIpAddress());
            pstmt.setString(7, auditLog.getUserAgent());
            pstmt.setInt(8, auditLog.getResult());
            pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    Long logId = rs.getLong(1);
                    logger.info("成功创建审计日志: ID={}, 管理员ID={}, 操作={}", 
                              logId, auditLog.getAdminId(), auditLog.getAction());
                    return logId;
                }
            }
            
            logger.warn("创建审计日志失败: 未能获取生成的ID");
            return null;
        } catch (SQLException e) {
            logger.error("创建审计日志时发生数据库异常: {}", e.getMessage(), e);
            return null;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 记录管理员登录日志
     * @param adminId 管理员ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param success 是否成功
     * @param details 详细信息
     * @return 日志ID，如果失败则返回null
     */
    public Long logAdminLogin(Long adminId, String ipAddress, String userAgent, 
                             boolean success, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAction("ADMIN_LOGIN");
        auditLog.setTargetType("ADMIN");
        auditLog.setTargetId(adminId);
        auditLog.setDetails(details);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setResult(success ? 1 : 0);
        
        return createAuditLog(auditLog);
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
