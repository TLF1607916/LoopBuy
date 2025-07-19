package com.shiwu.payment.model;

/**
 * 支付操作结果封装类
 */
public class PaymentOperationResult {
    
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
    private PaymentOperationResult(boolean success, String errorCode, String errorMessage, Object data) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }
    
    /**
     * 创建成功结果
     */
    public static PaymentOperationResult success(Object data) {
        return new PaymentOperationResult(true, null, null, data);
    }
    
    /**
     * 创建失败结果
     */
    public static PaymentOperationResult failure(String errorCode, String errorMessage) {
        return new PaymentOperationResult(false, errorCode, errorMessage, null);
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
        return "PaymentOperationResult{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", data=" + data +
                '}';
    }
}
