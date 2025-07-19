package com.shiwu.admin.model;

/**
 * 管理员商品管理数据传输对象
 */
public class AdminProductManageDTO {
    private String reason;    // 操作原因（审核拒绝、下架等需要）

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "AdminProductManageDTO{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
