package com.shiwu.admin.model;

import java.time.LocalDateTime;

/**
 * 操作上下文类
 * 用于存储待确认的高风险操作信息
 */
public class OperationContext {
    private String operationId;
    private Long adminId;
    private String operationCode;
    private String operationDescription;
    private Object operationData;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private Boolean confirmed;

    public OperationContext() {
    }

    public OperationContext(String operationId, Long adminId, String operationCode, 
                           String operationDescription, Object operationData, 
                           String ipAddress, String userAgent) {
        this.operationId = operationId;
        this.adminId = adminId;
        this.operationCode = operationCode;
        this.operationDescription = operationDescription;
        this.operationData = operationData;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createTime = LocalDateTime.now();
        this.expireTime = LocalDateTime.now().plusMinutes(5); // 5分钟过期
        this.confirmed = false;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationDescription() {
        return operationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }

    public Object getOperationData() {
        return operationData;
    }

    public void setOperationData(Object operationData) {
        this.operationData = operationData;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * 检查操作是否已过期
     * @return 是否已过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    @Override
    public String toString() {
        return "OperationContext{" +
                "operationId='" + operationId + '\'' +
                ", adminId=" + adminId +
                ", operationCode='" + operationCode + '\'' +
                ", operationDescription='" + operationDescription + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", createTime=" + createTime +
                ", expireTime=" + expireTime +
                ", confirmed=" + confirmed +
                '}';
    }
}
