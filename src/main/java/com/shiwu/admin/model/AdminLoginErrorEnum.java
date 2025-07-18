package com.shiwu.admin.model;

/**
 * 管理员登录错误枚举
 */
public enum AdminLoginErrorEnum {
    /**
     * 参数错误
     */
    PARAMETER_ERROR("A0101", "参数错误", "请检查用户名和密码是否正确填写"),
    
    /**
     * 管理员不存在
     */
    ADMIN_NOT_FOUND("A0102", "管理员不存在", "用户名不存在，请检查后重试"),
    
    /**
     * 密码错误
     */
    WRONG_PASSWORD("A0103", "密码错误", "密码不正确，请重新输入"),
    
    /**
     * 管理员账户被禁用
     */
    ADMIN_DISABLED("A0104", "账户已被禁用", "您的管理员账户已被禁用，请联系系统管理员"),
    
    /**
     * 管理员账户已删除
     */
    ADMIN_DELETED("A0105", "账户不存在", "管理员账户不存在或已被删除"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR("A0500", "系统错误", "系统内部错误，请稍后重试");

    private final String code;
    private final String message;
    private final String userTip;

    AdminLoginErrorEnum(String code, String message, String userTip) {
        this.code = code;
        this.message = message;
        this.userTip = userTip;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getUserTip() {
        return userTip;
    }

    @Override
    public String toString() {
        return "AdminLoginErrorEnum{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", userTip='" + userTip + '\'' +
                '}';
    }
}
