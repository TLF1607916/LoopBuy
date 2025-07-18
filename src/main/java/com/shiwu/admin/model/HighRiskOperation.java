package com.shiwu.admin.model;

/**
 * 高风险操作枚举
 * 定义需要二次密码确认的管理员操作
 */
public enum HighRiskOperation {
    
    // 用户管理相关高风险操作
    /**
     * 永久删除用户账户
     */
    DELETE_USER_PERMANENTLY("DELETE_USER_PERMANENTLY", "永久删除用户账户", "SUPER_ADMIN"),
    
    /**
     * 批量封禁用户
     */
    BATCH_BAN_USERS("BATCH_BAN_USERS", "批量封禁用户", "SUPER_ADMIN"),
    
    /**
     * 重置用户密码
     */
    RESET_USER_PASSWORD("RESET_USER_PASSWORD", "重置用户密码", "ADMIN"),
    
    // 商品管理相关高风险操作
    /**
     * 永久删除商品
     */
    DELETE_PRODUCT_PERMANENTLY("DELETE_PRODUCT_PERMANENTLY", "永久删除商品", "ADMIN"),
    
    /**
     * 批量下架商品
     */
    BATCH_REMOVE_PRODUCTS("BATCH_REMOVE_PRODUCTS", "批量下架商品", "ADMIN"),
    
    // 系统管理相关高风险操作
    /**
     * 修改系统配置
     */
    MODIFY_SYSTEM_CONFIG("MODIFY_SYSTEM_CONFIG", "修改系统配置", "SUPER_ADMIN"),
    
    /**
     * 清空审计日志
     */
    CLEAR_AUDIT_LOGS("CLEAR_AUDIT_LOGS", "清空审计日志", "SUPER_ADMIN"),
    
    /**
     * 创建管理员账户
     */
    CREATE_ADMIN_ACCOUNT("CREATE_ADMIN_ACCOUNT", "创建管理员账户", "SUPER_ADMIN"),
    
    /**
     * 删除管理员账户
     */
    DELETE_ADMIN_ACCOUNT("DELETE_ADMIN_ACCOUNT", "删除管理员账户", "SUPER_ADMIN"),
    
    /**
     * 修改管理员权限
     */
    MODIFY_ADMIN_PERMISSIONS("MODIFY_ADMIN_PERMISSIONS", "修改管理员权限", "SUPER_ADMIN"),
    
    // 数据管理相关高风险操作
    /**
     * 导出用户数据
     */
    EXPORT_USER_DATA("EXPORT_USER_DATA", "导出用户数据", "SUPER_ADMIN"),
    
    /**
     * 批量数据操作
     */
    BATCH_DATA_OPERATION("BATCH_DATA_OPERATION", "批量数据操作", "SUPER_ADMIN");

    private final String code;
    private final String description;
    private final String requiredRole;

    HighRiskOperation(String code, String description, String requiredRole) {
        this.code = code;
        this.description = description;
        this.requiredRole = requiredRole;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    /**
     * 根据代码获取高风险操作
     * @param code 操作代码
     * @return 高风险操作枚举，如果不存在则返回null
     */
    public static HighRiskOperation fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (HighRiskOperation operation : values()) {
            if (operation.code.equals(code)) {
                return operation;
            }
        }
        return null;
    }

    /**
     * 检查操作是否需要指定角色权限
     * @param operation 操作
     * @param adminRole 管理员角色
     * @return 是否有权限执行该操作
     */
    public static boolean hasPermissionForOperation(HighRiskOperation operation, String adminRole) {
        if (operation == null || adminRole == null) {
            return false;
        }
        
        // 超级管理员拥有所有权限
        if (AdminRole.isSuperAdmin(adminRole)) {
            return true;
        }
        
        // 检查是否满足操作所需的最低角色要求
        return operation.requiredRole.equals(adminRole);
    }

    /**
     * 检查操作是否为高风险操作
     * @param operationCode 操作代码
     * @return 是否为高风险操作
     */
    public static boolean isHighRiskOperation(String operationCode) {
        return fromCode(operationCode) != null;
    }

    @Override
    public String toString() {
        return "HighRiskOperation{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", requiredRole='" + requiredRole + '\'' +
                '}';
    }
}
