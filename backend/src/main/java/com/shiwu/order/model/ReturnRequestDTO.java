package com.shiwu.order.model;

/**
 * 申请退货请求DTO
 * 用于接收前端提交的退货申请数据
 * 
 * 根据SRS文档UC-10要求：
 * - 买家可以对已完成的订单申请退货
 * - 必须在7天内申请
 * - 需要填写退货原因
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class ReturnRequestDTO {
    
    /**
     * 退货原因（必填）
     * 最大长度500字符
     */
    private String reason;
    
    // 退货原因最大长度常量
    public static final int MAX_REASON_LENGTH = 500;
    
    // 构造函数
    public ReturnRequestDTO() {}
    
    public ReturnRequestDTO(String reason) {
        this.reason = reason;
    }
    
    // Getter和Setter方法
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    /**
     * 验证退货申请数据的有效性
     * @return 验证结果，true表示有效
     */
    public boolean isValid() {
        // 退货原因不能为空
        if (reason == null || reason.trim().isEmpty()) {
            return false;
        }
        
        // 退货原因长度不能超过限制
        if (reason.length() > MAX_REASON_LENGTH) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取验证失败的错误信息
     * @return 错误信息
     */
    public String getValidationError() {
        if (reason == null || reason.trim().isEmpty()) {
            return "退货原因不能为空";
        }
        
        if (reason.length() > MAX_REASON_LENGTH) {
            return "退货原因不能超过" + MAX_REASON_LENGTH + "个字符";
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return "ReturnRequestDTO{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
