package com.shiwu.review.model;

/**
 * 评价操作结果封装类
 */
public class ReviewOperationResult {
    
    /**
     * 操作是否成功
     */
    private boolean success;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 操作结果数据
     */
    private Object data;
    
    // 私有构造函数
    private ReviewOperationResult(boolean success, String errorCode, String errorMessage, Object data) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }
    
    /**
     * 创建成功结果
     */
    public static ReviewOperationResult success(Object data) {
        return new ReviewOperationResult(true, null, null, data);
    }
    
    /**
     * 创建失败结果
     */
    public static ReviewOperationResult failure(String errorCode, String errorMessage) {
        return new ReviewOperationResult(false, errorCode, errorMessage, null);
    }
    
    // Getter方法
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public Object getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return "ReviewOperationResult{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", data=" + data +
                '}';
    }
}
