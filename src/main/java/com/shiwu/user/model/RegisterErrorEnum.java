package com.shiwu.user.model;

/**
 * 注册错误枚举
 */
public enum RegisterErrorEnum {
    /**
     * 用户名已存在
     */
    USERNAME_EXISTS("USERNAME_EXISTS", "用户名已存在"),
    
    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS("EMAIL_EXISTS", "邮箱已被注册"),
    
    /**
     * 手机号已存在
     */
    PHONE_EXISTS("PHONE_EXISTS", "手机号已被注册"),
    
    /**
     * 密码强度不足
     */
    WEAK_PASSWORD("WEAK_PASSWORD", "密码强度不足，至少需要包含8个字符，包括字母和数字"),
    
    /**
     * 参数错误
     */
    PARAMETER_ERROR("PARAMETER_ERROR", "参数错误，请检查输入"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误，请稍后再试");
    
    private final String code;
    private final String message;
    
    RegisterErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 