package com.shiwu.admin.vo;

import com.shiwu.admin.model.AuditLog;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 审计日志视图对象
 * 用于前端展示，包含格式化的数据
 */
public class AuditLogVO {
    
    private Long id;
    private Long adminId;
    private String adminUsername;    // 管理员用户名
    private String action;
    private String actionDescription; // 操作描述
    private String targetType;
    private String targetTypeDescription; // 目标类型描述
    private Long targetId;
    private String details;
    private String ipAddress;
    private String userAgent;
    private Integer result;
    private String resultText;       // 结果文本：成功/失败
    private LocalDateTime createTime;
    private String createTimeText;   // 格式化的创建时间
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AuditLogVO() {
    }
    
    /**
     * 从AuditLog实体创建VO
     * @param auditLog 审计日志实体
     * @return AuditLogVO
     */
    public static AuditLogVO fromEntity(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        
        AuditLogVO vo = new AuditLogVO();
        vo.setId(auditLog.getId());
        vo.setAdminId(auditLog.getAdminId());
        vo.setAction(auditLog.getAction());
        vo.setTargetType(auditLog.getTargetType());
        vo.setTargetId(auditLog.getTargetId());
        vo.setDetails(auditLog.getDetails());
        vo.setIpAddress(auditLog.getIpAddress());
        vo.setUserAgent(auditLog.getUserAgent());
        vo.setResult(auditLog.getResult());
        vo.setCreateTime(auditLog.getCreateTime());
        
        // 设置结果文本
        vo.setResultText(auditLog.getResult() != null && auditLog.getResult() == 1 ? "成功" : "失败");
        
        // 设置格式化时间
        if (auditLog.getCreateTime() != null) {
            vo.setCreateTimeText(auditLog.getCreateTime().format(FORMATTER));
        }
        
        return vo;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAdminId() {
        return adminId;
    }
    
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    
    public String getAdminUsername() {
        return adminUsername;
    }
    
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getActionDescription() {
        return actionDescription;
    }
    
    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getTargetTypeDescription() {
        return targetTypeDescription;
    }
    
    public void setTargetTypeDescription(String targetTypeDescription) {
        this.targetTypeDescription = targetTypeDescription;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
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
    
    public Integer getResult() {
        return result;
    }
    
    public void setResult(Integer result) {
        this.result = result;
    }
    
    public String getResultText() {
        return resultText;
    }
    
    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        // 同时更新格式化时间
        if (createTime != null) {
            this.createTimeText = createTime.format(FORMATTER);
        }
    }
    
    public String getCreateTimeText() {
        return createTimeText;
    }
    
    public void setCreateTimeText(String createTimeText) {
        this.createTimeText = createTimeText;
    }
    
    @Override
    public String toString() {
        return "AuditLogVO{" +
                "id=" + id +
                ", adminId=" + adminId +
                ", adminUsername='" + adminUsername + '\'' +
                ", action='" + action + '\'' +
                ", actionDescription='" + actionDescription + '\'' +
                ", targetType='" + targetType + '\'' +
                ", targetTypeDescription='" + targetTypeDescription + '\'' +
                ", targetId=" + targetId +
                ", details='" + details + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", result=" + result +
                ", resultText='" + resultText + '\'' +
                ", createTime=" + createTime +
                ", createTimeText='" + createTimeText + '\'' +
                '}';
    }
}
