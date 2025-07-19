package com.shiwu.admin.enums;

/**
 * 审计日志操作类型枚举
 * 符合NFR-SEC-03要求，记录所有敏感操作
 */
public enum AuditActionEnum {
    
    // 管理员相关操作
    ADMIN_LOGIN("ADMIN_LOGIN", "管理员登录"),
    ADMIN_LOGOUT("ADMIN_LOGOUT", "管理员登出"),
    ADMIN_CREATE("ADMIN_CREATE", "创建管理员账户"),
    ADMIN_UPDATE("ADMIN_UPDATE", "更新管理员信息"),
    ADMIN_DELETE("ADMIN_DELETE", "删除管理员账户"),
    ADMIN_ROLE_CHANGE("ADMIN_ROLE_CHANGE", "修改管理员角色"),
    ADMIN_PASSWORD_RESET("ADMIN_PASSWORD_RESET", "重置管理员密码"),
    
    // 用户管理操作
    USER_BAN("USER_BAN", "封禁用户"),
    USER_UNBAN("USER_UNBAN", "解封用户"),
    USER_MUTE("USER_MUTE", "禁言用户"),
    USER_UNMUTE("USER_UNMUTE", "解除禁言"),
    USER_DELETE("USER_DELETE", "删除用户"),
    USER_BATCH_BAN("USER_BATCH_BAN", "批量封禁用户"),
    USER_BATCH_MUTE("USER_BATCH_MUTE", "批量禁言用户"),
    USER_PROFILE_UPDATE("USER_PROFILE_UPDATE", "修改用户资料"),
    
    // 商品管理操作
    PRODUCT_APPROVE("PRODUCT_APPROVE", "审核通过商品"),
    PRODUCT_REJECT("PRODUCT_REJECT", "审核拒绝商品"),
    PRODUCT_TAKEDOWN("PRODUCT_TAKEDOWN", "下架商品"),
    PRODUCT_DELETE("PRODUCT_DELETE", "删除商品"),
    PRODUCT_BATCH_APPROVE("PRODUCT_BATCH_APPROVE", "批量审核通过商品"),
    PRODUCT_BATCH_REJECT("PRODUCT_BATCH_REJECT", "批量审核拒绝商品"),
    PRODUCT_BATCH_TAKEDOWN("PRODUCT_BATCH_TAKEDOWN", "批量下架商品"),
    
    // 订单管理操作
    ORDER_CANCEL("ORDER_CANCEL", "取消订单"),
    ORDER_REFUND("ORDER_REFUND", "订单退款"),
    ORDER_STATUS_CHANGE("ORDER_STATUS_CHANGE", "修改订单状态"),
    
    // 系统配置操作
    SYSTEM_CONFIG_UPDATE("SYSTEM_CONFIG_UPDATE", "更新系统配置"),
    SYSTEM_MAINTENANCE("SYSTEM_MAINTENANCE", "系统维护"),
    SYSTEM_BACKUP("SYSTEM_BACKUP", "系统备份"),
    SYSTEM_RESTORE("SYSTEM_RESTORE", "系统恢复"),
    
    // 数据操作
    DATA_EXPORT("DATA_EXPORT", "数据导出"),
    DATA_IMPORT("DATA_IMPORT", "数据导入"),
    DATA_DELETE("DATA_DELETE", "数据删除"),
    
    // 安全相关操作
    SECURITY_POLICY_UPDATE("SECURITY_POLICY_UPDATE", "更新安全策略"),
    ACCESS_CONTROL_CHANGE("ACCESS_CONTROL_CHANGE", "修改访问控制"),
    PERMISSION_GRANT("PERMISSION_GRANT", "授予权限"),
    PERMISSION_REVOKE("PERMISSION_REVOKE", "撤销权限"),
    
    // 审计相关操作
    AUDIT_LOG_VIEW("AUDIT_LOG_VIEW", "查看审计日志"),
    AUDIT_LOG_EXPORT("AUDIT_LOG_EXPORT", "导出审计日志"),
    
    // 其他敏感操作
    SENSITIVE_DATA_ACCESS("SENSITIVE_DATA_ACCESS", "访问敏感数据"),
    BULK_OPERATION("BULK_OPERATION", "批量操作"),
    CRITICAL_FUNCTION_EXECUTE("CRITICAL_FUNCTION_EXECUTE", "执行关键功能");
    
    private final String code;
    private final String description;
    
    AuditActionEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     * @param code 操作代码
     * @return 对应的枚举，如果不存在则返回null
     */
    public static AuditActionEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (AuditActionEnum action : values()) {
            if (action.code.equals(code)) {
                return action;
            }
        }
        return null;
    }
    
    /**
     * 检查是否为敏感操作
     * @return true如果是敏感操作
     */
    public boolean isSensitiveOperation() {
        // 所有操作都被认为是敏感的，需要记录审计日志
        return true;
    }
    
    /**
     * 检查是否为高风险操作
     * @return true如果是高风险操作
     */
    public boolean isHighRiskOperation() {
        switch (this) {
            case ADMIN_DELETE:
            case ADMIN_ROLE_CHANGE:
            case USER_DELETE:
            case USER_BATCH_BAN:
            case USER_BATCH_MUTE:
            case PRODUCT_DELETE:
            case PRODUCT_BATCH_TAKEDOWN:
            case SYSTEM_CONFIG_UPDATE:
            case DATA_DELETE:
            case DATA_IMPORT:
            case SECURITY_POLICY_UPDATE:
            case ACCESS_CONTROL_CHANGE:
            case PERMISSION_REVOKE:
            case BULK_OPERATION:
            case CRITICAL_FUNCTION_EXECUTE:
                return true;
            default:
                return false;
        }
    }
}
