package com.shiwu.admin.model;

/**
 * 二次确认结果类
 */
public class SecondaryConfirmationResult {
    private Boolean success;
    private String message;
    private SecondaryConfirmationErrorEnum error;
    private Object data;

    private SecondaryConfirmationResult(Boolean success, String message, SecondaryConfirmationErrorEnum error, Object data) {
        this.success = success;
        this.message = message;
        this.error = error;
        this.data = data;
    }

    /**
     * 创建成功结果
     * @param message 成功消息
     * @return 成功结果
     */
    public static SecondaryConfirmationResult success(String message) {
        return new SecondaryConfirmationResult(true, message, null, null);
    }

    /**
     * 创建成功结果（带数据）
     * @param message 成功消息
     * @param data 返回数据
     * @return 成功结果
     */
    public static SecondaryConfirmationResult success(String message, Object data) {
        return new SecondaryConfirmationResult(true, message, null, data);
    }

    /**
     * 创建失败结果
     * @param error 错误枚举
     * @return 失败结果
     */
    public static SecondaryConfirmationResult fail(SecondaryConfirmationErrorEnum error) {
        return new SecondaryConfirmationResult(false, error.getMessage(), error, null);
    }

    /**
     * 创建失败结果（自定义消息）
     * @param error 错误枚举
     * @param customMessage 自定义消息
     * @return 失败结果
     */
    public static SecondaryConfirmationResult fail(SecondaryConfirmationErrorEnum error, String customMessage) {
        return new SecondaryConfirmationResult(false, customMessage, error, null);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SecondaryConfirmationErrorEnum getError() {
        return error;
    }

    public void setError(SecondaryConfirmationErrorEnum error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SecondaryConfirmationResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", error=" + error +
                ", data=" + data +
                '}';
    }
}
