package com.shiwu.user.service.impl;

import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.model.AdminUserQueryDTO;
import com.shiwu.admin.model.AuditLog;
import com.shiwu.user.dao.AdminUserDao;
import com.shiwu.user.dao.UserDao;
import com.shiwu.user.model.User;
import com.shiwu.user.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员用户服务实现类
 */
public class AdminUserServiceImpl implements AdminUserService {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);
    
    // 用户状态常量
    private static final Integer USER_STATUS_NORMAL = 0;
    private static final Integer USER_STATUS_BANNED = 1;
    private static final Integer USER_STATUS_MUTED = 2;
    
    private final AdminUserDao adminUserDao;
    private final UserDao userDao;
    private final AuditLogDao auditLogDao;

    public AdminUserServiceImpl() {
        this.adminUserDao = new AdminUserDao();
        this.userDao = new UserDao();
        this.auditLogDao = new AuditLogDao();
    }

    // 用于测试的构造函数
    public AdminUserServiceImpl(AdminUserDao adminUserDao, UserDao userDao, AuditLogDao auditLogDao) {
        this.adminUserDao = adminUserDao;
        this.userDao = userDao;
        this.auditLogDao = auditLogDao;
    }

    @Override
    public Map<String, Object> findUsers(AdminUserQueryDTO queryDTO) {
        if (queryDTO == null) {
            logger.warn("查询用户列表失败: 查询条件为空");
            return null;
        }

        try {
            // 查询用户列表
            List<Map<String, Object>> users = adminUserDao.findUsers(queryDTO);
            
            // 查询总数
            int totalCount = adminUserDao.countUsers(queryDTO);
            
            // 计算分页信息
            int totalPages = (int) Math.ceil((double) totalCount / queryDTO.getPageSize());
            
            Map<String, Object> result = new HashMap<>();
            result.put("users", users);
            result.put("totalCount", totalCount);
            result.put("totalPages", totalPages);
            result.put("currentPage", queryDTO.getPageNum());
            result.put("pageSize", queryDTO.getPageSize());
            
            logger.info("查询用户列表成功: 共{}条记录", totalCount);
            return result;
        } catch (Exception e) {
            logger.error("查询用户列表失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getUserDetail(Long userId, Long adminId) {
        if (userId == null || adminId == null) {
            logger.warn("获取用户详情失败: 参数为空");
            return null;
        }

        try {
            // 管理员可以查看所有用户详情（包括被封禁的用户）
            User user = userDao.findById(userId);
            
            if (user == null) {
                logger.warn("获取用户详情失败: 用户不存在, userId={}", userId);
                return null;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("user", convertToAdminUserVO(user));
            
            logger.info("管理员 {} 获取用户详情成功: userId={}", adminId, userId);
            return result;
        } catch (Exception e) {
            logger.error("获取用户详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean banUser(Long userId, Long adminId, String reason) {
        if (userId == null || adminId == null) {
            logger.warn("封禁用户失败: 参数为空");
            return false;
        }

        try {
            // 检查用户是否存在
            User user = userDao.findById(userId);
            if (user == null) {
                logger.warn("封禁用户失败: 用户不存在, userId={}", userId);
                return false;
            }

            // 检查用户当前状态
            if (USER_STATUS_BANNED.equals(user.getStatus())) {
                logger.warn("封禁用户失败: 用户已被封禁, userId={}", userId);
                return false;
            }

            // 更新用户状态为封禁
            boolean success = adminUserDao.updateUserStatus(userId, USER_STATUS_BANNED, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("USER_BAN");
                auditLog.setDetails("封禁用户: " + userId + (reason != null ? ", 原因: " + reason : ""));
                auditLogDao.createAuditLog(auditLog);
                
                logger.info("管理员 {} 封禁用户 {} 成功", adminId, userId);
                return true;
            } else {
                logger.warn("封禁用户失败: 更新状态失败, userId={}", userId);
                return false;
            }
        } catch (Exception e) {
            logger.error("封禁用户失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean muteUser(Long userId, Long adminId, String reason) {
        if (userId == null || adminId == null) {
            logger.warn("禁言用户失败: 参数为空");
            return false;
        }

        try {
            // 检查用户是否存在
            User user = userDao.findById(userId);
            if (user == null) {
                logger.warn("禁言用户失败: 用户不存在, userId={}", userId);
                return false;
            }

            // 检查用户当前状态
            if (USER_STATUS_BANNED.equals(user.getStatus())) {
                logger.warn("禁言用户失败: 用户已被封禁, userId={}", userId);
                return false;
            }

            if (USER_STATUS_MUTED.equals(user.getStatus())) {
                logger.warn("禁言用户失败: 用户已被禁言, userId={}", userId);
                return false;
            }

            // 更新用户状态为禁言
            boolean success = adminUserDao.updateUserStatus(userId, USER_STATUS_MUTED, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("USER_MUTE");
                auditLog.setDetails("禁言用户: " + userId + (reason != null ? ", 原因: " + reason : ""));
                auditLogDao.createAuditLog(auditLog);
                
                logger.info("管理员 {} 禁言用户 {} 成功", adminId, userId);
                return true;
            } else {
                logger.warn("禁言用户失败: 更新状态失败, userId={}", userId);
                return false;
            }
        } catch (Exception e) {
            logger.error("禁言用户失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean unbanUser(Long userId, Long adminId) {
        if (userId == null || adminId == null) {
            logger.warn("解封用户失败: 参数为空");
            return false;
        }

        try {
            // 检查用户是否存在
            User user = userDao.findById(userId);
            if (user == null) {
                logger.warn("解封用户失败: 用户不存在, userId={}", userId);
                return false;
            }

            // 检查用户当前状态
            if (!USER_STATUS_BANNED.equals(user.getStatus())) {
                logger.warn("解封用户失败: 用户未被封禁, userId={}, status={}", userId, user.getStatus());
                return false;
            }

            // 更新用户状态为正常
            boolean success = adminUserDao.updateUserStatus(userId, USER_STATUS_NORMAL, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("USER_UNBAN");
                auditLog.setDetails("解封用户: " + userId);
                auditLogDao.createAuditLog(auditLog);
                
                logger.info("管理员 {} 解封用户 {} 成功", adminId, userId);
                return true;
            } else {
                logger.warn("解封用户失败: 更新状态失败, userId={}", userId);
                return false;
            }
        } catch (Exception e) {
            logger.error("解封用户失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean unmuteUser(Long userId, Long adminId) {
        if (userId == null || adminId == null) {
            logger.warn("解除禁言失败: 参数为空");
            return false;
        }

        try {
            // 检查用户是否存在
            User user = userDao.findById(userId);
            if (user == null) {
                logger.warn("解除禁言失败: 用户不存在, userId={}", userId);
                return false;
            }

            // 检查用户当前状态
            if (!USER_STATUS_MUTED.equals(user.getStatus())) {
                logger.warn("解除禁言失败: 用户未被禁言, userId={}, status={}", userId, user.getStatus());
                return false;
            }

            // 更新用户状态为正常
            boolean success = adminUserDao.updateUserStatus(userId, USER_STATUS_NORMAL, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("USER_UNMUTE");
                auditLog.setDetails("解除禁言: " + userId);
                auditLogDao.createAuditLog(auditLog);
                
                logger.info("管理员 {} 解除用户 {} 禁言成功", adminId, userId);
                return true;
            } else {
                logger.warn("解除禁言失败: 更新状态失败, userId={}", userId);
                return false;
            }
        } catch (Exception e) {
            logger.error("解除禁言失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> batchBanUsers(List<Long> userIds, Long adminId, String reason) {
        if (userIds == null || userIds.isEmpty() || adminId == null) {
            logger.warn("批量封禁用户失败: 参数为空");
            return null;
        }

        try {
            int successCount = 0;
            int failCount = 0;
            
            for (Long userId : userIds) {
                boolean success = banUser(userId, adminId, reason);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", userIds.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            
            logger.info("管理员 {} 批量封禁用户完成: 总数={}, 成功={}, 失败={}", 
                       adminId, userIds.size(), successCount, failCount);
            return result;
        } catch (Exception e) {
            logger.error("批量封禁用户失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> batchMuteUsers(List<Long> userIds, Long adminId, String reason) {
        if (userIds == null || userIds.isEmpty() || adminId == null) {
            logger.warn("批量禁言用户失败: 参数为空");
            return null;
        }

        try {
            int successCount = 0;
            int failCount = 0;
            
            for (Long userId : userIds) {
                boolean success = muteUser(userId, adminId, reason);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", userIds.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            
            logger.info("管理员 {} 批量禁言用户完成: 总数={}, 成功={}, 失败={}", 
                       adminId, userIds.size(), successCount, failCount);
            return result;
        } catch (Exception e) {
            logger.error("批量禁言用户失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转换为管理员用户视图对象
     */
    private Map<String, Object> convertToAdminUserVO(User user) {
        Map<String, Object> userVO = new HashMap<>();
        userVO.put("id", user.getId());
        userVO.put("username", user.getUsername());
        userVO.put("email", user.getEmail());
        userVO.put("phone", user.getPhone());
        userVO.put("nickname", user.getNickname());
        userVO.put("status", user.getStatus());
        userVO.put("statusText", getStatusText(user.getStatus()));
        userVO.put("avatarUrl", user.getAvatarUrl());
        userVO.put("gender", user.getGender());
        userVO.put("bio", user.getBio());
        userVO.put("followerCount", user.getFollowerCount());
        userVO.put("averageRating", user.getAverageRating());
        userVO.put("lastLoginTime", user.getLastLoginTime());
        userVO.put("createTime", user.getCreateTime());
        userVO.put("updateTime", user.getUpdateTime());
        return userVO;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0: return "正常";
            case 1: return "已封禁";
            case 2: return "已禁言";
            default: return "未知";
        }
    }
}
