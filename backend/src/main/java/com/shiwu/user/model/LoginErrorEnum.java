package com.shiwu.user.model;

/**
 * 登录错误枚举
 */
public enum LoginErrorEnum {
    /**
     * 用户名不存在
     */
    USER_NOT_FOUND("USER_NOT_FOUND", "用户名不存在"),
    
    /**
     * 密码错误
     */
    WRONG_PASSWORD("WRONG_PASSWORD", "密码错误"),
    
    /**
     * 账户已被封禁
     */
    ACCOUNT_BANNED("ACCOUNT_BANNED", "账户已被封禁，请联系客服"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误，请稍后再试"),
    
    /**
     * 参数错误
     */
    PARAMETER_ERROR("PARAMETER_ERROR", "参数错误，请检查输入");
    
    private final String code;
    private final String message;
    
    LoginErrorEnum(String code, String message) {
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