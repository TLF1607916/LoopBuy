package com.shiwu.user.service;

import com.shiwu.admin.model.AdminUserQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService {
    
    /**
     * 查询用户列表（管理员视角）
     * @param queryDTO 查询条件
     * @return 分页查询结果
     */
    Map<String, Object> findUsers(AdminUserQueryDTO queryDTO);
    
    /**
     * 获取用户详情（管理员视角）
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @return 用户详情
     */
    Map<String, Object> getUserDetail(Long userId, Long adminId);
    
    /**
     * 封禁用户
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param reason 封禁原因
     * @return 操作是否成功
     */
    boolean banUser(Long userId, Long adminId, String reason);
    
    /**
     * 禁言用户
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param reason 禁言原因
     * @return 操作是否成功
     */
    boolean muteUser(Long userId, Long adminId, String reason);
    
    /**
     * 解封用户
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @return 操作是否成功
     */
    boolean unbanUser(Long userId, Long adminId);
    
    /**
     * 解除禁言
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @return 操作是否成功
     */
    boolean unmuteUser(Long userId, Long adminId);
    
    /**
     * 批量封禁用户
     * @param userIds 用户ID列表
     * @param adminId 管理员ID
     * @param reason 封禁原因
     * @return 操作结果统计
     */
    Map<String, Object> batchBanUsers(List<Long> userIds, Long adminId, String reason);
    
    /**
     * 批量禁言用户
     * @param userIds 用户ID列表
     * @param adminId 管理员ID
     * @param reason 禁言原因
     * @return 操作结果统计
     */
    Map<String, Object> batchMuteUsers(List<Long> userIds, Long adminId, String reason);
}
