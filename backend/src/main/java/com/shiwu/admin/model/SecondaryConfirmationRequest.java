package com.shiwu.admin.model;

/**
 * 二次确认请求类
 */
public class SecondaryConfirmationRequest {
    private String password;
    private String operationCode;
    private String operationDescription;
    private Object operationData;

    public SecondaryConfirmationRequest() {
    }

    public SecondaryConfirmationRequest(String password, String operationCode, String operationDescription, Object operationData) {
        this.password = password;
        this.operationCode = operationCode;
        this.operationDescription = operationDescription;
        this.operationData = operationData;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "SecondaryConfirmationRequest{" +
                "operationCode='" + operationCode + '\'' +
                ", operationDescription='" + operationDescription + '\'' +
                ", operationData=" + operationData +
                ", password='***'" +
                '}';
    }
}
