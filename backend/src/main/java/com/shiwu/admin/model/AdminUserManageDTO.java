package com.shiwu.admin.model;

import java.util.List;

/**
 * 管理员用户管理数据传输对象
 */
public class AdminUserManageDTO {
    private String reason;           // 操作原因（封禁、禁言等需要）
    private List<Long> userIds;      // 用户ID列表（批量操作使用）

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public String toString() {
        return "AdminUserManageDTO{" +
                "reason='" + reason + '\'' +
                ", userIds=" + userIds +
                '}';
    }
}
