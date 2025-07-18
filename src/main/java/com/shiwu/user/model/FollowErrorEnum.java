package com.shiwu.user.model;

/**
 * 用户关注操作错误枚举
 * 用于关注/取关功能的错误处理
 */
public enum FollowErrorEnum {
    /**
     * 目标用户不存在
     */
    TARGET_USER_NOT_FOUND("A0120", "目标用户不存在"),
    
    /**
     * 目标用户已被封禁
     */
    TARGET_USER_BANNED("A0121", "目标用户已被封禁"),
    
    /**
     * 不能关注自己
     */
    CANNOT_FOLLOW_SELF("A0130", "不能关注自己"),
    
    /**
     * 已经关注了该用户
     */
    ALREADY_FOLLOWING("A0131", "已经关注了该用户"),
    
    /**
     * 未关注该用户
     */
    NOT_FOLLOWING("A0132", "未关注该用户"),
    
    /**
     * 用户ID格式错误
     */
    INVALID_USER_ID("A0201", "用户ID格式错误"),
    
    /**
     * 参数错误
     */
    PARAMETER_ERROR("A0202", "参数错误，请检查输入"),
    
    /**
     * 未登录
     */
    NOT_AUTHENTICATED("A0300", "请先登录"),
    
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
    
    FollowErrorEnum(String code, String message) {
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
