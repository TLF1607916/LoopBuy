package com.shiwu.admin.model;

/**
 * 管理员角色枚举
 */
public enum AdminRole {
    /**
     * 普通管理员
     */
    ADMIN("ADMIN", "普通管理员"),
    
    /**
     * 超级管理员
     */
    SUPER_ADMIN("SUPER_ADMIN", "超级管理员");

    private final String code;
    private final String description;

    AdminRole(String code, String description) {
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
     * 根据代码获取角色
     * @param code 角色代码
     * @return 角色枚举，如果不存在则返回null
     */
    public static AdminRole fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AdminRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 检查是否为超级管理员
     * @param role 角色代码
     * @return 是否为超级管理员
     */
    public static boolean isSuperAdmin(String role) {
        return SUPER_ADMIN.code.equals(role);
    }

    /**
     * 检查是否为有效的管理员角色
     * @param role 角色代码
     * @return 是否为有效角色
     */
    public static boolean isValidRole(String role) {
        return fromCode(role) != null;
    }
}
