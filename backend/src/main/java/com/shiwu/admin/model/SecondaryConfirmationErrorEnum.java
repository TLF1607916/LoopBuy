package com.shiwu.admin.model;

/**
 * 二次确认错误枚举
 */
public enum SecondaryConfirmationErrorEnum {
    /**
     * 参数错误
     */
    PARAMETER_ERROR("SC0101", "参数错误", "请检查输入参数是否正确"),
    
    /**
     * 密码为空
     */
    PASSWORD_EMPTY("SC0102", "密码不能为空", "请输入管理员密码进行确认"),
    
    /**
     * 操作代码为空
     */
    OPERATION_CODE_EMPTY("SC0103", "操作代码不能为空", "系统错误，请联系技术支持"),
    
    /**
     * 密码错误
     */
    WRONG_PASSWORD("SC0201", "密码错误", "管理员密码不正确，请重新输入"),
    
    /**
     * 管理员不存在
     */
    ADMIN_NOT_FOUND("SC0202", "管理员不存在", "当前管理员账户不存在或已被删除"),
    
    /**
     * 管理员账户被禁用
     */
    ADMIN_DISABLED("SC0203", "账户已被禁用", "当前管理员账户已被禁用，无法执行操作"),
    
    /**
     * 权限不足
     */
    INSUFFICIENT_PERMISSION("SC0301", "权限不足", "您没有权限执行此操作"),
    
    /**
     * 操作不存在
     */
    OPERATION_NOT_FOUND("SC0302", "操作不存在", "指定的操作不存在或不是高风险操作"),
    
    /**
     * 操作已过期
     */
    OPERATION_EXPIRED("SC0303", "操作已过期", "操作确认已过期，请重新发起操作"),
    
    /**
     * 频繁操作
     */
    TOO_FREQUENT("SC0401", "操作过于频繁", "请稍后再试"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR("SC0500", "系统错误", "系统内部错误，请稍后重试");

    private final String code;
    private final String message;
    private final String userTip;

    SecondaryConfirmationErrorEnum(String code, String message, String userTip) {
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
        return "SecondaryConfirmationErrorEnum{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", userTip='" + userTip + '\'' +
                '}';
    }
}
