package com.shiwu.admin.service;

import com.shiwu.admin.dto.AuditLogQueryDTO;
import com.shiwu.admin.enums.AuditActionEnum;
import com.shiwu.admin.enums.AuditTargetTypeEnum;
import com.shiwu.admin.model.AuditLog;
import com.shiwu.admin.vo.AuditLogVO;

import java.util.List;
import java.util.Map;

/**
 * 审计日志服务接口
 * 实现NFR-SEC-03要求的审计日志功能
 */
public interface AuditLogService {
    
    /**
     * 记录审计日志
     * @param adminId 管理员ID
     * @param action 操作类型
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param details 操作详情
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param success 操作是否成功
     * @return 日志ID，如果失败则返回null
     */
    Long logAction(Long adminId, AuditActionEnum action, AuditTargetTypeEnum targetType, 
                   Long targetId, String details, String ipAddress, String userAgent, boolean success);
    
    /**
     * 记录审计日志（简化版本）
     * @param adminId 管理员ID
     * @param action 操作类型
     * @param details 操作详情
     * @param success 操作是否成功
     * @return 日志ID，如果失败则返回null
     */
    Long logAction(Long adminId, AuditActionEnum action, String details, boolean success);
    
    /**
     * 记录管理员登录日志
     * @param adminId 管理员ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param success 是否成功
     * @param details 详细信息
     * @return 日志ID，如果失败则返回null
     */
    Long logAdminLogin(Long adminId, String ipAddress, String userAgent, boolean success, String details);
    
    /**
     * 分页查询审计日志
     * @param queryDTO 查询条件
     * @return 分页结果，包含日志列表和总数
     */
    Map<String, Object> getAuditLogs(AuditLogQueryDTO queryDTO);
    
    /**
     * 获取审计日志详情
     * @param id 日志ID
     * @return 审计日志VO，如果不存在则返回null
     */
    AuditLogVO getAuditLogDetail(Long id);
    
    /**
     * 获取操作统计数据
     * @param days 统计天数
     * @return 统计数据
     */
    Map<String, Object> getOperationStats(int days);
    
    /**
     * 获取活动趋势数据
     * @param days 统计天数
     * @return 趋势数据列表
     */
    List<Map<String, Object>> getActivityTrend(int days);
    
    /**
     * 导出审计日志
     * @param queryDTO 查询条件
     * @return 导出的日志列表
     */
    List<AuditLogVO> exportAuditLogs(AuditLogQueryDTO queryDTO);
    
    /**
     * 检查操作是否需要记录审计日志
     * @param action 操作类型
     * @return true如果需要记录
     */
    boolean shouldLogAction(AuditActionEnum action);
    
    /**
     * 获取所有可用的操作类型
     * @return 操作类型列表
     */
    List<Map<String, String>> getAvailableActions();
    
    /**
     * 获取所有可用的目标类型
     * @return 目标类型列表
     */
    List<Map<String, String>> getAvailableTargetTypes();
}
