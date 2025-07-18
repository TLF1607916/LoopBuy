package com.shiwu.admin.service;

import com.shiwu.admin.model.AdminLoginResult;
import com.shiwu.admin.model.SecondaryConfirmationResult;
import com.shiwu.admin.model.OperationContext;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 登录结果，包含成功信息或失败原因
     */
    AdminLoginResult login(String username, String password, String ipAddress, String userAgent);
    
    /**
     * 验证管理员权限
     * @param adminId 管理员ID
     * @param requiredRole 需要的角色（可为null，表示任何管理员角色都可以）
     * @return 是否有权限
     */
    boolean hasPermission(Long adminId, String requiredRole);
    
    /**
     * 检查管理员是否为超级管理员
     * @param adminId 管理员ID
     * @return 是否为超级管理员
     */
    boolean isSuperAdmin(Long adminId);

    /**
     * 验证管理员二次密码确认
     * @param adminId 管理员ID
     * @param password 密码
     * @param operationCode 操作代码
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 确认结果
     */
    SecondaryConfirmationResult verifySecondaryConfirmation(Long adminId, String password,
                                                           String operationCode, String ipAddress, String userAgent);

    /**
     * 创建待确认的操作上下文
     * @param adminId 管理员ID
     * @param operationCode 操作代码
     * @param operationData 操作数据
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 操作上下文ID
     */
    String createOperationContext(Long adminId, String operationCode, Object operationData,
                                 String ipAddress, String userAgent);

    /**
     * 获取操作上下文
     * @param operationId 操作ID
     * @return 操作上下文
     */
    OperationContext getOperationContext(String operationId);

    /**
     * 检查操作是否需要二次确认
     * @param operationCode 操作代码
     * @param adminRole 管理员角色
     * @return 是否需要二次确认
     */
    boolean requiresSecondaryConfirmation(String operationCode, String adminRole);
}
