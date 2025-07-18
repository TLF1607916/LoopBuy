package com.shiwu.admin.dao;

import com.shiwu.admin.model.AuditLog;
import com.shiwu.common.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // ====================================================================
    // 统计查询方法（用于管理员仪表盘）
    // ====================================================================

    /**
     * 获取指定时间段内的审计日志数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志数量
     */
    public Long getAuditLogCount(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM audit_log WHERE create_time >= ? AND create_time < ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Long count = 0L;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, startTime);
            pstmt.setObject(2, endTime);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("查询审计日志数量失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return count;
    }

    /**
     * 获取管理员登录次数统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录次数
     */
    public Long getAdminLoginCount(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM audit_log WHERE action LIKE '%登录%' AND result = 1 AND create_time >= ? AND create_time < ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Long count = 0L;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, startTime);
            pstmt.setObject(2, endTime);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("查询管理员登录次数失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return count;
    }

    /**
     * 获取系统错误数量统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误数量
     */
    public Long getSystemErrorCount(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM audit_log WHERE result = 0 AND create_time >= ? AND create_time < ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Long count = 0L;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, startTime);
            pstmt.setObject(2, endTime);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("查询系统错误数量失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return count;
    }

    /**
     * 获取活动趋势数据（按天统计）
     * @param days 统计天数
     * @return 趋势数据列表，每个元素包含日期和当天活动数量
     */
    public List<Map<String, Object>> getActivityTrend(int days) {
        String sql = "SELECT DATE(create_time) as date, COUNT(*) as count " +
                    "FROM audit_log " +
                    "WHERE create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                    "GROUP BY DATE(create_time) " +
                    "ORDER BY date";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> trendData = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, days);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("date", rs.getDate("date").toString());
                data.put("count", rs.getLong("count"));
                trendData.add(data);
            }
        } catch (SQLException e) {
            logger.error("查询活动趋势失败: {}", e.getMessage(), e);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return trendData;
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
