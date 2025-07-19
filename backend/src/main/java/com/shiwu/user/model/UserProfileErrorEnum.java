package com.shiwu.user.model;

/**
 * 用户主页访问错误枚举
 * 用于UC-02: View User Profile的错误处理
 */
public enum UserProfileErrorEnum {
    /**
     * 用户不存在
     */
    USER_NOT_FOUND("A0120", "用户不存在"),
    
    /**
     * 用户已被封禁
     */
    USER_BANNED("A0121", "用户已被封禁，无法查看其主页"),
    
    /**
     * 用户已被禁言
     */
    USER_MUTED("A0122", "用户已被禁言"),
    
    /**
     * 用户ID格式错误
     */
    INVALID_USER_ID("A0201", "用户ID格式错误"),
    
    /**
     * 参数错误
     */
    PARAMETER_ERROR("A0202", "参数错误，请检查输入"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR("B0001", "系统执行错误，请稍后再试"),
    
    /**
     * 数据库访问错误
     */
    DATABASE_ERROR("B0002", "数据访问异常");
    
    private final String code;
    private final String message;
    
    UserProfileErrorEnum(String code, String message) {
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
