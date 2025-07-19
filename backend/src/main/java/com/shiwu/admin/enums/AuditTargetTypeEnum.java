package com.shiwu.admin.enums;

/**
 * 审计日志目标类型枚举
 */
public enum AuditTargetTypeEnum {
    
    ADMIN("ADMIN", "管理员"),
    USER("USER", "用户"),
    PRODUCT("PRODUCT", "商品"),
    ORDER("ORDER", "订单"),
    CATEGORY("CATEGORY", "分类"),
    SYSTEM("SYSTEM", "系统"),
    CONFIG("CONFIG", "配置"),
    DATA("DATA", "数据"),
    PERMISSION("PERMISSION", "权限"),
    ROLE("ROLE", "角色"),
    AUDIT_LOG("AUDIT_LOG", "审计日志"),
    FILE("FILE", "文件"),
    BATCH("BATCH", "批量操作");
    
    private final String code;
    private final String description;
    
    AuditTargetTypeEnum(String code, String description) {
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
     * @param code 目标类型代码
     * @return 对应的枚举，如果不存在则返回null
     */
    public static AuditTargetTypeEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (AuditTargetTypeEnum targetType : values()) {
            if (targetType.code.equals(code)) {
                return targetType;
            }
        }
        return null;
    }
}
