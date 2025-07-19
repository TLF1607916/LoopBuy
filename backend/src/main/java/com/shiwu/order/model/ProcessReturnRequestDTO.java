package com.shiwu.order.model;

/**
 * 处理退货申请请求DTO
 * 用于接收卖家处理退货申请的数据
 * 
 * 根据SRS文档UC-18要求：
 * - 卖家可以选择"同意退货"或"拒绝退货"
 * - 拒绝退货时必须填写拒绝原因
 * - 同意退货：订单状态 RETURN_REQUESTED → RETURNED
 * - 拒绝退货：订单状态 RETURN_REQUESTED → COMPLETED
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class ProcessReturnRequestDTO {
    
    /**
     * 处理决定：true-同意退货，false-拒绝退货
     */
    private Boolean approved;
    
    /**
     * 拒绝原因（拒绝退货时必填）
     * 最大长度500字符
     */
    private String rejectReason;
    
    // 拒绝原因最大长度常量
    public static final int MAX_REJECT_REASON_LENGTH = 500;
    
    // 构造函数
    public ProcessReturnRequestDTO() {}
    
    public ProcessReturnRequestDTO(Boolean approved, String rejectReason) {
        this.approved = approved;
        this.rejectReason = rejectReason;
    }
    
    // 便捷构造函数 - 同意退货
    public static ProcessReturnRequestDTO approve() {
        return new ProcessReturnRequestDTO(true, null);
    }
    
    // 便捷构造函数 - 拒绝退货
    public static ProcessReturnRequestDTO reject(String rejectReason) {
        return new ProcessReturnRequestDTO(false, rejectReason);
    }
    
    // Getter和Setter方法
    public Boolean getApproved() {
        return approved;
    }
    
    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    /**
     * 验证处理退货申请数据的有效性
     * @return 验证结果，true表示有效
     */
    public boolean isValid() {
        // 处理决定不能为空
        if (approved == null) {
            return false;
        }
        
        // 如果是拒绝退货，必须填写拒绝原因
        if (!approved) {
            if (rejectReason == null || rejectReason.trim().isEmpty()) {
                return false;
            }
            
            // 拒绝原因长度不能超过限制
            if (rejectReason.length() > MAX_REJECT_REASON_LENGTH) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 获取验证失败的错误信息
     * @return 错误信息
     */
    public String getValidationError() {
        if (approved == null) {
            return "处理决定不能为空";
        }
        
        if (!approved) {
            if (rejectReason == null || rejectReason.trim().isEmpty()) {
                return "拒绝退货时必须填写拒绝原因";
            }
            
            if (rejectReason.length() > MAX_REJECT_REASON_LENGTH) {
                return "拒绝原因不能超过" + MAX_REJECT_REASON_LENGTH + "个字符";
            }
        }
        
        return null;
    }
    
    /**
     * 判断是否为同意退货
     * @return true表示同意退货
     */
    public boolean isApproved() {
        return Boolean.TRUE.equals(approved);
    }
    
    /**
     * 判断是否为拒绝退货
     * @return true表示拒绝退货
     */
    public boolean isRejected() {
        return Boolean.FALSE.equals(approved);
    }
    
    @Override
    public String toString() {
        return "ProcessReturnRequestDTO{" +
                "approved=" + approved +
                ", rejectReason='" + rejectReason + '\'' +
                '}';
    }
}
